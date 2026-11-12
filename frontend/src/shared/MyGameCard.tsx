import { FaEllipsisVertical } from 'react-icons/fa6';
import { CustomIcon } from './Icon';
import './MyGameCard.css';
import upFillIcon from '../assets/caret-up-sharp-fill.svg';
import dropdownIcon from '../assets/dropdown.svg';
import downFillIcon from '../assets/caret-down-sharp-fill.svg';
import { StatusPill, type PillType } from './StatusPill';
import type { StatePreview } from '../dtos/Models';

interface MyGameCardProps {
    stateId: string;
    gameId: number;
    gameDifficulty: string;
    gameSeed: number;
    gameStartDate?: string;
    isGameCompleted: boolean;
    currentMonth: number;
    score: number;
    rank: number;
    totalRounds: number;
    stateList: StatePreview[];
    roundList: any[];
    click: () => void;
}

const MyGameCard = (props: MyGameCardProps) => {

    const userRounds = props.roundList
        .filter((round) => round.stateId === props.stateId)
        .toSorted((a, b) => b.currentMonth - a.currentMonth);
    const secondToLast = userRounds[1];
    const lastRound = userRounds[0];
    

    const currentGreater = lastRound?.roundScore > secondToLast?.roundScore;
    const diff = lastRound && secondToLast ? lastRound?.roundScore - secondToLast?.roundScore : 0;
    
    const activeCaret = currentGreater ? upFillIcon : downFillIcon;
    const caretColor = currentGreater ? "var(--status-good)" : "var(--status-bad)";
    
    const width = (props.currentMonth / props.totalRounds * 100);
    
    const tag = props.isGameCompleted && props.currentMonth === props.totalRounds ? "Completed" : 
    props.isGameCompleted && props.currentMonth !== props.totalRounds ? "Abandoned" : "In Progress";

    let pillType: PillType = "NULL";
    switch (tag) {
        case "Completed":
            pillType = "SUCCESS";
            break;
        case "Abandoned":
            pillType = "FAILURE";
            break;
        case "In Progress":
            pillType = "WARNING";
            break;
    }

    const progressColor = width < 33 ? "var(--status-bad)" : width < 67 ? "var(--status-warn)" : "var(--status-good)";
    
    const getUserState = (stateId: string): StatePreview | undefined=> {
        const state = props.stateList?.find((state) => state.id === stateId);
        return state ? state : undefined;
    }

    function resumeGameInProgress() {

    }

    return (
        <div className='gameCompElement'> 
            <div className="game_comp_top">
                <div className='game_chosen_state_container'>
                    <div className='state_initials'>
                        <p style={{fontWeight: 'var(--weight-semibold)'}}>
                            {getUserState(props.stateId)?.initials}
                        </p>
                    </div>
                    <div>
                        <p className='chosen_state_name'>
                            {getUserState(props.stateId)?.name}
                        </p>
                        <p className='chosen_state_diff'>
                            Game #{props.gameId} · {props.gameDifficulty}
                        </p>
                    </div>
                </div>
                <div className='game_comp_top_right'>
                    <StatusPill type={pillType} content={tag}/>
                    <div style={{marginTop: '3px'}}>
                        <CustomIcon icon={FaEllipsisVertical} fill='var(--text-muted)' strokeWidth={5} variant="circular" size={"var(--text-h2)"}/>
                    </div>
                </div>
            </div>
            <div className='game_comp_mid'>
                <div className='game_comp_mid_el'>
                    <p>Score</p>
                    <div className='staggered_row_mid'>
                        <p className="game_comp_mid_el_value">{props.score.toFixed(2)}</p>
                        <div className='caret_col'>
                            <img className='caret_style' src={activeCaret}/>
                            <p style={{color: caretColor, fontSize: "15px"}}>{diff.toFixed(2)}</p>
                        </div>
                    </div>
                </div>
                <div className='game_comp_mid_el'>
                    <p>Rank</p>
                    <div className='game_comp_rank'>
                        <p className="game_comp_mid_el_value">#{props.rank}</p>
                        <p>/</p>
                        <p style={{fontSize: 'var(--text-body)'}}>{props.stateList.length}</p>
                    </div>
                </div>
                <div className='game_comp_mid_el'>
                    <p>Month</p>
                    <div className='month_row'>
                        <div className='game_comp_rank'>
                            <p className="game_comp_mid_el_value">{props.currentMonth}</p>
                            <p>/</p> 
                            <p style={{fontSize: 'var(--text-body)'}}>{props.totalRounds}</p>
                        </div>
                    </div>
                    <div className='progress_container'>
                        <div className='progress_el' style={{width: width  + "%", backgroundColor: progressColor}}></div>
                    </div>
                </div>   
            </div>
            <div className='game_comp_bottom'>
                <div className='label'>
                    <p>Inflation</p>
                    <p style={{color: "var(--text-primary)"}}>{((lastRound?.lastInflation ?? 0) * 100).toFixed(2)}%</p>
                </div>
                <div className='label'>
                    <p>Tax Rate</p>
                    <p style={{color: "var(--text-primary)"}}>{((lastRound?.taxRate ?? 0) * 100).toFixed(2)}%</p>
                </div>
                <div className='label'>
                    <p>Debt(millions)</p>
                    <p style={{color: "var(--text-primary)"}}>{lastRound?.governmentDebt.toFixed(2)}</p>
                </div>
                <div className='label'>
                    <p>Stability</p>
                    <p style={{color: "var(--text-primary)"}}>{(lastRound?.stability ?? 0).toFixed(2)}%</p>
                </div>
            </div>
            <div className='game_comp_footer'>
                <div className='calendar'>
                    <input className='date_input' type='date'/>
                    <img src={dropdownIcon} className='date_icon'/>
                </div>
                {
                    tag === "Completed" ?
                    <div className='btn_resume view_report'>
                        View report
                    </div>
                    : (
                        tag === "Abandoned" ? 
                        <div className='btn_resume view_report'>
                            View report
                        </div>                     
                        :
                        <div className='btn_resume' onClick={() => props.click()}>
                            Resume
                        </div>
                    )
                    
                }
            </div>
        </div>
    )
}

export default MyGameCard