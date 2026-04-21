<script lang="ts" setup>
import { ref, watch, nextTick } from 'vue';
import {
    HeartOutlined,
    HeartFilled,
    SendOutlined,
    CloseOutlined,
    SmileOutlined,
    LoadingOutlined,
} from '@ant-design/icons-vue';
import { useAuthStore } from '../store/authStore';
import { resolveMediaUrl } from '../helpers/mediaHelper';
import callApi from '../helpers/mixinApi';
import { useI18n } from 'vue-i18n';

export interface CommentItem {
    maBinhLuan: number;
    maNguoiDung: number;
    hoTen: string;
    anhDaiDien: string;
    noiDung: string;
    ngayTao: string;
    luotThich?: number;
    daThich?: boolean;
}

const props = defineProps<{
    open: boolean;
    postId: number;
    username: string;
    avatar: string;
    image?: string;
    caption: string;
    timeAgo: string;
    likeCount: number;
    commentCount: number;
}>();

const emit = defineEmits<{
    'update:open': [value: boolean];
}>();

const { t } = useI18n();
const authStore = useAuthStore();

const comments = ref<CommentItem[]>([]);
const commentInput = ref('');
const loading = ref(false);
const submitting = ref(false);
const commentsEndRef = ref<HTMLElement | null>(null);
const inputRef = ref<HTMLInputElement | null>(null);

const defaultAvatar = 'https://testingbot.com/free-online-tools/random-avatar/100';


async function fetchComments() {
    loading.value = true;
    try {
        const result = await callApi(`/binh-luan/bai-viet/${props.postId}`, 'GET', null);
        if (result.data) {
            comments.value = Array.isArray(result.data) ? result.data : (result.data.content ?? []);
        }
    } catch {
        comments.value = [];
    } finally {
        loading.value = false;
    }
}

async function handleSubmitComment() {
    const text = commentInput.value.trim();
    if (!text || submitting.value) return;

    submitting.value = true;
    try {
        const result = await callApi('/binh-luan', 'POST', {
            maBaiDang: props.postId,
            noiDung: text,
            maNguoiDung: authStore.getUser?.maNguoiDung,
        });

        if (result.data) {
            const newComment: CommentItem = result.data.maBinhLuan
                ? result.data
                : {
                      maBinhLuan: Date.now(),
                      maNguoiDung: authStore.getUser?.maNguoiDung ?? 0,
                      hoTen: `${authStore.getUser?.ho ?? ''} ${authStore.getUser?.ten ?? ''}`.trim(),
                      anhDaiDien: authStore.getUser?.anhDaiDien ?? '',
                      noiDung: text,
                      ngayTao: new Date().toISOString(),
                      luotThich: 0,
                      daThich: false,
                  };
            comments.value.push(newComment);
            commentInput.value = '';
            await nextTick();
            commentsEndRef.value?.scrollIntoView({ behavior: 'smooth' });
        }
    } finally {
        submitting.value = false;
    }
}

function formatCommentTime(date: string): string {
    const created = new Date(date);
    const now = new Date();
    const diff = now.getTime() - created.getTime();
    if (Number.isNaN(diff) || diff < 0) return t('justNow');

    const mins = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);
    const weeks = Math.floor(days / 7);

    if (mins < 1) return t('justNow');
    if (mins < 60) return `${mins}${t('commentMinShort')}`;
    if (hours < 24) return `${hours}${t('commentHourShort')}`;
    if (days < 7) return `${days}${t('commentDayShort')}`;
    return `${weeks}${t('commentWeekShort')}`;
}

function close() {
    emit('update:open', false);
}

watch(
    () => props.open,
    async (isOpen) => {
        if (isOpen) {
            comments.value = [];
            await fetchComments();
            await nextTick();
            inputRef.value?.focus();
        }
    },
);
</script>

