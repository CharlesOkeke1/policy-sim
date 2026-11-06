import { useEffect, useState, createContext, useContext } from "react";
import type { ApiResponse, AuthContextType, LoginRequest, User } from "../dtos/Models";
import { loginUser, logoutUser } from "./ApiService";
import { isUserAuthenticated } from "./AuthContextManager";
import { useNavigate } from "react-router";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | undefined>(undefined);
    const [loading, setLoading] = useState(true);
    const router = useNavigate();

    useEffect(() => {
        async function authenticate() {
            const authenticatedUser = await isUserAuthenticated();
            setUser(authenticatedUser);
    
            if (authenticatedUser === undefined) router("/login");
            else router("/home");
            setLoading(false);
        }

        authenticate();
    }, []);

    async function login(req: LoginRequest): Promise<string> {
        setLoading(true);
        try {
            const res = await loginUser(req);
            setUser({
                name: res.result.name,
                email: res.result.email,
                id: res.result.id
            });
            return res.message;
        } finally {
            setLoading(false);
        }
    }

    async function logout(): Promise<void> {
        setLoading(true);
        try {
            const res: ApiResponse<string> = await logoutUser();

            if (res.code === 200) {
                router("/login");
                setUser(undefined);
                sessionStorage.clear();
                localStorage.removeItem("refreshToken");
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <AuthContext.Provider value={{
            user,
            loading,
            login,
            logout
        }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth must be used within AuthProvider");
    }

    return context;
}