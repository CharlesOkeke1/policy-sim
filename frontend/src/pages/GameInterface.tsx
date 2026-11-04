import '../styles/GameInterface.css';
import '../components/NewGame.css';
import backButtonIcon from '../assets/left-line.svg';
import upFillIcon from '../assets/caret-up-sharp-fill.svg';
import downFillIcon from '../assets/caret-down-sharp-fill.svg';
import pauseIcon from '../assets/pause-broken.svg';
import calendarIcon from '../assets/calendar.svg';
import { useLocation, useNavigate } from 'react-router';
import { useEffect, useRef, useState } from 'react';
import { YourEconomyStat } from '../shared/YourEconomyStat';
import { ProgressBar } from '../shared/ProgressBar';
import PolicyCard from '../shared/PolicyCard';
import { customFetch, handleTokenRefresh } from '../utils/FetchWrapper';
import { NewGame } from '../components/NewGame';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from 'recharts';
import { StatusPill } from '../shared/StatusPill';
import { formatDate, formatDateSeconds } from '../utils/Formatting';
import { MiniStatCard } from '../shared/MiniStatCard';
import type { ApiResponse } from '../dtos/Models';


/**
 *  GAME INITIALIZATION RRT STATS
 *  Average Round-Trip Time (Mean): 31.73 ms
 *  The p95 Percentile: 41.22 ms
 *  Fastest Execution: 17.02 ms
 *  Slowest Running Execution: 57.71 ms 
 */

/**
 *  GAMEPLAY ROUNDS RTT STATS
 *  Average Round-Trip Time (Mean): 1.29 ms
 *  The p95 Percentile: 2.10 ms
 *  The p99 Percentile: 2.42 ms
 *  Fastest Execution: -0.19 ms
 *  Slowest Running Execution: 2.47 ms
 */

interface Chart {
    month: number;
    targetInflation: number;
    inflation: number;
    unemployment: number;
}

