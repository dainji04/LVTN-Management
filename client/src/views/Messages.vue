<template>
  <AuthLayout :hide-right-sidebar="true">
    <template #left-sidebar>
      <SideBar />
    </template>

    <!-- Chat List (Middle Column) -->
    <div class="w-80 bg-white border-r border-gray-200 flex flex-col">
      <div class="p-4 border-b border-gray-200">
        <h2 class="text-xl font-bold text-gray-800">{{ $t('messages') }}</h2>
      </div>
      <div class="p-2 border-b border-gray-200">
        <a-input
          v-model:value="searchQuery"
          :placeholder="$t('search')"
          class="rounded-lg"
        >
          <template #prefix>
            <SearchOutlined class="text-gray-400" />
          </template>
        </a-input>
      </div>
      <div class="flex-1 overflow-y-auto">
        <ChatListItem
          v-for="conversation in filteredConversations"
          :key="conversation.id"
          :conversation="conversation"
          :is-active="selectedConversationId === conversation.id"
          @select="selectConversation"
        />
      </div>
    </div>

    <!-- Chat Conversation (Right Column) -->
    <div class="flex-1 flex flex-col bg-white">
      <!-- Chat Header -->
      <div
        v-if="selectedConversation"
        class="p-4 border-b border-gray-200 flex items-center justify-between"
      >
        <div class="flex items-center gap-3">
          <div class="relative">
            <a-avatar :size="40" :src="selectedConversation.userAvatar">
              <template #icon>
                <UserOutlined />
              </template>
            </a-avatar>
            <div
              v-if="selectedConversation.isActive"
              class="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-white"
            ></div>
          </div>
          <div>
            <h3 class="font-semibold text-gray-800">{{ selectedConversation.userName }}</h3>
            <p v-if="selectedConversation.isActive" class="text-xs text-green-500">
              {{ $t('active') }}
            </p>
          </div>
        </div>
        <div class="flex items-center gap-4">
          <PhoneOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
          <VideoCameraOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
          <MoreOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
        </div>
      </div>

      <!-- Messages Area -->
      <div
        v-if="selectedConversation"
        class="flex-1 overflow-y-auto p-4 bg-gray-50"
        ref="messagesContainer"
      >
        <MessageBubble
          v-for="message in selectedConversation.messages"
          :key="message.id"
          :message="message"
          :current-user-id="currentUserId"
        />
      </div>

      <!-- Empty State -->
      <div
        v-else
        class="flex-1 flex items-center justify-center text-gray-400"
      >
        <div class="text-center">
          <MessageOutlined class="text-6xl mb-4" />
          <p class="text-lg">{{ $t('messages') }}</p>
        </div>
      </div>

      <!-- Input Field -->
      <div
        v-if="selectedConversation"
        class="p-4 border-t border-gray-200 bg-white"
      >
        <div class="flex items-center gap-2">
          <CameraOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
          <a-input
            v-model:value="messageInput"
            :placeholder="$t('typeMessage')"
            class="flex-1 rounded-full"
            @keyup.enter="sendMessage"
          />
          <PictureOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
          <SmileOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
          <Button
            type="primary"
            classes="rounded-full"
            :isDisabled="!messageInput.trim()"
            @click="sendMessage"
          >
            <template #icon>
              <SendOutlined class="mr-1" />
            </template>
          </Button>
        </div>
      </div>
    </div>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { ref, computed, nextTick, onMounted } from 'vue';
import {
  SearchOutlined,
  MessageOutlined,
  UserOutlined,
  PhoneOutlined,
  VideoCameraOutlined,
  MoreOutlined,
  CameraOutlined,
  PictureOutlined,
  SmileOutlined,
  SendOutlined,
} from '@ant-design/icons-vue';
import AuthLayout from '../layouts/authLayout.vue';
import SideBar from '../components/SideBar.vue';
import Button from '../components/Button.vue';
import ChatListItem from '../components/ChatListItem.vue';
import MessageBubble from '../components/MessageBubble.vue';
import type { Conversation, Message } from '../types/messageType';

const currentUserId = ref('current-user-1');
const searchQuery = ref('');
const selectedConversationId = ref<string | null>(null);
const messageInput = ref('');
const messagesContainer = ref<HTMLElement | null>(null);

