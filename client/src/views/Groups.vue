<template>
    <AuthLayout>
        <template #left-sidebar>
            <SideBar />
        </template>

        <!-- Main Content -->
        <main class="main-content w-full flex-1 overflow-y-auto bg-[#f5f5f5]">
            <div class="max-w-7xl mx-auto px-4 py-6">
                <!-- Header with Tabs -->
                <div class="bg-white rounded-lg shadow-sm mb-6">
                    <div class="flex items-center justify-between p-4 border-b border-gray-100">
                        <div class="flex items-center gap-1">
                            <Button
                                :type="activeTab === 'discover' ? 'primary' : 'text'"
                                classes="px-6"
                                @click="activeTab = 'discover'"
                            >
                                {{ $t('discoverGroups') }}
                            </Button>
                            <Button
                                :type="activeTab === 'yourGroups' ? 'primary' : 'text'"
                                classes="px-6"
                                @click="activeTab = 'yourGroups'"
                            >
                                {{ $t('yourGroups') }}
                            </Button>
                        </div>
                        <Button type="primary">
                            <template #icon>
                                <PlusOutlined class="mr-1" />
                            </template>
                            {{ $t('createNewGroup') }}
                        </Button>
                    </div>
                </div>

                <!-- Discover Groups Content -->
                <div v-if="activeTab === 'discover'">
                    <!-- Section Title -->
                    <h2 class="text-2xl font-bold mb-4 text-gray-800">{{ $t('discoverCommunity') }}</h2>

                    <!-- Search Bar -->
                    <div class="mb-6">
                        <a-input v-model:value="searchQuery" :placeholder="$t('searchGroupsPlaceholder')" size="large"
                            class="rounded-lg">
                            <template #prefix>
                                <SearchOutlined class="text-gray-400" />
                            </template>
                        </a-input>
                    </div>

                    <!-- Category Filters -->
                    <div class="flex items-center gap-2 mb-6 flex-wrap">
                        <Button
                            v-for="category in categories"
                            :key="category.key"
                            :type="selectedCategory === category.key ? 'primary' : 'default'"
                            classes="rounded-full"
                            @click="selectedCategory = category.key"
                        >
                            {{ category.label }}
                        </Button>
                    </div>

                    <!-- Groups Grid -->
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
                        <GroupCard v-for="group in groupStore.groups" :key="group.id" :group="group" @join="handleJoinGroup" />
                    </div>
                </div>

                <!-- Your Groups Content -->
                <div v-if="activeTab === 'yourGroups'" class="bg-white rounded-lg p-8 text-center text-gray-500">
                    <TeamOutlined class="text-5xl mb-4 text-gray-300" />
                    <p class="text-lg">{{ $t('yourGroups') }}</p>
                </div>
            </div>
        </main>

        <!-- Right Sidebar -->
        <template #right-sidebar>
            <div class="p-6 space-y-6">
                <!-- Suggestions -->
                <div class="bg-white rounded-lg p-4 shadow-sm">
                    <h3 class="font-semibold text-lg mb-4 text-gray-800">{{ $t('suggestionsForYou') }}</h3>
                    <div class="space-y-4">
                        <div v-for="suggestion in suggestions" :key="suggestion.id"
                            class="flex items-center justify-between">
                            <div class="flex items-center gap-3">
                                <a-avatar :size="40" :src="suggestion.avatar">
                                    <template #icon>
                                        <UserOutlined />
                                    </template>
                                </a-avatar>
                                <div>
                                    <h4 class="font-semibold text-sm text-gray-800">{{ suggestion.name }}</h4>
                                    <p class="text-xs text-gray-500">{{ suggestion.members }} {{ $t('members') }}</p>
                                </div>
                            </div>
                            <Button type="primary" size="small">
                                <template #icon>
                                    <PlusOutlined />
                                </template>
                            </Button>
                        </div>
                    </div>
                </div>

                <!-- Trending Topics -->
                <div class="bg-white rounded-lg p-4 shadow-sm">
                    <h3 class="font-semibold text-lg mb-4 text-gray-800">{{ $t('trendingTopics') }}</h3>
                    <div class="flex flex-wrap gap-2">
                        <a-tag v-for="topic in trendingTopics" :key="topic" color="primary"
                            class="cursor-pointer hover:opacity-80">
                            {{ topic }}
                        </a-tag>
                    </div>
                </div>

                <!-- Connect More CTA -->
                <div class="bg-primary rounded-lg p-4 text-white">
                    <h3 class="font-semibold text-lg mb-2">{{ $t('connectMore') }}</h3>
                    <p class="text-sm mb-4 opacity-90">{{ $t('joinLocalGroups') }}</p>
                    <Button type="default" :block="true" classes="bg-white text-primary hover:bg-gray-100">
                        {{ $t('discoverNow') }}
                    </Button>
                </div>
            </div>
        </template>
    </AuthLayout>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import {
    PlusOutlined,
    SearchOutlined,
    TeamOutlined,
    UserOutlined,
} from '@ant-design/icons-vue';
import AuthLayout from '../layouts/authLayout.vue';
import SideBar from '../components/SideBar.vue';
import Button from '../components/Button.vue';
import GroupCard from '../components/GroupCard.vue';
import { notificationHelper } from '../helpers/notificationHelper';
import { useI18n } from 'vue-i18n';
import type { Group } from '../types/groupType';
import { useGroupStore } from '../store/groupStore';
import type { GroupItem } from '../types/groupType';

const { t } = useI18n();

const activeTab = ref('discover');
const searchQuery = ref('');
const selectedCategory = ref('all');
const groupStore = useGroupStore();

const categories = [
    { key: 'all', label: t('all') },
    { key: 'technology', label: t('technology') },
    { key: 'photography', label: t('photography') },
    { key: 'cooking', label: t('cooking') },
    { key: 'travel', label: t('travel') },
    { key: 'sports', label: t('sports') },
];

const groups = ref<Group[]>([]);

const suggestions = ref([
    {
        id: '1',
        name: 'Thế giới Code',
        members: '15.2k',
        avatar: 'https://testingbot.com/free-online-tools/random-avatar/200',
    },
    {
        id: '2',
        name: 'Hội Cẩm Trại Đêm',
        members: '8.7k',
        avatar: 'https://testingbot.com/free-online-tools/random-avatar/201',
    },
]);

const trendingTopics = ref([
    '#TechNews',
    '#Photography',
    '#CookingChallenge',
    '#WeekendVibes',
    '#ReactConf2024',
]);

onMounted(() => {
    groupStore.loadListGroup();
})

// const filteredGroups = computed(() => {
//     let result = groups.value;

//     // Filter by category
//     if (selectedCategory.value !== 'all') {
//         result = result.filter((group) => group.category === selectedCategory.value);
//     }

//     // Filter by search query
//     if (searchQuery.value.trim()) {
//         const query = searchQuery.value.toLowerCase();
//         result = result.filter((group) =>
//             group.title.toLowerCase().includes(query)
//         );
//     }

//     return result;
// });

const handleJoinGroup = (groupId: number) => {
    notificationHelper('success', t('joinGroup') + ' ' + t('loginSuccess'));
    // Handle join group logic here
    console.log('Joining group:', groupId);
};
</script>

<style scoped>
.main-content {
    -ms-overflow-style: none;
    scrollbar-width: none;
}

.main-content::-webkit-scrollbar {
    display: none;
}
</style>
