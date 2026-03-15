<template>
  <AuthLayout :hide-right-sidebar="true">
    <template #left-sidebar>
      <SideBar />
    </template>

    <!-- Main Content -->
    <main class="main-content w-full flex-1 overflow-y-auto bg-[#f5f5f5]">
      <div class="max-w-6xl mx-auto">
        <!-- Profile Header -->
        <div class="profile-header relative mb-4">
          <!-- Cover Photo -->
          <div class="cover-photo relative h-64 bg-gray-300 rounded-t-lg overflow-hidden">
            <img
              src="https://via.placeholder.com/1200x300"
              alt="Cover"
              class="w-full h-full object-cover"
            />
            <button
              class="absolute bottom-4 right-4 bg-white hover:bg-gray-50 px-4 py-2 rounded-lg flex items-center gap-2 shadow-md transition-colors"
            >
              <CameraOutlined />
              <span>{{ $t('editCover') }}</span>
            </button>
          </div>

          <!-- Profile Info Section -->
          <div class="bg-white rounded-b-lg px-6 pb-4">
            <div class="flex items-start justify-between -mt-16 mb-4">
              <!-- Avatar -->
              <div class="relative">
                <a-avatar
                  :size="120"
                  :src="profileData.avatar"
                  class="border-4 border-white shadow-lg"
                >
                  <template #icon>
                    <UserOutlined />
                  </template>
                </a-avatar>
              </div>

              <!-- Action Buttons -->
              <div class="flex items-center gap-3 mt-20">
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
                <h1 class="text-3xl font-bold">{{ profileData.username }}</h1>
                <CheckCircleOutlined class="text-primary text-2xl" />
              </div>
              <div class="flex items-center gap-6 text-gray-600">
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
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-4 px-4 pb-6">
          <!-- Left Column -->
          <div class="lg:col-span-1 space-y-4">
            <!-- About Card -->
            <div class="bg-white rounded-lg p-4 shadow-lg">
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
            <div class="bg-white rounded-lg p-4 shadow-lg">
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
            <div class="bg-white rounded-lg p-4 shadow-lg">
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
                <div class="flex items-center gap-4">
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
                v-for="post in profileData.posts"
                :key="post.id"
                :id="post.id"
                :username="post.username"
                :avatar="post.avatar"
                :image="post.image"
                :caption="post.caption"
                :time-ago="post.timeAgo"
                :like-count="post.likeCount"
                :comment-count="post.commentCount"
              />
            </div>

            <!-- Group Chat Tab Content -->
            <div v-if="activeTab === 'groupChat'" class="bg-white rounded-lg p-6 shadow-lg text-center text-gray-500">
              <TeamOutlined class="text-4xl mb-2" />
              <p>{{ $t('groupChat') }}</p>
            </div>

            <!-- Friends Tab Content -->
            <div v-if="activeTab === 'friends'" class="bg-white rounded-lg p-6 shadow-lg text-center text-gray-500">
              <UserOutlined class="text-4xl mb-2" />
              <p>{{ $t('friends') }}</p>
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
import { onMounted, ref } from 'vue';
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

const activeTab = ref('posts');

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
  posts: [
    {
      id: '1',
      username: 'Tuyết Nhi',
      avatar: 'https://testingbot.com/free-online-tools/random-avatar/100',
      image: 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRypbU3w47GYrpMjBwWbSwrsacu1UfaY4DGLw&s',
      caption: 'Mẫu mới vừa ra mắt ạ',
      timeAgo: '2 giờ trước',
      likeCount: 124,
      commentCount: 42,
    },
  ],
});

onMounted(() => {
  const authStore = useAuthStore();
  const currentUser:User | null = authStore.getUser;
  profileData.value.username = currentUser?.ho + ' ' + currentUser?.ten;
  profileData.value.avatar = currentUser?.anhDaiDien || 'https://testingbot.com/free-online-tools/random-avatar/500';
  profileData.value.bio = currentUser?.gioiThieu || 'Bạn hiện chưa có giới thiệu nào.';

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

