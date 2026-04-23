<template>
  <AuthLayout :hide-right-sidebar="true">
    <template #left-sidebar>
      <SideBar />
    </template>

    <main class="w-full flex-1 overflow-y-auto bg-[#f5f5f5] px-2 sm:px-4 py-6">
      <div class="max-w-2xl mx-auto w-full">
        <!-- Header -->
        <div class="mb-6">
          <h1 class="text-2xl font-bold text-gray-800">{{ t('searchFriends') }}</h1>
          <p class="text-sm text-gray-500 mt-1">{{ t('searchFriendsSubtitle') }}</p>
        </div>

        <!-- Search input -->
        <div class="search-box bg-white rounded-xl shadow-sm border border-gray-200 p-4 mb-6">
          <div class="relative">
            <SearchOutlined class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 text-lg" />
            <input
              ref="searchInputRef"
              v-model="searchQuery"
              type="text"
              :placeholder="t('searchFriendsPlaceholder')"
              class="w-full pl-12 pr-4 py-3 rounded-lg bg-gray-50 border border-gray-200 text-sm outline-none transition-all focus:border-primary focus:bg-white focus:shadow-sm"
              @input="handleSearch"
            />
            <button
              v-if="searchQuery.trim()"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              @click="clearSearch"
            >
              <CloseCircleOutlined class="text-lg" />
            </button>
          </div>
        </div>

        <!-- Result count -->
        <p v-if="hasSearched && !loading && results.length > 0" class="text-sm text-gray-500 mb-4">
          {{ t('searchResultCount', { count: results.length }) }}
        </p>

        <!-- Loading -->
        <div v-if="loading" class="flex flex-col items-center justify-center py-16">
          <LoadingOutlined class="text-3xl text-primary animate-spin mb-3" />
          <p class="text-sm text-gray-500">{{ t('searching') }}</p>
        </div>

        <!-- Empty initial state -->
        <div v-else-if="!hasSearched" class="flex flex-col items-center justify-center py-16 text-center">
          <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
            <SearchOutlined class="text-3xl text-gray-400" />
          </div>
          <p class="text-gray-500">{{ t('searchToFindFriends') }}</p>
        </div>

        <!-- No results -->
        <div v-else-if="results.length === 0" class="flex flex-col items-center justify-center py-16 text-center">
          <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
            <UserOutlined class="text-3xl text-gray-400" />
          </div>
          <p class="font-semibold text-gray-700 mb-1">{{ t('noSearchResults') }}</p>
          <p class="text-sm text-gray-500">{{ t('tryDifferentKeyword') }}</p>
        </div>

        <!-- Results list -->
        <div v-else class="space-y-3">
          <div
            v-for="user in results"
            :key="user.maNguoiDung"
            class="result-card bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-4 transition-all hover:shadow-md hover:border-gray-300"
          >
            <img
              :src="resolveMediaUrl(user.anhDaiDien) || defaultAvatar"
              :alt="user.ho + ' ' + user.ten"
              class="w-14 h-14 rounded-full object-cover border border-gray-200 flex-shrink-0"
            />
            <div class="flex-1 min-w-0">
              <p class="font-semibold text-gray-800 truncate">{{ user.ho }} {{ user.ten }}</p>
              <p v-if="user.bietDanh" class="text-sm text-gray-500 truncate">@{{ user.bietDanh }}</p>
              <p v-if="user.email" class="text-xs text-gray-400 truncate">{{ user.email }}</p>
            </div>
            <button
              :disabled="sendingIds.has(user.maNguoiDung) || sentIds.has(user.maNguoiDung)"
              class="add-friend-btn flex-shrink-0 px-4 py-2 rounded-lg text-sm font-semibold transition-all"
              :class="getButtonClass(user.maNguoiDung)"
              @click="handleAddFriend(user.maNguoiDung)"
            >
              <LoadingOutlined v-if="sendingIds.has(user.maNguoiDung)" class="mr-1" />
              <UserAddOutlined v-else-if="!sentIds.has(user.maNguoiDung)" class="mr-1" />
              <CheckOutlined v-else class="mr-1" />
              {{ getButtonText(user.maNguoiDung) }}
            </button>
          </div>
        </div>
      </div>
    </main>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import {
  SearchOutlined,
  CloseCircleOutlined,
  LoadingOutlined,
  UserOutlined,
  UserAddOutlined,
  CheckOutlined,
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import AuthLayout from '../layouts/authLayout.vue';
import SideBar from '../components/SideBar.vue';
import { useAuthStore } from '../store/authStore';
import axiosInstance from '../helpers/apiHelper';
import { resolveMediaUrl } from '../helpers/mediaHelper';
import { useI18n } from 'vue-i18n';
import { useDebounceFn } from '@vueuse/core';


const { t } = useI18n();
const authStore = useAuthStore();

interface SearchUserItem {
  maNguoiDung: number;
  ho: string;
  ten: string;
  bietDanh: string;
  anhDaiDien: string | null;
  email: string;
}

const searchQuery = ref('');
const results = ref<SearchUserItem[]>([]);
const loading = ref(false);
const hasSearched = ref(false);
const sendingIds = ref<Set<number>>(new Set());
const sentIds = ref<Set<number>>(new Set());
const searchInputRef = ref<HTMLInputElement | null>(null);

const defaultAvatar = 'https://testingbot.com/free-online-tools/random-avatar/100';

const handleSearch = useDebounceFn(async() => {
  const query = searchQuery.value.trim();
  if (!query) return;

  const userId = authStore.getUser?.maNguoiDung;
  if (!userId) return;

  loading.value = true;
  hasSearched.value = true;

  try {
    const response = await axiosInstance.get('/users/search', {
      params: { userId, query },
    });

    const data = response.data;
    if (data.code === 200 || data.code === 0) {
      results.value = Array.isArray(data.data) ? data.data : (data.data?.content ?? []);
    } else {
      results.value = [];
    }
  } catch {
    results.value = [];
  } finally {
    loading.value = false;
  }
}, 300);

async function handleAddFriend(targetUserId: number) {
  const currentUserId = authStore.getUser?.maNguoiDung;
  if (!currentUserId || sendingIds.value.has(targetUserId) || sentIds.value.has(targetUserId)) return;

  sendingIds.value.add(targetUserId);

  try {
    const response = await axiosInstance.post(`/loi-moi-ket-ban/gui?nguoiGuiId=${currentUserId}&nguoiNhanId=${targetUserId}`);

    const data = response.data;
    if (data.code === 200 || data.code === 0 || data.code === 201) {
      sentIds.value.add(targetUserId);
      message.success(t('addFriendSuccess'));
    } else {
      message.error(data.message || t('addFriendFailed'));
    }
  } catch {
    message.error(t('addFriendFailed'));
  } finally {
    sendingIds.value.delete(targetUserId);
  }
}

function clearSearch() {
  searchQuery.value = '';
  results.value = [];
  hasSearched.value = false;
  searchInputRef.value?.focus();
}

function getButtonClass(userId: number): string {
  if (sentIds.value.has(userId)) return 'sent';
  if (sendingIds.value.has(userId)) return 'sending';
  return 'default';
}

function getButtonText(userId: number): string {
  if (sendingIds.value.has(userId)) return t('sendingFriendRequest');
  if (sentIds.value.has(userId)) return t('friendRequestSent');
  return t('addFriend');
}

onMounted(() => {
  searchInputRef.value?.focus();
});
</script>

<style scoped>
.search-box input:focus {
  border-color: #FF436D;
}

.add-friend-btn {
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
}

.add-friend-btn.default {
  background: #FF436D;
  color: #fff;
}

.add-friend-btn.default:hover:not(:disabled) {
  background: #e63560;
  box-shadow: 0 2px 8px rgba(255, 67, 109, 0.3);
}

.add-friend-btn.sending {
  background: #fef2f2;
  color: #FF436D;
  cursor: wait;
}

.add-friend-btn.sent {
  background: #f0fdf4;
  color: #16a34a;
  cursor: default;
}

.result-card {
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 640px) {
  .add-friend-btn {
    padding: 8px 12px;
    font-size: 12px;
  }
}
</style>
