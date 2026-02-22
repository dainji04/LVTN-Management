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

<style>
/* Basic Login Form - Clean & Simple */

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
    background: #f8fafc;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    line-height: 1.6;
    color: #334155;
}
</style>