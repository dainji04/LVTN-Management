<template>
  <AuthLayout :hide-right-sidebar="true">
    <template #left-sidebar>
      <SideBar />
    </template>

    <main class="w-full flex-1 overflow-y-auto bg-[#f5f5f5] px-2 sm:px-4 py-6">
      <div class="max-w-3xl mx-auto w-full">
        <!-- Header -->
        <div class="mb-6">
          <h1 class="text-2xl font-bold text-gray-800">{{ t('friends') }}</h1>
          <p class="text-sm text-gray-500 mt-1">{{ t('friendsPageSubtitle') }}</p>
        </div>

        <!-- Stats -->
        <div class="grid grid-cols-3 gap-3 mb-6">
          <div class="bg-white rounded-xl border border-gray-200 p-4 text-center">
            <p class="text-2xl font-bold text-gray-800">{{ listFriends.length }}</p>
            <p class="text-xs text-gray-500 mt-1">{{ t('friends') }}</p>
          </div>
          <div class="bg-white rounded-xl border border-gray-200 p-4 text-center">
            <div class="relative inline-block">
              <p class="text-2xl font-bold text-primary">{{ listFriendRequests.length }}</p>
              <span
                v-if="listFriendRequests.length > 0"
                class="absolute -top-1 -right-3 w-2 h-2 bg-primary rounded-full animate-pulse"
              />
            </div>
            <p class="text-xs text-gray-500 mt-1">{{ t('friendRequestsLabel') }}</p>
          </div>
          <div class="bg-white rounded-xl border border-gray-200 p-4 text-center">
            <p class="text-2xl font-bold text-gray-800">{{ listRecommendations.length }}</p>
            <p class="text-xs text-gray-500 mt-1">{{ t('friendSuggestions') }}</p>
          </div>
        </div>

        <!-- Tabs -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 mb-6">
          <div class="flex border-b border-gray-100">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              class="flex-1 py-3.5 text-sm font-semibold transition-all relative"
              :class="activeTab === tab.key
                ? 'text-primary'
                : 'text-gray-500 hover:text-gray-700 hover:bg-gray-50'"
              @click="activeTab = tab.key"
            >
              {{ tab.label }}
              <span
                v-if="tab.badge && tab.badge > 0"
                class="ml-1.5 inline-flex items-center justify-center min-w-[20px] h-5 px-1.5 text-xs font-bold rounded-full bg-primary text-white"
              >
                {{ tab.badge }}
              </span>
              <span
                v-if="activeTab === tab.key"
                class="absolute bottom-0 left-1/2 -translate-x-1/2 w-12 h-0.5 bg-primary rounded-full"
              />
            </button>
          </div>
        </div>

        <!-- Tab: All Friends -->
        <div v-if="activeTab === 'all'">
          <!-- Search -->
          <div class="relative mb-4">
            <SearchOutlined class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              v-model="searchQuery"
              @input="handleSearch"
              type="text"
              :placeholder="t('searchFriendsInList')"
              class="w-full pl-11 pr-4 py-3 rounded-xl bg-white border border-gray-200 text-sm outline-none transition-all focus:border-primary focus:shadow-sm"
            />
          </div>

          <!-- Friends list -->
          <div v-if="listFriends.length > 0" class="space-y-3">
            <div
              v-for="friend in listFriends"
              :key="friend.maNguoiDung"
              class="friend-card bg-white rounded-xl border border-gray-200 p-4 flex items-center gap-4 transition-all hover:shadow-md hover:border-gray-300"
            >
              <div class="relative flex-shrink-0">
                <img
                  :src="friend.anhDaiDien"
                  :alt="friend.ho + ' ' + friend.ten"
                  class="w-14 h-14 rounded-full object-cover border-2 border-gray-100"
                />
                <span
                  v-if="isOnline(friend.hoatDongLanCuoi)"
                  class="absolute bottom-0 right-0 w-3.5 h-3.5 bg-green-500 border-2 border-white rounded-full"
                />
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-semibold text-gray-800 truncate">{{ friend.ho }} {{ friend.ten }}</p>
                <p class="text-xs text-gray-400 mt-0.5">
                  {{ friend.ngayKetBan }} {{ t('mutualFriends') }}
                </p>
              </div>
              <div class="flex items-center gap-2 flex-shrink-0">
                <button
                  class="w-9 h-9 rounded-lg bg-gray-50 hover:bg-gray-100 flex items-center justify-center text-gray-500 hover:text-primary transition-colors"
                  :title="t('messages')"
                >
                  <MessageOutlined class="text-base" />
                </button>
                <button
                  class="w-9 h-9 rounded-lg bg-gray-50 hover:bg-gray-100 flex items-center justify-center text-gray-500 hover:text-red-500 transition-colors"
                  :title="t('more')"
                >
                  <MoreOutlined class="text-base" />
                </button>
              </div>
            </div>
          </div>

          <!-- Empty state -->
          <div v-else class="flex flex-col items-center justify-center py-16 text-center">
            <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
              <UserOutlined class="text-3xl text-gray-400" />
            </div>
            <p class="font-semibold text-gray-700 mb-1">{{ t('noFriendsFound') }}</p>
            <p class="text-sm text-gray-500">{{ t('noFriendsFoundHint') }}</p>
          </div>
        </div>

        <!-- Tab: Friend Requests -->
        <div v-if="activeTab === 'requests'">
          <!-- Received requests -->
          <div v-if="listFriendRequests.length > 0" class="space-y-3">
            <div
              v-for="request in listFriendRequests"
              :key="request.maNguoiGui"
              class="friend-card bg-white rounded-xl border border-gray-200 p-4 transition-all hover:shadow-md hover:border-gray-300"
            >
              <div class="flex items-center gap-4">
                <img
                  :src="request.anhDaiDienNguoiGui"
                  :alt="request.hoTenNguoiGui"
                  class="w-14 h-14 rounded-full object-cover border-2 border-gray-100 flex-shrink-0"
                />
                <div class="flex-1 min-w-0">
                  <p class="font-semibold text-gray-800 truncate">{{ request.hoTenNguoiGui }}</p>
                  <p class="text-xs text-gray-400 mt-0.5">
                    {{ t('mutualFriends') }}
                  </p>
                </div>
              </div>
              <div class="flex gap-2 mt-3 ml-[72px]">
                <button
                  @click="handleAcceptFriendRequest(request.maNguoiGui, request.maNguoiNhan, request.id)"
                  class="flex-1 py-2 rounded-lg bg-primary text-white text-sm font-semibold hover:bg-primary/90 transition-colors">
                  {{ t('accept') }}
                </button>
                <button
                  class="flex-1 py-2 rounded-lg bg-gray-100 text-gray-700 text-sm font-semibold hover:bg-gray-200 transition-colors">
                  {{ t('decline') }}
                </button>
              </div>
            </div>
          </div>

          <!-- Empty state -->
          <div v-else class="flex flex-col items-center justify-center py-16 text-center">
            <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
              <UsergroupAddOutlined class="text-3xl text-gray-400" />
            </div>
            <p class="font-semibold text-gray-700 mb-1">{{ t('noFriendRequests') }}</p>
            <p class="text-sm text-gray-500">{{ t('noFriendRequestsHint') }}</p>
          </div>
        </div>

        <!-- Tab: Suggestions -->
        <div v-if="activeTab === 'suggestions'">
          <div v-if="listRecommendations.length > 0" class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div
              v-for="user in listRecommendations"
              :key="user.maNguoiDung"
              class="friend-card bg-white rounded-xl border border-gray-200 overflow-hidden transition-all hover:shadow-md hover:border-gray-300"
            >
              <!-- Cover gradient -->
              <div class="h-20 bg-gradient-to-br from-primary/20 via-rose-100 to-pink-50 relative">
                <img
                  :src="user.anhDaiDien"
                  :alt="user.ho + ' ' + user.ten"
                  class="absolute -bottom-7 left-4 w-14 h-14 rounded-full object-cover border-3 border-white shadow-sm"
                />
              </div>
              <div class="pt-10 px-4 pb-4">
                <p class="font-semibold text-gray-800 truncate">{{ user.ho }} {{ user.ten }}</p>
                <p class="text-xs text-gray-400 mt-0.5">
                  {{ t('mutualFriends') }}
                </p>
                <div class="flex gap-2 mt-3">
                  <button class="flex-1 py-2 rounded-lg bg-primary text-white text-sm font-semibold hover:bg-primary/90 transition-colors inline-flex items-center justify-center gap-1.5">
                    <UserAddOutlined class="text-xs" />
                    {{ t('addFriend') }}
                  </button>
                  <button class="py-2 px-3 rounded-lg bg-gray-100 text-gray-500 text-sm hover:bg-gray-200 transition-colors">
                    <CloseOutlined class="text-xs" />
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty state -->
          <div v-else class="flex flex-col items-center justify-center py-16 text-center">
            <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
              <TeamOutlined class="text-3xl text-gray-400" />
            </div>
            <p class="font-semibold text-gray-700 mb-1">{{ t('noSuggestions') }}</p>
            <p class="text-sm text-gray-500">{{ t('noSuggestionsHint') }}</p>
          </div>
        </div>
      </div>
    </main>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import {
  SearchOutlined,
  MessageOutlined,
  MoreOutlined,
  UserOutlined,
  UserAddOutlined,
  UsergroupAddOutlined,
  TeamOutlined,
  CloseOutlined,
} from '@ant-design/icons-vue';
import AuthLayout from '../layouts/authLayout.vue';
import SideBar from '../components/SideBar.vue';
import { useI18n } from 'vue-i18n';
import type { FriendItem, friendRequestItem, GoiYKetBanResponse } from '../types/friendType';
import friendHelper from '../helpers/friendHelper';
import { useAuthStore } from '../store/authStore';
import { useDebounceFn } from '@vueuse/core';

