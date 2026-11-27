import React, { useEffect, useState } from 'react'
import '../styles/Login.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import { useNavigate } from 'react-router';
import { ButtonLoader } from '../shared/ButtonLoader';
import { customFetch, produrl } from '../utils/FetchWrapper';
import { flushInput, validateEmail } from '../utils/Formatting';
import type { ApiResponse } from '../dtos/Models';

interface SignUpRequest {
    username: string;
    email: string;
    password: string;
}

const SignUp = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [submitted, setSubmitted] = useState(false);
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const router = useNavigate();
    const [strength, setStrength] = useState(0);
    const passwordsMismatch = confirmPassword !== password;
    const readyToPost = !passwordsMismatch && email.length > 0 && 
    username.length > 0 && 
    !flushInput(username) && validateEmail(email) && strength >= 4;
    const [signupfailed, setFailed] = useState(false);
    const hasError = signupfailed || !readyToPost;
    
    useEffect(() => {
        if (flushInput(username)) {
            setError("Username contains an invalid character");
        } else if (email.length <= 0 || username.length <= 0 || 
            password.length <= 0 || confirmPassword.length <= 0) {
            setError("All fields are required");
        } else if (!validateEmail(email)) {
            setError("Email is invalid");
        } else if (passwordsMismatch) {
            setError("Passwords do not match");
        }
    }, [email, username, password, confirmPassword, strength]);

    useEffect(() => {
        if (strength < 4) {
            setError("Password is not strong enough");
        }
    }, [strength]);


    useEffect(() => {
        getStrength(password);
    }, [password]);

    async function signUp() {
        if (passwordsMismatch) {
            return;
        }
        setSubmitted(true);
        setFailed(false);
        const payload: SignUpRequest = {
            username: username,
            email: email,
            password: password
        };

        const api = `${produrl}/api/users`;
        
        try {
            const res: ApiResponse<any> = await fetch(api, {
                method: "POST",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(payload)
            })
            .then((res) => {return res.json(); });

            if (res.code === 200) {
                router("/login", {replace: true});
            } else {
                setFailed(true);
                setError(res.message);
            }
        } catch (error) {
            console.log(error);
        } finally {
            setSubmitted(false);
        }
    }

    const getStrength = (password: string) => {
        let st = 0;
        
        if (password.length >= 7) {
            st++;
        } 
        if (/\d/.test(password)) {
            st++;
        } 
        if (/[^a-zA-Z]/.test(password)) {
            st++;
        } 
        if (/[A-Z]/.test(password)) {
            st++;
        }

        setStrength(st);
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
                            <p className='right_header'>Create your account</p>
                        </div>
                        <div className='input_block'>
                            <p className='input_label'>Username</p>
                            <input 
                            value={username} 
                            type='text' 
                            placeholder='John_Doe'
                            onChange={(e) => setUsername(e.target.value)}/>
                        </div>
                        <p className='supporting_text'>Username displayed to the public</p>
                        <div style={{width: "100%", flexDirection: "column"}}>
                            <div className='input_block'>
                                <p className='input_label'>Email</p>
                                <input 
                                value={email} 
                                type='text' 
                                placeholder='JohnDoe@gmail.com'
                                onChange={(e) => setEmail(e.target.value)}/>
                            </div>
                        </div>
                        
                        <div className='input_block'>
                            <p className='input_label'>Password</p>
                            <input 
                            value={password} 
                            type='password' 
                            onChange={(e) => setPassword(e.target.value)}/>
                            <p className='supporting_text'>Use 8+ characters with a mix of letters and numbers.</p>
                        </div>
                        <div className='input_block'>
                            <p className='input_label'>Confirm Password</p>
                            <input 
                            value={confirmPassword} 
                            type='password' 
                            onChange={(e) => {setConfirmPassword(e.target.value);}}/>
                            {
                                hasError ? 
                                <p className='error'>{error}</p> : ""
                            }
                            <div className='basicRow pills'>
                                {[0, 1, 2, 3].map((pill) => (
                                <div
                                    key={pill}
                                    id={`st${pill}`}
                                    className='pill'
                                    style={{
                                        backgroundColor:
                                            strength >= pill
                                                ? "var(--auth-link)"
                                                : "var(--auth-input-border)"
                                    }}
                                />
                            ))}
                            </div>
                        </div>
                        <div className='basicRow' style={{gap: '10px'}}>
                            <input type='checkbox' className='checkbox'/>
                            <p>I agree to the <a className='text_link'>Terms</a> and <a className='text_link'>Privacy policy.</a></p>
                        </div>
                        <div className='basicRow'>
                            <p className='right_sub_header'>Already have one? <a className='text_link' onClick={() => router("/login")}>Sign In</a></p>
                        </div>
                        <button className='submitBtn' onClick={() => signUp()} disabled={!readyToPost}>
                            { submitted ?
                                <ButtonLoader/> : <p>Create an account</p>
                            }
                        </button>
                        
                    </div> 
                </div>
            </div>
        </div>
    )
}

export default SignUp;