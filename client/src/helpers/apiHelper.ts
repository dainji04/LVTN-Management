import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig } from 'axios';
import { authHelper } from './authHelper';

interface options {
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
    body?: any;
    headers?: any;
    params?: any;
    query?: any;
    withCredentials?: boolean;
}

// Get API URL from environment variable or use default
let apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8000/api';

// Ensure the URL has a protocol
if (!apiUrl.startsWith('http://') && !apiUrl.startsWith('https://')) {
    apiUrl = `http://${apiUrl}`;
}

// Create axios instance with default configuration
const axiosInstance: AxiosInstance = axios.create({
    baseURL: apiUrl,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
    withCredentials: false,
});

// Add request interceptor to include token in headers
axiosInstance.interceptors.request.use(
    (config) => {
        const token = authHelper.getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor to handle 401 errors (unauthorized)
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Token expired or invalid, clear auth and redirect to login
            authHelper.logout();
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const helperApi = async (collection: string, options: options = {}) => {
    const { method = 'GET', body, headers = {}, params, withCredentials = false } = options;

    const config: AxiosRequestConfig = {
        method: method.toLowerCase() as any,
        url: collection,
        headers: {
            ...headers,
        },
        withCredentials,
    };

    // Add data (body) for POST, PUT, DELETE requests
    if (body && method !== 'GET') {
        config.data = body;
    }

    // Add query parameters
    if (params) {
        config.params = params;
    }

    try {
        const response = await axiosInstance.request(config);
        return response.data;
    } catch (error: any) {
        // Handle axios errors
        if (error.response) {
            // The request was made and the server responded with a status code
            // that falls out of the range of 2xx
            throw {
                status: false,
                message: error.response.data?.message || 'An error occurred',
                data: error.response.data,
                statusCode: error.response.status,
            };
        } else if (error.request) {
            // The request was made but no response was received
            throw {
                status: false,
                message: 'No response from server',
            };
        } else {
            // Something happened in setting up the request that triggered an Error
            throw {
                status: false,
                message: error.message || 'An error occurred',
            };
        }
    }
} 