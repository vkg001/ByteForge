// src/services/submission.service.ts
import { apiClientUser } from './apiClient';
import { SubmissionPayload, RunPayload, RecentSubmission } from '../types';


export const submissionService = {
    submitCode: async (data: SubmissionPayload) => {
        const response = await apiClientUser.post('/submissions/submit', data);
        return response.data;
    },

    runCode: async (data: RunPayload) => {
        const response = await apiClientUser.post('/submissions/run', data);
        return response.data;
    },

    getSubmissions: async (problemId: string, page: number = 0) => {
        const response = await apiClientUser.get(`/submissions/${problemId}/submissions/${page}`);
        return response.data;
    },

    // New endpoint implementation
    getRecentSubmissions: async (limit: number = 10): Promise<RecentSubmission[]> => {
        // Passing limit as a query parameter matches the @RequestParam in the Spring controller
        const response = await apiClientUser.get<RecentSubmission[]>('/submissions/recent', {
            params: { limit } 
        });
        return response.data;
    }
};