import React from "react";
import { Avatar } from "antd";
import { formatChatTime } from "../utils/formatTime.js";
import { useChatContext } from "../context/ChatContext.jsx";

const ChatItem = ({ chat,friend, isActive = false, onClick }) => {
  const { isUserOnline } = useChatContext();
  // const { name, avatar, lastMessage, lastMessageTime, online, unread } = chat;
  const {  TinNhanCuoi, NgayTinNhanCuoi, unread } = chat;
  const { name, username , avatar } = friend || {};
  const online = isUserOnline(friend?.id);
  return (
    <div
      onClick={() => onClick?.(chat.MaCuocTroChuyen)}
      className={`chat-list-item p-4 flex items-center gap-3 cursor-pointer hover:bg-gray-50 transition-colors rounded-lg ${isActive ? "bg-pink-50" : ""}`}
    >

      <div className="relative flex-shrink-0">
        <Avatar
          src={avatar}
          size={50}
          icon={<span className="font-semibold">{name?.[0]?.toUpperCase() || username?.[0]?.toUpperCase()}</span>}
          style={{ backgroundColor: "#F4517A" }}
        />
        {online && (
          <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-white" />
        )}
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between mb-1">
          <h3 className="font-semibold text-gray-800 truncate">{name || username}</h3>
          <span className="text-xs text-gray-500 flex-shrink-0 ml-2">{formatChatTime(NgayTinNhanCuoi)}</span>
        </div>
        <div className="flex items-center justify-between">
          <p className="text-sm text-gray-600 truncate">{TinNhanCuoi}</p>
          {unread > 0 && (
            <div className="bg-primary text-white text-xs rounded-full w-5 h-5 flex items-center justify-center flex-shrink-0 ml-2">
              {unread}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ChatItem;
