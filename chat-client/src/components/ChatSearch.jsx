import React from "react";

const SearchIcon = () => (
  <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <circle cx="11" cy="11" r="8" />
    <path d="M21 21l-4.35-4.35" strokeLinecap="round" />
  </svg>
);

const ClearIcon = () => (
  <svg className="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
    <path d="M18 6L6 18M6 6l12 12" strokeLinecap="round" />
  </svg>
);

/**
 * ChatSearch — controlled input.
 *
 * @param {string}   value      - Giá trị hiện tại (controlled)
 * @param {Function} onSearch   - Callback khi giá trị thay đổi
 * @param {string}   placeholder
 */
const ChatSearch = ({ value = "", onSearch, placeholder = "Tìm kiếm" }) => {
  const handleChange = (e) => onSearch?.(e.target.value);
  const handleClear  = () => onSearch?.("");

  return (
    <div className="relative flex items-center">
      <span className="absolute left-3 text-gray-400 pointer-events-none">
        <SearchIcon />
      </span>
      <input
        type="text"
        value={value}
        onChange={handleChange}
        placeholder={placeholder}
        className="
          w-full pl-9 pr-8 py-2.5 bg-gray-50 border border-gray-100
          rounded-full text-sm text-gray-700 placeholder-gray-400
          outline-none focus:border-pink-300 focus:ring-2 focus:ring-pink-100
          transition-all duration-200
        "
      />
      {value && (
        <button
          onClick={handleClear}
          className="absolute right-3 text-gray-400 hover:text-gray-600 transition-colors"
        >
          <ClearIcon />
        </button>
      )}
    </div>
  );
};

export default ChatSearch;
