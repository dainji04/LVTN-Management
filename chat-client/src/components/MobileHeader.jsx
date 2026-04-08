import React from "react";

const SearchIcon = () => (
  <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <circle cx="11" cy="11" r="8" /><path d="M21 21l-4.35-4.35" strokeLinecap="round" />
  </svg>
);

const MobileHeader = ({ activeTab, onTabChange }) => {
  const TABS = [
    { id: "home", label: "Trang chủ" },
    { id: "explore", label: "Khám phá" },
    { id: "reels", label: "Reels" },
    { id: "messages", label: "Tin nhắn" },
  ];

  return (
    <header className="md:hidden bg-white border-b border-gray-100 sticky top-0 z-20">
      <div className="flex items-center justify-between px-4 py-2">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-pink-500 rounded-xl flex items-center justify-center">
            <svg viewBox="0 0 24 24" className="w-4.5 h-4.5 text-white" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12c0 1.85.5 3.58 1.37 5.07L2 22l4.93-1.37A9.96 9.96 0 0 0 12 22c5.52 0 10-4.48 10-10S17.52 2 12 2z" />
            </svg>
          </div>
          <span className="text-lg font-bold text-pink-500">AloChat</span>
        </div>
        <button className="text-gray-500 hover:text-pink-500 transition-colors">
          <SearchIcon />
        </button>
      </div>

      {/* Tab strip */}
      <div className="flex px-4 gap-4 pb-2 overflow-x-auto scrollbar-none">
        {TABS.map(({ id, label }) => (
          <button
            key={id}
            onClick={() => onTabChange(id)}
            className={`flex-shrink-0 text-sm pb-1 border-b-2 transition-colors font-medium ${
              activeTab === id
                ? "text-pink-500 border-pink-500"
                : "text-gray-500 border-transparent"
            }`}
          >
            {label}
          </button>
        ))}
      </div>
    </header>
  );
};

export default MobileHeader;
