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
      :classes="'px-4 py-1 text-sm rounded-md text-white'"
      :type="'primary'"
      @click="handleFollow"
    >
      {{ isFollowing ? "Đang theo dõi" : "Theo dõi" }}
    </Button>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import axiosInstance from "../helpers/apiHelper";

interface GoiYKetBanResponse {
  maNguoiDung: number;
  ho: string;
  ten: string;
  bietDanh: string;
  anhDaiDien: string;
  email: string;
}

interface ApiRecommendationsResponse {
  code: number;
  message: string;
  data: {
    content: GoiYKetBanResponse[];
    hasNext: boolean;
    page: number;
    size: number;
  };
}

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

const listRecommendations = ref<GoiYKetBanResponse[]>([]);

const getRecommendations = async() => {
  const response = await axiosInstance.get<ApiRecommendationsResponse>('/ban-be/goi-y-ket-ban', {
    params: { page: 0, size: 4 },
  });
  listRecommendations.value = response.data.data.content;
};

const handleFollow = () => {
  isFollowing.value = !isFollowing.value;
  emit("follow", props.username, isFollowing.value);
};

onMounted(() => {
  getRecommendations();
});

</script>
