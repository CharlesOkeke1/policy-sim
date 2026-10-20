import './StatusPill.css';

export type PillType = 'SUCCESS' | 'FAILURE' | 'INFORMATION' | 'WARNING' | 'NULL';


interface PillProps {
    content: string;
    type: PillType;

}

export const StatusPill = ({content, type}: PillProps) => {
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

    }
    return (
        <div className='pillComponent' style={{backgroundColor: bgColor, border: border}}>
            <p className='pill_content' style={{color: textColor}}>{content}</p>
        </div>
    )
}
