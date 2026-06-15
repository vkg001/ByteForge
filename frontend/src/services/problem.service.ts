import { apiClient } from './apiClient';

export const problemService = {
    // FIXED: Changed from .post() to .get() and passing keyword as a query parameter
    searchProblems: async (keyword: string, page: number = 0) => {
        const response = await apiClient.get(`/problems/core/search-problem/${page}`, {
            params: { keyword } // This safely converts to ?keyword=two in the URL
        });
        return response.data;
    },

    getProblemById: async (problemId: string) => {
        console.log("Searching for id: ", problemId)
        const response = await apiClient.get(`/problems/core/${Number.parseInt(problemId)}/description`);
        return response.data;
    },

    getProblemStats: async (problemId: string) => {
        const response = await apiClient.get(`/problems/stats/${problemId}/stats`);
        return response.data;
    },

    addProblem: async (data: any) => {
        const response = await apiClient.post('/problems/core/add-problem', data);
        return response.data;
    },

    updateBoilerplate: async (data: any) => {
        const response = await apiClient.post('/boilerplate/update', data);
        return response.data;
    }
};