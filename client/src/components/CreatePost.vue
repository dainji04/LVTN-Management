<script setup lang="ts">
import { ref } from "vue";
import {
  PictureOutlined,
  PaperClipOutlined,
  SmileOutlined,
} from "@ant-design/icons-vue";

// Biến lưu trữ nội dung người dùng nhập
const postContent = ref("");

// Hàm xử lý khi bấm đăng bài
const handlePost = () => {
  if (!postContent.value.trim()) return;

  console.log("Nội dung chuẩn bị đăng:", postContent.value);
  // Gọi hàm API đăng bài ở đây

  // Xóa nội dung sau khi đăng thành công
  // postContent.value = '';
};
</script>

<template>
  <div class="w-full bg-white rounded-xl border border-gray-100 p-4 shadow-lg">
    <div class="flex items-center gap-3">
      <div
        class="w-10 h-10 shrink-0 rounded-full overflow-hidden bg-orange-200 border border-gray-200"
      >
        <img
          src="https://via.placeholder.com/40"
          alt="Avatar"
          class="w-full h-full object-cover"
        />
      </div>

      <input
        v-model="postContent"
        type="text"
        placeholder="Viết gì đó cho nhóm..."
        class="flex-1 bg-slate-50 hover:bg-slate-100 focus:bg-white border border-transparent focus:border-gray-200 rounded-full px-4 py-2.5 text-gray-700 outline-none transition-all duration-200 placeholder:text-gray-500"
        @keyup.enter="handlePost"
      />
    </div>

    <div class="h-px bg-gray-100 my-3"></div>

    <div class="flex items-center justify-between mt-1">
      <div class="flex items-center gap-1 sm:gap-2">
        <button
          class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-slate-50 text-gray-500 transition-colors cursor-pointer"
        >
          <PictureOutlined class="text-primary text-xl" />
          <span class="text-sm font-medium hidden sm:block">Ảnh/Video</span>
        </button>

        <button
          class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-slate-50 text-gray-500 transition-colors cursor-pointer"
        >
          <PaperClipOutlined class="text-primary text-xl" />
          <span class="text-sm font-medium hidden sm:block">Tệp</span>
        </button>

        <button
          class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-slate-50 text-gray-500 transition-colors cursor-pointer"
        >
          <SmileOutlined class="text-primary text-xl" />
          <span class="text-sm font-medium hidden sm:block">Cảm xúc</span>
        </button>
      </div>

      <button
        @click="handlePost"
        :disabled="!postContent.trim()"
        class="bg-primary hover:bg-primary/90 text-white disabled:opacity-60 disabled:cursor-not-allowed text-sm font-medium py-2 px-5 rounded-lg transition-colors"
      >
        Đăng bài viết
      </button>
    </div>
  </div>
</template>
