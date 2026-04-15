import axios from "axios";

const BASE_URL = import.meta.env.VITE_CHAT_API_URL;

const api = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

// Gắn JWT token vào mọi request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("Token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

//Tự động refresh token khi nhận 401
api.interceptors.response.use(
  (res) => res.data,
  async (err) => {
    const original = err.config;

    if (err.response?.status === 401 && !original._retry) {
      original._retry = true;
      try {
        const refreshToken = localStorage.getItem("refreshToken");
        const { token: accessToken } = await api.post("/auth/refresh", { refreshToken });
        localStorage.setItem("Token", accessToken);
        original.headers.Authorization = `Bearer ${accessToken}`;
        return api(original);
      } catch {
        // Refresh thất bại → xoá token, redirect login
        localStorage.removeItem("Token");
        localStorage.removeItem("refreshToken");
        // window.location.href = "/login";
      }
    }

    const message = err.response?.data?.message || err.message || "Something went wrong";
    return Promise.reject(new Error(message));
  }
);

// ─── Conversations ──────────────────────────────────────────────────────────

/**
 * GET /api/conversations?type=all|direct|group
 * @param {"direct"|"group"} [type="all"]
 */
export const getConversations = (type = "direct") =>
  api.get(`/api/v1/conversations/${type}`);

/**
 * GET /api/v1/conversations/:id
 * @param {string} conversationId
 */
export const getConversationById = (conversationId) =>
  api.get(`/api/v1/conversations/${conversationId}`);

/**
 * GET /api/v1/conversations/:id/messages?limit=50&offset=0
 * @param {string} conversationId
 * @param {{ limit?: number, offset?: number }} params
 */
export const getConversationMessages = (id, params = {}) =>
  api.get(`/api/v1/conversations/${id}/messages`, {
    params: { limit: 50, offset: 0, ...params },
  });

// ─── Messages ───────────────────────────────────────────────────────────────

/**
 * PUT /api/v1/messages/:id
 * @param {string} messageId
 * @param {{ content: string, userId: number }} data
 */
export const updateMessage = (messageId, data) =>
  api.put(`/api/v1/messages/${messageId}`, data);

/**
 * DELETE /api/v1/messages/:id
 * @param {string} messageId
 * @param {number} userId
 */
export const deleteMessage = (messageId, userId) =>
  api.delete(`/api/v1/messages/${messageId}`, { data: { userId } });

export default api;

