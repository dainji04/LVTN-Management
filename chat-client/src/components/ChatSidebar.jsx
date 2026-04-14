import React, { useState } from "react";
import { Avatar } from "antd";
import {
  HomeOutlined, SearchOutlined, TeamOutlined, CompassOutlined,
  PlayCircleOutlined, MessageOutlined, BellOutlined,
  PlusCircleOutlined, UserOutlined, MenuOutlined,
} from "@ant-design/icons";
import { ChangeLanguage } from "./ChangeLanguage.jsx";
import { useTranslation } from "react-i18next";


const NAV_ITEMS = [
  { icon: <HomeOutlined />,        label: "home",     path: "/" },
  { icon: <SearchOutlined />,      label: "search",      path: "/search" },
  { icon: <TeamOutlined />,        label: "groups",          path: "/groups" },
  { icon: <CompassOutlined />,     label: "explore",      path: "/explore" },
  { icon: <PlayCircleOutlined />,  label: "reels",         path: "/reels" },
  { icon: <MessageOutlined />,     label: "messages",      path: "/messages" },
  { icon: <BellOutlined />,        label: "notifications",     path: "/notifications", badge: 2 },
  { icon: <PlusCircleOutlined />,  label: "create",           path: "/create" },
  { icon: <UserOutlined />,        label: "profile", path: "/profile" },
];

const ChatSidebar = ({ currentUser, onLogout, activePath = "/messages" }) => {
  const { t } = useTranslation();

  const [active, setActive] = useState(activePath);

  return (
    <div className="sidebar w-64 bg-white border-r border-gray-200 flex flex-col left-0 top-0 h-full overflow-y-auto flex-shrink-0">
      {/* Logo */}
      <div className="p-6">
        <img
          src="/logo.jpg"
          alt="AloChat"
          className="logo-img w-10 h-10 rounded-lg object-cover"
          onError={e => {
            e.target.style.display = "none";
            e.target.nextSibling.style.display = "flex";
          }}
        />
        <div
          className="w-10 h-10 rounded-lg items-center justify-center hidden"
          style={{ background: "linear-gradient(135deg,#F96B8E,#F4517A)", display: "none" }}
        >
          <span className="text-white font-bold text-sm">AC</span>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-4">
        {NAV_ITEMS.map(({ icon, label, path, badge }) => (
          <a
            key={path}
            href={path}
            onClick={e => { e.preventDefault(); setActive(path); }}
            className={`nav-item flex items-center py-3 px-4 rounded-lg transition-colors text-black ${active === path ? "active" : ""}`}
          >
            <span className="text-xl mr-3 anticon">{icon}</span>
            <span className="font-medium">{t(label)}</span>
            {badge > 0 && (
              <span className="ml-auto bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                {badge}
              </span>
            )}
          </a>
        ))}
      </nav>
        
      {/* User profile */}
      <div className="p-4 border-t border-gray-200">
        <div className="group-user flex items-center space-x-3">
          <Avatar
            src={currentUser?.avatar || "https://testingbot.com/free-online-tools/random-avatar/100"}
            size={40}
            className="object-cover cursor-pointer"
          />
          <div className="flex flex-col justify-center">
            <span className="text-sm font-semibold">
              {currentUser?.name || currentUser?.username || "Username"}
            </span>
            <p className="text-primary text-xs cursor-pointer">{t("profile")}</p>
          </div>
        </div>

        <ChangeLanguage onLogout={onLogout} />
        
      </div>
    </div>
  );
};

export default ChatSidebar;
