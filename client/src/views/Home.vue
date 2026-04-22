<template>
  <AuthLayout>
    <!-- Left Sidebar -->
    <template #left-sidebar>
      <SideBar />
    </template>

    <!-- Main Content -->
    <main
      ref="mainContentRef"
      class="main-content w-full flex-1 flex justify-center overflow-y-auto px-2 sm:px-4 lg:px-6"
      @scroll.passive="handleScroll"
    >
      <div class="w-full max-w-[640px] py-4 sm:py-6">
        <!-- Stories -->
        <div class="stories-container flex gap-3 sm:gap-4 mb-4 sm:mb-6 overflow-x-auto pb-3 sm:pb-4">
          <Story
            avatar="https://testingbot.com/free-online-tools/random-avatar/100"
            username="Quang"
          />
          <Story
            avatar="https://testingbot.com/free-online-tools/random-avatar/120"
            username="Lan"
          />
          <Story
            avatar="https://testingbot.com/free-online-tools/random-avatar/101"
            username="Minh"
          />
          <Story
            avatar="https://testingbot.com/free-online-tools/random-avatar/122"
            username="Huy"
          />
          <Story
            avatar="https://testingbot.com/free-online-tools/random-avatar/240"
            username="Vy"
          />
          <Story :username="$t('addStory')" :is-add="true" />
        </div>

        <!-- Posts -->
        <div class="posts-container w-full">
          <Post
            v-for="post in postStore.posts"
            :maNguoiDung="post.maNguoiDung"
            :id="post.maBaiViet"
            :key="post.maBaiViet"
            :username="post.hoTen"
            :avatar="resolveMediaUrl(post.anhDaiDienNguoiDang) || defaultAvatar"
            :image="resolveMediaUrl(post.danhSachAnh?.[0])"
            :noiDung="post.noiDung"
            :time-ago="formatTimeAgo(post.ngayTao)"
            :like-count="post.luotThich"
            :comment-count="post.luotBinhLuan"
            :isOwner="isOwner(post.maNguoiDung)"
            :quyen-rieng-tu="post.quyenRiengTu"
            :vi-tri="post.viTri"
            :mau-nen="post.mauNen"
            :danh-sach-anh="post.danhSachAnh"
            @deletePost="handleDeletePost"
          />

          <div v-if="postStore.loading" class="text-center text-gray-500 py-4">
            <PostSkeleton />
            <PostSkeleton />
            <PostSkeleton />
          </div>
          <div
            v-else-if="!postStore.hasNext && postStore.posts.length > 0"
            class="text-center text-gray-400 py-4"
          >
            {{ t("allPostsDisplayed") }}
          </div>
        </div>
      </div>
    </main>

    <!-- Right Sidebar -->
    <template #right-sidebar>
      <div class="p-6 flex flex-col justify-between h-full">
        <!-- Suggestions Section 1 -->
        <div class="top">
          <div class="mb-6">
            <div class="flex items-center justify-between mb-4">
              <h3 class="font-semibold text-gray-500 text-sm">
                {{ $t("suggestionsForYou") }}
              </h3>
              <button
                class="text-sm text-primary font-semibold hover:opacity-70"
              >
                {{ $t("viewAll") }}
              </button>
            </div>
            <div class="space-y-3">
              <SuggestionItem
                username="minhvn"
                avatar="https://testingbot.com/free-online-tools/random-avatar/100"
                description="Bàm ngọc đụ vì nhân"
              />
              <SuggestionItem
                username="huy123"
                avatar="https://testingbot.com/free-online-tools/random-avatar/100"
                description="Bàm ngọc đụ vì nhân"
              />
              <SuggestionItem
                username="vy.xinh"
                avatar="https://testingbot.com/free-online-tools/random-avatar/100"
                description="Bàm ngọc đụ vì nhân"
              />
            </div>
          </div>
          <!-- Suggestions Section 2 -->
          <div class="mb-6">
            <div class="flex items-center justify-between mb-4">
              <h3 class="font-semibold text-gray-500 text-sm">
                {{ $t("suggestionsForYou") }}
              </h3>
            </div>
            <div class="space-y-3">
              <SuggestionItem
                username="quyen_le"
                avatar="https://testingbot.com/free-online-tools/random-avatar/100"
                description="Bàm ngọc đụ vì nhân"
              />
            </div>
          </div>
        </div>

        <!-- Footer Links -->
        <div class="footer-links text-xs text-gray-500 space-y-2 mb-4">
          <div class="flex flex-wrap gap-2">
            <a href="#" class="hover:underline">{{ $t("about") }}</a>
            <a href="#" class="hover:underline">{{ $t("help") }}</a>
            <a href="#" class="hover:underline">{{ $t("press") }}</a>
            <a href="#" class="hover:underline">{{ $t("api") }}</a>
            <a href="#" class="hover:underline">{{ $t("jobs") }}</a>
            <a href="#" class="hover:underline">{{ $t("privacy") }}</a>
            <a href="#" class="hover:underline">{{ $t("terms") }}</a>
          </div>

          <!-- Copyright -->
          <div class="text-xs text-gray-500">© 2024 ALOCHAT</div>
        </div>
      </div>
    </template>
  </AuthLayout>
