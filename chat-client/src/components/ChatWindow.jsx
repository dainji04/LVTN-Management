import React from "react";
import ChatHeader from "./ChatHeader";
import MessageList from "./MessageList";
import MessageInput from "./MessageInput";

const EmptyState = () => (
  <div className="flex-1 flex flex-col items-center justify-center bg-white text-gray-400">
    <div className="text-6xl mb-4">💬</div>
    <h3 className="text-lg font-semibold text-gray-600 mb-1">Chào mừng đến AloChat!</h3>
    <p className="text-sm">Chọn một cuộc trò chuyện để bắt đầu nhắn tin</p>
  </div>
);

const ChatWindow = ({
  chat, messages, loadingMessages, isTyping,
  onSendMessage, onTyping, onStopTyping, onBack, isMobile = false,
}) => {
  if (!chat) return <div className="flex-1 flex flex-col bg-white"><EmptyState /></div>;
  return (
    <div className="flex-1 flex flex-col bg-white min-h-0">
      <ChatHeader chat={chat} onBack={onBack} isMobile={isMobile} />
      <MessageList
        messages={messages}
        loading={loadingMessages}
        isTyping={isTyping}
      />
      <MessageInput
        conversationId={chat.id}
        onSend={onSendMessage}
        onTyping={onTyping}
        onStopTyping={onStopTyping}
      />
    </div>
  );
};

export default ChatWindow;
