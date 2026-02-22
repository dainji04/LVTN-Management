import { ref } from 'vue';

// Authentication helper functions

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'user';

// Reactive authentication state
const isAuthenticatedRef = ref<boolean>(!!localStorage.getItem(TOKEN_KEY));

export const authHelper = {
    // Get reactive authentication state
    isAuthenticatedRef,

    // Get token from localStorage
    getToken(): string | null {
        return localStorage.getItem(TOKEN_KEY);
    },

    // Set token in localStorage
    setToken(token: string): void {
        localStorage.setItem(TOKEN_KEY, token);
        isAuthenticatedRef.value = true;
    },

    // Get user from localStorage
    getUser(): any | null {
        const userStr = localStorage.getItem(USER_KEY);
        return userStr ? JSON.parse(userStr) : null;
    },

    // Set user in localStorage
    setUser(user: any): void {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    },

    // Check if user is authenticated
    isAuthenticated(): boolean {
        return isAuthenticatedRef.value;
    },

    // Clear authentication data
    logout(): void {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        isAuthenticatedRef.value = false;
    },

    // Set authentication data (token and user)
    setAuth(token: string, user: any): void {
        this.setToken(token);
        this.setUser(user);
        isAuthenticatedRef.value = true;
    },
};

