// src/services/apiClient.ts
import axios from 'axios';
import { BASE_URL, BASE_URL_USER, BASE_URL_ADMIN } from '../utils';

// Flag to prevent multiple simultaneous redirects on Promise.all
let isRedirecting = false;

// 1. Create a "blueprint" function to generate instances
const createAxiosClient = (baseURL: string) => {
    console.log("Request sent for ", baseURL);
    
    const instance = axios.create({
        baseURL: baseURL,
        headers: {
            'Content-Type': 'application/json',
        },
    });

    // Automatically attach Bearer token to EVERY request
    instance.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem('jwt_token');

            // If token exists, inject it into the Authorization header
            if (token && config.headers) {
                config.headers.Authorization = `Bearer ${token}`;
            }

            return config;
        },
        (error) => {
            return Promise.reject(error);
        }
    );

    // Automatically handle token expiration
    instance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response && error.response.status === 401) {
                // Only redirect if we aren't already doing it
                if (!isRedirecting) {
                    isRedirecting = true;
                    localStorage.removeItem('jwt_token');
                    window.location.href = '/login';
                }
            }
            return Promise.reject(error);
        }
    );

    return instance;
};

// 2. Use the blueprint to easily create and export your clients!
export const apiClient = createAxiosClient(BASE_URL);
export const apiClientUser = createAxiosClient(BASE_URL_USER);
export const apiClientAdmin = createAxiosClient(BASE_URL_ADMIN);