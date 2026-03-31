import React from "react";
import { formatMessageTime } from "../utils/formatTime";
import { useChatContext } from "../context/ChatContext.jsx";


const MessageBubble = ({ message }) => {
  const { currentUser } = useChatContext();

  // const { text, image, isSelf, timestamp } = message; Hình ảnh chưa có nên tạm thời ẩn đi, sau này sửa lại
  const { NoiDung, MaNguoiGui, NgayGuiTinNhan } = message;
  const time = formatMessageTime(NgayGuiTinNhan);

  
  const isSelf = MaNguoiGui === currentUser?.id; 

  if (isSelf) {
    return (
      <div className="message-bubble flex mb-4 justify-end">
        <div className="message-content max-w-xs lg:max-w-md px-4 py-2 rounded-2xl bg-primary text-white">
          {/* {image && (
            <p className="mb-2">
              <img src={image} alt="shared" className="rounded-lg max-w-full h-auto" />
            </p>
          )} */}
          {NoiDung && <p className="text-sm whitespace-pre-wrap">{NoiDung}</p>}
          <p className="text-xs mt-1 text-white/70">{time}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="message-bubble flex mb-4 justify-start">
      <div className="message-content max-w-xs lg:max-w-md px-4 py-2 rounded-2xl bg-white text-gray-800">
        {/* {image && (
          <p className="mb-2">
            <img src={image} alt="shared" className="rounded-lg max-w-full h-auto" />
          </p>
        )} */}
        {NoiDung && <p className="text-sm whitespace-pre-wrap">{NoiDung}</p>}
        <p className="text-xs mt-1 text-gray-500">{time}</p>
      </div>
    </div>
  );
};

export default MessageBubble;
