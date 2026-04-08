import i18n from "../i18n/i18n.js";

export const formatTime = (date) => {
  if (!date) return "";
  const d = typeof date === "string" ? new Date(date) : date;
  const hours = d.getHours().toString().padStart(2, "0");
  const minutes = d.getMinutes().toString().padStart(2, "0");
  return `${hours}:${minutes}`;
};

export const formatChatTime = (date) => {
  if (!date) return "";
  const inputDate = typeof date === "string" ? new Date(date) : date;
  const d =  new Date(inputDate.getTime() + 7 * 3600000) // Bù giờ GMT+7 để so sánh đúng giờ Việt Nam
  const now = new Date();
  const diffMs = now - d; 
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMins < 1) return i18n.t("time.just_now");
  if (diffMins < 60) return `${diffMins} ${i18n.t("time.minutes")}`;
  if (diffHours < 24) return `${diffHours} ${i18n.t("time.hours")}`;
  if (diffDays === 1) return i18n.t("time.yesterday");

  const days = [ i18n.t("days.sun"),
    i18n.t("days.mon"),
    i18n.t("days.tue"),
    i18n.t("days.wed"),
    i18n.t("days.thu"),
    i18n.t("days.fri"),
    i18n.t("days.sat")
  ];
  if (diffDays < 7) return days[d.getDay()];

  return `${d.getDate()}/${d.getMonth() + 1}`;
};

export const formatMessageTime = (date) => {
  if (!date) return "";
  const inputDate = typeof date === "string" ? new Date(date) : date;
  const d =  new Date(inputDate.getTime() + 7 * 3600000) // Bù giờ GMT+7 để hiển thị đúng giờ Việt Nam
  return formatTime(d); 
};
