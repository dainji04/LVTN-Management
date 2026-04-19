<template>
  <AuthLayout :hide-right-sidebar="true">
    <template #left-sidebar>
      <SideBar />
    </template>

    <!-- Main Content -->
    <main
      ref="mainContentRef"
      class="main-content w-full flex-1 overflow-y-auto bg-[#f5f5f5] px-2 sm:px-4"
      @scroll.passive="handleScrollPosts"
    >
      <div class="max-w-6xl mx-auto w-full">
        <!-- Profile Header -->
        <div class="profile-header relative mb-4">
          <!-- Cover Photo -->
          <div class="cover-photo relative h-40 sm:h-52 lg:h-64 bg-gray-300 rounded-t-lg overflow-hidden">
            <img
              :src="authStore.getUser?.anhNen"
              alt="Cover"
              class="w-full h-full object-cover"
            />
            <button
              class="absolute bottom-3 right-3 sm:bottom-4 sm:right-4 bg-white hover:bg-gray-50 px-3 sm:px-4 py-2 rounded-lg flex items-center gap-2 shadow-md transition-colors text-sm"
            >
              <CameraOutlined />
              <span>{{ $t('editCover') }}</span>
            </button>
          </div>

          <!-- Profile Info Section -->
          <div class="bg-white rounded-b-lg px-4 sm:px-6 pb-4">
            <div class="flex flex-col sm:flex-row sm:items-start sm:justify-between -mt-12 sm:-mt-16 mb-4 gap-3">
              <!-- Avatar -->
              <div class="relative">
                <a-avatar
                  :size="96"
                  :src="profileData.avatar"
                  class="border-4 border-white shadow-lg"
                >
                  <template #icon>
                    <UserOutlined />
                  </template>
                </a-avatar>
              </div>

              <!-- Action Buttons -->
              <div class="flex items-center gap-2 sm:gap-3 sm:mt-20">
                <Button type="primary">
                  <template #icon>
                    <EditOutlined class="mr-1" />
                  </template>
                  {{ $t('editInformation') }}
                </Button>
                <Button type="default" classes="flex items-center justify-center">
                  <template #icon>
                    <ShareAltOutlined class="text-xl" />
                  </template>
                </Button>
              </div>
            </div>

            <!-- Username and Stats -->
            <div class="mb-4">
              <div class="flex items-center gap-2 mb-2">
                <h1 class="text-xl sm:text-2xl lg:text-3xl font-bold break-all">{{ profileData.username }}</h1>
                <CheckCircleOutlined class="text-primary text-2xl" />
              </div>
              <div class="flex flex-wrap items-center gap-3 sm:gap-6 text-gray-600 text-sm sm:text-base">
                <span class="font-semibold">{{ profileData.friends }} {{ $t('friends') }}</span>
                <span class="font-semibold">{{ profileData.followers }} {{ $t('followers') }}</span>
              </div>
            </div>

            <!-- Tabs -->
            <a-tabs v-model:activeKey="activeTab" class="profile-tabs">
              <a-tab-pane key="posts" :tab="$t('posts')">
                <template #tab>
                  <span class="tab-label">{{ $t('posts') }}</span>
                </template>
              </a-tab-pane>
              <a-tab-pane key="groupChat" :tab="$t('groupChat')">
                <template #tab>
                  <span class="tab-label">{{ $t('groupChat') }}</span>
                </template>
              </a-tab-pane>
              <a-tab-pane key="friends" :tab="$t('friends')">
                <template #tab>
                  <span class="tab-label">{{ $t('friends') }}</span>
                </template>
              </a-tab-pane>
              <a-tab-pane key="seeMore" :tab="$t('seeMore')">
                <template #tab>
                  <span class="tab-label">{{ $t('seeMore') }}</span>
                </template>
              </a-tab-pane>
            </a-tabs>
          </div>
        </div>

        <!-- Content Area -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-4 px-0 sm:px-2 lg:px-4 pb-6">
          <!-- Left Column -->
          <div class="lg:col-span-1 space-y-4">
            <!-- About Card -->
            <div class="bg-white rounded-lg p-3 sm:p-4 shadow-lg">
              <div class="flex items-center gap-2 mb-4">
                <InfoCircleOutlined class="text-primary text-xl" />
                <h3 class="font-semibold text-lg">{{ $t('introduction') }}</h3>
              </div>
              <p class="text-gray-700 mb-4">{{ profileData.bio }}</p>
              <div class="space-y-3">
                <div class="flex items-center gap-2 text-gray-600">
                  <EnvironmentOutlined />
                  <span>{{ profileData.location }}</span>
                </div>
                <div class="flex items-center gap-2 text-gray-600">
                  <LinkOutlined />
                  <a :href="profileData.website" target="_blank" class="text-primary hover:underline">
                    {{ profileData.website }}
                  </a>
                </div>
                <div class="flex items-center gap-2 text-gray-600">
                  <CalendarOutlined />
                  <span>{{ profileData.birthday }}</span>
                </div>
              </div>
            </div>

            <!-- Photos Card -->
            <div class="bg-white rounded-lg p-3 sm:p-4 shadow-lg">
              <div class="flex items-center justify-between mb-4">
                <h3 class="font-semibold text-lg">{{ $t('photos') }}</h3>
                <a href="#" class="text-primary hover:underline text-sm">{{ $t('viewAll') }}</a>
              </div>
              <div class="grid grid-cols-3 gap-2">
                <div
                  v-for="(photo, index) in profileData.photos"
                  :key="index"
                  class="aspect-square rounded-lg overflow-hidden cursor-pointer hover:opacity-80 transition-opacity"
                >
                  <img
                    :src="photo"
                    :alt="`Photo ${index + 1}`"
                    class="w-full h-full object-cover"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Right Column -->
          <div class="lg:col-span-2 space-y-4">
            <!-- Create Post -->
            <div class="bg-white rounded-lg p-3 sm:p-4 shadow-lg">
              <div class="flex items-center gap-3 mb-3">
                <a-avatar :src="profileData.avatar" :size="40">
                  <template #icon>
                    <UserOutlined />
                  </template>
                </a-avatar>
                <input
                  type="text"
                  :placeholder="$t('howAreYouFeelingToday')"
                  class="flex-1 border border-gray-200 rounded-full px-4 py-2 focus:outline-none focus:border-pink-500"
                />
              </div>
              <div class="flex items-center justify-between pt-3 border-t border-gray-100">
                <div class="flex items-center gap-3 sm:gap-4 overflow-x-auto">
                  <button class="flex items-center gap-2 text-gray-600 hover:text-primary transition-colors">
                    <PictureOutlined class="text-xl" />
                    <span class="text-sm">{{ $t('images/videos') }}</span>
                  </button>
                  <button class="flex items-center gap-2 text-gray-600 hover:text-primary transition-colors">
                    <SmileOutlined class="text-xl" />
                    <span class="text-sm">{{ $t('feeling') }}</span>
                  </button>
                  <button class="flex items-center gap-2 text-gray-600 hover:text-primary transition-colors">
                    <CalendarOutlined class="text-xl" />
                    <span class="text-sm">{{ $t('lifeEvent') }}</span>
                  </button>
                </div>
              </div>
            </div>

            <!-- Posts -->
            <div v-if="activeTab === 'posts'" class="space-y-4">
              <Post
                v-for="post in postStore.postsByUserId"
                :key="post.maBaiViet"
                :id="post.maBaiViet"
                :username="post.hoTen"
                :avatar="resolveMediaUrl(post.anhDaiDienNguoiDang) || defaultAvatar"
                :image="resolveMediaUrl(post.danhSachAnh?.[0])"
                :caption="post.noiDung"
                :time-ago="formatTimeAgo(post.ngayTao)"
                :like-count="post.luotThich"
                :comment-count="post.luotBinhLuan"
              />
              <div v-if="isLoadingPosts" class="text-center text-gray-500 py-2">{{ $t('loadingPosts') }}</div>
              <div v-if="postError" class="text-center text-red-500 text-sm py-2">{{ postError }}</div>
              <div
                v-if="!isLoadingPosts && !postStore.hasNext && postStore.postsByUserId.length > 0"
                class="text-center text-gray-400 text-sm py-2"
              >
                {{ $t('allPostsDisplayed') }}
              </div>
            </div>

            <!-- Group Chat Tab Content -->
            <div v-if="activeTab === 'groupChat'" class="bg-white rounded-lg p-6 shadow-lg text-center text-gray-500">
              <TeamOutlined class="text-4xl mb-2" />
              <p>{{ $t('groupChat') }}</p>
            </div>

            <!-- Friends Tab Content -->
            <div v-if="activeTab === 'friends'" class="bg-white rounded-lg p-4 sm:p-6 shadow-lg">
              <div class="flex flex-wrap items-center justify-between gap-2 mb-4">
                <h3 class="text-lg font-semibold">{{ $t('friends') }}</h3>
                <span class="text-sm text-gray-500">{{ friends.length }} {{ $t('friends') }}</span>
              </div>

              <div v-if="isLoadingFriends" class="text-center text-gray-500 py-4">{{ $t('loadingFriends') }}</div>
              <div v-else-if="friendError" class="text-center text-red-500 text-sm py-4">{{ friendError }}</div>
              <div v-else-if="friends.length === 0" class="text-center text-gray-500 py-4">{{ $t('noFriendsYet') }}</div>

              <div v-else class="space-y-3">
                <div
                  v-for="friend in friends"
                  :key="friend.maNguoiDung"
                  class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 p-3 border border-gray-100 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  <div class="flex items-center gap-3 min-w-0">
                    <a-avatar :src="resolveMediaUrl(friend.anhDaiDien) || defaultAvatar" :size="46">
                      <template #icon>
                        <UserOutlined />
                      </template>
                    </a-avatar>
                    <div class="min-w-0">
                      <p class="font-semibold text-gray-800 truncate">
                        {{ friend.ho }} {{ friend.ten }}
                      </p>
                      <p class="text-xs text-gray-500 truncate">{{ friend.email }}</p>
                      <p class="text-xs text-gray-400">
                        {{ $t('friendSince') }}: {{ formatDateTime(friend.ngayKetBan) }}
                      </p>
                    </div>
                  </div>
                  <span class="text-xs text-gray-400 sm:text-right">
                    {{ $t('lastActive') }}: {{ formatDateTime(friend.hoatDongLanCuoi) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- See More Tab Content -->
            <div v-if="activeTab === 'seeMore'" class="bg-white rounded-lg p-6 shadow-lg text-center text-gray-500">
              <MoreOutlined class="text-4xl mb-2" />
              <p>{{ $t('seeMore') }}</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue';
import {
  CameraOutlined,
  EditOutlined,
  ShareAltOutlined,
  CheckCircleOutlined,
  InfoCircleOutlined,
  EnvironmentOutlined,
  LinkOutlined,
  CalendarOutlined,
  PictureOutlined,
  SmileOutlined,
  UserOutlined,
  TeamOutlined,
  MoreOutlined,
} from '@ant-design/icons-vue';
import AuthLayout from '../layouts/authLayout.vue';
import SideBar from '../components/SideBar.vue';
import Button from '../components/Button.vue';
import Post from '../components/Post.vue';
import { useAuthStore } from '../store/authStore';
import type { User } from '../types/userType';
import axiosInstance from '../helpers/apiHelper';
import type { PostItem } from '../types/postType';
import { resolveMediaUrl } from '../helpers/mediaHelper';
import { useI18n } from 'vue-i18n';
import { usePostStore } from '../store/postStore';

const activeTab = ref('posts');
const { t } = useI18n();
const mainContentRef = ref<HTMLElement | null>(null);
const defaultAvatar = 'https://testingbot.com/free-online-tools/random-avatar/500';
const hasNextPosts = ref(true);
const isLoadingPosts = ref(false);
const postError = ref('');
const SCROLL_THRESHOLD = 300;
const isLoadingFriends = ref(false);
const friendError = ref('');

interface FriendItem {
  maNguoiDung: number;
  ho: string;
  ten: string;
  bietDanh: string;
  anhDaiDien: string | null;
  email: string;
  hoatDongLanCuoi: string;
  ngayKetBan: string;
}

interface FriendResponse {
  code: number;
  message: string | null;
  data: FriendItem[];
}

const friends = ref<FriendItem[]>([]);

const profileData = ref({
  username: 'tuyetnhi0823',
  avatar: 'https://testingbot.com/free-online-tools/random-avatar/100',
  friends: '1.2k',
  followers: '458',
  bio: 'Hãy thiết kế theo cách của bạn',
  location: 'Da Nang, Vietnam',
  website: 'quynguyen.design',
  birthday: '01-02-2004',
  photos: [
    'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=400&h=400&fit=crop', // Desktop setup
    'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', // Laptop
    'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=400&h=400&fit=crop', // Group of people
    'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=400&h=400&fit=crop', // Notebook
    'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?w=400&h=400&fit=crop', // Smartphone
    'https://images.unsplash.com/photo-1526374965328-7f61d4a18db5?w=400&h=400&fit=crop', // Tech workspace
  ],
});

const authStore = useAuthStore();
const postStore = usePostStore();

const formatTimeAgo = (date: string): string => {
  const createdAt = new Date(date);
  const now = new Date();
  const diffMs = now.getTime() - createdAt.getTime();

  if (Number.isNaN(diffMs) || diffMs < 0) return t('justNow');

  const minuteMs = 60 * 1000;
  const hourMs = 60 * minuteMs;
  const dayMs = 24 * hourMs;

  if (diffMs < hourMs) {
    return `${Math.max(1, Math.floor(diffMs / minuteMs))} ${t('minutes')}`;
  }
  if (diffMs < dayMs) {
    return `${Math.floor(diffMs / hourMs)} ${t('hours')}`;
  }

  return `${Math.floor(diffMs / dayMs)} ${t('days')}`;
};

const formatDateTime = (date: string): string => {
  const raw = new Date(date);
  if (Number.isNaN(raw.getTime())) return t('unknown');
  return raw.toLocaleString('vi-VN');
};

const fetchFriends = async () => {
  const currentUserId = authStore.getUser?.maNguoiDung;
  if (!currentUserId || isLoadingFriends.value) return;

  isLoadingFriends.value = true;
  friendError.value = '';
  try {
    const response = await axiosInstance.get<FriendResponse>(`/ban-be/${currentUserId}`);
    const result = response.data;
    if (result.code !== 200 && result.code !== 0) {
      friendError.value = result.message || t('cannotLoadFriends');
      return;
    }

    friends.value = Array.isArray(result.data) ? result.data : [];
    profileData.value.friends = `${friends.value.length}`;
  } catch (error: any) {
    friendError.value = error?.response?.data?.message || t('cannotLoadFriends');
  } finally {
    isLoadingFriends.value = false;
  }
};

const handleScrollPosts = async () => {
  if (activeTab.value !== 'posts' || isLoadingPosts.value || !postStore.hasNext) return;

  const el = mainContentRef.value;
  if (!el) return;

  const distanceToBottom = el.scrollHeight - el.scrollTop - el.clientHeight;
  if (distanceToBottom <= SCROLL_THRESHOLD) {
    await postStore.fetchNextPageByUserId(authStore.getUser?.maNguoiDung || 0);
  }
};

onMounted(() => {
  const currentUser:User | null = authStore.getUser;
  profileData.value.username = currentUser?.ho + ' ' + currentUser?.ten;
  profileData.value.avatar = currentUser?.anhDaiDien || 'https://testingbot.com/free-online-tools/random-avatar/500';
  profileData.value.bio = currentUser?.gioiThieu || 'Bạn hiện chưa có giới thiệu nào.';
  postStore.fetchFirstPageByUserId(currentUser?.maNguoiDung || 0);
  fetchFriends();

});

watch(activeTab, (tab) => {
  if (tab === 'friends' && friends.value.length === 0 && !isLoadingFriends.value) {
    fetchFriends();
  }
});
</script>

<style scoped>
.profile-tabs :deep(.ant-tabs-tab) {
  padding: 12px 16px;
  font-weight: 500;
}

.profile-tabs :deep(.ant-tabs-tab-active) {
  color: #FF436D;
}

.profile-tabs :deep(.ant-tabs-ink-bar) {
  background: #FF436D;
}

.tab-label {
  font-size: 15px;
}
</style>

