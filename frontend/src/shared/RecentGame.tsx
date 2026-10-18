import './RecentGame.css';
import { StatusPill, type PillType } from './StatusPill';

interface RecentGameProps {
    type: PillType;
}

export const RecentGame = ({type}: RecentGameProps) => {
    /**
     * stateIcon will take game.stateName.substring(0,3);
     * stateName takes game.stateName;
     * game_date_and_progress will take currentRound/totalRounds and start date (day and month)
     * score will take a variable (yet to be added) game.score
     * type 
     */
    return (
        <div className='recentGameComponent'>
            <div className='stateIcon'>
                <p>Lon</p>
            </div>
            <div className='game_state_name_block'>
                <p className='stateName'>London</p>
                <p className='game_date_and_progress'>Month 240/240 · 16 Oct</p>
            </div>
            <p className='score'>88.4</p>
            <StatusPill content='Completed' type={type}/>
        </div>
    )
}