const { t } = useI18n();

const activeTab = ref<'all' | 'requests' | 'suggestions'>('all');
const searchQuery = ref('');

const tabs = computed(() => [
  { key: 'all' as const, label: t('allFriends'), badge: listFriends.value.length },
  { key: 'requests' as const, label: t('friendRequestsLabel'), badge: listFriendRequests.value.length },
  { key: 'suggestions' as const, label: t('friendSuggestions'), badge: listRecommendations.value.length },
]);

const authStore = useAuthStore();
const listFriends = ref<FriendItem[]>([]);
const listRecommendations = ref<GoiYKetBanResponse[]>([]);
const listFriendRequests = ref<friendRequestItem[]>([]);

const loadList = async () => {
  const response = await friendHelper.loadListFriends(authStore.getUser?.maNguoiDung || 0);
  listFriends.value = response;
};

const loadRecommendations = async () => {
  const response = await friendHelper.getRecommendations(0, 8);
  listRecommendations.value = response;
};

const loadFriendRequest = async () => {
  const response = await friendHelper.getFriendRequest(authStore.getUser?.maNguoiDung || 0);
  listFriendRequests.value = response;
};

onMounted(() => {
  loadList();
  loadRecommendations();
  loadFriendRequest();
});

const handleSearch = useDebounceFn(async () => {
  const query = searchQuery.value.trim();
  if (!query) return;

  const userId = authStore.getUser?.maNguoiDung;
  if (!userId) return;

  const response = await friendHelper.searchFriends(userId, query);
  listFriends.value = response;
}, 300);

const handleAcceptFriendRequest = async (senderUserId: number, receiverUserId: number, loiMoiId: number) => {
  if (!senderUserId) return;

  const response = await friendHelper.acceptFriendRequest(senderUserId, receiverUserId, loiMoiId);
  if (response.code === 200 || response.code === 0) {
    listFriendRequests.value = listFriendRequests.value.filter((request) => request.id !== loiMoiId);
  }
}

const isOnline = (hoatDongLanCuoi: string) => {
  //       "hoatDongLanCuoi": "2026-04-23T02:11:34.582Z",
  return hoatDongLanCuoi !== null && new Date(hoatDongLanCuoi) > new Date(new Date().setHours(0, 0, 0, 0)) && new Date(hoatDongLanCuoi) < new Date(new Date().setHours(23, 59, 59, 999));
}

</script>

<style scoped>
.friend-card {
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

.border-3 {
  border-width: 3px;
}
</style>
