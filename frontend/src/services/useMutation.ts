// hooks/useAppMutation.ts

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { invalidationMap } from "./invalidationMap";

type MutationName = keyof typeof invalidationMap;

export function useAppMutation<TData, TVariables>(
    mutationFn: (variables: TVariables) => Promise<TData>,
    mutationName: MutationName
) {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn,
        onSuccess: async () => {
            const keys = invalidationMap[mutationName];

            await Promise.all(
                keys.map(queryKey =>
                    queryClient.invalidateQueries({ queryKey })
                )
            );
        },
    });
}

