<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import axiosInstance from "./helpers/apiHelper";
import { useAuthStore } from "./store/authStore";
import { router } from "./router";
import type { User } from "./types/userType";

interface MeResponse {
  code: number;
  message: string;
  data: User | null;
}

const authStore = useAuthStore();

const bootstrapMe = async () => {
  const token = authStore.getToken;
  if (!token) {
    authStore.clearAuth();
    if (router.currentRoute.value.name !== "Login") {
      router.push({ name: "Login" });
    }
    return;
  }

  try {
    const response = await axiosInstance.get<MeResponse>("/me");
    const user = response.data?.data;

    if (!user) {
      authStore.clearAuth();
      if (router.currentRoute.value.name !== "Login") {
        router.push({ name: "Login" });
      }
      return;
    }

    authStore.setAuth(token, true, user);
  } catch (_error) {
    authStore.clearAuth();
    if (router.currentRoute.value.name !== "Login") {
      router.push({ name: "Login" });
    }
  }
};

onMounted(async () => {
  await bootstrapMe();
});
</script>