</template>

<script lang="ts" setup>
import Story from "../components/Story.vue";
import Post from "../components/Post.vue";
import SuggestionItem from "../components/SuggestionItem.vue";
import AuthLayout from "../layouts/authLayout.vue";
import { onMounted, ref } from "vue";
import { usePostStore } from "../store/postStore";
import { resolveMediaUrl } from "../helpers/mediaHelper";
import { useI18n } from "vue-i18n";
import PostSkeleton from "../components/PostSkeleton.vue";
import { useAuthStore } from "../store/authStore";

const postStore = usePostStore();
const authStore = useAuthStore();
const { t } = useI18n();
const mainContentRef = ref<HTMLElement | null>(null);
const defaultAvatar = "https://testingbot.com/free-online-tools/random-avatar/100";
const VIRTUAL_SCROLL_THRESHOLD = 350;


onMounted(() => {
  postStore.fetchFirstPage();
});

const handleScroll = async () => {
  const el = mainContentRef.value;
  if (!el || postStore.loading || !postStore.hasNext) return;

  const distanceToBottom = el.scrollHeight - el.scrollTop - el.clientHeight;
  if (distanceToBottom <= VIRTUAL_SCROLL_THRESHOLD) {
    await postStore.fetchNextPage();
  }
};

const formatTimeAgo = (date: string): string => {
  const createdAt = new Date(date);
  const now = new Date();
  const diffMs = now.getTime() - createdAt.getTime();

  if (Number.isNaN(diffMs) || diffMs < 0) return t("justNow");

  const minuteMs = 60 * 1000;
  const hourMs = 60 * minuteMs;
  const dayMs = 24 * hourMs;

  if (diffMs < hourMs) {
    return `${Math.max(1, Math.floor(diffMs / minuteMs))} ${t("minutes")}`;
  }
  if (diffMs < dayMs) {
    return `${Math.floor(diffMs / hourMs)} ${t("hours")}`;
  }

  return `${Math.floor(diffMs / dayMs)} ${t("days")}`;
};

const handleDeletePost = (postId: number) => {
  postStore.posts = postStore.posts.filter(post => post.maBaiViet !== postId);
};

const isOwner = (maNguoiDung: number) => {
  return maNguoiDung === authStore.getUser?.maNguoiDung;
};

</script>

<style scoped>
.home-container {
  font-family: system-ui, -apple-system, sans-serif;
}

.sidebar {
  z-index: 10;
}

.main-content {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* hide scroll bar in chrome */
.main-content::-webkit-scrollbar {
  display: none;
}

.right-sidebar {
  z-index: 10;
}

.logo-img {
  max-width: 150px;
  height: auto;
  object-fit: contain;
}

</style>
