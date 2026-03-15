<script setup lang="ts">
import { GlobalOutlined, EyeOutlined } from "@ant-design/icons-vue";
</script>

<template>
  <div
    class="bg-white rounded-2xl p-5 shadow-sm border border-slate-100 max-w-sm"
  >
    <h3 class="text-lg font-bold text-slate-800 mb-3">Giới thiệu về nhóm</h3>

    <p class="text-sm text-slate-500 leading-relaxed mb-5">
      Cộng đồng chia sẻ kiến thức, kinh nghiệm và cập nhật tin tức mới nhất về
      hệ sinh thái React (ReactJS, React Native, Next.js, etc.) tại Việt Nam.
    </p>

    <div class="flex flex-col gap-4 mb-6">
      <div class="flex items-start gap-3">
        <GlobalOutlined class="text-primary text-xl mt-0.5 shrink-0" />
        <div class="flex flex-col">
          <h4 class="text-sm font-semibold text-slate-800 leading-tight">
            Nhóm Công khai
          </h4>
          <span class="text-xs text-slate-500 mt-0.5">
            Mọi người đều có thể thấy thành viên và bài viết trong nhóm.
          </span>
        </div>
      </div>

      <div class="flex items-start gap-3">
        <EyeOutlined class="text-primary text-xl mt-0.5 shrink-0" />
        <div class="flex flex-col">
          <h4 class="text-sm font-semibold text-slate-800 leading-tight">
            Công khai
          </h4>
          <span class="text-xs text-slate-500 mt-0.5">
            Mọi người đều có thể tìm thấy nhóm này.
          </span>
        </div>
      </div>
    </div>

    <button
      class="w-full bg-primary/10 hover:bg-primary/20 text-primary font-semibold text-sm py-2.5 px-4 rounded-xl transition-colors cursor-pointer"
    >
      Tìm hiểu thêm
    </button>
  </div>
</template>
