/**
 * For the sake of the development stage, all endpoint return values will take type any
 * This holds until I make interfaces for the return values.
 */

import { useQuery } from "@tanstack/react-query";
import type { Difficulty } from "../components/NewGame";
import type { ApiResponse, Stat, StatePreview, Policy, LoginRequest, User } from "../dtos/Models";
import { customFetch } from "../utils/FetchWrapper";

export const API_DELAY = 350; // 0.35seconds

export const getUserData = async (): Promise<any> => {
    const api = `http://localhost:8080/api/users/${sessionStorage.getItem("userId")}/games`;
    const res: ApiResponse<any> = await customFetch({method: "GET", url: api});
    return res.result;
}

export const getUserStats = async (): Promise<Stat> => {
    const api = `http://localhost:8080/api/games/${sessionStorage.getItem("userId")}/stats`;
    const res: ApiResponse<Stat> = await customFetch({method: "GET", url: api});
    return res.result;
}

export const getStates = async (): Promise<StatePreview[]> => {
    const api = `http://localhost:8080/api/games/states`;
    const res: ApiResponse<StatePreview[]> = await customFetch({method: "GET", url: api});
    return res.result;
}

export const getDifficulty = async (): Promise<Difficulty[]> => {
    const api = `http://localhost:8080/api/games/difficulty`;
    const res: ApiResponse<Difficulty[]> = await customFetch({method: "GET", url: api});
    return res.result;
}

export const getPolicies = async (): Promise<Policy[]> => {
    const api = `http://localhost:8080/api/games/${sessionStorage.getItem("userId")}/policies`;
    const res: ApiResponse<Policy[]> = await customFetch({method: "GET", url: api});
    return res.result;
}


export const resumeGameInProgress = async (gameId: number): Promise<ApiResponse<any>> => {
    const url = `http://localhost:8080/api/games/${gameId}/resume`;
    const res: ApiResponse<any> = await customFetch({method: "POST", url: url});
    return res;
}

export async function logoutUser(): Promise<ApiResponse<string>> {
    const payload = {
        refreshToken: localStorage.getItem("refreshToken")
    }

    const url = `http://localhost:8080/api/users/logout`;

    const res = await fetch(url, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload)
    })

    const data: ApiResponse<string> = await res.json();

    if (!res.ok || data.code !== 200) {
        throw new Error(data.message || "Login failed");
    }
    return data;
}

export async function loginUser(req: LoginRequest): Promise<ApiResponse<any>> {
    const api = "http://localhost:8080/api/users/login";

    const response = await fetch(api, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(req)
    });

    const data: ApiResponse<any> = await response.json();

    if (!response.ok || data.code !== 200) {
        throw new Error(data.message || "Login failed");
    }

    sessionStorage.clear();
    sessionStorage.setItem("authToken", data.result.authToken);
    sessionStorage.setItem("userId", data.result.id);
    sessionStorage.setItem("expirytime", data.result.tokenExpirationTime);

    localStorage.setItem("refreshToken", data.result.refreshToken);

    return data;
}