import './StatusPill.css';

export type PillType = 'SUCCESS' | 'FAILURE' | 'INFORMATION' | 'WARNING' | 'NEUTRAL' | 'NULL';


interface PillProps {
    content: string;
    type: PillType;
    icon?: string;
    width?: number;
}

export const StatusPill = ({content, type, icon, width}: PillProps) => {
    let border = "1px solid "
    let bgColor = "";
    let textColor = "";
    switch (type) {
        case "SUCCESS": 
            textColor = "var(--status-good)";
            border += "var(--status-good)";
            bgColor = "var(--status-good-bg)";
            break;

        case "FAILURE": 
            textColor = "var(--status-bad)";
            border += "var(--status-bad)";
            bgColor = "var(--status-bad-bg)";
            break;
        
        case "WARNING": 
            textColor = "var(--status-warn)";
            border += "var(--status-warn)";
            bgColor = "var(--status-warn-bg)";
            break;

        case "INFORMATION": 
            textColor = "var(--status-info)";
            border += "var(--status-info)";
            bgColor = "var(--status-info-bg)";
            break;

        case "NEUTRAL": 
            textColor = "var(--status-neutral)";
            border += "var(--status-neutral)";
            bgColor = "var(--status-neutral-bg)";
            break;

    }
    return (
        <div className='pillComponent' style={{backgroundColor: bgColor, border: border,
            width: width ? width + "px" : ""}}>
            {
                icon ?
                <div className='pillComponent' style={{justifyContent: icon ? "space-between" : "center", width: width ? width + "px" : ""}}>
                    <img className='status_pill_icon' src={icon}/>
                    <p className='pill_content' style={{color: textColor}}>{content}</p>
                </div>
                
                : 
                <p className='pill_content' style={{color: textColor}}>{content}</p>
            }
        </div>
    )
}
