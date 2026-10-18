import './StatCard.css';

export interface StatCardProps {
    label: string;
    stat: number | string;
    supporting: string;
}

export const StatCard = ({label, stat, supporting}: StatCardProps) => {
    return (
        <div className='stat_card'>
            <p className='label'>{label}</p>
            <p className='stat'>{stat}</p>
            <p className='supporting'>{supporting}</p>
        </div>
    )
}
