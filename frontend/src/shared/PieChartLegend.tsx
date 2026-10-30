import './PieChartLegend.css';

interface RecentGameProps {
    type: "Completed" | "In progress" | "Abandoned";
    value: number;
    total: number;
}

export const PieChartLegend = ({type, value, total}: RecentGameProps) => {
    let color = "";
    switch (type) {
        case "Abandoned": 
            color = "var(--status-bad)";
            break;
        case "Completed":
            color = "var(--status-good)";
            break;
        case "In progress":
            color = "var(--status-warn)";
            break;
    }

    const percentage = Math.floor(value/total * 100);
    return (
        <div className='pieCellComponent'>
            <div className='game_completion'>
                <div className='game_status_pill' style={{backgroundColor: color}}></div>
                <p>{type}</p>
            </div>
            <div className='count'>
                <p className='stateName'>{value ?? 0}</p>
                <p className='game_date_and_progress'>{percentage ? percentage + "%" : "N/A"}</p>
            </div>
        </div>
    )
}