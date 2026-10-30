import './MiniStatCard.css';
import type { StatCardProps } from './StatCard';

export const MiniStatCard = ({label, stat, supporting, prevStat}: StatCardProps) => {

    const color = prevStat ? 
        prevStat > stat ? "var(--status-bad)" : prevStat < stat ? "var(--status-good)" : "var(--text-secondary)"
    : "";
    return (
        <div className='mini_stat_card'>
            <p className='mini_label'>{label}</p>
            <p className='mini_stat' style={{color: color}}>{stat}</p>
            <p className='mini_supporting'>{supporting}</p>
        </div>
    )
}