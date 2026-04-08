<template>
  <div class="users-container min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-900">{{ $t("userListTitle") }}</h1>
        <p class="text-gray-600 mt-2">{{ $t("userListSubtitle") }}</p>
      </div>

      <!-- Search -->
      <div class="mb-4">
        <input
          v-model.trim="searchKeyword"
          type="text"
          :placeholder="$t('userSearchPlaceholder')"
          class="w-full bg-white border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:border-primary"
        />
        <p class="text-sm text-gray-500 mt-2">
          {{ $t("foundUsers", { count: filteredUsers.length }) }}
        </p>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="flex justify-center items-center py-12">
        <div
          class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"
        ></div>
      </div>

      <!-- Error State -->
      <div
        v-else-if="error"
        class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6"
      >
        <p class="text-red-800">{{ error }}</p>
      </div>

      <!-- Users Table -->
      <div
        v-else
        class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden"
      >
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  ID
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  Họ tên
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  Biệt danh
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  Email
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  Số điện thoại
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  Ngày tạo
                </th>
                <th
                  class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
                >
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr
                v-for="user in filteredUsers"
                :key="user.maNguoiDung"
                class="hover:bg-gray-50 cursor-pointer transition-colors"
                @click="goToUserDetail(user.maNguoiDung)"
              >
                <td
                  class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900"
                >
                  {{ user.maNguoiDung }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10">
                      <img
                        v-if="user.anhDaiDien"
                        :src="user.anhDaiDien"
                        :alt="`${user.ho} ${user.ten}`"
                        class="h-10 w-10 rounded-full object-cover"
                      />
                      <div
                        v-else
                        class="h-10 w-10 rounded-full bg-gray-300 flex items-center justify-center"
                      >
                        <span class="text-gray-600 text-sm font-medium">
                          {{ getInitials(user.ho, user.ten) }}
                        </span>
                      </div>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">
                        {{ user.ho }} {{ user.ten }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ user.bietDanh || "-" }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ user.email }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ user.soDienThoai || "-" }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ formatDate(user.ngayTao) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <div class="flex items-center gap-3">
                    <button
                      @click.stop="goToUserDetail(user.maNguoiDung)"
                      class="text-blue-600 hover:text-blue-900 font-medium"
                    >
                      Xem
                    </button>
                    <button
                      @click.stop="goToUserEdit(user.maNguoiDung)"
                      class="text-green-600 hover:text-green-900 font-medium"
                    >
                      Sửa
                    </button>
                    <button
                      @click.stop="
                        handleDelete(user.maNguoiDung, user.ho, user.ten)
                      "
                      class="text-red-600 hover:text-red-900 font-medium"
                    >
                      Xóa
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Empty State -->
        <div v-if="filteredUsers.length === 0" class="text-center py-12">
          <p class="text-gray-500">{{ $t("noMatchingUsers") }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axiosInstance from "../helpers/apiHelper";
import { notificationHelper } from "../helpers/notificationHelper";
import type { User } from "../types/userType";
import type { ApiResponse } from "../types/responseAxios";
import { useI18n } from "vue-i18n";

const router = useRouter();
const { t } = useI18n();
const users = ref<User[]>([]);
const isLoading = ref(false);
const error = ref<string>("");
const searchKeyword = ref("");

const normalizeText = (value: string | number | null | undefined): string =>
  String(value || "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();

const filteredUsers = computed(() => {
  const keyword = normalizeText(searchKeyword.value);
  if (!keyword) return users.value;

  return users.value.filter((user) => {
    const fullName = `${user.ho || ""} ${user.ten || ""}`.trim();
    return (
      normalizeText(user.maNguoiDung).includes(keyword) ||
      normalizeText(fullName).includes(keyword) ||
      normalizeText(user.bietDanh).includes(keyword) ||
      normalizeText(user.email).includes(keyword) ||
      normalizeText(user.soDienThoai).includes(keyword)
    );
  });
});

const fetchUsers = async () => {
  isLoading.value = true;
  error.value = "";

  try {
    const response = await axiosInstance.get<ApiResponse>("/users");

    if (response.data.code === 200 && response.data.data) {
      users.value = response.data.data;
    } else {
      error.value =
        response.data.message || t("cannotLoadUsers");
      notificationHelper("error", error.value);
    }
  } catch (err: any) {
    error.value =
      err.response?.data?.message ||
      err.message ||
      t("cannotLoadUsers");
    notificationHelper("error", error.value);
    console.error("Error fetching users:", err);
  } finally {
    isLoading.value = false;
  }
};

const goToUserDetail = (userId: number) => {
  router.push(`/users/${userId}`);
};

const goToUserEdit = (userId: number) => {
  router.push(`/users/${userId}/edit`);
};

const handleDelete = async (userId: number, ho: string, ten: string) => {
  if (
    !confirm(
      `Bạn có chắc chắn muốn xóa người dùng "${ho} ${ten}"? Hành động này không thể hoàn tác.`
    )
  ) {
    return;
  }

  try {
    await axiosInstance.delete(`/users/${userId}`);
    notificationHelper("success", "Xóa người dùng thành công");
    // Reload danh sách
    fetchUsers();
  } catch (err: any) {
    const errorMessage =
      err.response?.data?.message ||
      err.message ||
      "Đã xảy ra lỗi khi xóa người dùng";
    notificationHelper("error", errorMessage);
    console.error("Error deleting user:", err);
  }
};

const getInitials = (ho: string, ten: string): string => {
  const firstInitial = ho ? ho.charAt(0).toUpperCase() : "";
  const lastInitial = ten ? ten.charAt(0).toUpperCase() : "";
  return `${firstInitial}${lastInitial}`;
};

const formatDate = (dateString: string): string => {
  if (!dateString) return "-";
  const date = new Date(dateString);
  return date.toLocaleDateString("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.users-container {
  font-family: system-ui, -apple-system, sans-serif;
}
</style>
