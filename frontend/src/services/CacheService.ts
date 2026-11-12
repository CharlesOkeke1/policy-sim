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