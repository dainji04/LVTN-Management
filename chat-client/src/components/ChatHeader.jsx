import React from "react";
import { Avatar } from "antd";
import {
  PhoneOutlined, VideoCameraOutlined, MoreOutlined, ArrowLeftOutlined,
} from "@ant-design/icons";

const ChatHeader = ({ chat, onBack, isMobile = false }) => {

  if (!chat) return null;

  return ( chat.SoLuongThanhVien === 2 ? (
    <div className="p-4 border-b border-gray-200 flex items-center justify-between bg-white flex-shrink-0">
      {/* Left: back (mobile) + avatar + name */}
      <div className="flex items-center gap-3">
        {isMobile && (
          <button onClick={onBack} className="text-gray-600 hover:text-primary mr-1">
            <ArrowLeftOutlined className="text-xl" />
          </button>
        )}
        <div className="relative">
          <Avatar
          src={chat.FriendAvatar || null}
            size={40}
            style={{ backgroundColor: "#F4517A" }}
          >
            {chat.FriendTen?.[0]?.toUpperCase()}
          </Avatar>
          {chat.online && (
            <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-white" />
          )}
        </div>
        <div>
          <h3 className="font-semibold text-gray-800">{chat.FriendHo??""} {chat.FriendTen??""}</h3>
          <p className={`text-xs ${chat.online ? "text-green-500" : "text-gray-400"}`}>
            {chat.online ? "Đang hoạt động" : "Không hoạt động"}
          </p>
        </div>
      </div>

      {/* Right: action icons */}
      <div className="flex items-center gap-4">
        <PhoneOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
        />
        <VideoCameraOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
        />
        <MoreOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
        />
      </div>
    </div>
  )
  
  : 

  <div className="p-4 border-b border-gray-200 flex items-center justify-between bg-white flex-shrink-0">
      {/* Left: back (mobile) + avatar + name */}
      <div className="flex items-center gap-3">
        {isMobile && (
          <button onClick={onBack} className="text-gray-600 hover:text-primary mr-1">
            <ArrowLeftOutlined className="text-xl" />
          </button>
        )}
        <div className="relative">
          <Avatar
          src={chat.AnhDaiDien || null}
            size={40}
            style={{ backgroundColor: "#F4517A" }}
          >
            {chat.Ten?.[0]?.toUpperCase()}
          </Avatar>
          {chat.online && (
            <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-white" />
          )}
        </div>
        <div>
          <h3 className="font-semibold text-gray-800">{chat.Ten}</h3>
          <p className={`text-xs ${chat.online ? "text-green-500" : "text-gray-400"}`}>
            {chat.online ? "Đang hoạt động" : "Không hoạt động"}
          </p>
        </div>
      </div>

      {/* Right: action icons */}
      <div className="flex items-center gap-4">
        <PhoneOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
        />
        <VideoCameraOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
        />
        <MoreOutlined
          className="text-xl text-gray-600 cursor-pointer hover:text-primary transition-colors"
        />
      </div>
    </div>
    );
};

export default ChatHeader;
