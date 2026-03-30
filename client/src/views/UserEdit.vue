<template>
    <div class="user-edit-container min-h-screen bg-gray-50 py-8">
        <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
            <!-- Back Button -->
            <button
                @click="goBack"
                class="mb-6 flex items-center text-gray-600 hover:text-gray-900 transition-colors"
            >
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                </svg>
                Quay lại
            </button>

            <!-- Header -->
            <div class="mb-6">
                <h1 class="text-3xl font-bold text-gray-900">Chỉnh sửa người dùng</h1>
                <p class="text-gray-600 mt-2">Cập nhật thông tin người dùng</p>
            </div>

            <!-- Loading State -->
            <div v-if="isLoading" class="flex justify-center items-center py-12">
                <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>

            <!-- Error State -->
            <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
                <p class="text-red-800">{{ error }}</p>
            </div>

            <!-- Edit Form -->
            <div v-else class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 sm:p-8">
                <form @submit.prevent="submitForm" class="space-y-6">
                    <!-- Họ và Tên -->
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Họ <span class="text-red-500">*</span>
                            </label>
                            <input
                                v-model="formData.ho"
                                type="text"
                                required
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Nhập họ"
                            />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Tên <span class="text-red-500">*</span>
                            </label>
                            <input
                                v-model="formData.ten"
                                type="text"
                                required
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Nhập tên"
                            />
                        </div>
                    </div>

                    <!-- Email và Mật khẩu -->
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Email <span class="text-red-500">*</span>
                            </label>
                            <input
                                v-model="formData.email"
                                type="email"
                                required
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="example@email.com"
                            />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Mật khẩu
                            </label>
                            <input
                                v-model="formData.matKhau"
                                type="password"
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Để trống nếu không đổi"
                            />
                            <p class="text-xs text-gray-500 mt-1">Để trống nếu không muốn thay đổi mật khẩu</p>
                        </div>
                    </div>

                    <!-- Biệt danh và Số điện thoại -->
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Biệt danh
                            </label>
                            <input
                                v-model="formData.bietDanh"
                                type="text"
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Nhập biệt danh"
                            />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Số điện thoại
                            </label>
                            <input
                                v-model="formData.soDienThoai"
                                type="tel"
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Nhập số điện thoại"
                            />
                        </div>
                    </div>

                    <!-- Ngày sinh -->
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">
                            Ngày sinh
                        </label>
                        <input
                            v-model="formData.ngaySinh"
                            type="datetime-local"
                            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        />
                    </div>

                    <!-- Ảnh đại diện và Ảnh nền -->
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Ảnh đại diện (URL)
                            </label>
                            <input
                                v-model="formData.anhDaiDien"
                                type="text"
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="https://example.com/avatar.jpg"
                            />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Ảnh nền (URL)
                            </label>
                            <input
                                v-model="formData.anhNen"
                                type="text"
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="https://example.com/cover.jpg"
                            />
                        </div>
                    </div>

                    <!-- Giới thiệu -->
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">
                            Giới thiệu
                        </label>
                        <textarea
                            v-model="formData.gioiThieu"
                            rows="4"
                            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Viết giới thiệu về bản thân..."
                        ></textarea>
                    </div>

                    <!-- Nơi làm việc và Nơi học tập -->
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Nơi làm việc
                            </label>
                            <input
                                v-model="formData.noiLamViec"
                                type="text"
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Nhập nơi làm việc"
                            />
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-2">
                                Nơi học tập
                            </label>
                            <input
                                v-model="formData.noiHocTap"
                                type="text"
                                class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Nhập nơi học tập"
                            />
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="flex items-center justify-end gap-4 pt-6 border-t border-gray-200">
                        <button
                            type="button"
                            @click="goBack"
                            class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
                        >
                            Hủy
                        </button>
                        <button
                            type="submit"
                            :disabled="isSubmitting"
                            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                            <span v-if="!isSubmitting">Lưu thay đổi</span>
                            <span v-else>Đang lưu...</span>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue';
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

interface UserFormData {
    ho: string;
    ten: string;
    email: string;
    matKhau: string;
    bietDanh: string;
    anhDaiDien: string;
    anhNen: string;
    ngaySinh: string;
    gioiThieu: string;
    noiLamViec: string;
    noiHocTap: string;
    soDienThoai: string;
}

