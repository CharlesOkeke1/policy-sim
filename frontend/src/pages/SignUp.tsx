import React, { useEffect, useState } from 'react'
import '../styles/Login.css';
import lightLogo from '../assets/Policy_Sim_Logo_Light_PNG.png';
import { useNavigate } from 'react-router';

interface SignUpRequest {
    username: string;
    email: string;
    password: string;
}

const SignUp = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const router = useNavigate();
    const [strength, setStrength] = useState(0);
    const [takenUserNames, setTakenUsernames] = useState<String[]>([]);
    const passwordsMismatch = confirmPassword !== password;
    const readyToPost = !passwordsMismatch && email.length > 0 && username.length > 0 && !isUsernameTaken();

    useEffect(() => {
        getUsernames();
    }, []);

    useEffect(() => {
        isUsernameTaken();
    }, [username]);

    useEffect(() => {
        getStrength(confirmPassword);
    }, [confirmPassword]);

    function signUp() {
        if (passwordsMismatch) {
            return;
        }
        const payload: SignUpRequest = {
            username: username,
            email: email,
            password: password
        };

        const api = "http://localhost:8080/api/users";

        fetch(api, {
            method: "POST", 
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
        .then((response) => {
            console.log(response.json())
        })
        .catch((e) => {
            console.log(e)
        });

        router("/login");
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

        console.log(`${confirmPassword}'s strength is ${st}`);
        setStrength(st);
    }

    function isUsernameTaken() {
        return takenUserNames.includes(username);
    }

    function getUsernames() {
        const api = "http://localhost:8080/api/usernames";

        fetch(api, {
            method: "GET", 
            headers: {
                "Content-Type": "application/json"
            },
        })
        .then((response) => {
            return response.json();
        })
        .then((data) => {
            setTakenUsernames(data.result);
        })
        .catch((e) => {
            console.log(e)
        });
    }

    return (
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
                        {
                            isUsernameTaken() ? <p className='error'>Username is already taken</p> :
                            <p className='supporting_text'>Username displayed to the public</p>
                        }
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
                        <p className='supporting_text'>Use 8+ characters with a mix of letters and numbers.</p>
                    </div>
                    <div className='input_block'>
                        <p className='input_label'>Confirm Password</p>
                        <input 
                        value={confirmPassword} 
                        type='password' 
                        onChange={(e) => {setConfirmPassword(e.target.value);}}/>
                        {
                            passwordsMismatch ?
                            <p className='error'>Passwords do not match</p> : !readyToPost ? 
                            <p className='error'>All fields are required</p> : ""
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
                                            ? "white" //var(--auth-link)
                                            : "#2E2E2E" //var(--auth-input-border)
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
                        <p>Create an account</p>
                    </button>
                    
                </div> 
            </div>
        </div>
    )
}

export default SignUp;