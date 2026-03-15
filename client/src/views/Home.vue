<template>
  <AuthLayout>
    <!-- Left Sidebar -->
    <template #left-sidebar>
      <SideBar />
    </template>

    <!-- Main Content -->
    <main
      class="main-content w-full flex-1 flex justify-center overflow-y-auto"
    >
      <div class="py-6">
        <!-- Stories -->
        <div class="stories-container flex gap-4 mb-6 overflow-x-auto pb-4">
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
        <div class="posts-container w-[600px]">
          <Post
            id="1"
            username="quang"
            avatar="https://testingbot.com/free-online-tools/random-avatar/300"
            image="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRypbU3w47GYrpMjBwWbSwrsacu1UfaY4DGLw&s"
            caption="Hôm nay trời đẹp quá"
            time-ago="2 giờ"
            :like-count="100"
            :comment-count="10"
          />
          <Post
            id="2"
            username="lan"
            avatar="https://testingbot.com/free-online-tools/random-avatar/400"
            image="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSldwR7RTUYwMvYM1BYXLYavtdQbVkshb1JCg&s"
            caption="Làm ly trà Sữa náo"
            time-ago="3 giờ"
            :like-count="50"
            :comment-count="5"
          />
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
import { onMounted } from "vue";
import axiosInstance from "../helpers/apiHelper";

onMounted(() => {
  getListPosts();
});

const getListPosts = async () => {
  try {
    const response = await axiosInstance.get("/bai-viet");
    console.log("Posts:", response.data);
  } catch (error) {
    
  }
}

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
