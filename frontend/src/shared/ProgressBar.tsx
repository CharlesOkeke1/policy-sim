import './ProgressBar.css';

interface ProgressProps {
    name: string;
    minimum: number
    current: number;
    target: number;
}

export const ProgressBar = ({name, minimum, current, target}: ProgressProps) => {
    const completion = ((current - minimum) / (target - minimum) * 100) + "%";

    return (
        <div className='progress_month'>
            <div className='progress_row'>
                <p>{name}</p>
                <p>{current.toFixed(2)}%</p>
            </div>
            <div className='progress_bar_container'>
                <div className='progress_bar_el' style={{width: completion}}></div>
            </div>
            <div className='footer_row'>
                <p>{minimum}%</p>
                <p>{target}%</p>
            </div>
        </div>
    )
}