<template>
    <Teleport to="body">
        <Transition name="modal-fade">
            <div
                v-if="open"
                class="modal-overlay"
                @click.self="close"
            >
                <div class="modal-container">
                    <!-- Close button -->
                    <button class="modal-close-btn" @click="close">
                        <CloseOutlined />
                    </button>

                    <div class="modal-body">
                        <!-- Left: Post image -->
                        <div v-if="image" class="modal-left">
                            <img
                                :src="image"
                                :alt="caption"
                                class="post-image"
                            />
                        </div>

                        <!-- Right: Comments panel -->
                        <div class="modal-right" :class="{ 'full-width': !image }">
                            <!-- Header -->
                            <div class="comment-header">
                                <div class="flex items-center gap-3">
                                    <img
                                        :src="avatar"
                                        :alt="username"
                                        class="avatar-sm"
                                    />
                                    <span class="font-semibold text-sm">{{ username }}</span>
                                </div>
                            </div>

                            <!-- Comments list -->
                            <div class="comments-list">
                                <!-- Caption as first "comment" -->
                                <div class="comment-item">
                                    <img
                                        :src="avatar"
                                        :alt="username"
                                        class="avatar-comment"
                                    />
                                    <div class="comment-content">
                                        <p>
                                            <span class="font-semibold text-sm mr-1">{{ username }}</span>
                                            <span class="text-sm">{{ caption }}</span>
                                        </p>
                                        <span class="comment-time">{{ timeAgo }}</span>
                                    </div>
                                </div>

                                <!-- Loading -->
                                <div v-if="loading" class="loading-container">
                                    <LoadingOutlined class="text-2xl text-gray-400 animate-spin" />
                                </div>

                                <!-- Comments -->
                                <div
                                    v-for="comment in comments"
                                    :key="comment.maBinhLuan"
                                    class="comment-item"
                                >
                                    <img
                                        :src="resolveMediaUrl(comment.anhDaiDien) || defaultAvatar"
                                        :alt="comment.hoTen"
                                        class="avatar-comment"
                                    />
                                    <div class="comment-content">
                                        <p>
                                            <span class="font-semibold text-sm mr-1">{{ comment.hoTen }}</span>
                                            <span class="text-sm">{{ comment.noiDung }}</span>
                                        </p>
                                        <div class="comment-meta">
                                            <span class="comment-time">{{ formatCommentTime(comment.ngayTao) }}</span>
                                            <button
                                                v-if="comment.luotThich"
                                                class="comment-like-count"
                                            >
                                                {{ comment.luotThich }} {{ t('commentLikes') }}
                                            </button>
                                            <button class="comment-reply-btn">{{ t('commentReply') }}</button>
                                        </div>
                                    </div>
                                    <button class="comment-heart-btn">
                                        <HeartFilled v-if="comment.daThich" class="text-xs text-red-500" />
                                        <HeartOutlined v-else class="text-xs text-gray-400" />
                                    </button>
                                </div>

                                <div ref="commentsEndRef" />
                            </div>

                            <!-- Actions -->
                            <div class="comment-actions">
                                <div class="action-row">
                                    <div class="flex items-center gap-4">
                                        <HeartOutlined class="action-icon" />
                                        <MessageIcon class="action-icon" />
                                        <SendOutlined class="action-icon" />
                                    </div>
                                </div>
                                <div class="likes-row">
                                    <span class="font-semibold text-sm">{{ likeCount }} {{ t('commentLikesLabel') }}</span>
                                </div>
                                <div class="time-row">
                                    <span class="text-xs text-gray-400 uppercase">{{ timeAgo }}</span>
                                </div>
                            </div>

                            <!-- Input -->
                            <div class="comment-input-area">
                                <SmileOutlined class="text-2xl text-gray-600 cursor-pointer hover:text-gray-400 transition-colors" />
                                <input
                                    ref="inputRef"
                                    v-model="commentInput"
                                    type="text"
                                    :placeholder="t('commentAddPlaceholder')"
                                    class="comment-text-input"
                                    @keyup.enter="handleSubmitComment"
                                />
                                <button
                                    :disabled="!commentInput.trim() || submitting"
                                    class="post-btn"
                                    :class="{ 'opacity-40 cursor-not-allowed': !commentInput.trim() || submitting }"
                                    @click="handleSubmitComment"
                                >
                                    {{ t('commentPost') }}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </Transition>
    </Teleport>
</template>

<script lang="ts">
import { MessageOutlined } from '@ant-design/icons-vue';
const MessageIcon = MessageOutlined;
export default { components: { MessageIcon } };
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    inset: 0;
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.65);
    backdrop-filter: blur(2px);
}

