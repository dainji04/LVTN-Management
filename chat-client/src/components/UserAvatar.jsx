import React from "react";

const sizeMap = {
  xs: "w-7 h-7 text-xs",
  sm: "w-9 h-9 text-sm",
  md: "w-11 h-11 text-base",
  lg: "w-14 h-14 text-lg",
  xl: "w-16 h-16 text-xl",
};

const dotSizeMap = {
  xs: "w-2 h-2 border",
  sm: "w-2.5 h-2.5 border",
  md: "w-3 h-3 border-2",
  lg: "w-3.5 h-3.5 border-2",
  xl: "w-4 h-4 border-2",
};

const UserAvatar = ({
  src,
  name = "",
  size = "md",
  online = false,
  showOnline = false,
  className = "",
  ringColor = "ring-alochat-pink",
  ring = false,
}) => {
  const initials = name
    .split(" ")
    .map((n) => n[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();

  return (
    <div className={`relative flex-shrink-0 ${className}`}>
      <div
        className={`
          ${sizeMap[size]} rounded-full overflow-hidden flex items-center justify-center
          bg-gradient-to-br from-pink-300 to-pink-500 text-white font-semibold
          ${ring ? `ring-2 ${ringColor} ring-offset-1` : ""}
        `}
      >
        {src ? (
          <img
            src={src}
            alt={name}
            className="w-full h-full object-cover"
            onError={(e) => { e.target.style.display = "none"; }}
          />
        ) : (
          <span>{initials}</span>
        )}
      </div>

      {showOnline && (
        <span
          className={`
            absolute bottom-0 right-0 rounded-full border-white
            ${dotSizeMap[size]}
            ${online ? "bg-green-400" : "bg-gray-300"}
          `}
        />
      )}
    </div>
  );
};

export default UserAvatar;
