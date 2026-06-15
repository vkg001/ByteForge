// src/services/user.service.ts
import { apiClient, apiClientUser } from './apiClient';
import { User, UserStats, CalendarEntry } from '../types';

export const userService = {
    // 1. Current user details
    getCurrentUser: async (): Promise<User> => {
        const response = await apiClient.get('/user/me');
        return response.data;
    },

    // 2. User Details By Id
    getUserById: async (userId: string): Promise<User> => {
        const response = await apiClient.get(`/user/${userId}`);
        return response.data;
    },

    // 3. User Stats
    getUserStats: async (userId?: string): Promise<UserStats> => {
        const endpoint = userId ? `/user/stats/${userId}` : '/user/stats/me';
        console.log("Stats endpoint: ", endpoint);
        const response = await apiClient.get(endpoint);
        return response.data;
    },

    // 4. User Calendar
    getUserCalendar: async (userId?: string): Promise<CalendarEntry[]> => {
        const endpoint = userId ? `/user/stats/${userId}/calendar` : '/user/stats/me/calendar';
        const response = await apiClient.get(endpoint);
        return response.data;
    }
};