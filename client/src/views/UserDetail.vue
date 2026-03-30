<template>
    <div class="user-detail-container min-h-screen bg-gray-50 py-8">
        <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
            <!-- Back Button -->
            <button
                @click="goBack"
                class="mb-6 flex items-center text-gray-600 hover:text-gray-900 transition-colors"
            >
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                </svg>
                Quay lại danh sách
            </button>

            <!-- Loading State -->
            <div v-if="isLoading" class="flex justify-center items-center py-12">
                <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>

            <!-- Error State -->
            <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
                <p class="text-red-800">{{ error }}</p>
            </div>

            <!-- User Detail Card -->
            <div v-else-if="user" class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                <!-- Header Section with Cover Image -->
                <div class="relative">
                    <div 
                        v-if="user.anhNen" 
                        class="h-64 bg-cover bg-center"
                        :style="{ backgroundImage: `url(${user.anhNen})` }"
                    ></div>
                    <div 
                        v-else 
                        class="h-64 bg-gradient-to-r from-blue-500 to-purple-600"
                    ></div>
                    
                    <!-- Avatar -->
                    <div class="absolute bottom-0 left-8 transform translate-y-1/2">
                        <div class="relative">
                            <img 
                                v-if="user.anhDaiDien" 
                                :src="user.anhDaiDien" 
                                :alt="`${user.ho} ${user.ten}`"
                                class="h-32 w-32 rounded-full border-4 border-white object-cover shadow-lg"
                            />
                            <div 
                                v-else 
                                class="h-32 w-32 rounded-full border-4 border-white bg-gray-300 flex items-center justify-center shadow-lg"
                            >
                                <span class="text-gray-600 text-3xl font-medium">
                                    {{ getInitials(user.ho, user.ten) }}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Content Section -->
                <div class="pt-20 px-8 pb-8">
                    <!-- Name and Basic Info -->
                    <div class="mb-6">
                        <h1 class="text-3xl font-bold text-gray-900 mb-2">
                            {{ user.ho }} {{ user.ten }}
                        </h1>
                        <p v-if="user.bietDanh" class="text-lg text-gray-600 mb-1">
                            @{{ user.bietDanh }}
                        </p>
                        <p v-if="user.gioiThieu" class="text-gray-700 mt-4">
                            {{ user.gioiThieu }}
                        </p>
                    </div>

                    <!-- Divider -->
                    <div class="border-t border-gray-200 my-6"></div>

                    <!-- Details Grid -->
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <!-- Email -->
                        <div class="flex items-start">
                            <div class="flex-shrink-0">
                                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm font-medium text-gray-500">Email</p>
                                <p class="text-sm text-gray-900">{{ user.email }}</p>
                            </div>
                        </div>

                        <!-- Phone -->
                        <div class="flex items-start">
                            <div class="flex-shrink-0">
                                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm font-medium text-gray-500">Số điện thoại</p>
                                <p class="text-sm text-gray-900">{{ user.soDienThoai || 'Chưa cập nhật' }}</p>
                            </div>
                        </div>

                        <!-- Date of Birth -->
                        <div class="flex items-start">
                            <div class="flex-shrink-0">
                                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm font-medium text-gray-500">Ngày sinh</p>
                                <p class="text-sm text-gray-900">{{ formatDate(user.ngaySinh) || 'Chưa cập nhật' }}</p>
                            </div>
                        </div>

                        <!-- Workplace -->
                        <div class="flex items-start">
                            <div class="flex-shrink-0">
                                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm font-medium text-gray-500">Nơi làm việc</p>
                                <p class="text-sm text-gray-900">{{ user.noiLamViec || 'Chưa cập nhật' }}</p>
                            </div>
                        </div>

                        <!-- School -->
                        <div class="flex items-start">
                            <div class="flex-shrink-0">
                                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm font-medium text-gray-500">Nơi học tập</p>
                                <p class="text-sm text-gray-900">{{ user.noiHocTap || 'Chưa cập nhật' }}</p>
                            </div>
                        </div>

                        <!-- Created Date -->
                        <div class="flex items-start">
                            <div class="flex-shrink-0">
                                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm font-medium text-gray-500">Ngày tạo</p>
                                <p class="text-sm text-gray-900">{{ formatDateTime(user.ngayTao) }}</p>
                            </div>
                        </div>

                        <!-- Updated Date -->
                        <div class="flex items-start">
                            <div class="flex-shrink-0">
                                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm font-medium text-gray-500">Cập nhật lần cuối</p>
                                <p class="text-sm text-gray-900">{{ formatDateTime(user.ngayCapNhat) }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axiosInstance from '../helpers/apiHelper';
import { notificationHelper } from '../helpers/notificationHelper';

interface User {
    maNguoiDung: number;
    ho: string;
    ten: string;
    bietDanh: string | null;
    email: string;
    anhDaiDien: string | null;
    anhNen: string | null;
    ngaySinh: string | null;
    gioiThieu: string | null;
    noiLamViec: string | null;
    noiHocTap: string | null;
    soDienThoai: string | null;
    ngayTao: string;
    ngayCapNhat: string;
}

interface ApiResponse<T> {
    code: number;
    message: string | null;
    data: T;
}

const router = useRouter();
const route = useRoute();
const user = ref<User | null>(null);
const isLoading = ref(false);
const error = ref<string | null>(null);

const fetchUserDetail = async () => {
    const userId = route.params.id as string;
    if (!userId) {
        error.value = 'Không tìm thấy ID người dùng';
        return;
    }

    isLoading.value = true;
    error.value = null;
    
    try {
        const response = await axiosInstance.get<ApiResponse<User>>(`/users/${userId}`);
        
        if (response.data.code === 200 && response.data.data) {
            user.value = response.data.data;
        } else {
            error.value = response.data.message || 'Không thể tải thông tin người dùng';
            notificationHelper('error', error.value || 'Không thể tải thông tin người dùng');
        }
    } catch (err: any) {
        error.value = err.response?.data?.message || err.message || 'Đã xảy ra lỗi khi tải thông tin người dùng';
        notificationHelper('error', error.value || 'Đã xảy ra lỗi khi tải thông tin người dùng');
        console.error('Error fetching user detail:', err);
    } finally {
        isLoading.value = false;
    }
};

const goBack = () => {
    router.push('/users');
};

const getInitials = (ho: string, ten: string): string => {
    const firstInitial = ho ? ho.charAt(0).toUpperCase() : '';
    const lastInitial = ten ? ten.charAt(0).toUpperCase() : '';
    return `${firstInitial}${lastInitial}`;
};

const formatDate = (dateString: string | null): string => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
    });
};

const formatDateTime = (dateString: string | null | undefined): string => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    });
};

onMounted(() => {
    fetchUserDetail();
});
</script>

<style scoped>
.user-detail-container {
    font-family: system-ui, -apple-system, sans-serif;
}
</style>

