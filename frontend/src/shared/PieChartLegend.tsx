import React from 'react';
import './PieChartLegend.css';

interface RecentGameProps {
    type: "Completed" | "In progress" | "Abandoned";
    value: number;
    total: number;
}

export const PieChartLegend = ({type, value, total}: RecentGameProps) => {
    // TODO: Finish up on the design
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
    /**
     * UNDER CONSTRUCTION
     */
    return (
        <div className='pieCellComponent'>
            <div className='game_completion'>
                <div className='game_status_pill' style={{backgroundColor: color}}></div>
                <p>{type}</p>
            </div>
            <div className='count'>
                <p className='stateName'>{value}</p>
                <p className='game_date_and_progress'>{Math.floor(value/total * 100)}%</p>
            </div>
        </div>
    )
}