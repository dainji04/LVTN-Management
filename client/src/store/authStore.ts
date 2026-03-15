import { defineStore } from "pinia";
import axiosInstance from "../helpers/apiHelper";

import type { User } from "../types/userType";
import type { LoginResponse, LogoutResponse } from "../types/responseAxios";

interface AuthState {
  user: User | null;
  token: string | null;
  authenticated: boolean;
}

const TOKEN_KEY = import.meta.env.TOKEN_KEY || "token";
const USER_KEY = import.meta.env.USER_KEY || "user";
const IS_AUTH = import.meta.env.IS_AUTH || "is_auth";

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    user: JSON.parse(localStorage.getItem(USER_KEY) || "null"),
    token: localStorage.getItem(TOKEN_KEY) || null,
    authenticated: !!localStorage.getItem(IS_AUTH) || false,
  }),
  getters: {
    getUser(state: AuthState): User | null {
      return state.user;
    },
    isAuthenticated(state: AuthState): boolean {
      return state.authenticated && state.token !== "" && state.token !== null;
    },
    getToken(state: AuthState): string {
      return state.token || "";
    },
  },
  actions: {
    setAuth(token: string, isAuthenticated: boolean, user: User | null = null): void {
      this.token = token;
      this.user = user;
      this.authenticated = isAuthenticated;
      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, JSON.stringify(user));
      localStorage.setItem(IS_AUTH, isAuthenticated.toString());
    },
    async login(email: string, password: string): Promise<Boolean> {
      try {
        const response = await axiosInstance.post<LoginResponse>(
          "/users/login",
          {
            email,
            password,
          }
        );
        const res = response.data;
        if (res.code === 200 && res.data.authenticated) {
          this.setAuth(res.data.token, res.data.authenticated, res.data.thongTinUser);
          return true;
        } else {
          return false;
        }
      } catch (err: any) {
        return false;
      }
    },
    clearAuth(): void {
      this.token = null;
      this.user = null;
      this.authenticated = false;
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      localStorage.removeItem(IS_AUTH);
    },
    async logout(): Promise<boolean> {
      try {
        const response = await axiosInstance.post<LogoutResponse>("/users/logout", {
          token: this.token,
        });
        if (response.data.code === 200) {
          this.clearAuth();
          return true;
        } else {
          return false;
        }
      } catch (error: any) {
        return false;
      }
    },
    getAuth(): AuthState {
      this.token = localStorage.getItem(TOKEN_KEY);
      this.user = JSON.parse(localStorage.getItem(USER_KEY) || "null");
      this.authenticated = !!localStorage.getItem(TOKEN_KEY);
      return {
        token: this.token,
        user: this.user,
        authenticated: this.authenticated,
      }
    },
  },
});
