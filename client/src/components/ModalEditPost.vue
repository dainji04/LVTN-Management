<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import {
    PictureOutlined,
    PaperClipOutlined,
    SmileOutlined,
} from '@ant-design/icons-vue';
import VModal from './VModal.vue';
import { useAuthStore } from '../store/authStore';
import { usePostStore } from '../store/postStore';
import type { EditPostRequest } from '../types/postType';
import Swal from 'sweetalert2';
import { useI18n } from 'vue-i18n';
import { resolveMediaUrl } from '../helpers/mediaHelper';

const props = defineProps<{
    open: boolean;
    postId: number;
    caption: string;
    avatar: string;
    username: string;
    image?: string;
    quyenRiengTu?: string;
    viTri?: string | null;
    mauNen?: string | null;
    danhSachAnh?: string[] | null;
}>();

const emit = defineEmits<{
    'update:open': [value: boolean];
    'update:postUpdated': [value: string];
}>();

const { t } = useI18n();
const authStore = useAuthStore();
const postStore = usePostStore();

const draftContent = ref('');
const saving = ref(false);

watch(
    () => props.open,
    (isOpen) => {
        if (isOpen) {
            draftContent.value = props.caption;
        }
    },
);

const imagePreview = computed(() =>
    props.image ? resolveMediaUrl(props.image) || props.image : '',
);

async function submitEdit(): Promise<void> {
    const text = draftContent.value.trim();
    if (!text) {
        await Swal.fire({
            title: t('error'),
            text: t('editPostEmptyContent'),
            icon: 'warning',
        });
        throw new Error('validation');
    }

    if (!authStore.getUser?.maNguoiDung) {
        await Swal.fire({
            title: t('error'),
            text: t('pleaseLoginAndTryAgain'),
            icon: 'error',
        });
        throw new Error('auth');
    }

    saving.value = true;
    try {
        const body: EditPostRequest = {
            maBaiViet: props.postId,
            maNguoiDung: authStore.getUser.maNguoiDung,
            maNhom: null,
            noiDung: text,
            quyenRiengTu: props.quyenRiengTu ?? 'friends',
            viTri: props.viTri ?? null,
            mauNen: props.mauNen ?? null,
            danhSachCongTacVien: null,
            danhSachAnh: props.danhSachAnh ?? null,
        };

        const ok = await postStore.editPost(body);
        if (!ok) {
            await Swal.fire({
                title: t('error'),
                text: t('editPostFailed'),
                icon: 'error',
            });
            throw new Error('api');
        }

        emit('update:open', false);
        emit('update:postUpdated', text);
        
        await Swal.fire({
            title: t('success'),
            text: t('editPostSuccess'),
            icon: 'success',
            timer: 1600,
            showConfirmButton: false,
        });
    } finally {
        saving.value = false;
    }
}
</script>

<template>
    <VModal
        :open="open"
        :title="$t('editPost')"
        :handle-submit="submitEdit"
        :width="560"
        :ok-text="$t('saveChanges')"
        :cancel-text="$t('cancel')"
        :confirm-loading="saving"
        @update:open="emit('update:open', $event)"
    >
        <div class="edit-post-body -mt-1">
            <div class="flex items-center gap-3 mb-4">
                <div
                    class="w-10 h-10 shrink-0 rounded-full overflow-hidden bg-orange-200 border border-gray-200"
                >
                    <img
                        :src="avatar"
                        :alt="username"
                        class="w-full h-full object-cover"
                    />
                </div>
                <div class="min-w-0">
                    <div class="font-semibold text-gray-900 truncate">
                        {{ username }}
                    </div>
                    <div class="text-xs text-gray-500">
                        {{ $t('editPostHint') }}
                    </div>
                </div>
            </div>

            <textarea
                v-model="draftContent"
                rows="5"
                :placeholder="$t('writeSomethingForTheGroup')"
                class="w-full bg-slate-50 hover:bg-slate-100 focus:bg-white border border-transparent focus:border-gray-200 rounded-xl px-4 py-3 text-gray-700 outline-none transition-all duration-200 placeholder:text-gray-500 resize-y min-h-[140px] text-sm leading-relaxed"
            />

            <div
                v-if="imagePreview"
                class="mt-4 rounded-xl overflow-hidden border border-gray-100 bg-gray-50"
            >
                <img
                    :src="imagePreview"
                    :alt="$t('editPost')"
                    class="w-full object-cover max-h-56"
                />
            </div>

            <div class="h-px bg-gray-100 my-4" />

            <div class="flex items-center justify-between gap-2">
                <div class="flex items-center gap-1 sm:gap-2 flex-wrap">
                    <button
                        type="button"
                        class="flex items-center justify-center gap-2 px-3 py-2 rounded-lg text-gray-400 cursor-not-allowed opacity-60"
                        disabled
                    >
                        <PictureOutlined class="text-primary text-xl" />
                        <span class="text-sm font-medium hidden sm:inline">
                            Ảnh/Video
                        </span>
                    </button>
                    <button
                        type="button"
                        class="flex items-center gap-2 px-3 py-2 rounded-lg text-gray-400 cursor-not-allowed opacity-60"
                        disabled
                    >
                        <PaperClipOutlined class="text-primary text-xl" />
                        <span class="text-sm font-medium hidden sm:inline">
                            Tệp
                        </span>
                    </button>
                    <button
                        type="button"
                        class="flex items-center gap-2 px-3 py-2 rounded-lg text-gray-400 cursor-not-allowed opacity-60"
                        disabled
                    >
                        <SmileOutlined class="text-primary text-xl" />
                        <span class="text-sm font-medium hidden sm:inline">
                            Cảm xúc
                        </span>
                    </button>
                </div>
            </div>
        </div>
    </VModal>
</template>
