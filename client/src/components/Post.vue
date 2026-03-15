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
                <MoreOutlined class="text-xl" />
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
            <div class="font-semibold mb-2">{{ currentLikeCount }} lượt thích</div>
            <div class="mb-2">
                <span class="font-semibold mr-2">{{ username }}</span>
                <span>{{ caption }}</span>
            </div>
            <button
                v-if="commentCount > 0"
                @click="handleViewComments"
                class="text-gray-500 text-sm mb-2 hover:text-gray-700"
            >
                Xem tất cả {{ commentCount }} bình luận
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
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { MoreOutlined } from '@ant-design/icons-vue';
import PostActions from './PostActions.vue';

const props = defineProps<{
    id: string;
    username: string;
    avatar: string;
    image?: string;
    caption: string;
    timeAgo: string;
    likeCount: number;
    commentCount: number;
    liked?: boolean;
    bookmarked?: boolean;
}>();

const emit = defineEmits<{
    like: [postId: string, value: boolean];
    comment: [postId: string];
    share: [postId: string];
    bookmark: [postId: string, value: boolean];
    viewComments: [postId: string];
}>();

const commentInput = ref('');
const currentLikeCount = ref(props.likeCount);

const handleLike = (value: boolean) => {
    // Update local count based on like action
    if (value) {
        currentLikeCount.value = props.likeCount + 1;
    } else {
        currentLikeCount.value = props.likeCount;
    }
    emit('like', props.id, value);
};

const handleComment = () => {
    emit('comment', props.id);
};

const handleShare = () => {
    emit('share', props.id);
};

const handleBookmark = (value: boolean) => {
    emit('bookmark', props.id, value);
};

const handleViewComments = () => {
    emit('viewComments', props.id);
};

const handleAddComment = () => {
    if (commentInput.value.trim()) {
        // Handle add comment
        commentInput.value = '';
    }
};
</script>

<style scoped>
.post-image img {
    max-height: 600px;
}
</style>

