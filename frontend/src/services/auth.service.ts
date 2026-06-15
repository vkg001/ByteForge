// src/services/auth.service.ts
import { apiClient } from './apiClient';

export const authService = {
    signupInit: async (data: any) => {
        const response = await apiClient.post('/auth/signup-init', data);
        return response.data;
    },
    signupComplete: async (data: any) => {
        const response = await apiClient.post('/auth/signup-complete', data);
        return response.data;
    },
    login: async (data: any) => {
        const response = await apiClient.post('/auth/login', data);
        return response.data;
    }
};