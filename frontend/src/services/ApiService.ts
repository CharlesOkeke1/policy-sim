/**
 * For the sake of the development stage, all endpoint return values will take type any
 * This holds until I make interfaces for the return values.
 */

import { useQuery } from "@tanstack/react-query";
import type { Difficulty } from "../components/NewGame";
import type { ApiResponse, Stat, StatePreview, Policy } from "../dtos/Models";
import { customFetch } from "../utils/FetchWrapper";


export const getUserData = async (userId: string): Promise<any> => {
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

// export const userStatsKeys = {
//     all: ["user_stats"] as const,
//     detail: (userId: number) => [...userStatsKeys.all, userId] as const,
// }

// export const useUserStats = (userId: number) => {
//     return useQuery<Stat>({
//         queryKey: userStatsKeys.detail(userId),
//         queryFn: () => getUserStats(), 
//         staleTime: Infinity
//     });
// }


export const resumeGameInProgress = async (gameId: number): Promise<any> => {
    const url = `http://localhost:8080/api/games/${gameId}/resume`;
    const res: ApiResponse<any> = await customFetch({method: "GET", url: url});
    return res.result;
}