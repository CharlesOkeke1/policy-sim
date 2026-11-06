import React, { useEffect, useState } from 'react'
import '../styles/Login.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';

import { useNavigate } from 'react-router';
import { ButtonLoader } from '../shared/ButtonLoader';
import getDeviceInfo from '../utils/GetDeviceInfo';
import { useAuth } from '../services/AuthProvider';


const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [submitted, setSubmitted] = useState(false);
    const [ip, setIp] = useState("");
    const [error, setError] = useState("");

    const readyToPost = email.length > 0 && password.length > 0 && ip != "";
    const router = useNavigate();

    const {login} = useAuth();

    useEffect(() => {
        fetchUserIp().then((ip) => {
            setIp(ip);
        });
    }, []);

    async function fetchUserIp(): Promise<string> {
        const response = await fetch('https://api.ipify.org?format=json');
        const data: { ip: string } = await response.json();
        return data.ip;
    }

    async function handleLogin() {
        setSubmitted(true);
        setError("");

        try {
            const message = await login({
                email,
                password,
                ipAddress: ip,
                deviceInfo: getDeviceInfo()
            });

            router("/home", { replace: true });
            setError(message);

        } catch (error) {
            setError(
                error instanceof Error ? error.message : "Login failed"
            );
        } finally {
            setSubmitted(false);
        }
    }

    return (
        <div className='auth'>
            <div className='login_container'>
                <div className='container_left'>
                    <div className='logo_layer'>
                        <img src={lightLogo} alt='PolicySim Logo' className='logoImg'/>
                        <p className='logo_text'>POLICYSIM</p>
                    </div>
                    <div className='left_text_area'>
                        <p className='text_header'>HOW A GAME WORKS</p>
                        <div className='text_item'>
                            <p className='text_item_number'>01</p>
                            <p className='rest_of_text'>Pick one of eight UK cities. The other seven are run by the computer.</p>
                        </div>
                        <div className='text_item'>
                            <p className='text_item_number'>02</p>
                            <p className='rest_of_text'>Each month, choose a policy: raise or cut taxes, expand spending, invest in infrastructure, or restructure debt.</p>
                        </div>
                        <div className='text_item'>
                            <p className='text_item_number'>03</p>
                            <p className='rest_of_text'>Inflation, unemployment, debt and stability respond. At the end, you are ranked against the other seven.</p>
                        </div>
                    </div>


                    <div className='left_main_text_area'>
                        <p className='main_text'>Build.</p>
                        <p className='main_text'>Govern.</p>
                        <p className='main_text'>Prosper.</p>
                        <p>© 2026 PolicySim</p>
                    </div>
                </div>
                <div className='container_right'>
                    <div className='login_block'>
                        <div>
                            <p className='right_header'>Login to your account</p>
                        </div>
                        <div className='input_block'>
                            <p className='input_label'>Email</p>
                            <input 
                            value={email} 
                            type='text' 
                            placeholder='JohnDoe@gmail.com'
                            onChange={(e) => setEmail(e.target.value)}/>
                        </div>
                        <div className='input_block'>
                            <p className='input_label'>Password</p>
                            <input 
                            value={password} 
                            type='password' 
                            onChange={(e) => setPassword(e.target.value)}/>
                        </div>
                        <div className='basicRow'>
                            <p className='right_sub_header'>Don't have an account? <a className='text_link' onClick={() => router("/sign-up")}>Sign Up</a></p>
                        </div>
                        <button className='submitBtn' onClick={() => handleLogin()} disabled={!readyToPost}>
                            { submitted ?
                                <ButtonLoader/> : <p>Login to your account</p>
                            } 
                        </button>  
                        {
                            error ?
                            <div className='error_div'><p>{error}</p></div>  : ""
                        }
                                         
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Login;