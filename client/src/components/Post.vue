<template>
    <div class="post bg-white rounded-lg border border-gray-200 mb-6 shadow-lg">
        <!-- Post Header -->
        <div class="post-header flex items-center justify-between p-4">
            <div class="flex items-center gap-3">
                <img
                    :src="avatar"
                    :alt="username"
                    class="w-10 h-10 rounded-full object-cover"
                />
                <div>
                    <div class="font-semibold">{{ username }}</div>
                    <div class="text-xs text-gray-500">{{ timeAgo }}</div>
                </div>
            </div>
            <button class="hover:opacity-70">
                <a-dropdown placement="bottomRight" class="cursor-pointer">
                    <a class="ant-dropdown-link" @click.prevent>
                        <MoreOutlined class="text-xl" />
                    </a>
                    <template #overlay>
                        <a-menu>
                            <a-menu-item key="1">
                                <div v-if="isOwner" class="flex items-center gap-2" @click="handleEditPost">
                                    <EditOutlined />
                                    <span>{{ $t('editPost') }}</span>
                                </div>
                            </a-menu-item>
                            <a-menu-item key="2">
                                <div v-if="isOwner" @click="handleDeletePost" class="flex items-center gap-2">
                                    <DeleteOutlined />
                                    <span>{{ $t('deletePost') }}</span>
                                </div>
                            </a-menu-item>
                            <a-menu-item key="3">
                                <div v-if="!isOwner" @click="handleEditPost" class="flex items-center gap-2">
                                    <EditOutlined />
                                    <span>{{ $t('editPost') }}</span>
                                </div>
                            </a-menu-item>
                            <a-menu-item key="4">
                                <div v-if="isOwner" @click="handleDeletePost" class="flex items-center gap-2">
                                    <DeleteOutlined />
                                    <span>{{ $t('deletePost') }}</span>
                                </div>
                            </a-menu-item>
                        </a-menu>
                    </template>
                </a-dropdown>
            </button>
        </div>

        <!-- Post Image -->
        <div v-if="image" class="post-image">
            <img
                :src="image"
                :alt="caption"
                class="w-full object-cover"
            />
        </div>

        <!-- Post Actions -->
        <div class="post-actions-container p-4">
            <PostActions
                :initial-liked="liked"
                :initial-like-count="likeCount"
                :initial-bookmarked="bookmarked"
                @like="handleLike"
                @comment="handleComment"
                @share="handleShare"
                @bookmark="handleBookmark"
            />
        </div>

        <!-- Post Details -->
        <div class="post-details px-4 pb-4">
            <div class="font-semibold mb-2" @click="seeListLikeOfPost">{{ currentLikeCount }} {{ $t('commentLikes') }}</div>
            <div class="mb-2">
                <span class="font-semibold mr-2">{{ username }}</span>
                <span>{{ caption }}</span>
            </div>
            <button
                v-if="commentCount > 0"
                @click="handleViewComments"
                class="text-gray-500 text-sm mb-2 hover:text-gray-700"
            >
                {{ $t('viewAllComments', { count: commentCount }) }}
            </button>
            <div class="flex items-center gap-2 pt-2 border-t border-gray-100">
                <input
                    type="text"
                    placeholder="Thêm bình luận..."
                    class="flex-1 border-none outline-none text-sm"
                    v-model="commentInput"
                    @keyup.enter="handleAddComment"
                />
            </div>
        </div>
    </div>

    <ModalEditPost
        :open="editModalOpen"
        :post-id="id"
        :caption="caption"
        :avatar="avatar"
        :username="username"
        :image="image"
        :quyen-rieng-tu="quyenRiengTu"
        :vi-tri="viTri"
        :mau-nen="mauNen"
        :danh-sach-anh="danhSachAnh ?? null"
        @update:open="setEditModalOpen"
    />

    <ModalComment
        :open="commentModalOpen"
        :post-id="id"
        :username="username"
        :avatar="avatar"
        :image="image"
        :caption="caption"
        :time-ago="timeAgo"
        :like-count="currentLikeCount"
        :comment-count="commentCount"
        @update:open="setCommentModalOpen"
    />

    <ModalListLike
        :open="listLikeModalOpen"
        :post-id="id"
        @update:open="setListLikeModalOpen"
    />
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { DeleteOutlined, EditOutlined, MoreOutlined } from '@ant-design/icons-vue';
import PostActions from './PostActions.vue';
import Swal from 'sweetalert2';
import { useI18n } from 'vue-i18n';
import { usePostStore } from '../store/postStore';
import ModalEditPost from './ModalEditPost.vue';
import ModalComment from './ModalComment.vue';
import ModalListLike from './ModalListLike.vue';
import callApi from '../helpers/mixinApi';
import { notificationHelper } from '../helpers/notificationHelper';
import { useAuthStore } from '../store/authStore';
import { toggleLikePostHelper } from '../helpers/postHelper';

