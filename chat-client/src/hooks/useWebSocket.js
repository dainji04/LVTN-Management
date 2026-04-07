/**
 * useWebSocket.js
 *
 * Hook quản lý Socket.IO — event names khớp với BE:
 *
 * Server → Client events được lắng nghe:
 * "connected"            { userId, socketId, onlineUsers }
 * "new_message"          { id, conversationId, senderId, content, type, createdAt, sender }
 * "typing"               { conversationId, userId }
 * "stop_typing"          { conversationId, userId }
 * "user_online"          { userId }
 * "user_offline"         { userId }
 * "conversation_created" { conversation }
 * "message_deleted"      { conversationId, messageId }
 * "message_edited"       { conversationId, message }
 *
 * Client → Server emit methods được trả về:
 * sendMessage(payload)          → "send_message"
 * sendTyping(conversationId)    → "typing"
 * sendStopTyping(conversationId)→ "stop_typing"
 * joinConversation(id)          → "join_conversation"
 * leaveConversation(id)         → "leave_conversation"
 * createConversation(payload)   → "create_conversation"
 * deleteMessage(payload)        → "delete_message"
 * editMessage(payload)          → "edit_message"
 */

import { useEffect, useCallback, useRef } from "react";
import socketService from "../services/websocketService";

/**
 * Dùng useRef để lưu callback mới nhất.
 * Giúp socket listener luôn gọi đúng callback hiện tại
 * mà không cần re-register listener mỗi khi callback thay đổi.
 */
const useStableCallback = (fn) => {
  const ref = useRef(fn);
  useEffect(() => {
    ref.current = fn;
  });
  return useCallback((...args) => ref.current?.(...args), []);
};

const useWebSocket = ({
  token,
  onConnected,
  onNewMessage,
  onTyping,
  onStopTyping,
  onUserOnline,
  onUserOffline,
  onConversationCreated,
  onMessageDeleted,
  onMessageEdited,
  onDisconnect,
  onMessageReaction
} = {}) => {

  // ─── Wrap tất cả callbacks thành stable version ─────────────────────
  // Nhờ vậy socket listener không bao giờ bị stale dù useChat re-render
  const stableOnConnected           = useStableCallback(onConnected);
  const stableOnNewMessage          = useStableCallback(onNewMessage);
  const stableOnTyping              = useStableCallback(onTyping);
  const stableOnStopTyping          = useStableCallback(onStopTyping);
  const stableOnUserOnline          = useStableCallback(onUserOnline);
  const stableOnUserOffline         = useStableCallback(onUserOffline);
  const stableOnConversationCreated = useStableCallback(onConversationCreated);
  const stableOnMessageDeleted      = useStableCallback(onMessageDeleted);
  const stableOnMessageEdited       = useStableCallback(onMessageEdited);
  const stableOnDisconnect          = useStableCallback(onDisconnect);
  const stableOnMessageReaction      = useStableCallback(onMessageReaction);

  // ─── Chỉ connect + register listeners 1 lần duy nhất ───────────────
  // Không cần re-run khi callback thay đổi vì đã dùng useRef bên trong
  useEffect(() => {
    socketService.connect(token);

    const unsubs = [
      onConnected           && socketService.on("connected",            stableOnConnected),
      onNewMessage          && socketService.on("new_message",          stableOnNewMessage),
      onTyping              && socketService.on("typing",               stableOnTyping),
      onStopTyping          && socketService.on("stop_typing",          stableOnStopTyping),
      onUserOnline          && socketService.on("user_online",          stableOnUserOnline),
      onUserOffline         && socketService.on("user_offline",         stableOnUserOffline),
      onConversationCreated && socketService.on("conversation_created", stableOnConversationCreated),
      onMessageDeleted      && socketService.on("message_deleted",      stableOnMessageDeleted),
      onMessageEdited       && socketService.on("message_edited",       stableOnMessageEdited),
      onDisconnect          && socketService.on("disconnect",           stableOnDisconnect),
      onMessageReaction     && socketService.on("reaction_update",     stableOnMessageReaction),
    ].filter(Boolean);

    return () => unsubs.forEach((unsub) => unsub());
  }, [token]); // eslint-disable-line react-hooks/exhaustive-deps
  // token là dependency duy nhất — khi token đổi thì reconnect với token mới

  // ─── Stable emit helpers ─────────────────────────────────────────────

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

  /** emit "delete_message" → { conversationId, messageId } */
  const deleteMessage = useCallback((payload) => {
    socketService.deleteMessage(payload);
  }, []);

  /** emit "edit_message" → { conversationId, messageId, newContent } */
  const editMessage = useCallback((payload) => {
    socketService.editMessage(payload);
  }, []);

  const reactMessage = useCallback((payload) => {
    socketService.reactMessage(payload);
  }, []);

  const isConnected = useCallback(() => socketService.isConnected(), []);

  return {
    sendMessage,
    sendTyping,
    sendStopTyping,
    joinConversation,
    leaveConversation,
    createConversation,
    deleteMessage,
    editMessage,
    isConnected,
    reactMessage
  };
};

export default useWebSocket;