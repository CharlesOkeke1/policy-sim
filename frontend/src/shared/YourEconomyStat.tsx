import './YourEconomyStat.css';
import upFillIcon from '../assets/caret-up-sharp-fill.svg';
import downFillIcon from '../assets/caret-down-sharp-fill.svg';

interface EconomyProps {
    name: string;
    value: number;
    previous: number;
    suffix?: string;
}   

export const YourEconomyStat = ({name, value, previous, suffix}: EconomyProps) => {
    const caret = value > previous ? upFillIcon : downFillIcon;
    const color = value > previous ? "var(--status-good)" : 
    value === previous ? "var(--text-muted)" : "var(--status-bad)";
    const difference = value && previous ? Math.abs(value - previous) : 0;
    return (
        <div className='economyStatComponent'>
            <p className='left_side'>{name}</p>
            <div className='right_side'>
                <p>{value ? value.toFixed(2) : ""}{suffix ? suffix : ""}</p>
                <div className='change_row'>
                    <img src={caret}/>
                    <p style={{color: color}}>{difference.toFixed(2)}</p>
                </div>
            </div>
        </div>
    )
}