<script setup lang="ts">
import { ref } from "vue";

// Khai báo mảng dữ liệu quy tắc nhóm để dễ dàng chỉnh sửa hoặc lấy từ API (Laravel)
const rules = ref([
  {
    id: 1,
    title: "Hãy tử tế và lịch sự",
    description: "Không sử dụng từ ngữ khiếm nhã trong nhóm",
  },
  {
    id: 2,
    title: "Không quảng cáo hoặc spam",
    description: "Ngăn chặn các hành vi spam hoặc quảng cáo",
  },
  {
    id: 3,
    title: "Tôn trọng quyền riêng tư",
    description: "Tôn trọng quyền riêng tư của các thành viên trong nhóm",
  },
]);
</script>

<template>
  <div
    class="bg-white rounded-2xl p-5 shadow-sm border border-slate-100 max-w-sm"
  >
    <h3
      class="text-[13px] font-bold text-slate-500 uppercase tracking-wide mb-4"
    >
      Quy tắc nhóm
    </h3>

    <div class="flex flex-col gap-4">
      <div v-for="(rule, index) in rules" :key="rule.id" class="flex flex-col">
        <h4 class="text-sm font-semibold text-slate-800 leading-tight mb-1">
          {{ index + 1 }}. {{ rule.title }}
        </h4>

        <p class="text-xs text-slate-500 leading-relaxed">
          {{ rule.description }}
        </p>
      </div>
    </div>
  </div>
</template>
