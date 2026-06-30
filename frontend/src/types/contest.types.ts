export interface Contest {
    id: number;
    title: string;
    description: string;
    startTime: string; // ISO date string
    endTime: string;   // ISO date string
}

export type ContestStatus = 'upcoming' | 'live' | 'ended';