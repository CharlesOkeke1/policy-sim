import '../styles/NotFoundPage.css';
import '../styles/Maintenance.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import { useEffect, useState } from 'react';
import { StatusPill } from '../shared/StatusPill';
import { formatDate, formatDateFull, formatSecondsBetweenDates, getSecondsBetweenDates } from '../utils/Formatting';
import type { MaintenanceProps } from '../dtos/Models';

const Maintenance = ({expectedUpTime}: MaintenanceProps) => {
    const timeBack = new Date(expectedUpTime);
    const [time, setTime] = useState(getSecondsBetweenDates(new Date(Date.now()).toString(), timeBack.toString()));
    const [timeDisplay, setTimeDisplay] = useState(formatSecondsBetweenDates(time));

    useEffect(() => {
        const interval = setInterval(() => {
            const intTime = time - 1;

            setTime(intTime);
            setTimeDisplay(formatSecondsBetweenDates(intTime));

            if (intTime === 0) clearInterval(interval);
        }, 1000);
        
        return () => clearInterval(interval); 
    }, [time]);


    return (
        <div className='not_found_page'>
            <div className='not_found_container'>
                <div className='not_found_logo'>
                    <img src={lightLogo} className='logoImg not_found_logo_icon'/>
                    <p>POLICYSIM</p>
                </div>
                <StatusPill type='WARNING' content='Scheduled maintenace'/>
                <p className='not_found_header'>We are making changes</p>
                <p className='not_found_subheader'>PolicySim is down while we work on it. Every game was saved before we stopped, and you will pick up exactly where you left off.</p>

                <div className='info_Row'>
                    <p>Expected back in</p>
                    <p style={{fontWeight: "var(--weight-semibold)"}}>{timeDisplay}</p>
                </div>

                <p className='contact_support_404' style={{textDecoration: "none", cursor: "default"}}>Please come back later</p>
            </div>
        </div>
    )
}

export default Maintenance;