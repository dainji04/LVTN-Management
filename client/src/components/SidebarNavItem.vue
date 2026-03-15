<template>
    <router-link
        :to="to"
        class="nav-item flex items-center py-3 px-4 rounded-lg transition-colors"
        :class="{ 'active': isActive, 'text-primary': isActive, 'text-black': !isActive }"
    >
        <component :is="icon" class="text-xl mr-3" />
        <span class="font-medium">{{ label }}</span>
        <span v-if="badge" class="ml-auto bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
            {{ badge }}
        </span>
    </router-link>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps<{
    to: string;
    icon: any;
    label: string;
    badge?: number | string;
    isActive?: boolean;
}>();

const route = useRoute();
const isActive = computed(() => props.isActive !== undefined ? props.isActive : route.path === props.to);
</script>

<style scoped>
.nav-item:hover {
    background-color: #f3f4f6;
}

.nav-item.active {
    background-color: #fef2f2;
}
</style>

