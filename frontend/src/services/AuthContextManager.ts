import { useNavigate } from "react-router";
import type { User } from "../dtos/Models";

export async function isUserAuthenticated(): Promise<User | undefined> {
    const payload = {
        refreshToken: localStorage.getItem("refreshToken") || ""
    };

    try {
        const response = await fetch("http://localhost:8080/api/users/me", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) return undefined;

        const data = await response.json();

        if (data.code !== 200) return undefined;

        sessionStorage.setItem("userId", data.result.id);
        sessionStorage.setItem("sessionExpirytime", data.result.sessionExpirationTime);
        localStorage.setItem("refreshToken", data.result.refreshToken);

        return {
            id: data.result.id,
            name: data.result.name,
            email: data.result.email
        };

    } catch (error) {
        console.error("Authentication error:", error);
        return undefined;
    }
}