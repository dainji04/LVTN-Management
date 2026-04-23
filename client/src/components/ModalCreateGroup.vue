<template>
  <VModal
    v-model:open="isOpen"
    :title="t('createNewGroup')"
    :ok-text="t('createGroup')"
    :cancel-text="t('cancel')"
    :handle-submit="handleSubmit"
    :confirm-loading="loading"
    :width="560"
  >
    <div class="space-y-5 py-2">
      <!-- Cover image preview -->
      <div
        class="relative h-36 rounded-xl overflow-hidden bg-gradient-to-br from-primary/10 via-rose-50 to-pink-50 border-2 border-dashed border-gray-200 cursor-pointer hover:border-primary/40 transition-colors group"
        @click="triggerCoverUpload"
      >
        <img
          v-if="coverPreview"
          :src="coverPreview"
          alt="Cover"
          class="w-full h-full object-cover"
        />
        <div
          v-else
          class="flex flex-col items-center justify-center h-full text-gray-400 group-hover:text-primary transition-colors"
        >
          <CameraOutlined class="text-2xl mb-1" />
          <span class="text-xs font-medium">{{ t('addCoverImage') }}</span>
        </div>
        <!-- Upload progress overlay -->
        <div
          v-if="uploadingCover"
          class="absolute inset-0 bg-black/40 flex items-center justify-center"
        >
          <LoadingOutlined class="text-2xl text-white animate-spin" />
        </div>
        <!-- Remove cover button -->
        <button
          v-if="coverPreview && !uploadingCover"
          class="absolute top-2 right-2 w-7 h-7 rounded-full bg-black/50 hover:bg-black/70 flex items-center justify-center text-white transition-colors"
          @click.stop="removeCover"
        >
          <CloseOutlined class="text-xs" />
        </button>
        <input
          ref="coverInputRef"
          type="file"
          accept="image/*"
          class="hidden"
          @change="handleCoverChange"
        />
        <!-- Avatar overlay -->
        <div
          class="absolute -bottom-1 left-4 w-16 h-16 rounded-full border-3 border-white bg-gray-100 shadow-sm flex items-center justify-center cursor-pointer overflow-hidden group/avatar"
          @click.stop="triggerAvatarUpload"
        >
          <img
            v-if="avatarPreview"
            :src="avatarPreview"
            alt="Avatar"
            class="w-full h-full object-cover"
          />
          <div v-if="uploadingAvatar" class="absolute inset-0 bg-black/40 flex items-center justify-center">
            <LoadingOutlined class="text-base text-white animate-spin" />
          </div>
          <div v-else-if="!avatarPreview" class="flex flex-col items-center text-gray-400 group-hover/avatar:text-primary transition-colors">
            <UserOutlined class="text-lg" />
            <span class="text-[10px]">Avatar</span>
          </div>
          <!-- Remove avatar button -->
          <button
            v-if="avatarPreview && !uploadingAvatar"
            class="absolute -top-0.5 -right-0.5 w-5 h-5 rounded-full bg-black/50 hover:bg-black/70 flex items-center justify-center text-white transition-colors z-10"
            @click.stop="removeAvatar"
          >
            <CloseOutlined style="font-size: 8px" />
          </button>
        </div>
        <input
          ref="avatarInputRef"
          type="file"
          accept="image/*"
          class="hidden"
          @change="handleAvatarChange"
        />
      </div>

      <!-- Group name -->
      <div>
        <label class="block text-sm font-semibold text-gray-700 mb-1.5">
          {{ t('groupName') }} <span class="text-red-500">*</span>
        </label>
        <input
          v-model="form.tenNhom"
          type="text"
          :placeholder="t('groupNamePlaceholder')"
          class="w-full px-4 py-2.5 rounded-lg border border-gray-200 bg-gray-50 text-sm outline-none transition-all focus:border-primary focus:bg-white focus:shadow-sm"
          :class="{ 'border-red-300 bg-red-50': errors.tenNhom }"
        />
        <p v-if="errors.tenNhom" class="text-xs text-red-500 mt-1">{{ errors.tenNhom }}</p>
      </div>

      <!-- Description -->
      <div>
        <label class="block text-sm font-semibold text-gray-700 mb-1.5">
          {{ t('groupDescription') }}
        </label>
        <textarea
          v-model="form.moTa"
          :placeholder="t('groupDescriptionPlaceholder')"
          rows="3"
          class="w-full px-4 py-2.5 rounded-lg border border-gray-200 bg-gray-50 text-sm outline-none transition-all focus:border-primary focus:bg-white focus:shadow-sm resize-none"
        />
      </div>

      <!-- Group type -->
      <div>
        <label class="block text-sm font-semibold text-gray-700 mb-2">
          {{ t('groupType') }}
        </label>
        <div class="grid grid-cols-2 gap-3">
          <button
            type="button"
            class="p-3 rounded-xl border-2 text-left transition-all"
            :class="form.loaiNhom === 'public'
              ? 'border-primary bg-primary/5'
              : 'border-gray-200 hover:border-gray-300'"
            @click="form.loaiNhom = 'public'"
          >
            <GlobalOutlined
              class="text-lg mb-1"
              :class="form.loaiNhom === 'public' ? 'text-primary' : 'text-gray-400'"
            />
            <p class="text-sm font-semibold" :class="form.loaiNhom === 'public' ? 'text-primary' : 'text-gray-700'">
              {{ t('publicGroup') }}
            </p>
            <p class="text-xs text-gray-400 mt-0.5">{{ t('publicGroupDesc') }}</p>
          </button>
          <button
            type="button"
            class="p-3 rounded-xl border-2 text-left transition-all"
            :class="form.loaiNhom === 'private'
              ? 'border-primary bg-primary/5'
              : 'border-gray-200 hover:border-gray-300'"
            @click="form.loaiNhom = 'private'"
          >
            <LockOutlined
              class="text-lg mb-1"
              :class="form.loaiNhom === 'private' ? 'text-primary' : 'text-gray-400'"
            />
            <p class="text-sm font-semibold" :class="form.loaiNhom === 'private' ? 'text-primary' : 'text-gray-700'">
              {{ t('privateGroup') }}
            </p>
            <p class="text-xs text-gray-400 mt-0.5">{{ t('privateGroupDesc') }}</p>
          </button>
        </div>
      </div>

      <!-- Require post approval -->
      <div class="flex items-center justify-between bg-gray-50 rounded-xl px-4 py-3">
        <div>
          <p class="text-sm font-semibold text-gray-700">{{ t('requirePostApproval') }}</p>
          <p class="text-xs text-gray-400 mt-0.5">{{ t('requirePostApprovalDesc') }}</p>
        </div>
        <a-switch v-model:checked="form.canDuyetDangBai" />
      </div>
    </div>
  </VModal>
