// src/services/submission.service.ts
import { apiClientUser } from './apiClient';
import { SubmissionPayload, RunPayload } from '../types';

export const submissionService = {
    submitCode: async (data: SubmissionPayload) => {
        // Uses the user-facing submission endpoint
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
    }
};