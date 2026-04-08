export const formatTime = (date) => {
  if (!date) return "";
  const d = typeof date === "string" ? new Date(date) : date;
  const hours = d.getHours().toString().padStart(2, "0");
  const minutes = d.getMinutes().toString().padStart(2, "0");
  return `${hours}:${minutes}`;
};

export const formatChatTime = (date) => {
  if (!date) return "";
  const d = typeof date === "string" ? new Date(date) : date;
  const now = new Date();
  const diffMs = now - d;
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMins < 1) return "Vừa xong";
  if (diffMins < 60) return `${diffMins} phút`;
  if (diffHours < 24) return `${diffHours} giờ`;
  if (diffDays === 1) return "Hôm qua";

  const days = ["CN", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"];
  if (diffDays < 7) return days[d.getDay()];

  return `${d.getDate()}/${d.getMonth() + 1}`;
};

export const formatMessageTime = (date) => {
  if (!date) return "";
  const d = typeof date === "string" ? new Date(date) : date;
  return formatTime(d);
};