const router = useRouter();
const route = useRoute();
const isLoading = ref(false);
const isSubmitting = ref(false);
const error = ref<string | null>(null);

const formData = reactive<UserFormData>({
    ho: '',
    ten: '',
    email: '',
    matKhau: '',
    bietDanh: '',
    anhDaiDien: '',
    anhNen: '',
    ngaySinh: '',
    gioiThieu: '',
    noiLamViec: '',
    noiHocTap: '',
    soDienThoai: '',
});

const fetchUser = async () => {
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
            const user = response.data.data;
            // Populate form with user data
            formData.ho = user.ho || '';
            formData.ten = user.ten || '';
            formData.email = user.email || '';
            formData.bietDanh = user.bietDanh || '';
            formData.anhDaiDien = user.anhDaiDien || '';
            formData.anhNen = user.anhNen || '';
            formData.ngaySinh = user.ngaySinh ? formatDateForInput(user.ngaySinh) : '';
            formData.gioiThieu = user.gioiThieu || '';
            formData.noiLamViec = user.noiLamViec || '';
            formData.noiHocTap = user.noiHocTap || '';
            formData.soDienThoai = user.soDienThoai || '';
            formData.matKhau = ''; // Don't populate password
        } else {
            error.value = response.data.message || 'Không thể tải thông tin người dùng';
            notificationHelper('error', error.value || 'Không thể tải thông tin người dùng');
        }
    } catch (err: any) {
        error.value = err.response?.data?.message || err.message || 'Đã xảy ra lỗi khi tải thông tin người dùng';
        notificationHelper('error', error.value || 'Đã xảy ra lỗi khi tải thông tin người dùng');
        console.error('Error fetching user:', err);
    } finally {
        isLoading.value = false;
    }
};

const formatDateForInput = (dateString: string | null | undefined): string => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
};

const submitForm = async () => {
    const userId = route.params.id as string;
    if (!userId) {
        notificationHelper('error', 'Không tìm thấy ID người dùng');
        return;
    }

    isSubmitting.value = true;
    
    try {
        // Prepare payload - convert empty strings to null
        const toNullIfEmpty = (value: string): string | null => {
            return value.trim() === '' ? null : value;
        };
        
        const payload: {
            ho: string;
            ten: string;
            email: string;
            matKhau?: string;
            bietDanh: string | null;
            anhDaiDien: string | null;
            anhNen: string | null;
            ngaySinh: string | null;
            gioiThieu: string | null;
            noiLamViec: string | null;
            noiHocTap: string | null;
            soDienThoai: string | null;
        } = {
            ho: formData.ho,
            ten: formData.ten,
            email: formData.email,
            bietDanh: toNullIfEmpty(formData.bietDanh),
            anhDaiDien: toNullIfEmpty(formData.anhDaiDien),
            anhNen: toNullIfEmpty(formData.anhNen),
            ngaySinh: formData.ngaySinh ? new Date(formData.ngaySinh).toISOString() : null,
            gioiThieu: toNullIfEmpty(formData.gioiThieu),
            noiLamViec: toNullIfEmpty(formData.noiLamViec),
            noiHocTap: toNullIfEmpty(formData.noiHocTap),
            soDienThoai: toNullIfEmpty(formData.soDienThoai),
        };

        // Only include password if it's not empty
        if (formData.matKhau) {
            payload.matKhau = formData.matKhau;
        }

        const response = await axiosInstance.put<ApiResponse<User>>(`/users/${userId}`, payload);
        
        if (response.data.code === 200 || response.data.data) {
            notificationHelper('success', 'Cập nhật người dùng thành công');
            router.push(`/users/${userId}`);
        } else {
            notificationHelper('error', response.data.message || 'Cập nhật thất bại');
        }
    } catch (err: any) {
        const errorMessage = err.response?.data?.message || err.message || 'Đã xảy ra lỗi khi cập nhật người dùng';
        notificationHelper('error', errorMessage);
        console.error('Error updating user:', err);
    } finally {
        isSubmitting.value = false;
    }
};

const goBack = () => {
    router.push('/users');
};

onMounted(() => {
    fetchUser();
});
</script>

<style scoped>
.user-edit-container {
    font-family: system-ui, -apple-system, sans-serif;
}
</style>


