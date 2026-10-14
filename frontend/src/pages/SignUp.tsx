import React, { useState } from 'react'
import '../styles/Login.css';
import { useNavigate } from 'react-router';

interface SignUpRequest {
    email: string;
    password: string;
    name: string;
}

const SignUp = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [name, setName] = useState("");
    const router = useNavigate();

    function signUp() {
        console.log(`Email: ${username} and Password: ${password} and Name: ${name}`);
        const payload: SignUpRequest = {
            email: username,
            password: password,
            name: name
        };

        const api = "http://localhost:8080/api/users/new";

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

    return (
        <div className='login_container'>
            <div className='login_block'>
                <p className='header'>Sign Up to PolicySim</p>

                <div className='auth_input_block'>
                    <p>Name:</p>
                    <input 
                    value={name} 
                    type='text' 
                    placeholder='John Doe'
                    onChange={(e) => setName(e.target.value)}/>
                </div>

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

                <button className='submitBtn' onClick={() => signUp()} name='loginBtn'>
                    <p className='loginText'>Sign Up</p>
                </button>
            </div>
        </div>
    )
}

export default SignUp;