import './RecentGame.css';
import { StatusPill, type PillType } from './StatusPill';

interface RecentGameProps {
    content: string;
    name: string;
    currentMonth: number;
    totalRounds: number;
    startDate: string;
    score: string
}


export const RecentGame = ({content, name, currentMonth, totalRounds, 
    startDate, score
}: RecentGameProps) => {
    let type: PillType = 'NULL';

    switch(content) {
        case "Completed":
            type = "SUCCESS";
            break;
        case "In progress":
            type = "WARNING";
            break;
        case "Abandoned":
            type = "FAILURE";
            break;
        case "Information":
            type = "INFORMATION";
            break;

    }

    return (
        <div className='recentGameComponent'>
            <div className='stateIcon'>
                <p>{name.substring(0,3)}</p>
            </div>
            <div className='game_state_name_block'>
                <p className='stateName'>{name}</p>
                <p className='game_date_and_progress'>Month {currentMonth} / {totalRounds} · {startDate}</p>
            </div>
            <p className='score'>{score}</p>
            <StatusPill content={content} type={type}/>
        </div>
    )
}