.modal-container {
    position: relative;
    display: flex;
    max-width: 70vw;
    max-height: 90vh;
    width: 100%;
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 25px 60px rgba(0, 0, 0, 0.3);
}

.modal-close-btn {
    position: absolute;
    top: -40px;
    right: -40px;
    z-index: 10;
    color: #fff;
    font-size: 20px;
    background: none;
    border: none;
    cursor: pointer;
    padding: 8px;
    transition: opacity 0.15s;
}

.modal-close-btn:hover {
    opacity: 0.7;
}

.modal-body {
    display: flex;
    width: 100%;
    min-height: 480px;
    max-height: 90vh;
}

.modal-left {
    flex: 1.2;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #000;
    min-width: 0;
    max-width: 60%;
}

.post-image {
    width: 100%;
    height: 100%;
    object-fit: contain;
    max-height: 90vh;
}

.modal-right {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 340px;
    max-width: 500px;
    border-left: 1px solid #efefef;
}

.modal-right.full-width {
    max-width: 100%;
    min-width: 480px;
}

.comment-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 16px;
    border-bottom: 1px solid #efefef;
}

.avatar-sm {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
    border: 1px solid #efefef;
}

.comments-list {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    scrollbar-width: thin;
    scrollbar-color: #d4d4d4 transparent;
}

.comments-list::-webkit-scrollbar {
    width: 4px;
}

.comments-list::-webkit-scrollbar-thumb {
    background: #d4d4d4;
    border-radius: 4px;
}

.loading-container {
    display: flex;
    justify-content: center;
    padding: 32px 0;
}

.comment-item {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
    align-items: flex-start;
}

.avatar-comment {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
    flex-shrink: 0;
    border: 1px solid #efefef;
}

.comment-content {
    flex: 1;
    min-width: 0;
}

.comment-content p {
    margin: 0;
    line-height: 1.4;
    word-break: break-word;
}

.comment-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-top: 6px;
}

.comment-time {
    font-size: 12px;
    color: #8e8e8e;
    margin-top: 4px;
}

.comment-meta .comment-time {
    margin-top: 0;
}

.comment-like-count {
    font-size: 12px;
    color: #8e8e8e;
    font-weight: 600;
    background: none;
    border: none;
    cursor: pointer;
    padding: 0;
}

.comment-reply-btn {
    font-size: 12px;
    color: #8e8e8e;
    font-weight: 600;
    background: none;
    border: none;
    cursor: pointer;
    padding: 0;
}

.comment-reply-btn:hover,
.comment-like-count:hover {
    color: #262626;
}

.comment-heart-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;
    margin-top: 6px;
    flex-shrink: 0;
}

.comment-actions {
    padding: 8px 16px;
    border-top: 1px solid #efefef;
}

.action-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 6px 0;
}

.action-icon {
    font-size: 24px;
    cursor: pointer;
    transition: opacity 0.15s;
}

.action-icon:hover {
    opacity: 0.5;
}

.likes-row {
    padding: 4px 0 0;
}

.time-row {
    padding: 2px 0 4px;
}

.comment-input-area {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    border-top: 1px solid #efefef;
}

.comment-text-input {
    flex: 1;
    border: none;
    outline: none;
    font-size: 14px;
    background: transparent;
}

.comment-text-input::placeholder {
    color: #8e8e8e;
}

.post-btn {
    background: none;
    border: none;
    color: #0095f6;
    font-weight: 600;
    font-size: 14px;
    cursor: pointer;
    padding: 0;
    transition: color 0.15s;
}

.post-btn:hover:not(:disabled) {
    color: #00376b;
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

/* Responsive */
@media (max-width: 768px) {
    .modal-container {
        max-width: 95vw;
        max-height: 95vh;
    }

    .modal-body {
        flex-direction: column;
    }

    .modal-left {
        max-width: 100%;
        max-height: 45vh;
    }

    .modal-right {
        min-width: unset;
        max-width: 100%;
        border-left: none;
        border-top: 1px solid #efefef;
    }

    .modal-right.full-width {
        min-width: unset;
    }

    .modal-close-btn {
        top: -36px;
        right: 4px;
    }
}
</style>