</template>

<script lang="ts" setup>
import { ref, reactive, watch } from 'vue';
import {
  CameraOutlined,
  UserOutlined,
  GlobalOutlined,
  LockOutlined,
  LoadingOutlined,
  CloseOutlined,
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import VModal from './VModal.vue';
import { useI18n } from 'vue-i18n';
import { uploadToCloudinary } from '../helpers/cloudinaryHelper';

const { t } = useI18n();

const props = defineProps<{
  open: boolean;
}>();

const emit = defineEmits<{
  'update:open': [value: boolean];
  'created': [form: CreateGroupPayload];
}>();

export interface CreateGroupPayload {
  tenNhom: string;
  moTa: string;
  loaiNhom: 'public' | 'private';
  canDuyetDangBai: boolean;
  anhBia: string | null;
  anhDaiDien: string | null;
}

const isOpen = ref(props.open);
watch(() => props.open, (v) => { isOpen.value = v; });
watch(isOpen, (v) => { emit('update:open', v); });

const loading = ref(false);
const uploadingCover = ref(false);
const uploadingAvatar = ref(false);
const coverInputRef = ref<HTMLInputElement | null>(null);
const avatarInputRef = ref<HTMLInputElement | null>(null);
const coverPreview = ref('');
const avatarPreview = ref('');

const form = reactive({
  tenNhom: '',
  moTa: '',
  loaiNhom: 'public' as 'public' | 'private',
  canDuyetDangBai: false,
  anhBiaFile: null as File | null,
  anhDaiDienFile: null as File | null,
  anhBiaUrl: '',
  anhDaiDienUrl: '',
});

const errors = reactive({
  tenNhom: '',
});

function triggerCoverUpload() {
  if (uploadingCover.value) return;
  coverInputRef.value?.click();
}

function triggerAvatarUpload() {
  if (uploadingAvatar.value) return;
  avatarInputRef.value?.click();
}

async function handleCoverChange(e: Event) {
  const input = e.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;

  form.anhBiaFile = file;
  coverPreview.value = URL.createObjectURL(file);
  uploadingCover.value = true;
  try {
    const res = await uploadToCloudinary(file, 'groups/covers');
    form.anhBiaUrl = res.secure_url;
  } catch {
    message.error(t('uploadImageFailed'));
    coverPreview.value = '';
    form.anhBiaFile = null;
    form.anhBiaUrl = '';
  } finally {
    uploadingCover.value = false;
    input.value = '';
  }
}

async function handleAvatarChange(e: Event) {
  const input = e.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;

  form.anhDaiDienFile = file;
  avatarPreview.value = URL.createObjectURL(file);
  uploadingAvatar.value = true;
  try {
    const res = await uploadToCloudinary(file, 'groups/avatars');
    form.anhDaiDienUrl = res.secure_url;
  } catch {
    message.error(t('uploadImageFailed'));
    avatarPreview.value = '';
    form.anhDaiDienFile = null;
    form.anhDaiDienUrl = '';
  } finally {
    uploadingAvatar.value = false;
    input.value = '';
  }
}

function removeCover() {
  coverPreview.value = '';
  form.anhBiaFile = null;
  form.anhBiaUrl = '';
}

function removeAvatar() {
  avatarPreview.value = '';
  form.anhDaiDienFile = null;
  form.anhDaiDienUrl = '';
}

function validate(): boolean {
  errors.tenNhom = '';
  if (!form.tenNhom.trim()) {
    errors.tenNhom = t('groupNameRequired');
    return false;
  }
  return true;
}

function resetForm() {
  form.tenNhom = '';
  form.moTa = '';
  form.loaiNhom = 'public';
  form.canDuyetDangBai = false;
  form.anhBiaFile = null;
  form.anhDaiDienFile = null;
  form.anhBiaUrl = '';
  form.anhDaiDienUrl = '';
  coverPreview.value = '';
  avatarPreview.value = '';
  errors.tenNhom = '';
}

async function handleSubmit() {
  if (!validate()) return;
  if (uploadingCover.value || uploadingAvatar.value) {
    message.warning(t('waitForUpload'));
    return;
  }

  loading.value = true;
  try {
    const payload: CreateGroupPayload = {
      tenNhom: form.tenNhom.trim(),
      moTa: form.moTa.trim(),
      loaiNhom: form.loaiNhom,
      canDuyetDangBai: form.canDuyetDangBai,
      anhBia: form.anhBiaUrl || null,
      anhDaiDien: form.anhDaiDienUrl || null,
    };
    emit('created', payload);
    resetForm();
    isOpen.value = false;
  } finally {
    loading.value = false;
  }
}

watch(isOpen, (val) => {
  if (!val) resetForm();
});
</script>

<style scoped>
.border-3 {
  border-width: 3px;
}
</style>
