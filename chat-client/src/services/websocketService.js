/**
 * websocketService.js
 *
 * Singleton Socket.IO client — event names khớp chính xác với BE handler.
 *
 * ── Client → Server ──────────────────────────────────────────────────────────
 *  "user_connect"       ()                           → trigger sau khi connect
 *  "send_message"       { conversationId, content, type? }
 *  "create_conversation"{ participantIds, type?, name? }
 *  "typing"             { conversationId }
 *  "stop_typing"        { conversationId }
 *  "join_conversation"  { conversationId }
 *  "leave_conversation" { conversationId }
 *
 * ── Server → Client ──────────────────────────────────────────────────────────
 *  "connected"          { userId, socketId, onlineUsers }
 *  "new_message"        { id, conversationId, senderId, content, type, createdAt, ... }
 *  "typing"             { conversationId, userId }
 *  "stop_typing"        { conversationId, userId }
 *  "user_online"        { userId }
 *  "user_offline"       { userId }
 *  "conversation_created" { conversation }
 *  "error"              { message }
 */

import { io } from "socket.io-client";

const SERVER_URL = import.meta.env.VITE_APP_SOCKET_URL;

class SocketIOService {
  constructor() {
    /** @type {import("socket.io-client").Socket | null} */
    this.socket = null;
  }

  // ─── Lifecycle ─────────────────────────────────────────────────────

  /**
   * Khởi tạo kết nối Socket.IO.
   * Token JWT được gửi qua auth object — server đọc ở socket.handshake.auth.token
   * (AuthMiddleware.verifySocketToken).
   *
   * @param {string} [token]
   */
  connect(token) {
    if (this.socket?.connected || this.socket?.active) return;

    this.socket = io(SERVER_URL, {
      auth: { token: token || "" },
      transports: ["websocket", "polling"],
      reconnection: true,
      reconnectionAttempts: 10,
      reconnectionDelay: 1500,
      reconnectionDelayMax: 10000,
      randomizationFactor: 0.4,
      timeout: 10000,
    });

    this._attachCoreListeners();
  }

  /** Ngắt kết nối và huỷ socket. */
  disconnect() {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
    }
  }

  // ─── Subscription ──────────────────────────────────────────────────

  /**
   * Đăng ký lắng nghe một event.
   * Trả về hàm unsubscribe để dùng trong useEffect cleanup.
   *
   * @param   {string}   event
   * @param   {Function} callback
   * @returns {() => void}
   */
  on(event, callback) {
    this.socket?.on(event, callback);
    return () => this.socket?.off(event, callback);
  }

  off(event, callback) {
    this.socket?.off(event, callback);
  }

  // ─── Emit helpers (khớp với BE handler) ───────────────────────────

  /**
   * Bước bắt buộc sau khi socket connect:
   * kích hoạt handleUserConnect ở BE, nhận lại event "connected".
   */
  emitUserConnect() {
    this._emit("user_connect");
  }

  /**
   * Gửi tin nhắn — BE xử lý ở chatService.sendMessage.
   *
   * @param {{ conversationId: string, content: string, type?: string }} payload
   */
  sendMessage(payload) {
    this._emit("send_message", payload);
  }

  /**
   * Tạo conversation mới — BE xử lý ở chatService.createConversation.
   *
   * @param {{ participantIds: string[], type?: string, name?: string }} payload
   */
  createConversation(payload) {
    this._emit("create_conversation", payload);
  }

  /**
   * Gửi "đang nhập..." — BE broadcast "typing" đến conversation room.
   *
   * @param {string} conversationId
   */
  sendTyping(conversationId) {
    this._emit("typing", { conversationId });
  }

  /**
   * Gửi "ngừng nhập" — BE broadcast "stop_typing" đến conversation room.
   *
   * @param {string} conversationId
   */
  sendStopTyping(conversationId) {
    this._emit("stop_typing", { conversationId });
  }

  /**
   * Vào room của một conversation — server gọi socket.join(`conversation_${id}`).
   *
   * @param {string} conversationId
   */
  joinConversation(conversationId) {
    this._emit("join_conversation", { conversationId });
  }

  /**
   * Rời room của một conversation.
   *
   * @param {string} conversationId
   */
  leaveConversation(conversationId) {
    this._emit("leave_conversation", { conversationId });
  }

  // ─── Status ────────────────────────────────────────────────────────

  /** @returns {boolean} */
  isConnected() {
    return this.socket?.connected === true;
  }

  /** @returns {string | undefined} */
  get id() {
    return this.socket?.id;
  }

  // ─── Private ───────────────────────────────────────────────────────

  _emit(event, payload) {
    if (!this.socket?.connected) {
      console.warn(`[Socket.IO] Not connected — "${event}" dropped.`);
      return;
    }
    // payload undefined → emit chỉ event name (e.g. "user_connect")
    if (payload !== undefined) {
      this.socket.emit(event, payload);
    } else {
      this.socket.emit(event);
    }
  }

  _attachCoreListeners() {
    if (!this.socket) return;

    this.socket.on("connect", () => {
      console.info(`[Socket.IO] ✅ Connected  id=${this.socket.id}`);
      // Ngay sau khi connect, phải emit "user_connect" để BE gọi handleUserConnect
      this.emitUserConnect();
    });

    this.socket.on("disconnect", (reason) => {
      console.info(`[Socket.IO] ⚡ Disconnected  reason=${reason}`);
    });

    this.socket.on("connect_error", (err) => {
      console.error(`[Socket.IO] ❌ Connection error: ${err.message}`);
    });

    this.socket.on("error", ({ message }) => {
      console.error(`[Socket.IO] Server error: ${message}`);
    });
  }
}

const socketService = new SocketIOService();
export default socketService;

