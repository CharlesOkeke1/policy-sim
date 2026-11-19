// queries/invalidationMap.ts

import { queryKeys } from "./queryKeys";

export const invalidationMap = {
    finishGame: [ queryKeys.games, queryKeys.userData, queryKeys.userStats ],
    createGame: [ queryKeys.games ],
    getGamesPaginated: [ queryKeys.games ]
};