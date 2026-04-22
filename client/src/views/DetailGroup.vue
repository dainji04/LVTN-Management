<template>
  <AuthLayout :hide-right-sidebar="true">
    <template #left-sidebar>
      <SideBar />
    </template>
    <div class="group w-[calc(100%-256px)] grid gap-4">
      <div class="group-header w-full flex items-center flex-col">
        <div class="background-group w-full h-[400px] relative" :style="{ backgroundImage: `url(${group?.anhBia || 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPQDHr44w7T579OHXCSVyqpb_vztOyuN3K6Q&s'})` }">
          <div class="absolute bottom-5 left-5">
            <h1 class="name-group text-4xl font-bold mb-2 text-white">
              {{ group?.tenNhom }}
            </h1>
            <div>
              <div class="flex items-center gap-2">
                <template v-if="!isPrivate">
                  <GlobalOutlined class="flex item-center text-white" />
                  <h3 class="text-white">{{ $t("publicGroup") }}</h3>
                </template>
                <template v-else>
                  <LockOutlined class="flex item-center text-white" />
                  <h3 class="text-white">{{ $t("privateGroup") }}</h3>
                </template>
              </div>
            </div>
          </div>
        </div>
        <nav
          class="nav-bar-group bg-white flex items-center justify-between w-full h-[60px] shadow-lg"
        >
          <ul class="flex items-center">
            <li class="p-5 hover:underline decoration-primary">
              {{ $t("post") }}
            </li>
            <li class="p-5 hover:underline decoration-primary">
              {{ $t("introduction") }}
            </li>
            <li class="p-5 hover:underline decoration-primary">
              {{ $t("members") }}
            </li>
            <li class="p-5 hover:underline decoration-primary">
              {{ $t("images/videos") }}
            </li>
          </ul>
          <ul class="flex items-center">
            <li class="p-5 hover:underline decoration-primary">
              {{ $t("joinGroup") }}
            </li>
            <li class="p-5 hover:underline decoration-primary">
              <ShareAltOutlined />
            </li>
          </ul>
        </nav>
      </div>
      <div class="grid grid-cols-[5fr_2fr] gap-4">
        <div class="flex flex-col gap-4">
          <CreatePost class="" />
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
        </div>
        <div class="flex flex-col gap-6">
          <AboutGroup />
          <RulesGroup />
          <div
            class="bg-white rounded-2xl p-5 shadow-sm border border-slate-100 max-w-sm"
          >
            <h3 class="text-lg font-bold text-slate-800 mb-4">Gợi ý nhóm</h3>

            <div class="flex flex-col gap-3">
              <GroupSuggestItem
                v-for="group in suggestedGroups"
                :key="group.id"
                :title="group.title"
                :members="group.members"
                :avatar-text="group.avatarText"
                :avatar-bg-class="group.avatarBgClass"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import {
  GlobalOutlined,
  LockOutlined,
  ShareAltOutlined,
} from "@ant-design/icons-vue";
import SideBar from "../components/SideBar.vue";
import AuthLayout from "../layouts/authLayout.vue";
import { computed, onMounted, ref } from "vue";
import CreatePost from "../components/CreatePost.vue";
import GroupSuggestItem from "../components/GroupSuggestItem.vue";
import AboutGroup from "../components/AboutGroup.vue";
import RulesGroup from "../components/RulesGroup.vue";
import { useGroupStore } from "../store/groupStore";
import { useRoute } from "vue-router";
import type { GroupItem } from "../types/groupType";

const route = useRoute();
const groupStore = useGroupStore();

const groupId = ref(0);
const group = ref<GroupItem | null>(null);

const suggestedGroups = ref([
  {
    id: 1,
    title: "JavaScript Việt Nam",
    members: "45K thành viên",
    avatarText: "JS",
    avatarBgClass: "bg-blue-500", // Màu xanh dương
  },
  {
    id: 2,
    title: "VueJS Vietnam",
    members: "12K thành viên",
    avatarText: "VUE",
    avatarBgClass: "bg-emerald-500", // Màu xanh ngọc
  },
  {
    id: 3,
    title: "Next.js Ecosystem",
    members: "8.2K thành viên",
    avatarText: "NEXT",
    avatarBgClass: "bg-slate-800", // Màu đen/xám đậm
  },
]);

onMounted(async () => {
  groupId.value = parseInt(route.params.idGroup as string);

  await groupStore.getDetailGroup(groupId.value);
  group.value = groupStore.group;
});

const isPrivate = computed(() => {
  return group.value?.loaiNhom === 'private';
});
</script>
