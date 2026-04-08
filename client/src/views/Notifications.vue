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
        <div v-if="isLoading" class="flex justify-center py-8">
          <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-primary"></div>
        </div>
        <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
          <p class="text-red-700">{{ error }}</p>
        </div>
        <div v-else-if="filteredNotifications.length === 0" class="bg-white rounded-lg p-8 text-center text-gray-500">
          {{ $t('noNotifications') }}
        </div>
        <div v-else class="space-y-3 mb-6">
          <NotificationItem
            v-for="notification in filteredNotifications"
            :key="notification.id"
            :notification="notification"
            @accept="handleAcceptFriend"
            @decline="handleDeclineFriend"
          />
        </div>

        <!-- Load More Button -->
        <div v-if="filteredNotifications.length > 0" class="text-center">
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
import { ref, computed, onMounted } from 'vue';
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
import axiosInstance from '../helpers/apiHelper';
import { useAuthStore } from '../store/authStore';

const { t } = useI18n();
const authStore = useAuthStore();

const activeTab = ref('all');
const notifications = ref<Notification[]>([]);
const isLoading = ref(false);
const error = ref('');

interface NotificationApiItem {
  id: number;
  maNguoiHanhDong: number;
  maNguoiNhan: number;
  loaiHanhDong: string;
  maDoiTuong: number;
  loaiDoiTuong: string;
  daDoc: boolean;
  ngayTao: string;
}

interface NotificationApiResponse {
  code: number;
  message: string | null;
  data: NotificationApiItem[];
}

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

const normalizeType = (actionType: string): Notification['type'] => {
  const key = (actionType || '').toLowerCase();
  if (key.includes('like') || key.includes('thich')) return 'like';
  if (key.includes('comment') || key.includes('binh_luan') || key.includes('binhluan')) return 'comment';
  if (key.includes('friend') || key.includes('ket_ban') || key.includes('ketban')) return 'friend_request';
  if (key.includes('mention') || key.includes('nhac')) return 'mention';
  return 'new_post';
};

const formatTimeAgo = (date: string): string => {
  const createdAt = new Date(date);
  const now = new Date();
  const diffMs = now.getTime() - createdAt.getTime();
  if (Number.isNaN(diffMs) || diffMs < 0) return t('justNow');

  const minuteMs = 60 * 1000;
  const hourMs = 60 * minuteMs;
  const dayMs = 24 * hourMs;
  if (diffMs < hourMs) return `${Math.max(1, Math.floor(diffMs / minuteMs))} ${t('minutesAgo')}`;
  if (diffMs < dayMs) return `${Math.floor(diffMs / hourMs)} ${t('hoursAgo')}`;
  return `${Math.floor(diffMs / dayMs)} ${t('daysAgo')}`;
};

const buildContent = (item: NotificationApiItem): string => {
  const action = item.loaiHanhDong || '';
  const objectType = item.loaiDoiTuong || '';
  return ` da thuc hien ${action} voi ${objectType} #${item.maDoiTuong}`;
};

const mapApiNotification = (item: NotificationApiItem): Notification => ({
  id: String(item.id),
  type: normalizeType(item.loaiHanhDong),
  user: {
    name: `Nguoi dung #${item.maNguoiHanhDong}`,
    avatarColor: '#1890ff',
  },
  content: buildContent(item),
  timeAgo: formatTimeAgo(item.ngayTao),
  isRead: item.daDoc,
});

const fetchNotifications = async () => {
  const currentUserId = authStore.getUser?.maNguoiDung;
  if (!currentUserId) {
    error.value = t('cannotFindUserInfo');
    return;
  }

  isLoading.value = true;
  error.value = '';
  try {
    const response = await axiosInstance.get<NotificationApiResponse>(`/thongbao/user/${currentUserId}`);
    const result = response.data;
    if (result.code !== 200 && result.code !== 0) {
      error.value = result.message || t('cannotLoadNotifications');
      return;
    }
    notifications.value = Array.isArray(result.data) ? result.data.map(mapApiNotification) : [];
  } catch (err: any) {
    error.value = err?.response?.data?.message || t('cannotLoadNotifications');
  } finally {
    isLoading.value = false;
  }
};

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
  fetchNotifications();
};

onMounted(() => {
  fetchNotifications();
});
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

