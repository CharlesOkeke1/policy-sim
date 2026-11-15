import { Navigate, Route, Routes } from "react-router";
import AppRoutes from "../AppRoutes";
import Maintenance from "../pages/Maintenace";

interface MaintenanceGateProps {
    status: boolean;
    upTime: string;
}

export default function MaintenanceGate({ status, upTime }: MaintenanceGateProps) {
    if (!status) {
        return(
            <Routes>
                <Route
                    path="*"
                    element={
                        <>
                            <Navigate to="/maintenance" replace />
                            <Maintenance expectedUpTime={upTime} />
                        </>
                    }
                />
            </Routes>
        );
    }

    return <AppRoutes />;
}