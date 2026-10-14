import React, { useState } from 'react'
import '../styles/Login.css';
import { useNavigate } from 'react-router';

interface LoginRequest {
    email: string;
    password: string;
}

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const router = useNavigate();

    function login() {
        console.log(`Email: ${username} and Password: ${password}`);
        const payload: LoginRequest = {
            email: username,
            password: password
        };

        const api = "http://localhost:8080/api/users/login";

        fetch(api, {
            method: "POST", 
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`Login failed: ${response.status}`);
            }

            return response.json();
        })
        .then((data: any) => {
            sessionStorage.setItem("authToken", data.result.authToken);
            router("/home");
        })
        .catch((error) => {
            console.error("Login error:", error);
        });

    }

    return (
        <div className='login_container'>
            <div className='login_block'>
                <p className='header'>Login to PolicySim</p>
                <p className='sub_header'>Login with your credentials</p>

                <div className='auth_input_block'>
                    <p>Username:</p>
                    <input 
                    value={username} 
                    type='text' 
                    placeholder='JohnDoe@gmail.com'
                    onChange={(e) => setUsername(e.target.value)}/>
                </div>
                
                <div className='auth_input_block'>
                    <p>Password:</p>
                    <input 
                    value={password} 
                    type='password' 
                    placeholder='Pass123'
                    onChange={(e) => setPassword(e.target.value)}/>
                </div>

                <button className='submitBtn' onClick={() => login()} name='loginBtn'>
                    <p className='loginText'>Login</p>
                </button>
            </div>
        </div>
    )
}

export default Login;