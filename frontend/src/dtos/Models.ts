import Login from '../pages/Login';
import Maintenance from '../pages/Maintenace';
export interface ApiResponse<T> {
    code: number;
    result: T;
    message: string;
    error: number;
}

export interface Stat {
    userId: number;
    avgScore: number;
    finishedFirst: number;
    monthsGoverned: number;
    positions: Map<number, number>;
    completed: number;
    abandoned: number;
    inProgress: number;
}

export interface StatePreview {
    id: string;
    name: string;
    population: number;
    stability: number;
    productivity: number;
    luck: "Even start" | "Tough start" | "Strong start";
    initials: string;
}

export interface Policy {
    name: string;
    policyId: string;
    effectOnStability: number;
    policySpecificEffect: number;
    affectedLever: string;
}

export interface User {
    id: number;
    email: string;
    name: string;
    authToken?: string;
}

export interface AuthContextType {
    user: User | undefined;
    loading: boolean;
    login: (req: LoginRequest) => Promise<ApiResponse<any>>;
    logout: () => void;
    authenticate: () => void;
    isAuthenticated: boolean;
}

export interface LoginRequest {
    email: string; // Email by payload name but can also be username
    password: string;
    ipAddress: string;
    deviceInfo: string;
}

export interface ServerStatus {
    serverStatus: boolean;
    expectedUpTime: string; 
}

export interface MaintenanceProps {
    expectedUpTime: string; 
}

export interface ThemeResponse {
    theme: string;
    lastChanged: string;
}