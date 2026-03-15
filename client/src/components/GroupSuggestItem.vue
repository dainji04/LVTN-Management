<script setup lang="ts">
import { PlusCircleOutlined } from "@ant-design/icons-vue";

// Khai báo các props để component cha truyền dữ liệu vào
defineProps<{
  title: string;
  members: string;
  avatarText: string;
  avatarBgClass: string;
}>();
</script>

<template>
  <div class="flex items-center justify-between py-2">
    <div class="flex items-center gap-3">
      <div
        :class="[
          'w-11 h-11 rounded-xl flex items-center justify-center text-white font-bold text-sm shrink-0 shadow-sm',
          avatarBgClass,
        ]"
      >
        {{ avatarText }}
      </div>

      <div class="flex flex-col">
        <h4 class="text-sm font-semibold text-slate-800 leading-tight mb-0.5">
          {{ title }}
        </h4>
        <span class="text-xs text-slate-500">
          {{ members }}
        </span>
      </div>
    </div>

    <button
      class="text-primary hover:opacity-70 transition-opacity cursor-pointer"
    >
      <PlusCircleOutlined class="text-[22px]" />
    </button>
  </div>
</template>
