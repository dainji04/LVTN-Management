import { io } from "socket.io-client";

const SERVER_URL = import.meta.env.VITE_APP_SOCKET_URL;

class SocketIOService {
  constructor() {
    this.socket = null;
  }

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

  disconnect() {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
    }
  }

  on(event, callback) {
    this.socket?.on(event, callback);
    return () => this.socket?.off(event, callback);
  }

  off(event, callback) {
    this.socket?.off(event, callback);
  }

  emitUserConnect() {
    this._emit("user_connect");
  }

  sendMessage(payload) {
    this._emit("send_message", payload);
  }

  createConversation(payload) {
    this._emit("create_conversation", payload);
  }

  sendTyping(conversationId) {
    this._emit("typing", { conversationId });
  }

  sendStopTyping(conversationId) {
    this._emit("stop_typing", { conversationId });
  }

  joinConversation(conversationId) {
    this._emit("join_conversation", { conversationId });
  }

  leaveConversation(conversationId) {
    this._emit("leave_conversation", { conversationId });
  }

  deleteMessage(payload) {
    this._emit("delete_message", payload);
  }

  editMessage(payload) {
    this._emit("edit_message", payload);
  }

  reactMessage(payload) {
    this._emit("react_message", payload);
  }
  // Status 

  isConnected() {
    return this.socket?.connected === true;
  }

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