export const GameInterface = () => {
    const router = useNavigate();    
    const [currentMonth, setCurrentMonth] = useState(0);
    const [totalRounds, setTotalRounds] = useState(0);
    const [roundData, setRoundData] = useState<any>(null);
    const [policyList, setPolicyList] = useState<any[]>([]);
    const [currentPolicy, setCurrentPolicy] = useState<any>(policyList[0]);

    const [userRoundData, setUserRoundData] = useState<any>(null);
    const [previousUserRoundData, setPreviousUserRoundData] = useState<any>(null);
    const [gameData, setGameData] = useState<any>(null);
    const socketRef = useRef<WebSocket | null>(null);
    const location = useLocation();
    const gameInitState = location.state;

    const [chartData, setChartData] = useState<Chart[]>([]);

    const scoreUpdateColor = previousUserRoundData && userRoundData.roundScore > previousUserRoundData.roundScore ? "var(--status-good)" : "var(--status-bad)";
    const [gameStarted, setGameStarted] = useState(false);
    const [paused, setPaused] = useState(false);
    const [pauseTime, setPauseTime] = useState<String>("");
    const completion: string = userRoundData ? userRoundData.currentMonth/gameData.game.noOfRounds * 100 + "%" : "0%";
    const activeCaret = previousUserRoundData && userRoundData.roundScore > previousUserRoundData.roundScore ? upFillIcon : downFillIcon;
    const [pingInterval, setPingInterval] = useState<ReturnType<typeof setInterval>>();
    const [refreshInterval, setRefreshInterval] = useState<ReturnType<typeof setInterval>>();
    let gameInitTime = "";
    let roundInitTime = "";

    const [pauseDisplayTime, setPauseDisplayTime] = useState("");
    
    useEffect(() => {
        getPolicies();
        const socket = new WebSocket("ws://localhost:8081");
        socketRef.current = socket;

        socket.addEventListener("open", () => {
            console.log("Connected to engine");
        });
    }, []);

    // Ping server
    useEffect(() => {
        const socket = socketRef.current;
        setPingInterval(setInterval(() => {
            pingServer(socket);  
        }, 10000));
    }, [])
    
    // Update the pause timer
    useEffect(() => {
        const interval = setInterval(() => {
            if (paused) {
                setPauseTime(new Date(Date.now()).toString());
                setPauseDisplayTime(formatDateSeconds(String(pauseTime)));
            }
        }, 1000);

        return () => clearInterval(interval);
    }, [paused]);

    // Refresh user token so that server authentication works just fine
    useEffect(() => {
        setRefreshInterval(setInterval(() => { 
            const refreshSuccess = handleTokenRefresh();
        }, 29 * 60 * 1000));
    }, []);


    function pingServer(socket: WebSocket | null) {
        const payload = { networkPing: "ping" };
        socket?.send(JSON.stringify({
            type: "PING_SERVER",
            payload: payload
        }));
    }

    function addToChart(round: any) {
        setChartData(prev => [...prev, {
            month: round.currentMonth,
            targetInflation: 2.4,
            inflation: round.lastInflation * 100,
            unemployment: round.unemployment * 100
        }].slice(-24));
    }

    async function playRound() {
        setPreviousUserRoundData(userRoundData);
        const socket = socketRef.current;

        roundInitTime = `${(new Date(Date.now())).toISOString()}`;

        if (!socket || socket.readyState !== WebSocket.OPEN) {
            console.error("WebSocket is not open");
            return;
        }

        const res: any = await new Promise((resolve, reject) => {
            socket.onmessage = (event) => {
                resolve(JSON.parse(event.data));
                if (currentMonth === totalRounds) {
                    // Response from FINISH_GAME
                    navToStep(0);
                    console.log("GAME FINISHED, CONNECTION TERMINATED");
                    closeConnection();
                }
            };


            socket.onerror = (event) => {
                console.error("Play round error", event);
                reject();
            }

            if (currentMonth < totalRounds) {
                const payload = {
                    policyId: currentPolicy.policyId,
                    stateId: gameInitState.state.id
                }
                socket.send(JSON.stringify({
                    type: "ADVANCE_ROUND",
                    payload: payload
                }));
            } else if (currentMonth === totalRounds) {
                const payload = { gameId: gameData.game.id, stateId: gameInitState.state.id }
                socket.send(JSON.stringify({
                    type: "FINISH_GAME",
                    payload: payload
                }));
            }
        });

        const userData = res.data.result?.find((el: any) => el.stateId === gameInitState.state.id);
        setCurrentMonth(userData.currentMonth);
        setRoundData(res.data.result);
        addToChart(userData)
        calculateRRT(roundInitTime, res.data.responseTime);
        setUserRoundData(userData);
        return res;
    }

    function closeConnection() {
        const socket = socketRef.current;

        if (!socket || socket.readyState !== WebSocket.OPEN) {
        console.error("WebSocket is not open");
        return;
        }

        socket.close();
        clearInterval(pingInterval);
        clearInterval(refreshInterval);
        console.log("Connection successfully closed");
    }

    async function createGame() {
        const res: ApiResponse<string> = await 
        customFetch({url: `http://localhost:8080/api/tokens/${sessionStorage.getItem("userId")}`,
        method: "GET"});
        const token = res.result;

        const socket = socketRef.current;
        const payload = {
            stateId: location.state.state.id,
            rounds: location.state.noOfRounds,
            seed: location.state.seed,
            difficulty: location.state.difficulty,
        };

        try {
            gameInitTime = `${(new Date(Date.now())).toISOString()}`;
            const res: any = await customFetch({method: "POST", url: "http://localhost:8080/api/games",
                body: payload
            })

            setGameData(res.result);
            setTotalRounds(res.result.game.noOfRounds);

            const initData = {
                ...res.result, 
                authToken: token,
            }

            postToServer("INITIALIZE_SESSION", initData);
        } catch (error) {
            console.error(error);
        }
    }

    function saveGame() {
        postToServer("SAVE_GAME", null);
        navToStep(0);
        closeConnection();
    }

    function abandonGame() {
        const payload = { gameId: gameData.game.id, stateId: gameInitState.state.id }
        postToServer("ABANDON_GAME", payload);
        closeConnection();
        router("/home");
    }

    function pauseGame() {
        setPaused(true);
        setPauseTime((new Date(Date.now())).toString());
        postToServer("SAVE_GAME", null);
    }

    function resumeGame() { setPaused(false); }

    async function getPolicies() {
        const url = "http://localhost:8080/api/games/policies";
        const res: any = await customFetch({method: "GET", url: url});
        setPolicyList(res.result);
    }

    function calculateRRT(initTime: string, resTime: string) {
        const resSeconds = resTime.split(":")[2].split("").filter((el) => el.toLowerCase() !== "z").join("");
        const reqSeconds = initTime.split(":")[2].split("").filter((el) => el.toLowerCase() !== "z").join("");

        console.log("Request to response time => ", (Number(resSeconds) - Number(reqSeconds))*1000, "ms");
    }

    function setPolicy(policyId: string) {
        const policy = policyList.find((el) => el.policyId === policyId);
        setCurrentPolicy(policy);
    }

    async function postToServer(type: string, data: any) {
        const socket = socketRef.current;

        if (!socket) {
            console.error("WebSocket has not been created");
            return;
        }

        if (socket.readyState === WebSocket.CONNECTING) {
            socket.addEventListener("open", () => {
            socket.send(JSON.stringify({ type, payload: data }));
            }, { once: true });
            return;
        }

        if (socket.readyState === WebSocket.OPEN) {
            socket.send(JSON.stringify({ type, payload: data }));
            socket.addEventListener("message", (e) => {
                const res = JSON.parse(e.data);
                if (res.type === "INITIALIZE_SESSION")  {
                    calculateRRT(gameInitTime, res.data.responseTime);
                    setGameStarted(true);
                    return "Game started";
                }
            });
            return;
        }

        console.error("WebSocket is closed or closing:", socket.readyState);
    }


    function navToStep(step: number) {
        sessionStorage.setItem("active_step", JSON.stringify(step));
        router("/home");
    }

    function formatValue(num: number): string {
        if (!num) return '0'
        if (num > 0) return `+${num}`;

        return `${num}`;
    }

    return (
        <>
            <div className='game_interface'>
                <div className='game_header'>
                    <div className='header_left'>
                        <div className='save_and_exit_container' onClick={() => saveGame()}>
                            <img className='' src={backButtonIcon}/>
                            <a className='save_and_exit'>Save and exit</a>
                        </div>
                        <div className='game_chosen_state_container'>
                            <div className='state_initials'>
                                <p style={{fontWeight: 'var(--weight-semibold)'}}>
                                    {gameInitState.state.initials}
                                </p>
                            </div>
                            <div>
                                <p className='chosen_state_name'>
                                    {gameInitState.state.name}
                                </p>
                                <p className='chosen_state_diff'>
                                    Game #{gameData?.game.id} · {gameInitState.difficulty} · Seed {location.state.seed}
                                </p>
                            </div>
                        </div>
                        <div className='current_month'>
                            <div className='month_row'>
                                <div style={{display: 'flex', gap: '3px'}}>
                                    <p>Month</p>
                                    <p>{currentMonth}</p>
                                    <p>/{gameData?.game.noOfRounds || 0}</p>
                                </div>
                                <p>Year {Math.floor(userRoundData?.currentMonth / 12) + 1 || "0"} of {Math.floor(gameData?.game.noOfRounds / 12)}</p>
                            </div>
                            <div className='progress_container'>
                                <div className='progress_el' style={{width: completion}}></div>
                            </div>
                        </div>
                    </div>
                    <div className='header_right'>
                        <div className='rank rank_gap'>
                            <p>Rank</p>
                            <p style={{fontWeight: 'var(--weight-medium)', fontSize: 'var(--text-body)'}}>#{userRoundData?.roundRank || 0}</p>
                            <p>/</p>
                            <p>{location.state.noOfStates || 0}</p>
                        </div>
                        <div className='rank'>
                            <p>Score</p>
                            <p>{userRoundData?.roundScore.toFixed(2) ?? (0).toFixed(2)}</p>
                            <div style={{alignItems: 'center', display: 'flex'}}>
                                <img src={activeCaret}/>
                                <p style={{color: scoreUpdateColor}}>{((userRoundData?.roundScore - previousUserRoundData?.roundScore) || 0).toFixed(2)}</p>
                            </div>
                        </div>
                        {/* <div className='rank election'>
                            <div style={{color: 'red'}}>
                                <img src={calendarIcon}/>
                            </div>
                            <p>Election</p>
                            <p style={{fontWeight: 'var(--weight-medium)', fontSize: 'var(--text-body)'}}>2</p>
                            <p>months</p>
                        </div> */}
                        <div className='rank pause' onClick={() => pauseGame()}>
                            <img src={pauseIcon}/>
                        </div>
                    </div>
                </div>
                <div className='main_game_int'>
                    <div className='left_panel'>                            
                        <div className='your_economy_display'>
                            <p className='your_economy_header'>Your economy</p>
                            <p className='your_economy_sub_header'>End of month {userRoundData?.currentMonth}, change on the month before.</p>
                            <YourEconomyStat suffix='' value={userRoundData?.priceLevel} name='Price level' previous={previousUserRoundData?.priceLevel}/>
                            <YourEconomyStat suffix='%' value={userRoundData?.lastInflation * 100} name='Last Inflation' previous={previousUserRoundData?.lastInflation * 100}/>
                            <YourEconomyStat suffix='%' value={userRoundData?.unemployment * 100} name='Unemployment' previous={previousUserRoundData?.unemployment * 100}/>
                            <YourEconomyStat suffix='' value={userRoundData?.output} name='Economic output' previous={previousUserRoundData?.output}/>
                            <YourEconomyStat suffix='%' value={userRoundData?.stability} name='Stability' previous={previousUserRoundData?.stability}/>
                            <YourEconomyStat suffix='' value={userRoundData?.governmentDebt} name='Government Debt' previous={previousUserRoundData?.governmentDebt}/>
                        </div>

                        <div className='your_economy_display'>
                            <p className='your_economy_header'>Your levers</p>
                            <p className='your_economy_sub_header'>Policies move these. Bars show where each sits in its allowed range.</p>
                            <ProgressBar name='Tax Rate' current={userRoundData?.taxRate * 100} target={60} minimum={0}/>
                            <ProgressBar name='Investment Share' current={userRoundData?.investmentShare * 100} target={100} minimum={0}/>
                            <ProgressBar name='Transfer Share' current={userRoundData?.transferShare * 100} target={20} minimum={0}/>
                            <ProgressBar name='Spending Share' current={userRoundData?.spendingShare * 100} target={50} minimum={0}/>
                        </div>
                        
                    </div>
                    <div className='center_panel'>
                        <div className='choose_policy'>
                            <div>
                                <p className='your_economy_header'>Choose this month's policy</p>
                                <p className='your_economy_sub_header'>One policy per month. It applies before the economy moves.</p>
                            </div>
                            <div className='policies'>
                                {
                                    policyList.map((policy: any) => (
                                        <PolicyCard name={policy.name}  policyId={policy.policyId} effect={policy.policySpecificEffect}
                                        effectOnStability={policy.effectOnStability} activePolicyId={currentPolicy?.policyId} affectedLever={policy.affectedLever}
                                        onClick={() => setPolicy(policy.policyId)}/>                                    
                                    ))
                                }
                            </div>
                            <div className='policies_footer'>
                                <div>
                                    <div className='chosen_policy_row'>
                                        <p>Selected Policy:</p>
                                        <p style={{fontWeight: "var(--weight-medium)"}}>{currentPolicy?.name.toUpperCase() ?? 'None'}</p>
                                    </div>
                                    <div className='chosen_policy_row'>
                                        <p>Policy Effect: </p>
                                        <p>{currentPolicy?.affectedLever  ?? 'None'} {formatValue(currentPolicy?.policySpecificEffect)}</p>
                                    </div>
                                </div>                                
                                <button className='next_round' onClick={() => playRound()} disabled={currentPolicy === null || currentPolicy === undefined}>
                                    { currentMonth === totalRounds ?
                                        <p>Finish Game</p> : <p>Advance to Month {(userRoundData?.currentMonth ?? 0) + 1}</p>
                                    }
                                </button>
                            </div>
                        </div>
                        <div className='choose_policy'>
                            <div>
                                <p className='your_economy_header'>Inflation and unemployment</p>
                                <p className='your_economy_sub_header'>Last 24 months, annual rates. Dashed line is the 2.4% inflation target.</p>
                            </div>
                            <div style={{ width: '100%', height: 300, display: 'flex', justifyContent: 'center'}}>
                                
                                    {
                                        chartData.length > 0 ?
                                        <ResponsiveContainer>
                                            <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                                                <CartesianGrid strokeDasharray="3 3" />
                                                <XAxis dataKey="month"/>
                                                <YAxis />
                                                <Tooltip />
                                                <Legend />
                                                <Line type="monotone" name="Last Inflation" dataKey="inflation" stroke="#ff8042"/>
                                                <Line type="monotone" strokeDasharray="5 5" name="Target Inflation" dataKey="targetInflation" stroke="var(--text-muted)"/>
                                                <Line type="monotone" name="Unemployment" dataKey="unemployment" stroke="#0088fe"/>
                                            </LineChart>
                                        </ResponsiveContainer>
                                        : 
                                        <div className='chart_empty_state'>
                                            <p> No data to display</p>
                                        </div>
                                    }
                                
                            </div>
                        </div>
                    </div>
                    <div className='right_panel'>
                        <div className='your_economy_display'>
                            <p className='your_economy_header'>Federal economy</p>
                            <p className='your_economy_sub_header'>Shared between all eight cities.</p>
                            <YourEconomyStat suffix='%' value={userRoundData?.federalTaxShare * 100} name='Federal tax share' previous={previousUserRoundData?.federalTaxShare * 100}/>
                            <YourEconomyStat suffix='' value={userRoundData?.resourcePrice} name='Resource price' previous={previousUserRoundData?.resourcePrice}/>
                            <YourEconomyStat suffix='' value={userRoundData?.federalTax} name='You paid in' previous={previousUserRoundData?.federalTax}/>
                            <YourEconomyStat suffix='' value={userRoundData?.allocation} name='You received' previous={previousUserRoundData?.allocation}/>
                            <p className='your_economy_sub_header'>Allocation is split by an equal share, population and output.</p>
                        </div>

                        <div className='your_economy_display'>
                                <p className='your_economy_header'>Month {userRoundData?.currentMonth ?? 0} report</p>
                                <p className='your_economy_sub_header'>Activity across different states within the NewGame</p>
                                <p>TODO</p>
                        </div>
                    </div>
                </div>
            </div>

            {!gameStarted ? 
                <>
                    <div className='start-modal'>
                        <div className='modal'>
                            <p className='modal_header'>Ready to start?</p>
                            <p className='modal_sub_header'>Your world is seeded. The first month runs when you choose your first policy.</p>
                            <div className='chosen_state_container'>
                                <div className='state_initials'>
                                    <p style={{fontWeight: 'var(--weight-semibold)'}}>
                                    {gameInitState.state.initials}
                                    </p>
                                </div>
                                <div>
                                    <p className='chosen_state_name'>
                                    {gameInitState.state.name}
                                </p>
                                    <p className='chosen_state_diff'>
                                    {gameInitState.state.luck}
                                    </p>
                                </div>
                            </div>
                            <div className='your_game_summary_item'>
                                <p>Difficulty</p>
                                <p>{location.state.difficulty}</p>
                            </div>
                            <div className='your_game_summary_item'>
                                <p>Length</p>
                                <p>{location.state.noOfRounds} months · {location.state.noOfRounds / 12} years</p>
                            </div>
                            <div className='your_game_summary_item'>
                                <p>Rivals</p>
                                <p>7 computer governors</p>
                            </div>
                            <div className='your_game_summary_item'>
                                <p>Seed</p>
                                <p>{location.state.seed}</p>
                            </div>
                            <div className='startGameBtnContainer' onClick={() => createGame()}>
                                <button className='startGameBtn'>
                                    <p>Start Game</p>
                                </button>
                            </div>
                            <a className='modal_button' onClick={() => navToStep(2)}> Change game configuration.</a>
                        </div>
                    </div>
                </>
            : <div></div> 
            }

            {paused ? 
                <>
                    <div className='start-modal'>
                        <div className='modal'>
                            <div className='pause_header'>
                                <div>
                                    <p className='modal_sub_header'>GAME #{gameData?.game.id} · PAUSED</p>
                                    <p className='modal_header'>Game paused</p>
                                </div>
                                <StatusPill content={"Saved " + pauseDisplayTime} type='SUCCESS'/>
                            </div>
                            <div className='game_paused_container'>
                                <div className='chosen_state_top'>
                                    <div className='game_pause_state'>
                                        <div className='state_initials'>
                                            <p style={{fontWeight: 'var(--weight-semibold)'}}>
                                                {gameInitState.state.initials}
                                            </p>
                                        </div>
                                        <div>
                                            <p className='chosen_state_name'>
                                                {gameInitState.state.name}
                                            </p>
                                            <p className='chosen_state_diff'>
                                                {gameInitState.difficulty}
                                            </p>
                                        </div>
                                    </div>
                                    <div className='game_pause_values'>
                                        <div className='game_pause_rank'>
                                            <p>Rank</p>
                                            <p style={{fontWeight: 'var(--weight-medium)', fontSize: 'var(--text-body)'}}>#{userRoundData?.roundRank || 0}</p>
                                            <p>/</p>
                                            <p>{location.state.noOfStates || 0}</p>
                                        </div>
                                        <div className='game_pause_rank'>
                                            <p>Score</p>
                                            <p>{userRoundData?.roundScore.toFixed(2) ?? (0).toFixed(2)}</p>
                                            <div style={{alignItems: 'center', display: 'flex'}}>
                                                <img src={activeCaret}/>
                                                <p style={{color: scoreUpdateColor}}>{((userRoundData?.roundScore - previousUserRoundData?.roundScore) || 0).toFixed(2)}</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='game_paused_month'>
                                    <div className='month_row'>
                                        <div style={{display: 'flex', gap: '3px'}}>
                                            <p>Month</p>
                                            <p>{currentMonth}</p>
                                            <p>/{gameData?.game.noOfRounds || 0}</p>
                                        </div>
                                        <p>{gameData?.game.noOfRounds - (userRoundData?.currentMonth ?? 0)} months left</p>
                                    </div>
                                    <div className='progress_container'>
                                        <div className='progress_el' style={{width: completion}}></div>
                                    </div>
                                </div>    
                            </div>
                            <div className='buttonRow'>
                                <MiniStatCard label='Inflation' stat={(userRoundData?.lastInflation * 100).toFixed(2) ?? "0.0"} prevStat={(previousUserRoundData?.lastInflation * 100).toFixed(2) ?? "0.0"}/>
                                <MiniStatCard label='Unemployment' stat={(userRoundData?.unemployment * 100).toFixed(2) ?? "0.0"} prevStat={(previousUserRoundData?.unemployment * 100).toFixed(2) ?? "0.0"}/>
                            </div>

                            <button className='PauseBtnContainer' onClick={() => resumeGame()}>
                                <p>Resume Game</p>
                            </button>
                            <div className='buttonRow'>
                                <button className='PauseBtnContainer save_pause_container' onClick={() => saveGame()}>
                                    <p>Save and exit</p>
                                </button>
                                <button className='PauseBtnContainer abandon_pause_container' onClick={() => abandonGame()}>
                                    <p>Abandon Game</p>
                                </button>
                            </div>
                            
                        </div>
                    </div>
                </>
            : <div></div> 
            }
        </>
    )
}