const conversations = ref<Conversation[]>([
  {
    id: '1',
    userId: 'user-1',
    userName: 'Minh',
    userAvatar: 'https://testingbot.com/free-online-tools/random-avatar/100',
    lastMessage: 'Đang hoạt động',
    lastMessageTime: '09:20',
    unreadCount: 0,
    isActive: true,
    messages: [],
  },
  {
    id: '2',
    userId: 'user-2',
    userName: 'Quang',
    userAvatar: 'https://testingbot.com/free-online-tools/random-avatar/101',
    lastMessage: 'Minh đến lúc 9h sáng, đang ở đây rồi nè',
    lastMessageTime: '09:21',
    unreadCount: 0,
    isActive: true,
    messages: [
      {
        id: 'm1',
        content: 'Mấy giờ cậu đến skate park vậy?',
        senderId: currentUserId.value,
        receiverId: 'user-2',
        timestamp: new Date(Date.now() - 30 * 60000).toISOString(),
        isRead: true,
      },
      {
        id: 'm2',
        content: 'Minh đến lúc 9h sáng, đang ở đây rồi nè',
        senderId: 'user-2',
        receiverId: currentUserId.value,
        timestamp: new Date(Date.now() - 25 * 60000).toISOString(),
        isRead: true,
      },
      {
        id: 'm3',
        content: 'Trời đẹp quá! 👍',
        senderId: currentUserId.value,
        receiverId: 'user-2',
        timestamp: new Date(Date.now() - 24 * 60000).toISOString(),
        isRead: true,
      },
      {
        id: 'm4',
        content: 'Trời đẹp quá!',
        senderId: 'user-2',
        receiverId: currentUserId.value,
        timestamp: new Date(Date.now() - 24 * 60000).toISOString(),
        image: 'https://images.unsplash.com/photo-1571008887538-b36bb32f4571?w=400&h=300&fit=crop',
        isRead: true,
      },
      {
        id: 'm5',
        content: 'Đỉnh thè, cậu biểu diễn máy trick nào rồi?',
        senderId: currentUserId.value,
        receiverId: 'user-2',
        timestamp: new Date(Date.now() - 20 * 60000).toISOString(),
        isRead: true,
      },
      {
        id: 'm6',
        content: 'Minh vừa thử ollie mới rồi landing đẹp cực này haha! 😊',
        senderId: 'user-2',
        receiverId: currentUserId.value,
        timestamp: new Date(Date.now() - 20 * 60000).toISOString(),
        isRead: true,
      },
    ],
  },
  {
    id: '3',
    userId: 'user-3',
    userName: 'Vy',
    userAvatar: 'https://testingbot.com/free-online-tools/random-avatar/102',
    lastMessage: 'Có nhé',
    lastMessageTime: '25 phút',
    unreadCount: 2,
    isActive: false,
    messages: [],
  },
  {
    id: '4',
    userId: 'user-4',
    userName: 'Thanh',
    userAvatar: 'https://testingbot.com/free-online-tools/random-avatar/103',
    lastMessage: 'Haha đúng rồi, đi sớm chút cho đó',
    lastMessageTime: '2 giờ',
    unreadCount: 0,
    isActive: false,
    messages: [],
  },
  {
    id: '5',
    userId: 'user-5',
    userName: 'Lan',
    userAvatar: 'https://testingbot.com/free-online-tools/random-avatar/104',
    lastMessage: 'Ok',
    lastMessageTime: '5 giờ',
    unreadCount: 0,
    isActive: false,
    messages: [],
  },
  {
    id: '6',
    userId: 'user-6',
    userName: 'Linh',
    userAvatar: 'https://testingbot.com/free-online-tools/random-avatar/105',
    lastMessage: 'Hello',
    lastMessageTime: 'Hôm qua',
    unreadCount: 0,
    isActive: false,
    messages: [],
  },
  {
    id: '7',
    userId: 'user-7',
    userName: 'Minhvn',
    userAvatar: 'https://testingbot.com/free-online-tools/random-avatar/106',
    lastMessage: 'Hi',
    lastMessageTime: 'Thứ 6',
    unreadCount: 0,
    isActive: false,
    messages: [],
  },
]);

const filteredConversations = computed(() => {
  if (!searchQuery.value.trim()) {
    return conversations.value;
  }
  const query = searchQuery.value.toLowerCase();
  return conversations.value.filter((conv) =>
    conv.userName.toLowerCase().includes(query)
  );
});

const selectedConversation = computed(() => {
  if (!selectedConversationId.value) return null;
  return conversations.value.find((c) => c.id === selectedConversationId.value) || null;
});

// const unreadTotal = computed(() => {
//   return conversations.value.reduce((sum, conv) => sum + conv.unreadCount, 0);
// });

const selectConversation = (conversationId: string) => {
  selectedConversationId.value = conversationId;
  // Mark as read
  const conversation = conversations.value.find((c) => c.id === conversationId);
  if (conversation) {
    conversation.unreadCount = 0;
  }
  scrollToBottom();
};

const sendMessage = () => {
  if (!messageInput.value.trim() || !selectedConversationId.value) return;

  const conversation = conversations.value.find(
    (c) => c.id === selectedConversationId.value
  );
  if (!conversation) return;

  const newMessage: Message = {
    id: `m${Date.now()}`,
    content: messageInput.value,
    senderId: currentUserId.value,
    receiverId: conversation.userId,
    timestamp: new Date().toISOString(),
    isRead: false,
  };

  conversation.messages.push(newMessage);
  conversation.lastMessage = messageInput.value;
  conversation.lastMessageTime = new Date().toLocaleTimeString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
  });

  messageInput.value = '';
  scrollToBottom();
};

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

onMounted(() => {
  // Auto-select first conversation
  if (conversations.value.length > 0) {
    selectedConversationId.value = conversations.value[1]?.id || null; // Select Quang
  }
});
</script>

<style scoped>
.messages-container {
  font-family: system-ui, -apple-system, sans-serif;
}

/* Custom scrollbar */
:deep(.ant-input) {
  border-radius: 9999px;
}

/* Scrollbar styling */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>

