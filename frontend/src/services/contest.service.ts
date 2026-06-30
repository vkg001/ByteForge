import { apiClient } from './apiClient';

export const contestService = {
    getContests: async (pageNumber: number = 0) => {
        const response = await apiClient.get(`/contest/${pageNumber}`);
        return response.data;
    },
};