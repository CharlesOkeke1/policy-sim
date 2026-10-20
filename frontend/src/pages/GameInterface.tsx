import '../styles/GameInterface.css';
import '../components/NewGame.css';
import backButtonIcon from '../assets/left-line.svg';
import upFillIcon from '../assets/caret-up-sharp-fill.svg';
import downFillIcon from '../assets/caret-down-sharp-fill.svg';
import calendarIcon from '../assets/calendar.svg';
import { useLocation, useNavigate } from 'react-router';
import { useEffect, useRef, useState } from 'react';



export const GameInterface = () => {
    const router = useNavigate();    
    const [currentMonth, setCurrentMonth] = useState(0);
    const [totalRounds, setTotalRounds] = useState(0);
    const [roundData, setRoundData] = useState<any>(null);

    const [userRoundData, setUserRoundData] = useState<any>(null);
    const [previousUserRoundData, setPreviousUserRoundData] = useState<any>(null);
    const [gameData, setGameData] = useState<any>(null);
    const socketRef = useRef<WebSocket | null>(null);
    const location = useLocation();
    const userState = location.state.state;

    const scoreUpdateColor = previousUserRoundData && userRoundData.roundScore > previousUserRoundData.roundScore ? "var(--status-good)" : "var(--status-bad)";
    const [gameStarted, setGameStarted] = useState(false);
    const completion: string = userRoundData ? userRoundData.currentMonth/gameData.game.noOfRounds * 100 + "%" : "0%";
    const activeCaret = previousUserRoundData && userRoundData.roundScore > previousUserRoundData.roundScore ? upFillIcon : downFillIcon;
    
    useEffect(() => {
        const socket = new WebSocket("ws://localhost:8081");
        socketRef.current = socket;

        socket.addEventListener("open", () => {
            console.log("Connected to engine");
        });
    }, []);


    async function playRound() {
        setPreviousUserRoundData(userRoundData);
        const socket = socketRef.current;

        if (!socket || socket.readyState !== WebSocket.OPEN) {
        console.error("WebSocket is not open");
        return;
        }

        const res: any = await new Promise((resolve, reject) => {
        socket.onmessage = (event) => {
            resolve(JSON.parse(event.data));
        };

        socket.onerror = (event) => {
            console.error("Play round error", event);
            reject();
        }

        if (currentMonth < totalRounds) {
            socket.send(JSON.stringify({
                type: "GET_ROUND",
                payload: null
            }));
        } else if (currentMonth === totalRounds) {
            const payload = { gameId: gameData.game.id, stateId: userState.id }
            socket.send(JSON.stringify({
                type: "FINISH_GAME",
                payload: payload
            }));
            console.log("GAME FINISHED BLOCK")
        }
        });
        setCurrentMonth(res.data[0].currentMonth);
        setRoundData(res.data);
        setUserRoundData(res.data.find((el: any) => el.stateId === userState.id));

        if (currentMonth === totalRounds) {
            console.log("CONNECTION CLOSED BLOCK")
            closeConnection();
        }
        return res;
    }

    function closeConnection() {
        const socket = socketRef.current;

        if (!socket || socket.readyState !== WebSocket.OPEN) {
        console.error("WebSocket is not open");
        return;
        }

        socket.close();
        console.log("Connection successfully closed");
    }

    async function createGame() {
        const payload = {
            stateId: location.state.state.id,
            rounds: location.state.noOfRounds,
            seed: location.state.seed,
            difficulty: location.state.difficulty,
        };

        try {
            const res = await fetch("http://localhost:8080/api/games", {
                method: "POST",
                headers: {
                    //"credentials": "include",
                    "Authorization": `Bearer ${sessionStorage.getItem("authToken")}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                throw new Error(`Game creation failed: ${res.status}`);
            }

            const data = await res.json();
            setGameData(data.result);
            setTotalRounds(data.result.game.noOfRounds);

            const initData = {
                ...data.result, 
                authToken: sessionStorage.getItem("authToken"),
            }

            oneTest("INITIALIZE_SESSION", initData);
            setGameStarted(true);
        } catch (error) {
            console.error(error);
        }
    }

    function saveGame() {
        oneTest("SAVE_GAME", null);
        sessionStorage.setItem("active_step", JSON.stringify(0));
        router("/home");
    }

    function oneTest(type: string, data: any) {
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
        return;
        }

        console.error("WebSocket is closed or closing:", socket.readyState);
    }

    function backToNewGame() {
        sessionStorage.setItem("current_step", JSON.stringify(2));
        router("/home");
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
                                    {userState.initials}
                                </p>
                            </div>
                            <div>
                                <p className='chosen_state_name'>
                                    {userState.name}
                                </p>
                                <p className='chosen_state_diff'>
                                    Game #{gameData?.game.id} · {location.state.difficulty} · Seed {location.state.seed}
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
                            <p>{userRoundData?.roundScore.toFixed(2)}</p>
                            <div style={{alignItems: 'center', display: 'flex'}}>
                                <img src={activeCaret}/>
                                <p style={{color: scoreUpdateColor}}>{((userRoundData?.roundScore - previousUserRoundData?.roundScore) || 0).toFixed(2)}</p>
                            </div>
                        </div>
                        <div className='rank election'>
                            <div style={{color: 'red'}}>
                                <img src={calendarIcon}/>
                            </div>
                            <p>Election</p>
                            <p style={{fontWeight: 'var(--weight-medium)', fontSize: 'var(--text-body)'}}>2</p>
                            <p>months</p>
                        </div>
                    </div>
                </div>
                <div className='main_game_int'>
                    <div>
                        <button onClick={() => playRound()}>
                            <p>{ currentMonth === totalRounds ?
                            "Finish the game" : "Play a goddamn round"}</p>
                        </button>
                    </div>
                    <div>

                    </div>
                    <div>

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
                                    {userState.initials}
                                    </p>
                                </div>
                                <div>
                                    <p className='chosen_state_name'>
                                    {userState.name}
                                </p>
                                    <p className='chosen_state_diff'>
                                    {userState.luck}
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
                            <a className='modal_button' onClick={() => backToNewGame()}> Change game configuration.</a>
                        </div>
                    </div>
                </>
            : <div></div> 
            }
        </>
    )
}
