import { useNavigate } from 'react-router';
import './App.css';
import AppRoutes from "./AppRoutes";
import { useEffect, useState } from 'react';

export default function App() {
    const router = useNavigate();
    const [isAuthenticated, setAuth] = useState<boolean>(JSON.parse(localStorage.getItem("authenticated") || "false"));
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        checkAuthentication();
    }, []);
    
    function checkAuthentication() {
        setLoading(true);
        const payload = {
            refreshToken: localStorage.getItem("refreshToken") || ""
        }
        const api = "http://localhost:8080/api/users/me";

        fetch(api, {
            method: "POST", 
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        })
        .then((response) => {
            if (response.status !== 200) router("/login"); // Temporary until I handle all exceptions so that the response returns a code
            return response.json();

        })
        .then((data: any) => {
            if (data.code === 200) {
                sessionStorage.clear();
                sessionStorage.setItem("userId", data.result.id);
                sessionStorage.setItem("sessionExpirytime", data.result.sessionExpirationTime);
                localStorage.setItem("refreshToken", data.result.refreshToken);
                sessionStorage.setItem("authToken", data.result.authToken);
                localStorage.setItem("authenticated", JSON.stringify(true));
                router("/home", { replace: true });
                setAuth(true);
            } else {
                router("/login");
            }
        })
        .catch((error) => {
            console.error("Login error:", error);
        })
        .finally(() => {
            setLoading(false);
        })
    }

    return (
        <AppRoutes/>
    );
}