<script lang="ts" setup>
import { ref, watch } from 'vue';
import {
    CloseOutlined,
    HeartFilled,
    LoadingOutlined,
} from '@ant-design/icons-vue';
import axiosInstance from '../helpers/apiHelper';
import { resolveMediaUrl } from '../helpers/mediaHelper';
import { useI18n } from 'vue-i18n';

interface LikeUser {
    maNguoiDung: number;
    hoTen: string;
    bietDanh?: string;
    anhDaiDien: string | null;
}

const props = defineProps<{
    open: boolean;
    postId: number;
}>();

const emit = defineEmits<{
    'update:open': [value: boolean];
}>();

const { t } = useI18n();

const users = ref<LikeUser[]>([]);
const loading = ref(false);
const defaultAvatar = 'https://testingbot.com/free-online-tools/random-avatar/100';

async function fetchLikes() {
    loading.value = true;
    try {
        const response = await axiosInstance.get('/luot-thich/danh-sach', {
            params: {
                maDoiTuong: props.postId,
                loaiDoiTuong: 'BaiViet',
            },
        });

        const data = response.data;
        if (data.code === 200 || data.code === 0) {
            users.value = Array.isArray(data.data) ? data.data : (data.data?.content ?? []);
        } else {
            users.value = [];
        }
    } catch {
        users.value = [];
    } finally {
        loading.value = false;
    }
}

function close() {
    emit('update:open', false);
}

watch(
    () => props.open,
    (isOpen) => {
        if (isOpen) {
            users.value = [];
            fetchLikes();
        }
    },
);
</script>

<template>
    <Teleport to="body">
        <Transition name="modal-fade">
            <div
                v-if="open"
                class="overlay"
                @click.self="close"
            >
                <div class="modal-box">
                    <!-- Header -->
                    <div class="modal-header">
                        <span class="modal-title">{{ t('likedByTitle') }}</span>
                        <button class="close-btn" @click="close">
                            <CloseOutlined />
                        </button>
                    </div>

                    <!-- Body -->
                    <div class="modal-body">
                        <!-- Loading -->
                        <div v-if="loading" class="empty-state">
                            <LoadingOutlined class="text-2xl text-gray-400 animate-spin" />
                            <p class="text-sm text-gray-500 mt-2">{{ t('loadingLikes') }}</p>
                        </div>

                        <!-- Empty -->
                        <div v-else-if="users.length === 0" class="empty-state">
                            <HeartFilled class="text-3xl text-gray-300" />
                            <p class="text-sm text-gray-500 mt-2">{{ t('noLikesYet') }}</p>
                        </div>

                        <!-- User list -->
                        <div
                            v-for="user in users"
                            :key="user.maNguoiDung"
                            class="user-row"
                        >
                            <img
                                :src="resolveMediaUrl(user.anhDaiDien) || defaultAvatar"
                                :alt="user.hoTen"
                                class="user-avatar"
                            />
                            <div class="user-info">
                                <p class="user-name">{{ user.hoTen }}</p>
                                <p v-if="user.bietDanh" class="user-nickname">@{{ user.bietDanh }}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </Transition>
    </Teleport>
</template>

<style scoped>
.overlay {
    position: fixed;
    inset: 0;
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(2px);
}

.modal-box {
    background: #fff;
    border-radius: 12px;
    width: 400px;
    max-width: 90vw;
    max-height: 70vh;
    display: flex;
    flex-direction: column;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.25);
    overflow: hidden;
}

.modal-header {
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    padding: 14px 16px;
    border-bottom: 1px solid #efefef;
}

.modal-title {
    font-size: 16px;
    font-weight: 600;
    color: #262626;
}

.close-btn {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    cursor: pointer;
    font-size: 18px;
    color: #262626;
    display: flex;
    align-items: center;
    padding: 4px;
}

.close-btn:hover {
    opacity: 0.6;
}

.modal-body {
    flex: 1;
    overflow-y: auto;
    padding: 8px 0;
    scrollbar-width: thin;
    scrollbar-color: #d4d4d4 transparent;
}

.modal-body::-webkit-scrollbar {
    width: 4px;
}

.modal-body::-webkit-scrollbar-thumb {
    background: #d4d4d4;
    border-radius: 4px;
}

.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 16px;
}

.user-row {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 16px;
    transition: background 0.15s;
}

.user-row:hover {
    background: #fafafa;
}

.user-avatar {
    width: 44px;
    height: 44px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
    border: 1px solid #efefef;
}

.user-info {
    flex: 1;
    min-width: 0;
}

.user-name {
    margin: 0;
    font-size: 14px;
    font-weight: 600;
    color: #262626;
    line-height: 1.4;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.user-nickname {
    margin: 0;
    font-size: 13px;
    color: #8e8e8e;
    line-height: 1.3;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

/* Transition */
.modal-fade-enter-active,
.modal-fade-leave-active {
    transition: opacity 0.2s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
    opacity: 0;
}
</style>
