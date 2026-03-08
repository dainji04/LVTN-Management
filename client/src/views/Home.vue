<template>
    <div class="home-container flex h-screen bg-gray-50 overflow-hidden">
        <!-- Left Sidebar -->
        <aside class="sidebar w-64 bg-white border-r border-gray-200 flex flex-col fixed left-0 top-0 h-full overflow-y-auto">
            <!-- Logo -->
            <div class="p-6">
                <img src="../assets/logo.jpg" alt="AloChat" class="logo-img" />
            </div>

            <!-- Navigation -->
            <nav class="flex-1 px-4">
                <SidebarNavItem
                    to="/"
                    :icon="HomeOutlined"
                    :label="$t('home')"
                />
                <SidebarNavItem
                    to="/search"
                    :icon="SearchOutlined"
                    :label="$t('search')"
                />
                <SidebarNavItem
                    to="/explore"
                    :icon="CompassOutlined"
                    :label="$t('explore')"
                />
                <SidebarNavItem
                    to="/reels"
                    :icon="PlayCircleOutlined"
                    :label="$t('reels')"
                />
                <SidebarNavItem
                    to="/messages"
                    :icon="MessageOutlined"
                    :label="$t('messages')"
                />
                <SidebarNavItem
                    to="/notifications"
                    :icon="BellOutlined"
                    :label="$t('notifications')"
                    :badge="2"
                />
                <SidebarNavItem
                    to="/create"
                    :icon="PlusCircleOutlined"
                    :label="$t('create')"
                />
                <SidebarNavItem
                    to="/profile"
                    :icon="UserOutlined"
                    :label="$t('profile')"
                />
            </nav>

            <!-- Bottom -->
            <div class="p-4 border-t border-gray-200 flex items-center justify-between">
                <span class="text-sm font-semibold">AloChat</span>
                <MenuOutlined class="text-xl" />
            </div>
        </aside>

        <!-- Main Content -->
        <main class="main-content flex-1 ml-64 overflow-y-auto">
            <div class="max-w-2xl mx-auto px-4 py-6">
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
                    <Story
                        username="Thêm"
                        :is-add="true"
                    />
                </div>

                <!-- Posts -->
                <div class="posts-container">
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
        <aside class="right-sidebar w-80 bg-white border-l border-gray-200 fixed right-0 top-0 h-full overflow-y-auto hidden xl:block">
            <div class="p-6">
                <!-- Suggestions Section 1 -->
                <div class="mb-6">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="font-semibold text-gray-500 text-sm">{{ $t('suggestionsForYou') }}</h3>
                        <button class="text-sm text-primary font-semibold hover:opacity-70">
                            {{ $t('viewAll') }}
                        </button>
                    </div>
                    <div class="space-y-3">
                        <SuggestionItem
                            username="minhvn"
                            avatar="https://via.placeholder.com/40"
                            description="Bàm ngọc đụ vì nhân"
                        />
                        <SuggestionItem
                            username="huy123"
                            avatar="https://via.placeholder.com/40"
                            description="Bàm ngọc đụ vì nhân"
                        />
                        <SuggestionItem
                            username="vy.xinh"
                            avatar="https://via.placeholder.com/40"
                            description="Bàm ngọc đụ vì nhân"
                        />
                    </div>
                </div>

                <!-- Suggestions Section 2 -->
                <div class="mb-6">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="font-semibold text-gray-500 text-sm">{{ $t('suggestionsForYou') }}</h3>
                    </div>
                    <div class="space-y-3">
                        <SuggestionItem
                            username="quyen_le"
                            avatar="https://via.placeholder.com/40"
                            description="Bàm ngọc đụ vì nhân"
                        />
                    </div>
                </div>

                <!-- Footer Links -->
                <div class="footer-links text-xs text-gray-500 space-y-2 mb-4">
                    <div class="flex flex-wrap gap-2">
                        <a href="#" class="hover:underline">{{ $t('about') }}</a>
                        <a href="#" class="hover:underline">{{ $t('help') }}</a>
                        <a href="#" class="hover:underline">{{ $t('press') }}</a>
                        <a href="#" class="hover:underline">{{ $t('api') }}</a>
                        <a href="#" class="hover:underline">{{ $t('jobs') }}</a>
                        <a href="#" class="hover:underline">{{ $t('privacy') }}</a>
                        <a href="#" class="hover:underline">{{ $t('terms') }}</a>
                    </div>
                </div>

                <!-- Copyright -->
                <div class="text-xs text-gray-500">
                    © 2024 ALOCHAT
                </div>
            </div>
        </aside>
    </div>
</template>

<script lang="ts" setup>
import {
    HomeOutlined,
    SearchOutlined,
    CompassOutlined,
    PlayCircleOutlined,
    MessageOutlined,
    BellOutlined,
    PlusCircleOutlined,
    UserOutlined,
    MenuOutlined,
} from '@ant-design/icons-vue';
import SidebarNavItem from '../components/SidebarNavItem.vue';
import Story from '../components/Story.vue';
import Post from '../components/Post.vue';
import SuggestionItem from '../components/SuggestionItem.vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
</script>

<style scoped>
.home-container {
    font-family: system-ui, -apple-system, sans-serif;
}

.sidebar {
    z-index: 10;
}

.main-content {
    margin-left: 16rem;
}

.right-sidebar {
    z-index: 10;
}

.logo-img {
    max-width: 150px;
    height: auto;
    object-fit: contain;
}

.stories-container {
    scrollbar-width: none;
    -ms-overflow-style: none;
}

.stories-container::-webkit-scrollbar {
    display: none;
}

/* Responsive */
@media (max-width: 1280px) {
    .right-sidebar {
        display: none;
    }
}

@media (max-width: 768px) {
    .sidebar {
        width: 80px;
    }

    .main-content {
        margin-left: 80px;
    }

    .logo-img {
        max-width: 60px;
    }
}
</style>
