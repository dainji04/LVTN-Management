<template>
  <div
    class="home-container flex h-screen bg-[#f5f5f5] overflow-hidden"
  >
    <!-- Left Sidebar -->
    <div
      v-if="!hideLeftSidebar"
      class="sidebar hidden lg:flex lg:w-64 bg-white border-r border-gray-200 flex-col h-full overflow-y-auto"
    >
      <slot name="left-sidebar"></slot>
    </div>
    <!-- Main content -->
    <main
      class="main-content w-full flex-1 flex justify-center overflow-y-auto min-w-0 lg:pb-0"
    >
      <slot></slot>
    </main>
    <!-- Right Sidebar -->
    <div
      v-if="!hideRightSidebar"
      class="right-sidebar w-80 bg-white border-l border-gray-200 justify-end h-full overflow-y-auto hidden xl:flex"
    >
      <slot name="right-sidebar"></slot>
    </div>

    <!-- Mobile Bottom Navigation -->
    <nav
      v-if="!hideMobileBottomNav"
      class="mobile-bottom-nav lg:hidden fixed bottom-4 left-1/2 -translate-x-1/2 z-50 bg-white/95 backdrop-blur-sm border border-gray-200 rounded-2xl shadow-xl px-3 py-2 flex items-center gap-1"
    >
      <router-link
        v-for="item in mobileNavItems"
        :key="item.to"
        :to="item.to"
        class="mobile-nav-item relative w-12 h-12 rounded-xl flex items-center justify-center transition-colors"
        :class="isActive(item.to) ? 'text-primary bg-rose-50' : 'text-gray-700 hover:bg-gray-100'"
      >
        <component :is="item.icon" class="text-xl" />
        <span
          v-if="isActive(item.to)"
          class="absolute -bottom-1.5 w-1.5 h-1.5 rounded-full bg-primary"
        ></span>
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
import {
  HomeOutlined,
  SearchOutlined,
  PlusCircleOutlined,
  BellOutlined,
  UserOutlined,
} from "@ant-design/icons-vue";
import { useRoute } from "vue-router";

const props = defineProps<{
  hideRightSidebar?: boolean;
  hideLeftSidebar?: boolean;
  hideMobileBottomNav?: boolean;
}>();

const route = useRoute();
const mobileNavItems = [
  { to: "/", icon: HomeOutlined },
  { to: "/users", icon: SearchOutlined },
  { to: "/create", icon: PlusCircleOutlined },
  { to: "/notifications", icon: BellOutlined },
  { to: "/profile", icon: UserOutlined },
];

const isActive = (path: string) => route.path === path;
</script>

<style>
.main-content::-webkit-scrollbar {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

/* Responsive */
@media (max-width: 1024px) {
  .logo-img {
    max-width: 60px;
  }
}
</style>
