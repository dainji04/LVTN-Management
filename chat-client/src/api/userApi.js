/**
 * userApi.js
 *
 * Axios calls cho Auth + Users + Friends — khớp với API Quick Reference v1.
 *
 *  POST /api/auth/refresh        → refresh access token
 *  GET  /api/auth/me             → current user (Protected)
 *  GET  /api/auth/verify         → verify token (Protected)
 *  POST /api/auth/logout         → logout (Protected)
 *
 *  GET  /api/friends             → danh sách bạn bè
 *  GET  /api/friends/search      → tìm kiếm bạn bè ?query=
 *
 *  GET  /api/users/:id           → user theo ID
 *  GET  /api/users/search        → tìm kiếm users ?query=
 */

import axios from "axios";

const BASE_URL = import.meta.env.VITE_USER_API_URL;

const api = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

// Gắn JWT access token vào mọi request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("Token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res.data,
  (err) => {
    const message = err.response?.data?.message || err.message || "Something went wrong";
    return Promise.reject(new Error(message));
  }
);

// ─── Auth ────────────────────────────────────────────────────────────────────

/** POST /api/auth/login — response: { code, data: { token, authenticated } } */
export const login = (credentials) => api.post("/users/login", credentials);

/** POST /api/auth/refresh */
export const refreshAccessToken = (refreshToken) =>
  api.post("/auth/refresh", { refreshToken });

/** GET /api/auth/me */
export const getMe = () => api.get("/me");

/** GET /api/auth/verify */
export const verifyToken = () => api.get("/auth/introspect");

/** POST /api/auth/logout */
export const logout = () => api.post("/auth/logout");

// ─── Friends ─────────────────────────────────────────────────────────────────

/** GET /api/friends */
export const getFriends = async () => {
  const res = await getMe();
  const userId = res.data.data.maNguoiDung;
  return api.get(`/ban-be/${userId}`);
}

/** GET /api/friends/search?query=... */ //chưa có api ở user service
export const searchFriends = (query, userId) =>
  api.get("/ban-be/search", { params: { userId, query } });

// ─── Users ───────────────────────────────────────────────────────────────────

/** GET /api/users/:id */
export const getUserById = (id) => api.get(`/users/${id}`);

export const getFriendsByIds = (ids) => api.post("/ban-be/by-ids",  ids );
  
export default api;
