import '../styles/NotFoundPage.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import noInternetIcon from '../assets/no-internet.svg';
import { useEffect, useState } from 'react';
import { FAILED_AUTH_RETRY } from '../utils/FetchWrapper';
import { StatusPill } from '../shared/StatusPill';
import { useAuth } from '../services/AuthProvider';

const ServerError = () => {
    const [countDown, setCountDown] = useState(FAILED_AUTH_RETRY);
    const {authenticate} = useAuth();
    const [autoRetry, setAutoRetry] = useState(0);
    const [mult, setMult] = useState(0);

    useEffect(() => {
        if (autoRetry < 3) {
            const interval = setInterval(() => {
                setCountDown(currentCount => {
                    if (currentCount > 0) return currentCount - 1;

                    authenticate();
                    setAutoRetry(curr => { return curr + 1; });
                    return FAILED_AUTH_RETRY + mult;
                });
            }, 1000);
            setMult(curr => { return curr + 10; });
            return () => clearInterval(interval);
        } else {
            setCountDown(0);
        }
    }, [authenticate]);

    function retry() {
        authenticate();
        setCountDown(FAILED_AUTH_RETRY);
    }

    return (
        <div className='not_found_page'>
            <div className='not_found_container'>
                <div className='not_found_logo'>
                    <img src={lightLogo} className='logoImg not_found_logo_icon'/>
                    <p>POLICYSIM</p>
                </div>
                <StatusPill icon={noInternetIcon} type='NEUTRAL' content='Connection problem' width={165}/>
                <p className='not_found_header'>We can’t reach PolicySim right now</p>
                <p className='not_found_subheader'>This is usually brief. Your games are saved up to the last minute of play.</p>

                <button className='PauseBtnContainer' style={{width: "30%"}} onClick={() => retry()}>
                    <p>Try again</p>
                </button>

                <p className='contact_support_404'>Trying automatically in {countDown}s</p>
            </div>
        </div>
    )
}

export default ServerError;