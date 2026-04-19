<template>
    <div class="post-actions flex items-center gap-4">
        <button
            @click="handleLike"
            class="action-btn hover:opacity-70 transition-opacity"
            :class="{ 'text-primary': isLiked }"
        >
            <HeartOutlined v-if="!isLiked" class="text-2xl" />
            <HeartFilled v-else class="text-2xl text-primary" />
        </button>

        <button
            @click="handleComment"
            class="action-btn hover:opacity-70 transition-opacity"
        >
            <MessageOutlined class="text-2xl" />
        </button>

        <button
            @click="handleShare"
            class="action-btn hover:opacity-70 transition-opacity"
        >
            <SendOutlined class="text-2xl" />
        </button>

        <!-- <button
            @click="handleBookmark"
            class="action-btn ml-auto hover:opacity-70 transition-opacity"
            :class="{ 'text-primary': isBookmarked }"
        >
            <BookOutlined v-if="!isBookmarked" class="text-2xl" />
            <BookFilled v-else class="text-2xl text-primary" />
        </button> -->
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { HeartOutlined, HeartFilled, MessageOutlined, SendOutlined, BookOutlined, BookFilled } from '@ant-design/icons-vue';

const props = defineProps<{
    initialLiked?: boolean;
    initialLikeCount?: number;
    initialBookmarked?: boolean;
}>();

const emit = defineEmits<{
    like: [value: boolean];
    comment: [];
    share: [];
    bookmark: [value: boolean];
}>();

const isLiked = ref(props.initialLiked || false);
const likeCount = ref(props.initialLikeCount || 0);
const isBookmarked = ref(props.initialBookmarked || false);

const handleLike = () => {
    isLiked.value = !isLiked.value;
    if (isLiked.value) {
        likeCount.value++;
    } else {
        likeCount.value--;
    }
    emit('like', isLiked.value);
};

const handleComment = () => {
    emit('comment');
};

const handleShare = () => {
    emit('share');
};

const handleBookmark = () => {
    isBookmarked.value = !isBookmarked.value;
    emit('bookmark', isBookmarked.value);
};
</script>

<style scoped>
.action-btn {
    background: none;
    border: none;
    cursor: pointer;
    color: black;
}
</style>

