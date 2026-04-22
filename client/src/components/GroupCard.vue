<template>
    <div @click="goToDetailGroup(group)"
        class="group-card bg-white rounded-lg shadow-lg overflow-hidden 
        hover:shadow-xl transition-shadow cursor-pointer"
    >
        <!-- Group Image -->
        <div class="group-image w-full h-48 overflow-hidden">
            <img :src="anhBia" :alt="group.tenNhom" class="w-full h-full object-cover" />
        </div>

        <!-- Group Info -->
        <div class="p-4">
            <h3 class="font-bold text-lg mb-2 text-gray-800">{{ group.tenNhom }}</h3>
            <p class="text-gray-600 text-sm mb-3">{{ group.soThanhVien }} {{ $t('members') }}</p>

            <!-- Member Avatars -->
            <!-- <div class="flex items-center gap-2 mb-4">
                <div class="flex -space-x-2">
                    <a-avatar v-for="(avatar, index) in group.anhDaiDien" :key="index" :size="32" :src="avatar"
                        class="border-2 border-white">
                        <template #icon>
                            <UserOutlined />
                        </template>
                    </a-avatar>
                </div>
                <span class="text-sm text-gray-500 ml-2">+{{ group.soThanhVien }}</span>
            </div> -->

            <!-- Join Button -->
            <Button type="primary" :block="true" @click.stop="handleJoin">
                {{ $t('joinGroup') }}
            </Button>
        </div>
    </div>
</template>

<script lang="ts" setup>
// import { UserOutlined } from '@ant-design/icons-vue';
import { useRouter } from 'vue-router';
import Button from './Button.vue';
import type { GroupItem } from '../types/groupType';
import { computed } from 'vue';

const router = useRouter();

const props = defineProps<{
    group: GroupItem;
}>();

const emit = defineEmits<{
    join: [groupId: number];
}>();

const handleJoin = () => {
    emit('join', props.group.id);
};

const goToDetailGroup = (group: GroupItem) => {
    router.push(`/group/${group.id}`);
};

const anhBia = computed(() => {
    return props.group.anhBia ?? 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPQDHr44w7T579OHXCSVyqpb_vztOyuN3K6Q&s';
});
</script>

<style scoped>
.group-card {
    transition: transform 0.2s;
}

.group-card:hover {
    transform: translateY(-2px);
}
</style>
