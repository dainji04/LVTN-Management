<template>
  <AuthLayout :hide-right-sidebar="true">
    <template #left-sidebar>
      <SideBar />
    </template>

    <!-- Main Content -->
    <main class="main-content w-full flex-1 flex justify-center overflow-y-auto bg-[#f5f5f5]">
      <div class="w-full max-w-2xl px-4 py-6">
        <!-- Create Post Card -->
        <div class="bg-white rounded-lg shadow-lg p-6">
          <!-- User Profile Section -->
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-3">
              <a-avatar
                :size="50"
                :src="userAvatar"
                class="border-2 border-orange-400"
              >
                <template #icon>
                  <UserOutlined />
                </template>
              </a-avatar>
              <div>
                <h3 class="font-bold text-gray-800">{{ userName }}</h3>
                <a-dropdown :trigger="['click']">
                  <Button type="text" classes="p-0 h-auto text-gray-600 hover:text-primary">
                    <template #icon>
                      <UserOutlined class="text-xs mr-1" />
                    </template>
                    {{ privacySetting }}
                    <DownOutlined class="text-xs ml-1" />
                  </Button>
                  <template #overlay>
                    <a-menu @click="handlePrivacyChange">
                      <a-menu-item key="friends">
                        <UserOutlined class="mr-2" />
                        {{ $t('friends') }}
                      </a-menu-item>
                      <a-menu-item key="public">
                        <GlobalOutlined class="mr-2" />
                        {{ $t('public') }}
                      </a-menu-item>
                      <a-menu-item key="onlyMe">
                        <LockOutlined class="mr-2" />
                        {{ $t('onlyMe') }}
                      </a-menu-item>
                    </a-menu>
                  </template>
                </a-dropdown>
              </div>
            </div>
            <MoreOutlined class="text-xl text-gray-600 cursor-pointer hover:text-primary" />
          </div>

          <!-- Text Input Area -->
          <div class="mb-4">
            <a-textarea
              v-model:value="postContent"
              :placeholder="$t('whatAreYouThinking')"
              :rows="6"
              class="rounded-lg border-gray-200"
              :class="{ 'border-primary': postContent }"
            />
          </div>

          <!-- Preview Image -->
          <div v-if="selectedImage" class="mb-4 relative">
            <img
              :src="selectedImage"
              alt="Preview"
              class="w-full rounded-lg max-h-96 object-cover"
            />
            <button
              @click="removeImage"
              class="absolute top-2 right-2 bg-black/50 hover:bg-black/70 text-white rounded-full w-8 h-8 flex items-center justify-center"
            >
              <CloseOutlined />
            </button>
          </div>

          <!-- Post Options -->
          <div class="flex items-center justify-between mb-4 pb-4 border-b border-gray-200">
            <div class="flex items-center gap-4">
              <label class="flex items-center gap-2 cursor-pointer hover:text-primary transition-colors">
                <input
                  type="file"
                  accept="image/*,video/*"
                  class="hidden"
                  @change="handleImageSelect"
                />
                <PictureOutlined class="text-primary text-xl" />
                <span class="text-sm font-medium">{{ $t('images/videos') }}</span>
              </label>
              <button
                class="flex items-center gap-2 hover:text-primary transition-colors"
                @click="showEmojiPicker = !showEmojiPicker"
              >
                <SmileOutlined class="text-yellow-500 text-xl" />
                <span class="text-sm font-medium">{{ $t('feeling') }}</span>
              </button>
            </div>
            <a-dropdown :trigger="['click']">
              <Button type="text" classes="p-0 h-auto text-gray-600 hover:text-primary">
                <template #icon>
                  <GlobalOutlined class="text-xs mr-1" />
                </template>
                {{ privacySetting }}
                <DownOutlined class="text-xs ml-1" />
              </Button>
              <template #overlay>
                <a-menu @click="handlePrivacyChange">
                  <a-menu-item key="friends">
                    <UserOutlined class="mr-2" />
                    {{ $t('friends') }}
                  </a-menu-item>
                  <a-menu-item key="public">
                    <GlobalOutlined class="mr-2" />
                    {{ $t('public') }}
                  </a-menu-item>
                  <a-menu-item key="onlyMe">
                    <LockOutlined class="mr-2" />
                    {{ $t('onlyMe') }}
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>

          <!-- Post Button -->
          <div class="flex justify-end">
            <Button
              type="primary"
              classes="rounded-lg px-8"
              :isDisabled="isSubmitting || (!postContent.trim() && !selectedImage)"
              @click="handlePost"
            >
              {{ isSubmitting ? $t('posting') : $t('post') }}
            </Button>
          </div>
        </div>
      </div>
    </main>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import {
  UserOutlined,
  DownOutlined,
  MoreOutlined,
  PictureOutlined,
  SmileOutlined,
  GlobalOutlined,
  LockOutlined,
  CloseOutlined,
} from '@ant-design/icons-vue';
import AuthLayout from '../layouts/authLayout.vue';
import SideBar from '../components/SideBar.vue';
import Button from '../components/Button.vue';
import { notificationHelper } from '../helpers/notificationHelper';
import { useI18n } from 'vue-i18n';
import axiosInstance from '../helpers/apiHelper';
import { useAuthStore } from '../store/authStore';
import { resolveMediaUrl } from '../helpers/mediaHelper';

