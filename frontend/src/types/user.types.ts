// src/types/user.types.ts

export interface User {
    id: number | string; // Adjusted to handle numeric IDs from your new JSON
    name: string;
    email?: string;
    avatar?: string;
    location?: string; // Placeholder field
    company?: string;  // Placeholder field
}

export interface ProblemStats {
    id: number;
    userStatsId: number;
    school: number;
    easy: number;
    medium: number;
    hard: number;
    extreme: number;
}

export interface UserStats {
    id: number;
    userId: number;
    totalSubmissions: number;
    problemStats: ProblemStats;
    lastSubmissionDate: string;
    lastActivityDate: string;
    reputation: number;
    totalComments: number;
    totalSolutionsAdded: number;
    maxStreak: number;
    currentStreak: number;
}

export interface CalendarEntry {
    date: string;
    count: number;
}