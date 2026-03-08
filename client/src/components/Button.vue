<template>
    <button class="" :class="buttonClasses" :disabled="isDisabled">
        <slot />
    </button>
</template>

<script lang="ts" setup>
import { computed } from 'vue';

const props = defineProps<{
    classes?: string;
    type?: 'primary' | 'secondary';
    border?: boolean;
    isDisabled?: boolean;
}>();

const buttonClasses = computed<string>(() => {
    const textColor = props.type === 'primary' ? 'text-black' : 'text-primary';
    const backgroundColor = props.type === 'primary' ? 'bg-primary' : 'bg-secondary';
    const border = props.border ? 'border border-black rounded-2xl' : '';
    return `${textColor} ${backgroundColor} ${border} ${props.classes}`;
});

const isDisabled = computed<boolean>(() => {
    return props.isDisabled;
});
</script>

<style>
button[disabled] {
    opacity: 0.5;
    cursor: not-allowed;
}
</style>