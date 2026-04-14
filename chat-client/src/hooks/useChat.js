import { useState, useEffect, useCallback, useRef } from "react";
import { getConversations, getConversationMessages } from "../api/chatApi";
import { getMe } from "../api/userApi";
import useWebSocket from "./useWebSocket";

const useChat = (token) => {
  const [currentUser, setCurrentUser]         = useState(null);
  const [conversations, setConversations]     = useState([]);
  const [activeConvId, setActiveConvId]       = useState(null);
  const [typingUsers, setTypingUsers]         = useState({});
  const [onlineUsers, setOnlineUsers]         = useState(new Set());
  const [loadingConvs, setLoadingConvs]       = useState(false);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [error, setError]                     = useState(null);
  const [messagesByConversation, setMessagesByConversation] = useState({});

  const activeConvRef  = useRef(null);
  const currentUserRef = useRef(null);

  useEffect(() => { currentUserRef.current = currentUser; }, [currentUser]);
  useEffect(() => { activeConvRef.current  = activeConvId; }, [activeConvId]);

  useEffect(() => {
    console.log("messagesByConversation updated:", messagesByConversation);
  }, [messagesByConversation]);

  const stopTypingTimers = useRef({});

  //  Load current user 
  useEffect(() => {
    const fetchMe = async () => {
      try {
        const res  = await getMe();
        const user = res.data;
        setCurrentUser({
          id:       user.maNguoiDung,
          name:     `${user.ho} ${user.ten}`,
          username: user.bietDanh,
          avatar:   user.anhDaiDien,
        });
      } catch (err) {
        console.error("getMe error:", err);
        setCurrentUser(null);
      }
    };
    fetchMe();
  }, []);

  // Load conversation list 
  useEffect(() => {
    const fetch = async () => {
      setLoadingConvs(true);
      setError(null);
      try {
        const data = await getConversations();
        setConversations(Array.isArray(data.data) ? data.data : []);
      } catch {
        setConversations([]);
      } finally {
        setLoadingConvs(false);
      }
    };
    fetch();
  }, []);

  //Load messages khi đổi conversation 
  useEffect(() => {
    if (!activeConvId) return;
    if (messagesByConversation[activeConvId]) return;

    const fetch = async () => {
      setLoadingMessages(true);
      const id = activeConvId;
      try {
        const data = await getConversationMessages(id);
        setMessagesByConversation((prev) => ({
          ...prev,
          [id]: Array.isArray(data.data) ? data.data : [],
        }));
      } catch {
        setMessagesByConversation((prev) => ({ ...prev, [id]: [] }));
      } finally {
        setLoadingMessages(false);
      }
    };
    fetch();
  }, [activeConvId]);

  // Socket event handlers 

  const handleConnected = useCallback(({ onlineUsers: onlineList = [] }) => {
    setOnlineUsers(new Set(onlineList.map(String)));
    console.log("Socket connected. Online users:", onlineList);
  }, []);

  const handleNewMessage = useCallback(({ conversationId, message }) => {
    const convId    = String(conversationId);
    const normalised = {
      MaTinNhan:       message.MaTinNhan,
      MaCuocTroChuyen: convId,
      NoiDung:         message.NoiDung,
      LoaiTinNhan:     message.LoaiTinNhan || "text",
      NgayGuiTinNhan:  message.NgayGuiTinNhan,
      MaNguoiGui:      message.MaNguoiGui,
      AnhNguoiGui:     message.AnhNguoiGui,
      TenNguoiGui:     message.TenNguoiGui,
      HoNguoiGui:      message.HoNguoiGui,
      status:          "delivered",
    };

    setMessagesByConversation((prev) => {
      const convMessages = prev[convId] || [];

      const exists = convMessages.some(
        (m) => String(m.MaTinNhan) === String(normalised.MaTinNhan)
      );
      if (exists) return prev;

      const optimisticIndex = convMessages.findIndex(
        (m) =>
          String(m.MaTinNhan).startsWith("temp-") &&
          m.MaNguoiGui === normalised.MaNguoiGui &&
          m.NoiDung    === normalised.NoiDung
      );

      let updated;
      if (optimisticIndex !== -1) {
        updated = [...convMessages];
        updated[optimisticIndex] = normalised;
      } else {
        updated = [...convMessages, normalised];
      }

      return { ...prev, [convId]: updated };
    });

    setConversations((prev) =>
      prev.map((c) =>
        String(c.MaCuocTroChuyen) === convId
          ? {
              ...c,
              TinNhanCuoi:     normalised.NoiDung,
              NgayTinNhanCuoi: normalised.NgayGuiTinNhan,
              unread:
                convId === String(activeConvRef.current)
                  ? 0
                  : (c.unread || 0) + 1,
            }
          : c
      )
    );
  }, []);

  const handleConversationCreated = useCallback(({ conversation, participants_info }) => {
    const user = currentUserRef.current;
    if (!user) return;

    const participants = Object.values(participants_info);
    const convId       = String(conversation.MaCuocTroChuyen);

    participants.length === 2 &&
      setConversations((prev) => {
        const exists = prev.some((c) => String(c.MaCuocTroChuyen) === convId);
        if (exists) return prev;

        const friend = participants.find(
          (m) => String(m.maNguoiDung) !== String(user.id)
        );
        return [
          {
            ...conversation,
            FriendId:         friend.maNguoiDung,
            FriendTen:        friend.ten,
            FriendAvatar:     friend.anhDaiDien,
            FriendLastActive: friend.hoatDongLanCuoi,
          },
          ...prev,
        ];
      });

    setMessagesByConversation((prev) => ({ ...prev, [convId]: [] }));
    joinConversation(convId);
    setActiveConvId(convId);
  }, []);
  
  const updateReaction = useCallback(
    (conversationId, messageId, reactions) => {
      console.log(`Cập nhật phản ứng cho tin nhắn ${messageId} trong cuộc trò chuyện ${conversationId}:`, reactions);
      setMessagesByConversation((prev) => {
        const current = prev[String(activeConvRef.current)] || [];

        return {
          ...prev,
          [String(conversationId)]: current.map((msg) => {
            if (String(msg.MaTinNhan) !== String(messageId)) return msg;
            console.log(`Cập nhật phản ứng cho tin nhắn ${messageId}:`, reactions);
            return {
              ...msg,
              reactions: reactions,
            };
          }),
        };
      });
    },
    []
  );

  const handleMessageReaction = useCallback(
    ({ conversationId, messageId, reactions }) => {
      updateReaction(conversationId, messageId, reactions);
    },
    [updateReaction]
  );

  const handleTyping = useCallback(({ conversationId, userId }) => {
    const convId = String(conversationId);
    const uid    = String(userId);
    setTypingUsers((prev) => ({
      ...prev,
      [convId]: [...new Set([...(prev[convId] || []), uid])],
    }));

    clearTimeout(stopTypingTimers.current[`${convId}_${uid}`]);
    stopTypingTimers.current[`${convId}_${uid}`] = setTimeout(() => {
      setTypingUsers((prev) => ({
        ...prev,
        [convId]: (prev[convId] || []).filter((id) => id !== uid),
      }));
    }, 4000);
  }, []);

  const handleStopTyping = useCallback(({ conversationId, userId }) => {
    const convId = String(conversationId);
    const uid    = String(userId);
    clearTimeout(stopTypingTimers.current[`${convId}_${uid}`]);
    setTypingUsers((prev) => ({
      ...prev,
      [convId]: (prev[convId] || []).filter((id) => id !== uid),
    }));
  }, []);

  const handleUserOnline  = useCallback(({ userId }) => {
    setOnlineUsers((prev) => new Set([...prev, String(userId)]));
  }, []);

  const handleUserOffline = useCallback(({ userId }) => {
    setOnlineUsers((prev) => {
      const next = new Set(prev);
      next.delete(String(userId));
      return next;
    });
  }, []);

  /** Xóa tin nhắn khỏi UI */
  const removeMessage = useCallback((conversationId, messageId) => {
    setMessagesByConversation((prev) => {
      const current = prev[String(conversationId)] || [];
      return {
        ...prev,
        [String(conversationId)]: current.filter(
          (msg) => String(msg.MaTinNhan) !== String(messageId)
        ),
      };
    });
  }, []);

  /** "message_deleted" — socket báo xóa */
  const handleMessageDeletedSocket = useCallback(
    ({ conversationId, messageId }) => {
      console.log(`Socket báo: Tin nhắn ${messageId} đã bị xóa!`);
      removeMessage(conversationId, messageId);
    },
    [removeMessage]
  );

  /** "message_edited" — socket báo sửa */
  const handleMessageEditedSocket = useCallback(
    ({ conversationId, message }) => {
      console.log(`Socket báo: Tin nhắn ${message?.MaTinNhan} đã được sửa!`);
      setMessagesByConversation((prev) => {
        const current = prev[String(conversationId)] || [];
        return {
          ...prev,
          [String(conversationId)]: current.map((msg) =>
            String(msg.MaTinNhan) === String(message.MaTinNhan)
              ? { ...msg, ...message }
              : msg
          ),
        };
      });
    },
    []
  );

  // ─── Mount socket hooks ─────────────────────────────────────────────
  const {
    sendMessage: wsSendMessage,
    sendTyping,
    sendStopTyping,
    joinConversation,
    leaveConversation,
    createConversation,
    deleteMessage: wsDeleteMessage,
    editMessage:   wsEditMessage,
    reactMessage
  } = useWebSocket({
    token,
    onConnected:           handleConnected,
    onNewMessage:          handleNewMessage,
    onTyping:              handleTyping,
    onStopTyping:          handleStopTyping,
    onUserOnline:          handleUserOnline,
    onUserOffline:         handleUserOffline,
    onConversationCreated: handleConversationCreated,
    onMessageDeleted:      handleMessageDeletedSocket,
    onMessageEdited:       handleMessageEditedSocket,
    onMessageReaction: handleMessageReaction
  });

  // ─── Public actions ─────────────────────────────────────────────────

  const reactMessageAction = useCallback(
  (conversationId, messageId, reaction) => {
    reactMessage({ conversationId, messageId, reaction });
  },
  [reactMessage]
);

  const selectConversation = useCallback(
    (convId) => {
      const id = String(convId);
      if (activeConvId && activeConvId !== id) leaveConversation(activeConvId);
      joinConversation(id);
      setActiveConvId(id);
      setConversations((prev) =>
        prev.map((c) =>
          String(c.MaCuocTroChuyen) === id ? { ...c, unread: 0 } : c
        )
      );
      console.log("messageByConversation trước khi load:", messagesByConversation);
    },
    [activeConvId, joinConversation, leaveConversation]
  );

  const sendMessage = useCallback(
    ({ content, type = "text", convId }) => {
      const conversationId = convId || activeConvId;
      if (!conversationId || !content?.trim()) return;

      const optimisticId = `temp-${Date.now()}`;
      const optimistic   = {
        MaTinNhan:       optimisticId,
        MaCuocTroChuyen: String(conversationId),
        NoiDung:         content,
        LoaiTinNhan:     type,
        NgayGuiTinNhan:  new Date().toISOString(),
        MaNguoiGui:      currentUser?.id,
        status:          "sending",
      };

      setMessagesByConversation((prev) => {
        const convMessages = prev[String(conversationId)] || [];
        return {
          ...prev,
          [String(conversationId)]: [...convMessages, optimistic],
        };
      });

      wsSendMessage({ conversationId, content, type });
    },
    [activeConvId, currentUser, wsSendMessage]
  );

  const deleteMessageAction = useCallback(
    (conversationId, messageId) => {
      wsDeleteMessage({ conversationId, messageId });
    },
    [wsDeleteMessage]
  );

  const editMessageAction = useCallback(
    (conversationId, messageId, content) => {
      wsEditMessage({ conversationId, messageId, content });
    },
    [wsEditMessage]
  );

  /**
   * Cập nhật tin nhắn thủ công (dự phòng, ưu tiên dùng socket)
   */
  const updateMessageInState = useCallback((conversationId, messageId, newContent) => {
    setMessagesByConversation((prev) => {
      const current = prev[String(conversationId)] || [];
      return {
        ...prev,
        [String(conversationId)]: current.map((msg) =>
          String(msg.MaTinNhan) === String(messageId)
            ? { ...msg, NoiDung: newContent }
            : msg
        ),
      };
    });
  }, []);

  const notifyTyping = useCallback(
    (conversationId) => sendTyping(conversationId || activeConvId),
    [activeConvId, sendTyping]
  );

  const notifyStopTyping = useCallback(
    (conversationId) => sendStopTyping(conversationId || activeConvId),
    [activeConvId, sendStopTyping]
  );

  // ─── Selectors ──────────────────────────────────────────────────────

  const activeConversation = conversations.find(
    (c) => String(c.MaCuocTroChuyen) === String(activeConvId)
  ) ?? null;

  const isUserOnline = useCallback(
    (userId) => onlineUsers.has(String(userId)),
    [onlineUsers]
  );

  const isSomeoneTyping = useCallback(
    (convId) => {
      const id = String(convId || activeConvId);
      return (typingUsers[id] || []).some(
        (uid) => uid !== String(currentUser?.id)
      );
    },
    [typingUsers, activeConvId, currentUser]
  );

  const totalUnread = conversations.reduce((sum, c) => sum + (c.unread || 0), 0);
  const messages    = messagesByConversation[activeConvId];

  return {
    // State
    currentUser,
    conversations,
    activeConvId,
    activeConversation,
    activeConvRef,
    currentUserRef,
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
    removeMessage,
    updateMessageInState,
    deleteMessageAction,
    editMessageAction,
    reactMessageAction,

    // Selectors
    isUserOnline,
    isSomeoneTyping,
  };
};

export default useChat;