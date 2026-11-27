import { useEffect, useState, createContext, useContext, useCallback } from "react";
import type { ApiResponse, AuthContextType, LoginRequest, User } from "../dtos/Models";
import { loginUser, logoutUser } from "./ApiService";
import { isUserAuthenticated } from "./AuthContextManager";
import { useNavigate } from "react-router";

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | undefined>(undefined);
    const [loading, setLoading] = useState(true);
    const isAuthenticated = user !== undefined;
    const router = useNavigate();

    useEffect(() => {
        if (!isAuthenticated) authenticate();         
    }, []);

    const authenticate = useCallback(async () => {
        try {
            const authenticatedUser = await isUserAuthenticated();
            setUser(authenticatedUser);
        } catch (error) {
            console.error("Provider error:", error);

            setUser(undefined);
            router("/server-error");
        } finally {
            setLoading(false);
        }
    }, [router]);

    async function login(req: LoginRequest): Promise<ApiResponse<any>> {
        const res = await loginUser(req);
        setUser({
            name: res.result.name,
            email: res.result.email,
            id: res.result.id
        });
        return res;
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
            logout,
            authenticate,
            isAuthenticated
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