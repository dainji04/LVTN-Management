import React, { useState, useEffect, useRef, useCallback } from "react";
import { Input, Avatar } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import ChatItem from "./ChatItem";
import { searchFriends, getFriendsByIds, getMe } from "../api/userApi";

// ── Skeleton loading row ──────────────────────────────────────────────────────
const SkeletonRow = () => (
  <div className="p-4 flex items-center gap-3 animate-pulse">
    <div className="w-12 h-12 rounded-full bg-gray-200 flex-shrink-0" />
    <div className="flex-1 space-y-2">
      <div className="h-3 bg-gray-200 rounded w-2/3" />
      <div className="h-2.5 bg-gray-100 rounded w-4/5" />
    </div>
  </div>
);

// ── Friend result row ─────────────────────────────────────────────────────────
const FriendItem = ({ friend, onClick }) => (
  <div
    onClick={() => onClick?.(friend)}
    className="chat-list-item p-4 flex items-center gap-3 cursor-pointer hover:bg-gray-50 transition-colors rounded-lg"
  >
    <Avatar
      src={friend.avatar}
      size={50}
      style={{ backgroundColor: "#F4517A", flexShrink: 0 }}
    >
      {(friend.name || friend.username)?.[0]?.toUpperCase()}
    </Avatar>
    <div className="flex-1 min-w-0">
      <h3 className="font-semibold text-gray-800 truncate">
        {friend.name || friend.username}
      </h3>
      {friend.username && friend.name && (
        <p className="text-sm text-gray-500 truncate">@{friend.username}</p>
      )}
    </div>
  </div>
);

// ── Main component ────────────────────────────────────────────────────────────
const ChatList = ({
  chats = [],
  activeChatId,
  onSelectChat,
  onSelectFriend,
  // currentUser,
  loading = false,
}) => {
  const [query, setQuery]             = useState("");
  const [friends, setFriends]         = useState([]);
  const [searching, setSearching]     = useState(false);
  const [searchError, setSearchError] = useState("");
  const debounceRef                   = useRef(null);
  const [friendsMap, setFriendsMap] = useState({});

  const doSearch = useCallback(async (q) => {
    if (!q.trim()) { setFriends([]); return; }
    setSearching(true);
    setSearchError("");
    try {

      const me = await getMe();
      const userId = parseInt(me.data.maNguoiDung);
      const res = await searchFriends(q.trim(), userId);
      const raw = (Array.isArray(res.data) ? res.data : []);
      console.log("searchFriends result", raw); 
      setFriends(
        raw.map(user => ({
          id: user.maNguoiDung,
          name: `${user.ho} ${user.ten}`,
          username: user.bietDanh,
          avatar: user.anhDaiDien
        }))
      );
    } catch {
      setSearchError("Không thể tìm kiếm. Thử lại sau.");
      setFriends([]);
    } finally {
      setSearching(false);
    }
  }, []);

  useEffect(() => {
  const fetchFriends = async () => {
    if (!chats.length) return;
    try {
      const friendIds = chats
        .map(chat => chat.FriendId)
        .filter(Boolean);  

      const uniqueIds = [...new Set(friendIds)];

      if (!uniqueIds.length) return;

      const res = await getFriendsByIds(uniqueIds);

      const raw = Array.isArray(res.data) ? res.data : [];

      const map = {};

      raw.forEach(user => { 
        map[String(user.maNguoiDung)] = {
          id: user.maNguoiDung,
          name: `${user.ho} ${user.ten}`,
          username: user.bietDanh,
          avatar: user.anhDaiDien
        };
      });

      setFriendsMap(map);

    } catch (err) {
      console.error("fetchFriends error", err);
    }
  };

  fetchFriends();
  }, [chats]);

  useEffect(() => {
    clearTimeout(debounceRef.current);
    if (!query.trim()) { setFriends([]); setSearching(false); return; }
    setSearching(true);
    debounceRef.current = setTimeout(() => doSearch(query), 400);
    return () => clearTimeout(debounceRef.current);
  }, [query, doSearch]);

  const isSearchMode = query.trim().length > 0;

  // ── Render list content ───────────────────────────────────────────────────
  const renderContent = () => {
    // Search mode
    if (isSearchMode) {
      if (searching) return Array.from({ length: 4 }).map((_, i) => <SkeletonRow key={i} />);
      if (searchError) return (
        <div className="p-4 text-center text-gray-400 text-sm">{searchError}</div>
      );
      if (friends.length === 0) return (
        <div className="flex flex-col items-center justify-center h-40 text-gray-400">
          <span className="text-3xl mb-2">🔍</span>
          <p className="text-sm">Không tìm thấy bạn bè</p>
        </div>
      );
      return (
        <>
          <p className="px-4 pt-3 pb-1 text-xs font-semibold text-gray-400 uppercase tracking-wider">
            Bạn bè ({friends.length})
          </p>
          {friends.map((f) => (
            <FriendItem key={f.id} friend={f} onClick={onSelectFriend} />
          ))}
        </>
      );
    }

    // Conversation list mode
    if (loading) return Array.from({ length: 6 }).map((_, i) => <SkeletonRow key={i} />);
    if (chats.length === 0) return (
      <div className="flex flex-col items-center justify-center h-40 text-gray-400">
        <span className="text-3xl mb-2">💬</span>
        <p className="text-sm">Chưa có cuộc trò chuyện</p>
      </div>
    );
    return chats.map((chat) => (
      <ChatItem
        key={chat.MaCuocTroChuyen}
        chat={chat}
        friend={friendsMap[String(chat.FriendId)]}
        isActive={String(chat.MaCuocTroChuyen) === String(activeChatId)}
        onClick={onSelectChat}
      />
    ));
  };

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <>
      {/* Header */}
      <div className="p-4 border-b border-gray-200 flex-shrink-0">
        <h2 className="text-xl font-bold text-gray-800">Tin nhắn</h2>
      </div>

      {/* Search input */}
      <div className="p-2 border-b border-gray-200 flex-shrink-0">
        <Input
          prefix={<SearchOutlined className="text-gray-400" />}
          placeholder="Tìm kiếm"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          allowClear
          className="rounded-lg"
        />
      </div>

      {/* Scrollable list */}
      <div className="flex-1 overflow-y-auto">
        {renderContent()}
      </div>
    </>
  );
};

export default ChatList;
