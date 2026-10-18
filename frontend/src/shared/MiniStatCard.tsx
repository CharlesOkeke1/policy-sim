import './MiniStatCard.css';
import type { StatCardProps } from './StatCard';

export const MiniStatCard = ({label, stat, supporting}: StatCardProps) => {
    return (
        <div className='mini_stat_card'>
            <p className='mini_label'>{label}</p>
            <p className='mini_stat'>{stat}</p>
            <p className='mini_supporting'>{supporting}</p>
        </div>
    )
}