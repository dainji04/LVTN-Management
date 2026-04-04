import React from "react";
import ChatHeader from "./ChatHeader";
import MessageList from "./MessageList";
import MessageInput from "./MessageInput";
import { useTranslation } from "react-i18next";

const EmptyState = ({t}) => (
  <div className="flex-1 flex flex-col items-center justify-center bg-white text-gray-400">
    <div className="text-6xl mb-4">💬</div>
    <h3 className="text-lg font-semibold text-gray-600 mb-1">{t("welcome")}</h3>
    <p className="text-sm">{t("welcome_disc")}</p>
  </div>
);

const ChatWindow = ({
  chat, messages, loadingMessages, isTyping,
  onSendMessage, onTyping, onStopTyping, onBack, onMessageDeleted, isMobile = false,
}) => {
  const { t } = useTranslation();
  if (!chat) return <div className="flex-1 flex flex-col bg-white"><EmptyState t={t} /></div>;
  return (
    <div className="flex-1 flex flex-col bg-white min-h-0">
      <ChatHeader chat={chat} onBack={onBack} isMobile={isMobile} />
      <MessageList
        messages={messages}
        loading={loadingMessages}
        isTyping={isTyping}
        onMessageDeleted={onMessageDeleted}
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
