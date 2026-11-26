    import './App.css';
import { useEffect, useState } from 'react';
import { produrl } from './utils/FetchWrapper';
import type { ApiResponse, ServerStatus, ThemeResponse } from './dtos/Models';
import MaintenanceGate from './services/MaintenanceGate';
import { useNavigate } from 'react-router';
import { useAuth } from './services/AuthProvider';
import { getUserTheme } from './services/ApiService';
import PageLoader from './shared/PageLoader';

export default function App() {
    const [status, setStatus] = useState<boolean>(true);
    const [upTime, setUpTime] = useState<string>("");
    const [loading, setLoading] = useState(false);
    const router = useNavigate();
    const {user} = useAuth();

    useEffect(() => {
        getTheme();
    }, []);

    async function getServerStatus() {
        try {
            const url = `${produrl}/api/server/status`;
            const response = await fetch(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            const data = await response.json() as  ApiResponse<ServerStatus>;
            setStatus(data.result.serverStatus);
            setUpTime(data.result.expectedUpTime);
        } catch(error) {
            if (error instanceof TypeError) {
                router("/server-error");
            }
        }
    }

    async function getTheme() {
        setLoading(true);
        
        try {
            //if (!user) router("/login");
            if (user) {
                const themeObj: ThemeResponse = await getUserTheme(user?.id);
                document.documentElement.setAttribute("data-theme", themeObj.theme);
            }            
        } catch (error) {
            console.log(error);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        if (!status) {
            router("/maintenance");
        }
        getServerStatus();
    }, []);


    return (
        loading ?
        <PageLoader/> 
            :
        <MaintenanceGate status={status} upTime={upTime} />
    );
}