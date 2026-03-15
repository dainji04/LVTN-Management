<template>
  <div
    class="chat-list-item p-4 flex items-center gap-3 cursor-pointer hover:bg-gray-50 transition-colors rounded-lg"
    :class="{ 'bg-pink-50': isActive }"
    @click="$emit('select', conversation.id)"
  >
    <!-- Avatar -->
    <div class="relative flex-shrink-0">
      <a-avatar :size="50" :src="conversation.userAvatar">
        <template #icon>
          <UserOutlined />
        </template>
      </a-avatar>
      <div
        v-if="conversation.isActive"
        class="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-white"
      ></div>
    </div>

    <!-- Content -->
    <div class="flex-1 min-w-0">
      <div class="flex items-center justify-between mb-1">
        <h3 class="font-semibold text-gray-800 truncate">{{ conversation.userName }}</h3>
        <span class="text-xs text-gray-500 flex-shrink-0 ml-2">{{ formatTime(conversation.lastMessageTime) }}</span>
      </div>
      <div class="flex items-center justify-between">
        <p class="text-sm text-gray-600 truncate">{{ conversation.lastMessage }}</p>
        <div
          v-if="conversation.unreadCount > 0"
          class="bg-primary text-white text-xs rounded-full w-5 h-5 flex items-center justify-center flex-shrink-0 ml-2"
        >
          {{ conversation.unreadCount }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { UserOutlined } from '@ant-design/icons-vue';
import type { Conversation } from '../types/messageType';

const props = defineProps<{
  conversation: Conversation;
  isActive: boolean;
}>();

defineEmits<{
  select: [conversationId: string];
}>();

const formatTime = (time: string) => {
  // Handle different time formats
  if (time.includes('phút') || time.includes('giờ') || time.includes('Hôm qua') || time.includes('Thứ')) {
    return time;
  }
  // Format as HH:MM if it's a time string
  return time;
};
</script>

<style scoped>
.chat-list-item {
  transition: background-color 0.2s;
}
</style>