const { t } = useI18n();
const authStore = useAuthStore();

const userName = ref(
  authStore.getUser ? `${authStore.getUser.ho} ${authStore.getUser.ten}` : 'Nguoi dung'
);
const userAvatar = ref(
  resolveMediaUrl(authStore.getUser?.anhDaiDien) || 'https://testingbot.com/free-online-tools/random-avatar/100'
);
const postContent = ref('');
const selectedImage = ref<string | null>(null);
const privacySetting = ref(t('friends'));
const privacyKey = ref<'friends' | 'public' | 'onlyMe'>('friends');
const showEmojiPicker = ref(false);
const isSubmitting = ref(false);

interface CreatePostRequest {
  maNguoiDung: number;
  noiDung: string;
  quyenRiengTu: string;
  viTri: string;
  mauNen: string;
  danhSachCongTacVien: number[];
  danhSachAnh: string[];
}

const handleImageSelect = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      selectedImage.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const removeImage = () => {
  selectedImage.value = null;
};

const handlePrivacyChange = ({ key }: { key: string }) => {
  if (key === 'friends') {
    privacyKey.value = 'friends';
    privacySetting.value = t('friends');
  } else if (key === 'public') {
    privacyKey.value = 'public';
    privacySetting.value = t('public');
  } else if (key === 'onlyMe') {
    privacyKey.value = 'onlyMe';
    privacySetting.value = t('onlyMe');
  }
};

const handlePost = async () => {
  if (!postContent.value.trim() && !selectedImage.value) {
    return;
  }

  if (!authStore.getUser?.maNguoiDung) {
    notificationHelper('error', t('cannotFindUserInfo'));
    return;
  }

  isSubmitting.value = true;
  try {
    const payload: CreatePostRequest = {
      maNguoiDung: authStore.getUser.maNguoiDung,
      noiDung: postContent.value.trim(),
      quyenRiengTu: privacyKey.value,
      viTri: '',
      mauNen: '',
      danhSachCongTacVien: [],
      danhSachAnh: selectedImage.value ? [selectedImage.value] : [],
    };

    const response = await axiosInstance.post('/bai-viet', payload);
    if (response.data?.code == 200 || response.data?.code == 201) {
      notificationHelper('success', t('createPostSuccess'));
      postContent.value = '';
      selectedImage.value = null;
      privacyKey.value = 'friends';
      privacySetting.value = t('friends');
    } else {
      notificationHelper('error', response.data?.message || t('createPostFailed'));
    }
  } catch (error: any) {
    notificationHelper(
      'error',
      error?.response?.data?.message || t('cannotCreatePostNow')
    );
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.main-content {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.main-content::-webkit-scrollbar {
  display: none;
}

:deep(.ant-textarea) {
  border-radius: 8px;
  font-size: 16px;
  resize: none;
}

:deep(.ant-textarea:focus) {
  border-color: #FF436D;
  box-shadow: 0 0 0 2px rgba(255, 67, 109, 0.1);
}
</style>

