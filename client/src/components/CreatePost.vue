<template>
  <div class="w-full bg-white rounded-xl border border-gray-100 p-4 shadow-lg">
    <div class="flex items-center gap-3">
      <div
        class="w-10 h-10 shrink-0 rounded-full overflow-hidden bg-orange-200 border border-gray-200"
      >
        <img
          src="https://via.placeholder.com/40"
          alt="Avatar"
          class="w-full h-full object-cover"
        />
      </div>

      <input
        v-model="postContent"
        type="text"
        :placeholder="$t('writeSomethingForTheGroup')"
        class="flex-1 bg-slate-50 hover:bg-slate-100 focus:bg-white border border-transparent focus:border-gray-200 rounded-full px-4 py-2.5 text-gray-700 outline-none transition-all duration-200 placeholder:text-gray-500"
        @keyup.enter="handlePost"
      />
    </div>

    <div class="h-px bg-gray-100 my-3"></div>

    <div class="flex items-center justify-between mt-1">
      <div class="flex items-center gap-1 sm:gap-2">
        <button
          class="flex items-center justify-center gap-2 px-3 py-2 rounded-lg hover:bg-slate-50 text-gray-500 transition-colors cursor-pointer"
        >
          <span class="text-sm font-medium hidden sm:block"></span>
          <a-upload
            v-model:file-list="fileList"
            name="file"
            action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
            :headers="{
              authorization: 'authorization-text',
            }"
            @change="handleChange"
          >
            <a-button>
              <PictureOutlined class="text-primary text-xl" />
              Ảnh/Video
            </a-button>
          </a-upload>
        </button>

        <button
          class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-slate-50 text-gray-500 transition-colors cursor-pointer"
        >
          <PaperClipOutlined class="text-primary text-xl" />
          <span class="text-sm font-medium hidden sm:block">Tệp</span>
        </button>

        <button
          class="flex items-center gap-2 px-3 py-2 rounded-lg hover:bg-slate-50 text-gray-500 transition-colors cursor-pointer"
        >
          <SmileOutlined class="text-primary text-xl" />
          <span class="text-sm font-medium hidden sm:block">Cảm xúc</span>
        </button>
      </div>

      <button
        @click="handlePost"
        :disabled="!postContent.trim()"
        class="bg-primary hover:bg-primary/90 text-white disabled:opacity-60 disabled:cursor-not-allowed text-sm font-medium py-2 px-5 rounded-lg transition-colors"
      >
        {{ $t('post') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import {
  PictureOutlined,
  PaperClipOutlined,
  SmileOutlined,
} from "@ant-design/icons-vue";
import type { CreatePostRequest } from "../types/postType";
import { useAuthStore } from "../store/authStore";
import Swal from "sweetalert2";
import { useI18n } from "vue-i18n";
import { usePostStore } from "../store/postStore";
import { message } from 'ant-design-vue';
import type { UploadChangeParam } from 'ant-design-vue';

const {t} = useI18n();
const postContent = ref("");
const authStore = useAuthStore();
const postStore = usePostStore();

const fileList = ref([]);

const handlePost = async () => {
  if (!postContent.value.trim()) return;

  try {
    if (!authStore.getUser?.maNguoiDung) {
      Swal.fire({
        title: t('error'),
        text: t('pleaseLoginAndTryAgain'),
        icon: 'error',
      });
      return;
    }
    const requestBody: CreatePostRequest = {
      maNguoiDung: authStore.getUser?.maNguoiDung,
      maNhom: null,
      noiDung: postContent.value.trim(),
      quyenRiengTu: 'friends',
      viTri: null,
      mauNen: null,
      danhSachCongTacVien: null,
      danhSachAnh: null,
    }
    const isCreatePostSuccess = await postStore.createPost(requestBody);
    if (isCreatePostSuccess) {
      Swal.fire({
        title: t('success'),
        text: t('createPostSuccess'),
        icon: 'success',
      });
    } else {
      Swal.fire({
        title: t('error'),
        text: t('createPostFailed'),
        icon: 'error',
      });
    }
  } catch (error) {
    
  }
};

const handleChange = (info: UploadChangeParam) => {
  if (info.file.status !== 'uploading') {
    console.log(info.file, info.fileList);
  }
  if (info.file.status === 'done') {
    message.success(`${info.file.name} file uploaded successfully`);
  } else if (info.file.status === 'error') {
    message.error(`${info.file.name} file upload failed.`);
  }
};

</script>
