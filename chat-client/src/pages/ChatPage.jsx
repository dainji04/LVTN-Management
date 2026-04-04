import React, { useState, useCallback } from "react";
import ChatSidebar from "../components/ChatSidebar";
import ChatList from "../components/ChatList";
import ChatWindow from "../components/ChatWindow";
import { useChatContext } from "../context/ChatContext";
import ChatProvider from "../context/ChatProvider.jsx";

const ChatPage = ({ onLogout }) => {
  const [mobileView, setMobileView] = useState("list"); // "list" | "chat"
  
  const {
    currentUser,
    conversations,
    activeConvId,
    activeConversation,
    messages,
    loadingConvs,
    loadingMessages,
    sendMessage,
    notifyTyping,
    notifyStopTyping,
    selectConversation,
    createConversation,
    isSomeoneTyping,
  } = useChatContext();


  const handleSelectConversation = useCallback((convId) => {
    selectConversation(convId);
    setMobileView("chat");
  }, [selectConversation]);

  const handleSelectFriend = useCallback((friend) => {
    const friendId = String(friend.id); 
    const existing = conversations.find(
      (c) => c.SoLuongThanhVien === 2 && String(c.FriendId) === friendId 
    );
    console.log("Existing conversation with friendId", friendId, ":", existing);
    if (existing) {
      selectConversation(String(existing.MaCuocTroChuyen));
    } else {
      createConversation({ memberIds: [parseInt(friendId)]});
    }
    setMobileView("chat");
  }, [conversations, selectConversation, createConversation]);

  const handleBack = useCallback(() => setMobileView("list"), []);

  // Xóa tin nhắn khỏi danh sách
  const handleMessageDeleted = useCallback((messageId) => {
    console.log("Tin nhắn đã được xóa:", messageId);
    // Optionally: refetch messages or filter from current state
  }, []);

  return (
    <div className="home-container flex justify-between h-screen bg-[#f5f5f5] overflow-hidden">

      {/* ── Sidebar ───────────────────────────────────────────── */}
      <ChatSidebar
        currentUser={currentUser}
        onLogout={onLogout}
        activePath="/messages"
      />

      {/* ── Main content ──────────────────────────────────────── */}
      {/* DESKTOP: ChatList + ChatWindow đều là direct children của main */}
      <ChatProvider token={localStorage.getItem("Token")}>
      <main className="main-content w-full flex-1 flex overflow-hidden">
        
        {/* ── ChatList panel (desktop luôn hiển thị, mobile toggle) ── */}
        <div className={`${mobileView === "list" ? "flex" : "hidden"} md:flex flex-col w-80 flex-shrink-0 bg-white border-r border-gray-200`}>

            <ChatList
              // currentUser={currentUser}
              chats={conversations}
              activeChatId={activeConvId}
              onSelectChat={handleSelectConversation}
              onSelectFriend={handleSelectFriend}
              loading={loadingConvs}
            />
        </div>

        {/* ── ChatWindow panel (desktop luôn hiển thị, mobile toggle) ── */}
        <div className={`${mobileView === "chat" ? "flex" : "hidden"} md:flex flex-1 flex-col overflow-hidden`}>
            <ChatWindow
              chat={activeConversation}
              messages={messages}
              loadingMessages={loadingMessages}
              isTyping={isSomeoneTyping(activeConvId)}
              onSendMessage={sendMessage}
              onTyping={() => notifyTyping(activeConvId)}
              onStopTyping={() => notifyStopTyping(activeConvId)}
              onBack={handleBack}
              onMessageDeleted={handleMessageDeleted}
              isMobile={mobileView === "chat"}
            />
          
        </div>
      </main>
      </ChatProvider>
    </div>
  );
};

export default ChatPage;
