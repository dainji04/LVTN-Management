import React, { useState, useEffect } from "react";
import { ConfigProvider } from "antd";
import viVN from "antd/locale/vi_VN";
import LoginPage from "./pages/LoginPage";
import ChatPage from "./pages/ChatPage";
import ChatProvider from "./context/ChatProvider.jsx"

// Ant Design theme override — primary = #F4517A
const antdTheme = {
  token: {
    colorPrimary: "#F4517A",
    colorPrimaryHover: "#E03A65",
    borderRadius: 8,
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
  },
};

function App() {
  const [token, setToken] = useState(() => localStorage.getItem("Token") || "");

  // Cross-tab sync
  useEffect(() => {
    const sync = () => setToken(localStorage.getItem("Token") || "");
    window.addEventListener("storage", sync);
    return () => window.removeEventListener("storage", sync);
  }, []);

  const handleLoginSuccess = (newToken) => setToken(newToken);

  const handleLogout = () => {
    localStorage.removeItem("Token");
    setToken("");
  };

  return (
    <ConfigProvider theme={antdTheme} locale={viVN}>
      {!token ? (
        <LoginPage onLoginSuccess={handleLoginSuccess} />
      ) : (
        <ChatProvider token={token}>
          <ChatPage onLogout={handleLogout} />
        </ChatProvider>
      )}
    </ConfigProvider>
  );
}

export default App;
