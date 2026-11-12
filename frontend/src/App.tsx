import { useNavigate } from 'react-router';
import './App.css';
import AppRoutes from './AppRoutes';
import { useEffect, useState } from 'react';
import { devurl, produrl } from './utils/FetchWrapper';
import Maintenance from './pages/Maintenace';
import type { ApiResponse, ServerStatus } from './dtos/Models';
import MaintenanceGate from './services/MaintenanceGate';

export default function App() {
    const [status, setStatus] = useState<boolean>(true);
    const [upTime, setUpTime] = useState<string>("");
    const router = useNavigate();

    async function getServerStatus() {
        try {
            const url = `${devurl}/api/server/status`;
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

    useEffect(() => {
        if (!status) {
            router("/maintenance");
        }
        getServerStatus();
    }, []);

    return (
        <MaintenanceGate status={status} upTime={upTime} />
    );
}