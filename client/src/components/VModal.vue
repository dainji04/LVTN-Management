<template>
    <div>
      <a-modal
        v-model:open="openModel"
        :title="props.title"
        :width="props.width"
        :ok-text="props.okText"
        :cancel-text="props.cancelText"
        :confirm-loading="props.confirmLoading"
        @ok="handleOk"
      >
        <slot />
      </a-modal>
    </div>
  </template>
  <script lang="ts" setup>
  import { computed } from 'vue';
  const props = withDefaults(defineProps<{
    open: boolean;
    handleSubmit: () => void | Promise<void>;
    title: string;
    width?: string | number;
    okText?: string;
    cancelText?: string;
    confirmLoading?: boolean;
  }>(), {
    open: false,
    title: 'Modal',
    handleSubmit: () => {},
    width: 520,
    okText: undefined,
    cancelText: undefined,
    confirmLoading: false,
  });

  async function handleOk() {
    await props.handleSubmit();
  }

  const emit = defineEmits<{
    'update:open': [value: boolean];
  }>();

  const openModel = computed({
    get: () => props.open,
    set: (v: boolean) => emit('update:open', v),
  });
  </script>
  
  