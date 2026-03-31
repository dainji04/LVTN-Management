/**
 * useChat.js
 *
 * Central state hook cho toàn bộ chat app.
 *
 * Flow khởi động:
 *  1. Gọi GET /api/conversations để load danh sách
 *  2. Socket connect → tự động emit "user_connect" (trong websocketService)
 *  3. Server emit "connected" → lưu onlineUsers ban đầu
 *  4. Khi chọn conversation: emit "join_conversation", load messages qua API
 *  5. Khi gửi tin: emit "send_message" (socket only, không gọi API)
 *  6. Server broadcast "new_message" → append vào message list
 *
 * Server → Client events handled:
 *   "connected"   → seed onlineUsers từ danh sách server trả về
 *   "new_message" → append message, update preview conversation
 *   "typing"      → thêm userId vào typingUsers[conversationId]
 *   "stop_typing" → xoá userId khỏi typingUsers[conversationId]
 *   "user_online" → thêm vào onlineUsers set
 *   "user_offline"→ xoá khỏi onlineUsers set
 */

import { useState, useEffect, useCallback, useRef } from "react";
import { getConversations, getConversationMessages } from "../api/chatApi";
import { getMe } from "../api/userApi";
import useWebSocket from "./useWebSocket";

const useChat = (token) => {
  const [currentUser, setCurrentUser]         = useState(null);
  const [conversations, setConversations]     = useState([]);
  const [activeConvId, setActiveConvId]       = useState(null);
  // const [messages, setMessages]               = useState([]);
  const [typingUsers, setTypingUsers]         = useState({});   // { [convId]: userId[] }
  const [onlineUsers, setOnlineUsers]         = useState(new Set());
  const [loadingConvs, setLoadingConvs]       = useState(false);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [error, setError]                     = useState(null);
  const [messagesByConversation, setMessagesByConversation] = useState({});
  const activeConvRef = useRef(null);

  useEffect(() => {
    activeConvRef.current = activeConvId;
  }, [activeConvId]);

  useEffect(() => {
  console.log("messagesByConversation updated:", messagesByConversation);
}, [messagesByConversation]);

  const stopTypingTimers = useRef({}); // auto-clear typing if stop_typing never arrives

  // ─── Load current user ──────────────────────────────────────────────
  useEffect(() => {
    const fetchMe = async () => {
    try {
      const res = await getMe();
      const user = res.data;
      setCurrentUser({
        id: user.maNguoiDung,
        name: `${user.ho} ${user.ten}`,
        username: user.bietDanh,
        avatar: user.anhDaiDien
      });

    } catch (err) {
      console.error("getMe error:", err);
      setCurrentUser(null);
    }
  };

  fetchMe();
  }, []);

  // ─── Load conversation list ─────────────────────────────────────────
  useEffect(() => {
    const fetch = async () => {
      setLoadingConvs(true);
      setError(null);
      try {
        const data = await getConversations();
        // Chuẩn hoá shape: BE có thể trả { conversations: [...] } hoặc mảng trực tiếp
        console.log("Loaded conversations:", data);
        setConversations(
          Array.isArray(data.data.data) ? data.data.data : []
        );
      } catch {
        setConversations([]);
      } finally {
        setLoadingConvs(false);
      }
    };
    fetch();
  }, []);

  // ─── Load messages khi đổi conversation ────────────────────────────
  useEffect(() => {
    if (!activeConvId) return;
      if (messagesByConversation[activeConvId]) return;
    const fetch = async () => {
      setLoadingMessages(true);
      const id = activeConvId;
      try {
        const data = await getConversationMessages(id);
        console.log(`Loaded messages for conversation ${id}:`, data);
        // setMessages(
        //   Array.isArray(data.data.data) ? data.data.data : []
        // );
        setMessagesByConversation((prev) => ({
          ...prev,
          [id]: Array.isArray(data.data.data) ? data.data.data : []
        }));
      } catch {
        // setMessages([]);
        setMessagesByConversation((prev) => ({
          ...prev,
          [id]: []
        }));
      } finally {
        setLoadingMessages(false);
      }
    };
    fetch();
  }, [activeConvId]);

  // ─── Socket event handlers ──────────────────────────────────────────

  /** "connected" — seed trạng thái online từ server */
  const handleConnected = useCallback(({ onlineUsers: onlineList = [] }) => {
    setOnlineUsers(new Set(onlineList.map(String)));
  }, []);

  /**
   * "new_message" — server broadcast sau khi chatService.sendMessage xử lý.
   * Shape dự kiến: { id, conversationId, senderId, content, type, createdAt, sender }
   */
  const handleNewMessage = useCallback(({ conversationId, message }) => {
    
    const convId = String(conversationId);
    const Message = message;
    // Normalise về shape FE dùng
    const normalised = {
      MaTinNhan:         Message.MaTinNhan,
      MaCuocTroChuyen: convId,
      NoiDung:       Message.NoiDung,
      LoaiTinNhan:       Message.LoaiTinNhan || "text",
      NgayGuiTinNhan:  Message.NgayGuiTinNhan,
      MaNguoiGui:   Message.MaNguoiGui,
      AnhNguoiGui:     Message.AnhNguoiGui,
      TenNguoiGui:     Message.TenNguoiGui,
      HoNguoiGui:      Message.HoNguoiGui,
      status:     "delivered",
    };
    
    // Nếu đang xem conversation này thì append
    // setMessages((prev) =>
    //   convId === String(activeConvRef.current)
    //     ? // Xoá optimistic nếu có, rồi thêm message thật
    //       [...prev.filter((m) => !String(m.MaTinNhan).startsWith("temp-")), normalised]
    //     : prev
    // );

    setMessagesByConversation(prev => {
  const convMessages = prev[convId] || [];

  // nếu message đã tồn tại → ignore
  const exists = convMessages.some(
    m => String(m.MaTinNhan) === String(normalised.MaTinNhan)
  );

  if (exists) return prev;

  // tìm optimistic message tương ứng
  const optimisticIndex = convMessages.findIndex(
    m =>
      String(m.MaTinNhan).startsWith("temp-") &&
      m.MaNguoiGui === normalised.MaNguoiGui &&
      m.NoiDung === normalised.NoiDung
  );

  let updated;

  if (optimisticIndex !== -1) {
    // replace optimistic
    updated = [...convMessages];
    updated[optimisticIndex] = normalised;
  } else {
    // append message mới
    updated = [...convMessages, normalised];
  }

  return {
    ...prev,
    [convId]: updated
  };
});

    setConversations((prev) =>
      prev.map((c) => 
        String(c.MaCuocTroChuyen) === convId
          ? {
              ...c,
              TinNhanCuoi:     normalised.NoiDung,
              NgayTinNhanCuoi: normalised.NgayGuiTinNhan,
              unread: convId === String(activeConvRef.current) ? 0 : (c.unread || 0) + 1,
            }
          : c
      )
    );
  }, []);

  const handleConversationCreated = useCallback(({ conversation, participantIds = [] }) => {
  const convId = String(conversation.MaCuocTroChuyen);
    console.log("New conversation created:", conversation);
    console.log("userid get this event is user:", currentUser?.id);
  // add conversation nếu chưa tồn tại
  participantIds.length === 2 &&
    setConversations(prev => {
      const exists = prev.some(
        c => String(c.MaCuocTroChuyen) === convId
      ); 

      if (exists) return prev;
      const id = participantIds.filter(m => String(m) !== String(currentUser?.id))[0];
      return [{ ...conversation, FriendId: id }, ...prev];
    });
  console.log("conversations after adding new one:", conversations);
  // init message list
  setMessagesByConversation(prev => ({
    ...prev,
    [convId]: []
  }));

  // join socket room trước
  joinConversation(convId);

  // open conversation
  setActiveConvId(convId);

}, [currentUser]);

  /** "typing" — { conversationId, userId } */
  const handleTyping = useCallback(({ conversationId, userId }) => {
    const convId = String(conversationId);
    const uid    = String(userId);

    setTypingUsers((prev) => ({
      ...prev,
      [convId]: [...new Set([...(prev[convId] || []), uid])],
    }));

    // Auto-clear sau 4s phòng trường hợp stop_typing không đến
    clearTimeout(stopTypingTimers.current[`${convId}_${uid}`]);
    stopTypingTimers.current[`${convId}_${uid}`] = setTimeout(() => {
      setTypingUsers((prev) => ({
        ...prev,
        [convId]: (prev[convId] || []).filter((id) => id !== uid),
      }));
    }, 4000);
  }, []);

  /** "stop_typing" — { conversationId, userId } */
  const handleStopTyping = useCallback(({ conversationId, userId }) => {
    const convId = String(conversationId);
    const uid    = String(userId);
    clearTimeout(stopTypingTimers.current[`${convId}_${uid}`]);
    setTypingUsers((prev) => ({
      ...prev,
      [convId]: (prev[convId] || []).filter((id) => id !== uid),
    }));
  }, []);

  /** "user_online" — { userId } */
  const handleUserOnline = useCallback(({ userId }) => {
    setOnlineUsers((prev) => new Set([...prev, String(userId)]));
  }, []);

  /** "user_offline" — { userId } */
  const handleUserOffline = useCallback(({ userId }) => {
    setOnlineUsers((prev) => {
      const next = new Set(prev);
      next.delete(String(userId));
      return next;
    });
  }, []);

  // ─── Mount socket hooks ─────────────────────────────────────────────
  
  const {
    sendMessage: wsSendMessage,
    sendTyping,
    sendStopTyping,
    joinConversation,
    leaveConversation,
    createConversation,
  } = useWebSocket({
    token,
    onConnected:   handleConnected,
    onNewMessage:  handleNewMessage,
    onTyping:      handleTyping,
    onStopTyping:  handleStopTyping,
    onUserOnline:  handleUserOnline,
    onUserOffline: handleUserOffline,
    onConversationCreated: handleConversationCreated
  });

  // ─── Public actions ─────────────────────────────────────────────────

  /**
   * Chọn conversation để chat.
   * Emit leave_conversation cho conv cũ, join_conversation cho conv mới.
   */
  const selectConversation = useCallback((convId) => {
    const id = String(convId);
    if (activeConvId && activeConvId !== id) leaveConversation(activeConvId);
    joinConversation(id);
    setActiveConvId(id);
    // Xoá unread badge
    setConversations((prev) =>
      prev.map((c) => (String(c.MaCuocTroChuyen) === id ? { ...c, unread: 0 } : c))
    );
  }, [activeConvId, joinConversation, leaveConversation]);

  /**
   * Gửi tin nhắn.
   * Hiển thị optimistic ngay lập tức, sau đó emit "send_message" qua socket.
   * Server sẽ broadcast "new_message" xác nhận lại.
   *
   * @param {{ content: string, type?: string, convId?: string }} param
   */
  const sendMessage = useCallback(({ content, type = "text", convId }) => {
    const conversationId = convId || activeConvId;
    if (!conversationId || !content?.trim()) return;

    // Hiển thị optimistic message
    const optimisticId = `temp-${Date.now()}`;
    const optimistic = {
      MaTinNhan:        optimisticId,
      MaCuocTroChuyen:    String(conversationId),
      NoiDung:      content,
      LoaiTinNhan:      type,
      NgayGuiTinNhan: new Date().toISOString(),
      MaNguoiGui:  currentUser?.id,
      status:    "sending",
    };
    // setMessages((prev) => [...prev, optimistic]);
    setMessagesByConversation(prev => {
      const convMessages = prev[String(conversationId)] || [];
      return {
        ...prev,
        [String(conversationId)]: [...convMessages, optimistic]
      };
    });

    // Emit qua socket — khớp với BE: socket.on("send_message", async (data) => ...)
    wsSendMessage({ conversationId, content, type });
  }, [activeConvId, currentUser, wsSendMessage]);

  /**
   * Emit typing indicator.
   * MessageInput gọi hàm này khi user đang nhập.
   */
  const notifyTyping = useCallback((conversationId) => {
    sendTyping(conversationId || activeConvId);
  }, [activeConvId, sendTyping]);

  /**
   * Emit stop typing.
   * MessageInput gọi khi user ngừng nhập hoặc gửi tin.
   */
  const notifyStopTyping = useCallback((conversationId) => {
    sendStopTyping(conversationId || activeConvId);
  }, [activeConvId, sendStopTyping]);

  // ─── Selectors ──────────────────────────────────────────────────────

  /** Conversation đang active */
  const activeConversation = conversations.find(
    (c) => String(c.MaCuocTroChuyen) === String(activeConvId)
  ) ?? null;

  /** Kiểm tra user có online không */
  const isUserOnline = useCallback(
    (userId) => onlineUsers.has(String(userId)),
    [onlineUsers]
  );

  /** Có ai đang gõ trong conversation không */
  const isSomeoneTyping = useCallback(
    (convId) => {
      const id = String(convId || activeConvId);
      // Loại bỏ chính mình
      return (typingUsers[id] || []).some(
        (uid) => uid !== String(currentUser?.id)
      );
    },
    [typingUsers, activeConvId, currentUser]
  );

  /** Tổng số unread */
  const totalUnread = conversations.reduce((sum, c) => sum + (c.unread || 0), 0);
  
const messages = messagesByConversation[activeConvId]; 

  return {
    // State
    currentUser,
    conversations,
    activeConvId,
    activeConversation,
    // messages,
    messages,
    loadingConvs,
    loadingMessages,
    error,
    totalUnread,

    // Actions
    sendMessage,
    selectConversation,
    notifyTyping,
    notifyStopTyping,
    createConversation,

    // Selectors
    isUserOnline,
    isSomeoneTyping,
  };
};

export default useChat;
