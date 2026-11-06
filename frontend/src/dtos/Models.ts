import Login from '../pages/Login';
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
    luck: string;
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
    login: (req: LoginRequest) => Promise<string>;
    logout: () => void;
}

export interface LoginRequest {
    email: string; // Email by payload name but can also be username
    password: string;
    ipAddress: string;
    deviceInfo: string;
}

// export interface LoginFn {
//     req: LoginRequest;
//     setError: (err: string) => void;
//     setSubmitted: (sub: boolean) => void;
// }