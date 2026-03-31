/**
 * useWebSocket.js
 *
 * Hook quản lý Socket.IO — event names khớp với BE:
 *
 *  Server → Client events được lắng nghe:
 *    "connected"            { userId, socketId, onlineUsers }
 *    "new_message"          { id, conversationId, senderId, content, type, createdAt, sender }
 *    "typing"               { conversationId, userId }
 *    "stop_typing"          { conversationId, userId }
 *    "user_online"          { userId }
 *    "user_offline"         { userId }
 *    "conversation_created" { conversation }
 *
 *  Client → Server emit methods được trả về:
 *    sendMessage(payload)          → "send_message"
 *    sendTyping(conversationId)    → "typing"
 *    sendStopTyping(conversationId)→ "stop_typing"
 *    joinConversation(id)          → "join_conversation"
 *    leaveConversation(id)         → "leave_conversation"
 *    createConversation(payload)   → "create_conversation"
 */

import { useEffect, useCallback } from "react";
import socketService from "../services/websocketService";

/**
 * @param {Object}   options
 * @param {string}   [options.token]                 JWT auth token
 * @param {Function} [options.onConnected]           "connected"            { userId, socketId, onlineUsers }
 * @param {Function} [options.onNewMessage]          "new_message"          { id, conversationId, ... }
 * @param {Function} [options.onTyping]              "typing"               { conversationId, userId }
 * @param {Function} [options.onStopTyping]          "stop_typing"          { conversationId, userId }
 * @param {Function} [options.onUserOnline]          "user_online"          { userId }
 * @param {Function} [options.onUserOffline]         "user_offline"         { userId }
 * @param {Function} [options.onConversationCreated] "conversation_created" { conversation }
 * @param {Function} [options.onDisconnect]          built-in "disconnect"
 */
const useWebSocket = ({
  token,
  onConnected,
  onNewMessage,
  onTyping,
  onStopTyping,
  onUserOnline,
  onUserOffline,
  onConversationCreated,
  onDisconnect,
} = {}) => {
  useEffect(() => {
    socketService.connect(token);

    const unsubs = [
      onConnected           && socketService.on("connected",            onConnected),
      onNewMessage          && socketService.on("new_message",          onNewMessage),
      onTyping              && socketService.on("typing",               onTyping),
      onStopTyping          && socketService.on("stop_typing",          onStopTyping),
      onUserOnline          && socketService.on("user_online",          onUserOnline),
      onUserOffline         && socketService.on("user_offline",         onUserOffline),
      onConversationCreated && socketService.on("conversation_created", onConversationCreated),
      onDisconnect          && socketService.on("disconnect",           onDisconnect),
    ].filter(Boolean);

    return () => unsubs.forEach((unsub) => unsub());
  }, [token]); // eslint-disable-line react-hooks/exhaustive-deps

  // ─── Stable emit helpers ──────────────────────────────────────────

  /** emit "send_message" → { conversationId, content, type? } */
  const sendMessage = useCallback((payload) => {
    socketService.sendMessage(payload);
  }, []);

  /** emit "typing" → { conversationId } */
  const sendTyping = useCallback((conversationId) => {
    socketService.sendTyping(conversationId);
  }, []);

  /** emit "stop_typing" → { conversationId } */
  const sendStopTyping = useCallback((conversationId) => {
    socketService.sendStopTyping(conversationId);
  }, []);

  /** emit "join_conversation" → { conversationId } */
  const joinConversation = useCallback((conversationId) => {
    socketService.joinConversation(conversationId);
  }, []);

  /** emit "leave_conversation" → { conversationId } */
  const leaveConversation = useCallback((conversationId) => {
    socketService.leaveConversation(conversationId);
  }, []);

  /** emit "create_conversation" → { participantIds, type?, name? } */
  const createConversation = useCallback((payload) => {
    socketService.createConversation(payload);
  }, []);

  const isConnected = useCallback(() => socketService.isConnected(), []);

  return {
    sendMessage,
    sendTyping,
    sendStopTyping,
    joinConversation,
    leaveConversation,
    createConversation,
    isConnected,
  };
};

export default useWebSocket;
