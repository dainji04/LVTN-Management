import React, { useState, useRef, useCallback, useEffect } from "react";
import { Input, Button } from "antd";
import {
  CameraOutlined, PictureOutlined, SmileOutlined, SendOutlined,
} from "@ant-design/icons";
import { useChatContext } from "../context/ChatContext.jsx";


const MessageInput = ({ onSend, onTyping, onStopTyping, disabled = false }) => {
  const { activeConvRef, currentUserRef } = useChatContext();
  const { activeConvId, currentUser } = useChatContext();
  console.log("MessageInput render with conversationId:", activeConvId, "currentUser:", currentUser);
  console.log("Refs - activeConvRef:", activeConvRef.current, "currentUserRef:", currentUserRef.current);
  const [text, setText]               = useState("");
  const [isTypingLocal, setTypingLocal] = useState(false);
  const typingTimerRef                = useRef(null);
  const fileInputRef                  = useRef(null);

  const startTyping = useCallback(() => {
    if (!isTypingLocal) { setTypingLocal(true); onTyping?.(); }
    clearTimeout(typingTimerRef.current);
    typingTimerRef.current = setTimeout(() => {
      setTypingLocal(false); onStopTyping?.();
    }, 2500);
  }, [isTypingLocal, onTyping, onStopTyping]);

  const stopTypingNow = useCallback(() => {
    clearTimeout(typingTimerRef.current);
    if (isTypingLocal) { setTypingLocal(false); onStopTyping?.(); }
  }, [isTypingLocal, onStopTyping]);

  const handleChange = e => {
    setText(e.target.value);
    e.target.value.trim() ? startTyping() : stopTypingNow();
  };

  const handleSend = useCallback(() => {
    const trimmed = text.trim();
    if (!trimmed || disabled) return;
    stopTypingNow();
    onSend?.({ convId: activeConvRef.current, content: trimmed, type: "text" });
    setText("");
  });  

  const handleKeyDown = e => {
    if (e.key === "Enter" && !e.shiftKey) { e.preventDefault(); handleSend(); }
  };

  const handleFileChange = e => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = ev => { stopTypingNow(); onSend?.({ content: ev.target.result, type: "image" }); };
    reader.readAsDataURL(file);
    e.target.value = "";
  };

  useEffect(() => () => clearTimeout(typingTimerRef.current), []);

  return (
    <div className="p-4 border-t border-gray-200 bg-white flex-shrink-0">
      <div className="flex items-center gap-2">
        {/* Camera */}
        <CameraOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
          onClick={() => fileInputRef.current?.click()}
        />

        {/* Text input */}
        <Input
          className="flex-1 rounded-full"
          placeholder="Nhập tin nhắn..."
          value={text}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
          disabled={disabled}
        />

        {/* Picture */}
        <PictureOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
          onClick={() => fileInputRef.current?.click()}
        />

        {/* Smile */}
        <SmileOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
        />

        {/* Send button */}
        <Button
          type="primary"
          onClick={handleSend}
          disabled={!text.trim() || disabled}
          className="inline-flex items-center justify-center rounded-full px-4 py-2"
          icon={<SendOutlined />}
        >
        </Button>

        <input
          ref={fileInputRef}
          type="file"
          accept="image/*"
          className="hidden"
          onChange={handleFileChange}
        />
      </div>
    </div>
  );
};

export default MessageInput;
