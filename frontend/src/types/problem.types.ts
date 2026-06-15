// src/types/problem.types.ts
export interface Problem {
    id: string;
    problemTitle: string;
    problemStatement: string;
    problemDifficulty: 'EASY' | 'MEDIUM' | 'HARD';
    topics: string[];
    companyTags: string[];
    acceptanceRate?: number; // Fetched from stats
}

export interface ProblemDetail extends Problem {
    constraints: string[];
    hints: string[];
    examples: Array<{
        input: string;
        output: string;
        explanation: string;
    }>;
    memoryLimitInMB: number;
    timeLimitInMS: number;
}

export interface SubmissionPayload {
    languageId: number;
    problemId: number;
    sourceCode: string;
}

export interface RunPayload extends SubmissionPayload {
    customTestCases: string[];
}

export interface RecentSubmission {
    id: number;          
    problemId: number;   
    problemTitle: string;
    status: string; 
    languageId: number;
    submittedAt: string;
}