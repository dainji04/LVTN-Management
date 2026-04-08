<template>
  <!-- Logo -->
  <div class="p-3 lg:p-6 flex justify-center lg:justify-start">
    <img src="../assets/logo.jpg" alt="AloChat" class="logo-img w-10 h-10" />
  </div>

  <!-- Navigation -->
  <nav class="flex-1 px-2 lg:px-4">
    <SidebarNavItem to="/" :icon="HomeOutlined" :label="$t('home')" />
    <SidebarNavItem to="/search" :icon="SearchOutlined" :label="$t('search')" />
    <SidebarNavItem to="/groups" :icon="TeamOutlined" :label="$t('groups')" />
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
    <SidebarNavItem to="/profile" :icon="UserOutlined" :label="$t('profile')" />
  </nav>

  <!-- Bottom -->
  <div class="p-4 border-t border-gray-200 items-center justify-between hidden lg:flex">
    <div class="group-user flex items-center space-x-3 cursor-pointer" @click="router.push('/profile')">
      <img
        class="w-10 h-10 rounded-full object-cover"
        :src="user?.anhDaiDien"
        alt="avatar"
      />
      <div class="flex flex-col justify-center">
        <span class="text-sm font-semibold">{{ user?.ho + ' ' + user?.ten }}</span>
        <p class="text-primary text-xs">{{ $t("seeProfile") }}</p>
      </div>
    </div>
    <ActionUser direction="topRight" />
  </div>
</template>

<script setup lang="ts">
import {
  HomeOutlined,
  SearchOutlined,
  CompassOutlined,
  PlayCircleOutlined,
  MessageOutlined,
  BellOutlined,
  PlusCircleOutlined,
  UserOutlined,
  TeamOutlined,
} from "@ant-design/icons-vue";
import SidebarNavItem from "../components/SidebarNavItem.vue";
import ActionUser from "./ActionUser.vue";
import { useAuthStore } from '../store/authStore';
import { useRouter } from 'vue-router';
const authStore = useAuthStore();
const user = authStore.user;
const router = useRouter();
</script>
