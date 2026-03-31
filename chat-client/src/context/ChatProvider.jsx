import ChatContext from "./ChatContext";
import useChat from "../hooks/useChat";

const ChatProvider = ({ token, children }) => {
  const chat = useChat(token);

  return (
    <ChatContext.Provider value={chat} >
      {children}
    </ChatContext.Provider>
  );
};


export default ChatProvider;