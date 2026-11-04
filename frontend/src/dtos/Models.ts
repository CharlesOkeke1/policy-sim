export interface ApiResponse<T> {
    code: number;
    result: T;
    message: string;
    status: number;
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