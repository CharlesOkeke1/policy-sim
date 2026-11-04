import './MyGameCard.css';
import { StatusPill, type PillType } from './StatusPill';

interface MyGameCardProps {
    stateName: string;
    stateId: string;
    gameId: number;
    gameDifficulty: string;
    gameSeed: number;
    gameStartDate: string;
    isGameCompleted: boolean;
    currentMonth: number;
    score: number;
    rank: number;
    totalRounds: number;
    lastInflation: number;
    lastUnemployment: number;
    lastDebtToGDP: number;
    lastStability: number;
}
const MyGameCard = (props: MyGameCardProps) => {
    const width = (props.currentMonth / props.totalRounds) * 100 + "%";
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

    return (
        <div>
            <StatusPill type={pillType} content={tag}/>
        </div>
    )
}

export default MyGameCard