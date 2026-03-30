<script setup lang="ts">
import Header from './layouts/Header.vue';
import { authHelper } from './helpers/authHelper';

// Use reactive authentication state
const showHeader = authHelper.isAuthenticatedRef;
</script>

<template>
    <Header v-if="showHeader" />
    <router-view />
</template>
