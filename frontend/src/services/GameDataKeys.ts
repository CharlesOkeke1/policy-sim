// hooks/useGameData.ts

import { useQueries, useQuery, useQueryClient } from "@tanstack/react-query";
import { getDifficulty, getPaginatedGames, getPolicies, getStates, getUserData, getUserStats } from "./ApiService";

export const useGameData = () => {
    return useQueries({
        queries: [
            {
                queryKey: ["states"],
                queryFn: getStates,
                staleTime: Infinity,
                refetchOnMount: false,
                refetchOnWindowFocus: false,
                refetchOnReconnect: false
            },
            {
                queryKey: ["difficulty"],
                queryFn: getDifficulty,
                staleTime: Infinity,
                refetchOnMount: false,
                refetchOnWindowFocus: false,
                refetchOnReconnect: false
            },
            {
                queryKey: ["policy"],
                queryFn: getPolicies,
                staleTime: Infinity,
                refetchOnMount: false,
                refetchOnWindowFocus: false,
                refetchOnReconnect: false
            }, 
            {
                queryKey: ["user_stats"],
                queryFn: getUserStats,
                staleTime: 1000 * 60 * 5,
            },
            {
                queryKey: ["user_game_data"],
                queryFn: getUserData,
                staleTime: 1000 * 60 * 5,
            }       
        ]
    });
};

export const usePaginatedGames = (
    userId: number,
    start: number,
    stop: number
) => {
    const queryClient = useQueryClient();

    const query = useQuery({
        queryKey: ["user", userId, "_games", start, stop],
        queryFn: () => getPaginatedGames(userId, start, stop),
        staleTime: 1000 * 60 * 5,
        enabled: !!userId
    });

    const invalidateGames = () => queryClient.invalidateQueries({
        queryKey: ["user", userId, "_games"]
    });

    return {
        ...query,
        invalidateGames
    };
};