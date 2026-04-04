import i18n from "../i18n/i18n.js";
import { useTranslation } from "react-i18next";
import { Globe,  LogOut } from "lucide-react";

export const ChangeLanguage = ({ onLogout }) => {

  const { i18n: i18next, t } = useTranslation();


  const lang = i18next.language;   // en | vi

  const changeLang = (lng) => {
    i18n.changeLanguage(lng);
    localStorage.setItem("lang", lng);
  };

  return (
    <div className="flex flex-col gap-2 p-4 border-t border-gray-200 mt-auto w-full max-w-xs bg-white">

      <button
        className="flex items-center justify-between w-full px-3 py-2 text-sm font-medium text-gray-700 transition-colors rounded-md hover:bg-gray-100"
        onClick={() => changeLang(lang === "vi" ? "en" : "vi")}
      >
        <div className="flex items-center gap-3">
          <Globe className="w-5 h-5 text-gray-500" />
          <span>
            {lang === "vi" ? "Tiếng Việt" : "English"}
          </span>
        </div>
      </button>

      <button
        className="flex items-center w-full px-3 py-2 text-sm font-medium text-red-600 rounded-md hover:bg-red-50"
        onClick={onLogout}
      >
        <LogOut className="w-5 h-5 mr-3" />
        <span>{t("logout")}</span>
      </button>

    </div>
  );
};