import React, { useState } from "react";
import { formatMessageTime } from "../utils/formatTime";
import { useChatContext } from "../context/ChatContext.jsx";
import { deleteMessage } from "../api/chatApi";


const MessageBubble = ({ message, onMessageDeleted }) => {
  const { currentUser } = useChatContext();
  const [isDeleting, setIsDeleting] = useState(false);

  // const { text, image, isSelf, timestamp } = message; Hình ảnh chưa có nên tạm thời ẩn đi, sau này sửa lại
  const { MaTinNhan, NoiDung, MaNguoiGui, NgayGuiTinNhan } = message;
  const time = formatMessageTime(NgayGuiTinNhan);

  const isSelf = MaNguoiGui === currentUser?.id;

  // Hàm xóa tin nhắn
  const handleDeleteMessage = async () => {
    if (!window.confirm("Bạn chắc chắn muốn xóa tin nhắn này?")) return;
    
    setIsDeleting(true);
    try {
      await deleteMessage(MaTinNhan, currentUser?.id);
      if (onMessageDeleted) {
        onMessageDeleted(MaTinNhan);
      }
    } catch (error) {
      console.error("Lỗi xóa tin nhắn:", error);
      alert("Không thể xóa tin nhắn!");
    } finally {
      setIsDeleting(false);
    }
  }; 
  if (isSelf) {
    return (
      <div className="message-bubble flex mb-4 justify-end group">
        <div className="flex items-center gap-2">
          {/* Nút xóa (ẩn đi, hiện khi hover) */}
          {isSelf && (
            <button
              onClick={handleDeleteMessage}
              disabled={isDeleting}
              className="px-2 py-1 text-xs bg-red-500 text-white rounded opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-600 disabled:opacity-50"
              title="Xóa tin nhắn"
            >
              {isDeleting ? "Đang xóa..." : "Xóa"}
            </button>
          )}
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
