import React from "react";

const TypingIndicator = () => (
  <div className="flex mb-4 justify-start">
    <div className="px-4 py-3 rounded-2xl bg-white text-gray-800 flex items-center gap-1 shadow-sm">
      {[0, 1, 2].map(i => (
        <span
          key={i}
          className="w-2 h-2 bg-gray-400 rounded-full animate-bounce"
          style={{ animationDelay: `${i * 150}ms` }}
        />
      ))}
    </div>
  </div>
);

export default TypingIndicator;
