<template>
    <div v-if="avatar" class="story-item flex flex-col items-center cursor-pointer">
        <div class="story-avatar relative">
            <img
                :src="avatar"
                :alt="username"
                class="w-16 h-16 rounded-full object-cover border-2 border-primary"
            />
            <div v-if="isAdd" class="absolute bottom-0 right-0 bg-primary text-white rounded-full w-6 h-6 flex items-center justify-center border-2 border-white">
                <PlusOutlined class="text-xs" />
            </div>
        </div>
        <span class="text-xs mt-2 text-center max-w-[80px] truncate">{{ username }}</span>
    </div>
    <div v-else class="story-item flex flex-col items-center cursor-pointer">
        <div class="story-avatar relative">
            <div class="w-16 h-16 rounded-full object-cover border-2
                border-gray-400 flex justify-center items-center text-4xl">
                <svg 
                    xmlns="http://www.w3.org/2000/svg" 
                    class="w-10 h-10 text-gray-400 stroke-1" 
                    fill="none" 
                    viewBox="0 0 24 24" 
                    stroke="currentColor"
                >
                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
                </svg>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { PlusOutlined } from '@ant-design/icons-vue';

defineProps<{
    avatar?: string;
    username?: string;
    isAdd?: boolean;
}>();
</script>

<style scoped>
.story-avatar {
    width: 64px;
    height: 64px;
}
</style>

