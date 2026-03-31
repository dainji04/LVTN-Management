import React from "react";

const HomeIcon = ({ active }) => (
  <svg className="w-5 h-5" viewBox="0 0 24 24" fill={active ? "currentColor" : "none"} stroke="currentColor" strokeWidth="1.8">
    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
  </svg>
);
const ExploreIcon = () => (
  <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
    <circle cx="12" cy="12" r="10" /><path d="M16.24 7.76l-2.12 6.36-6.36 2.12 2.12-6.36 6.36-2.12z" />
  </svg>
);
const ReelsIcon = () => (
  <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
    <rect x="2" y="2" width="20" height="20" rx="3" /><path d="M17 12l-8-5v10l8-5z" />
  </svg>
);
const MessageIcon = ({ active, badge }) => (
  <div className="relative">
    <svg className="w-5 h-5" viewBox="0 0 24 24" fill={active ? "currentColor" : "none"} stroke="currentColor" strokeWidth="1.8">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
    </svg>
    {badge > 0 && (
      <span className="absolute -top-1.5 -right-1.5 w-3.5 h-3.5 bg-pink-500 text-white text-[8px] font-bold rounded-full flex items-center justify-center">
        {badge}
      </span>
    )}
  </div>
);

const MobileNav = ({ activeTab, onTabChange, unreadCount = 0 }) => (
  <nav className="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 flex items-center justify-around px-4 py-2 z-20 safe-area-pb">
    {[
      { id: "home", icon: <HomeIcon active={activeTab === "home"} />, label: "Trang chủ" },
      { id: "explore", icon: <ExploreIcon />, label: "Khám phá" },
      { id: "reels", icon: <ReelsIcon />, label: "Reels" },
      { id: "messages", icon: <MessageIcon active={activeTab === "messages"} badge={unreadCount} />, label: "Tin nhắn" },
    ].map(({ id, icon, label }) => (
      <button
        key={id}
        onClick={() => onTabChange(id)}
        className={`flex flex-col items-center gap-1 px-3 py-1 rounded-xl transition-colors ${
          activeTab === id ? "text-pink-500" : "text-gray-400 hover:text-gray-600"
        }`}
      >
        {icon}
        <span className="text-[10px] font-medium">{label}</span>
      </button>
    ))}
  </nav>
);

export default MobileNav;
