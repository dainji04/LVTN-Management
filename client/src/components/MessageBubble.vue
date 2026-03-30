<template>
  <div
    class="message-bubble flex mb-4"
    :class="{ 'justify-end': isSent, 'justify-start': !isSent }"
  >
    <div
      class="message-content max-w-xs lg:max-w-md px-4 py-2 rounded-2xl"
      :class="isSent ? 'bg-primary text-white' : 'bg-white text-gray-800'"
    >
      <p v-if="message.image" class="mb-2">
        <img
          :src="message.image"
          :alt="message.content"
          class="rounded-lg max-w-full h-auto"
        />
      </p>
      <p class="text-sm whitespace-pre-wrap">{{ message.content }}</p>
      <p
        class="text-xs mt-1"
        :class="isSent ? 'text-white/70' : 'text-gray-500'"
      >
        {{ formatTime(message.timestamp) }}
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { Message } from '../types/messageType';
import { computed } from 'vue';

const props = defineProps<{
  message: Message;
  currentUserId: string;
}>();

const isSent = computed(() => props.message.senderId === props.currentUserId);

const formatTime = (timestamp: string) => {
  const date = new Date(timestamp);
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
};
</script>

<style scoped>
.message-content {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}
</style>

