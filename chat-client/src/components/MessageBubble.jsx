import React, { useState } from "react";
import { formatMessageTime } from "../utils/formatTime";
import { useChatContext } from "../context/ChatContext.jsx";
import { useTranslation } from "react-i18next";
import { Modal } from "antd";

const MessageBubble = ({ message }) => {
  const [modal, contextHolder] = Modal.useModal();
  const { t } = useTranslation();
  // Lấy thêm action xóa và sửa từ Context
  const { currentUser, deleteMessageAction, editMessageAction, reactMessageAction } = useChatContext();
  
  // State cho việc Xóa và Sửa
  const [isDeleting, setIsDeleting] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(message.NoiDung || "");

  const { MaTinNhan, NoiDung, MaNguoiGui, NgayGuiTinNhan, MaCuocTroChuyen } = message;
  const time = formatMessageTime(NgayGuiTinNhan);

  const isSelf = MaNguoiGui === currentUser?.id;

  const REACTIONS = ["👍","❤️","😂","😮","😢"];

  const handleReaction = (emoji) => {
    if (!reactMessageAction) return;

    reactMessageAction(
      MaCuocTroChuyen,
      MaTinNhan,
      emoji
    );
  };

  const confirmDelete = () => {
    modal.confirm({
      title: t("confirm_delete"),
      okText: t("delete"),
      cancelText: t("cancel"),
      okType: "danger",
      onOk() {
        handleDeleteMessage();
      }
    });
  };

  const renderReactions = () => {
    if (!message.reactions || message.reactions.length === 0) return null;

    return (
      <div
        className={`absolute -bottom-3 flex gap-1 bg-white border rounded-full px-2 py-0.5 shadow text-xs
        ${isSelf ? "left-0" : "right-0"}`}
      >
        {message.reactions.map((r, index) => (
          <span key={index}>{r.CamXuc}</span>
        ))}
      </div>
    );
  };

  const renderReactionPicker = () => (
    <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition">
      {REACTIONS.map((emoji) => (
        <button
          key={emoji}
          onClick={() => handleReaction(emoji)}
          className="text-lg hover:scale-125 transition"
        >
          {emoji}
        </button>
      ))}
    </div>
  );

  // ─── XỬ LÝ XÓA ──────────────────────────────────────────────
  const handleDeleteMessage = () => {
    
    setIsDeleting(true);
    try {
      if (deleteMessageAction) {
        deleteMessageAction(MaCuocTroChuyen, MaTinNhan);
      }
    } catch (error) {
      console.error("Lỗi gửi yêu cầu xóa qua socket:", error);
      alert("Không thể xóa tin nhắn!");
    } finally {
      setTimeout(() => setIsDeleting(false), 500);
    }
  }; 

  // ─── XỬ LÝ SỬA ──────────────────────────────────────────────
  const handleSaveEdit = () => {
    // Nếu nội dung trống hoặc không có gì thay đổi thì hủy bỏ
    if (!editContent.trim() || editContent === NoiDung) {
      setIsEditing(false);
      return;
    }
    
    try {
      if (editMessageAction) {
        editMessageAction(MaCuocTroChuyen, MaTinNhan, editContent);
      }
      setIsEditing(false); // Tắt form sửa sau khi gửi lệnh
    } catch (error) {
      console.error("Lỗi gửi yêu cầu sửa:", error);
    }
  };

  // ─── GIAO DIỆN KHI ĐANG SỬA TIN NHẮN ──────────────────────────
  if (isEditing && isSelf) {
    return (
      <div className="message-bubble flex mb-4 justify-end">
        <div className="flex flex-col items-end gap-2 max-w-md w-full">
          <textarea
            value={editContent}
            onChange={(e) => setEditContent(e.target.value)}
            className="w-full text-sm text-black px-3 py-2 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-primary/50 resize-none shadow-sm"
            rows="2"
            autoFocus
          />
          <div className="flex gap-2">
            <button 
              onClick=  {() => {
                setIsEditing(false);
                setEditContent(NoiDung); // Phục hồi nội dung cũ nếu bấm Hủy
              }}
              className="px-3 py-1 text-xs text-gray-600 bg-gray-200 rounded hover:bg-gray-300 transition"
            >
              {t("cancel")}
            </button>
            <button 
              onClick={handleSaveEdit}
              className="px-3 py-1 text-xs text-white bg-blue-500 rounded hover:bg-blue-600 transition shadow-sm"
            >
              {t("confirm")}
            </button>
          </div>
        </div>
      </div>
    );
  }

  // ─── GIAO DIỆN BÌNH THƯỜNG (CỦA BẠN) ──────────────────────────
  if (isSelf) {
    return (
      <div className="message-bubble flex mb-4 justify-end group">
        <div className="flex items-center gap-2">
          {/* Nhóm Nút thao tác (ẩn đi, hiện khi hover) */}
          <div className="flex flex-col gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
            <button
              onClick={() => {
                setEditContent(NoiDung); // Đảm bảo lấy nội dung mới nhất
                setIsEditing(true);
              }}
              className="px-2 py-1 text-xs bg-gray-200 text-gray-700 rounded hover:bg-gray-300 transition"
              title="Sửa tin nhắn"
            >
              {t("edit_message")}
            </button>

               {contextHolder}
              
            <button
              onClick={confirmDelete}
              disabled={isDeleting}
              className="px-2 py-1 text-xs bg-red-500 text-white rounded hover:bg-red-600 disabled:opacity-50 transition"
              title={t("delete_message")}
            >
              {isDeleting ? "..." : t("delete_message")}
            </button>
          </div>

          <div className="relative message-content max-w-xs lg:max-w-md px-4 py-2 rounded-2xl bg-primary text-white shadow-sm">
            {NoiDung && <p className="text-sm whitespace-pre-wrap">{NoiDung}</p>}
            <p className="text-xs mt-1 text-white/70 text-right">{time}</p>

            {renderReactions()}
          </div>
        </div>
      </div>
    );
  }

  // ─── GIAO DIỆN BÌNH THƯỜNG (CỦA NGƯỜI KHÁC) ───────────────────
  return (
    <div className="message-bubble flex mb-4 justify-start group">
      
      <div className="relative message-content max-w-xs lg:max-w-md px-4 py-2 rounded-2xl bg-white text-gray-800 border border-gray-100 shadow-sm">
        
        {NoiDung && <p className="text-sm whitespace-pre-wrap">{NoiDung}</p>}
        
        <p className="text-xs mt-1 text-gray-500">{time}</p>

        {renderReactions()}

      </div>

      {renderReactionPicker()}
      
    </div>
  );
};

export default MessageBubble;