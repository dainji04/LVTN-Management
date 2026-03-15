<template>
    <button
        :class="buttonClasses"
        :disabled="isDisabled"
        :type="htmlType"
        @click="$emit('click', $event)"
    >
        <slot name="icon" />
        <slot />
    </button>
</template>

<script lang="ts" setup>
import { computed } from 'vue';

const props = withDefaults(defineProps<{
    classes?: string;
    type?: 'primary' | 'secondary' | 'text' | 'default';
    border?: boolean;
    isDisabled?: boolean;
    block?: boolean;
    size?: 'small' | 'medium' | 'large';
    htmlType?: 'button' | 'submit' | 'reset';
}>(), {
    type: 'default',
    isDisabled: false,
    border: false,
    block: false,
    size: 'medium',
    htmlType: 'button',
});

defineEmits<{
    click: [event: MouseEvent];
}>();

const buttonClasses = computed<string>(() => {
    const baseClasses = 'inline-flex items-center justify-center transition-colors';
    
    // Type styles
    let typeClasses = '';
    if (props.type === 'primary') {
        typeClasses = 'bg-primary hover:bg-primary/90 text-white';
    } else if (props.type === 'secondary') {
        typeClasses = 'bg-secondary text-primary';
    } else if (props.type === 'text') {
        typeClasses = 'bg-transparent text-gray-600 hover:text-primary';
    } else {
        typeClasses = 'bg-white text-primary hover:bg-gray-100 border border-gray-200';
    }
    
    // Size styles
    let sizeClasses = '';
    if (props.size === 'small') {
        sizeClasses = 'px-3 py-1.5 text-sm';
    } else if (props.size === 'large') {
        sizeClasses = 'px-6 py-3 text-lg';
    } else {
        sizeClasses = 'px-4 py-2';
    }
    
    // Border
    const borderClass = props.border ? 'border border-black rounded-2xl' : 'rounded-lg';
    
    // Block
    const blockClass = props.block ? 'w-full' : '';
    
    return `${baseClasses} ${typeClasses} ${sizeClasses} ${borderClass} ${blockClass} ${props.classes || ''}`.trim();
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