<template>
    <div class="suggestion-item flex items-center justify-between py-2">
        <div class="flex items-center gap-3">
            <img
                :src="avatar"
                :alt="username"
                class="w-10 h-10 rounded-full object-cover"
            />
            <div class="flex-1">
                <div class="font-semibold text-sm">{{ username }}</div>
                <div class="text-xs text-gray-500">{{ description }}</div>
            </div>
        </div>
        <Button
            :classes="'px-4 py-1 text-sm round'"
            :type="'primary'"
            @click="handleFollow"
        >
            {{ isFollowing ? 'Đang theo dõi' : 'Theo dõi' }}
        </Button>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import Button from './button.vue';

const props = defineProps<{
    username: string;
    avatar: string;
    description: string;
    initialFollowing?: boolean;
}>();

const emit = defineEmits<{
    follow: [username: string, value: boolean];
}>();

const isFollowing = ref(props.initialFollowing || false);

const handleFollow = () => {
    isFollowing.value = !isFollowing.value;
    emit('follow', props.username, isFollowing.value);
};
</script>

