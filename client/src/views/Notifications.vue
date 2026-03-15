<template>
  <AuthLayout>
    <template #left-sidebar>
      <SideBar />
    </template>

    <!-- Main Content -->
    <main class="main-content w-full flex-1 overflow-y-auto bg-[#f5f5f5]">
      <div class="max-w-4xl mx-auto px-4 py-6">
        <!-- Header -->
        <div class="bg-white rounded-lg shadow-sm mb-6 p-4">
          <div class="flex items-center justify-between mb-4">
            <h1 class="text-2xl font-bold text-gray-800">{{ $t('notifications') }}</h1>
            <div class="flex items-center gap-3">
              <CheckCircleOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
              <MoreOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
              <BellOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
            </div>
          </div>

          <!-- Tabs -->
          <a-tabs v-model:activeKey="activeTab" class="notification-tabs">
            <a-tab-pane key="all" :tab="$t('all')">
              <template #tab>
                <span class="tab-label">{{ $t('all') }}</span>
              </template>
            </a-tab-pane>
            <a-tab-pane key="unread" :tab="$t('unread')">
              <template #tab>
                <span class="tab-label">{{ $t('unread') }}</span>
              </template>
            </a-tab-pane>
          </a-tabs>
        </div>

        <!-- Notification List -->
        <div class="space-y-3 mb-6">
          <NotificationItem
            v-for="notification in filteredNotifications"
            :key="notification.id"
            :notification="notification"
            @accept="handleAcceptFriend"
            @decline="handleDeclineFriend"
          />
        </div>

        <!-- Load More Button -->
        <div class="text-center">
          <Button
            type="default"
            classes="border-primary text-primary hover:bg-primary hover:text-white"
            @click="loadMore"
          >
            {{ $t('viewMoreOldNotifications') }}
          </Button>
        </div>
      </div>
    </main>

    <!-- Right Sidebar -->
    <template #right-sidebar>
      <div class="p-6 space-y-6">
        <!-- Friend Suggestions -->
        <div class="bg-white rounded-lg p-4 shadow-sm">
          <h3 class="font-semibold text-lg mb-4 text-gray-800">{{ $t('friendSuggestions') }}</h3>
          <div class="space-y-4">
            <div
              v-for="suggestion in friendSuggestions"
              :key="suggestion.id"
              class="flex items-center justify-between"
            >
              <div class="flex items-center gap-3">
                <a-avatar
                  :size="40"
                  :style="{ backgroundColor: suggestion.avatarColor }"
                >
                  <UserOutlined class="text-white" />
                </a-avatar>
                <div>
                  <h4 class="font-semibold text-sm text-gray-800">{{ suggestion.name }}</h4>
                  <p class="text-xs text-gray-500">{{ suggestion.mutualFriends }} {{ $t('mutualFriends') }}</p>
                </div>
              </div>
              <Button type="text" classes="text-primary hover:text-primary/80">
                {{ $t('add') }}
              </Button>
            </div>
          </div>
        </div>

        <!-- Trending Topics -->
        <div class="bg-white rounded-lg p-4 shadow-sm">
          <h3 class="font-semibold text-lg mb-4 text-gray-800">{{ $t('trendingForYou') }}</h3>
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <div>
                <a-tag color="primary" class="mb-1">#AloChatDesign</a-tag>
                <p class="text-xs text-gray-500">1.2k {{ $t('posts') }}</p>
              </div>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <a-tag color="primary" class="mb-1">#TailwindCSS</a-tag>
                <p class="text-xs text-gray-500">850 {{ $t('posts') }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import {
  CheckCircleOutlined,
  MoreOutlined,
  BellOutlined,
  UserOutlined,
} from '@ant-design/icons-vue';
import AuthLayout from '../layouts/authLayout.vue';
import SideBar from '../components/SideBar.vue';
import Button from '../components/Button.vue';
import NotificationItem from '../components/NotificationItem.vue';
import type { Notification } from '../types/notificationType';
import { notificationHelper } from '../helpers/notificationHelper';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const activeTab = ref('all');

const notifications = ref<Notification[]>([
  {
    id: '1',
    type: 'like',
    user: {
      name: 'Nguyễn Văn A',
      avatar: 'https://testingbot.com/free-online-tools/random-avatar/100',
    },
    content: ` ${t('likedYourPost')}`,
    postTitle: 'Hành trình khám phá Đà Lạt 2024...',
    timeAgo: `2 ${t('minutesAgo')}`,
    isRead: false,
  },
  {
    id: '2',
    type: 'comment',
    user: {
      name: 'Trần Thị B',
      avatarColor: '#1890ff',
      avatarIcon: 'comment',
    },
    content: ` ${t('commentedInGroup')}: 'Món này trông ngon quá bạn ơi, chia sẻ công thức nhé!'`,
    groupName: 'Yêu Bếp',
    timeAgo: `15 ${t('minutesAgo')}`,
    isRead: true,
  },
  {
    id: '3',
    type: 'friend_request',
    user: {
      name: 'Lê Minh C',
      avatarColor: '#ff4d4f',
      avatarIcon: 'userAdd',
    },
    content: ` ${t('sentFriendRequest')}`,
    timeAgo: `1 ${t('hoursAgo')}`,
    isRead: false,
  },
  {
    id: '4',
    type: 'mention',
    user: {
      name: 'Phạm Diệu Linh',
      avatarColor: '#52c41a',
      avatarIcon: 'book',
    },
    content: ` ${t('mentionedYou')}: 'Bạn thấy ý tưởng này thế nào?'`,
    mentionedUser: '@Hoàng Nam',
    timeAgo: `3 ${t('hoursAgo')}`,
    isRead: true,
  },
  {
    id: '5',
    type: 'new_post',
    user: {
      name: 'Hoàng Anh Tuấn',
      avatarColor: '#52c41a',
      avatarIcon: 'appstore',
    },
    content: ` ${t('postedInGroup')}.`,
    groupName: 'Cộng đồng UI/UX Việt Nam',
    timeAgo: `5 ${t('hoursAgo')}`,
    isRead: true,
  },
]);

const friendSuggestions = ref([
  {
    id: '1',
    name: 'Đặng Văn Khoa',
    avatarColor: '#d9d9d9',
    mutualFriends: 12,
  },
  {
    id: '2',
    name: 'Mai Phương Thảo',
    avatarColor: '#b7eb8f',
    mutualFriends: 8,
  },
]);

const filteredNotifications = computed(() => {
  if (activeTab.value === 'unread') {
    return notifications.value.filter((n) => !n.isRead);
  }
  return notifications.value;
});

const handleAcceptFriend = (notificationId: string) => {
  notificationHelper('success', t('accept') + ' ' + t('loginSuccess'));
  // Handle accept friend request
  const notification = notifications.value.find((n) => n.id === notificationId);
  if (notification) {
    notification.isRead = true;
  }
};

const handleDeclineFriend = (notificationId: string) => {
  // Handle decline friend request
  const index = notifications.value.findIndex((n) => n.id === notificationId);
  if (index !== -1) {
    notifications.value.splice(index, 1);
  }
};

const loadMore = () => {
  // Handle load more notifications
  console.log('Loading more notifications...');
};
</script>

<style scoped>
.notification-tabs :deep(.ant-tabs-tab) {
  padding: 12px 16px;
  font-weight: 500;
}

.notification-tabs :deep(.ant-tabs-tab-active) {
  color: #FF436D;
}

.notification-tabs :deep(.ant-tabs-ink-bar) {
  background: #FF436D;
}

.tab-label {
  font-size: 15px;
}

.main-content {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.main-content::-webkit-scrollbar {
  display: none;
}
</style>

