import React from 'react'
import './RecentActivity.css';

interface RecentActivityProps {
    type: "Completed" | "In progress" | "Abandoned";
    main: string;
    supporting: string;
    time: string;
}

export const RecentActivity = ({type, main, supporting, time}: RecentActivityProps) => {
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
    };

    return (
        <div className='recentActivityComponent'>
            <div className='recentActivityMain'>
                <div className='recent_activity_pill' style={{backgroundColor: color}}></div>
                <div>
                    <p className='main'>{main}</p>
                    <p className='supporting'>{supporting}</p>
                </div>
            </div>
            <p className='recent_activity_time'>{time}</p>
        </div>
    )
}
