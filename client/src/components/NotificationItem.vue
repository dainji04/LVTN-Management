<template>
  <div
    class="notification-item bg-gray-50 rounded-lg p-4 mb-3 flex items-start gap-3 hover:bg-gray-100 transition-colors"
    :class="{ 'bg-white': !notification.isRead }"
  >
    <!-- Avatar -->
    <div class="flex-shrink-0">
      <a-avatar
        v-if="notification.user.avatar"
        :size="48"
        :src="notification.user.avatar"
        class="border-2 border-white"
      >
        <template #icon>
          <UserOutlined />
        </template>
      </a-avatar>
      <a-avatar
        v-else
        :size="48"
        :style="{ backgroundColor: notification.user.avatarColor || '#1890ff' }"
        class="border-2 border-white"
      >
        <component
          v-if="notification.user.avatarIcon"
          :is="getIcon(notification.user.avatarIcon)"
          class="text-white text-xl"
        />
        <UserOutlined v-else class="text-white text-xl" />
      </a-avatar>
    </div>

    <!-- Content -->
    <div class="flex-1 min-w-0">
      <p class="text-gray-800 text-sm leading-relaxed">
        <span class="font-semibold">{{ notification.user.name }}</span>
        {{ notification.content }}
        <span v-if="notification.groupName" class="text-primary font-semibold">
          **{{ notification.groupName }}**
        </span>
        <span v-if="notification.mentionedUser" class="text-primary font-semibold">
          {{ notification.mentionedUser }}
        </span>
        <span v-if="notification.postTitle" class="text-gray-600 italic">
          '{{ notification.postTitle }}'
        </span>
      </p>
      <p class="text-gray-500 text-xs mt-1">{{ notification.timeAgo }}</p>

      <!-- Friend Request Actions -->
      <div v-if="notification.type === 'friend_request'" class="flex gap-2 mt-3">
        <Button
          type="primary"
          size="small"
          @click="handleAccept"
        >
          {{ $t('accept') }}
        </Button>
        <Button
          type="default"
          size="small"
          @click="handleDecline"
        >
          {{ $t('decline') }}
        </Button>
      </div>
    </div>

    <!-- Unread Indicator -->
    <div v-if="!notification.isRead" class="flex-shrink-0">
      <div class="w-2 h-2 bg-red-500 rounded-full"></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { UserOutlined, HeartOutlined, CommentOutlined, UserAddOutlined, BookOutlined, AppstoreOutlined } from '@ant-design/icons-vue';
import Button from './Button.vue';
import type { Notification } from '../types/notificationType';

const props = defineProps<{
  notification: Notification;
}>();

const emit = defineEmits<{
  accept: [notificationId: string];
  decline: [notificationId: string];
}>();

const getIcon = (iconName: string) => {
  const icons: Record<string, any> = {
    heart: HeartOutlined,
    comment: CommentOutlined,
    userAdd: UserAddOutlined,
    book: BookOutlined,
    appstore: AppstoreOutlined,
  };
  return icons[iconName] || UserOutlined;
};

const handleAccept = () => {
  emit('accept', props.notification.id);
};

const handleDecline = () => {
  emit('decline', props.notification.id);
};
</script>

<style scoped>
.notification-item {
  transition: background-color 0.2s;
}
</style>