const props = defineProps<{
    id: number;
    username: string;
    avatar: string;
    image?: string;
    caption: string;
    timeAgo: string;
    likeCount: number;
    commentCount: number;
    liked?: boolean;
    bookmarked?: boolean;
    isOwner?: boolean;
    quyenRiengTu?: string;
    viTri?: string | null;
    mauNen?: string | null;
    danhSachAnh?: string[] | null;
}>();

const emit = defineEmits<{
    like: [postId: number, value: boolean];
    comment: [postId: number];
    share: [postId: number];
    bookmark: [postId: number, value: boolean];
    viewComments: [postId: number];
    deletePost: [postId: number];
}>();

const { t } = useI18n();
const postStore = usePostStore();
const commentInput = ref('');
const currentLikeCount = ref(props.likeCount);
const authStore = useAuthStore();

const commentCount = ref(props.commentCount);

const handleLike = async (value: boolean) => {
    await toggleLikePostHelper(props.id, authStore.getUser?.maNguoiDung ?? 0, 'BaiViet', value);
    if (value) {
        currentLikeCount.value = props.likeCount + 1;
    } else {
        currentLikeCount.value = props.likeCount;
    }
};

const handleComment = () => {
    commentModalOpen.value = true;
};

const handleShare = () => {
    emit('share', props.id);
};

const handleBookmark = (value: boolean) => {
    emit('bookmark', props.id, value);
};

const commentModalOpen = ref(false);

const setCommentModalOpen = (v: boolean) => {
    commentModalOpen.value = v;
};

const handleViewComments = () => {
    commentModalOpen.value = true;
};

const handleAddComment = () => {
    if (commentInput.value.trim() !== '') {
        callApi(`/binh-luan`, 'POST', {
            maBaiDang: props.id,
            noiDung: commentInput.value,
            maNguoiDung: authStore.getUser?.maNguoiDung,
        }).then((result) => {
            if (!result.data) {
                notificationHelper('error', 'Thêm bình luận thất bại');
            } else {
                notificationHelper('success', 'Thêm bình luận thành công');
                commentCount.value++;
                commentInput.value = '';
            }
        });
    }
};

const handleDeletePost = () => {
    Swal.fire({
        title: t('areYouSure'),
        text: t('youWillNotBeAbleToRevertThis'),
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: t('yesDeleteIt')
    }).then(async (result) => {
        const resultDelete = await deletePostMethod();
        if (result.isConfirmed && resultDelete) {
            Swal.fire({
                title: t('deleted'),
                text: t('yourPostHasBeenDeleted'),
                icon: 'success'
            });
            emit('deletePost', props.id);
        } else {
            Swal.fire({
                title: t('error'),
                text: t('deletePostFailed'),
                icon: 'error'
            });
        }
    });
};

const deletePostMethod = async () => {
    try {
        const result = await postStore.deletePost(props.id);
        return result;
    } catch (error) {
        console.error(error);
        return false;
    }
};

const editModalOpen = ref(false);

const setEditModalOpen = (v: boolean) => {
    editModalOpen.value = v;
};

const handleEditPost = () => {
    editModalOpen.value = true;
};

const listLikeModalOpen = ref(false);

const setListLikeModalOpen = (v: boolean) => {
    listLikeModalOpen.value = v;
};

const seeListLikeOfPost = () => {
    listLikeModalOpen.value = true;
};

</script>

<style scoped>
.post-image img {
    max-height: 600px;
}
</style>

