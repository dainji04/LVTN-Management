<template>
  <div
    class="home-container flex justify-between h-screen bg-[#f5f5f5] overflow-hidden"
  >
    <!-- Left Sidebar -->
    <div
      class="sidebar w-64 bg-white border-r border-gray-200 flex flex-col left-0 top-0 h-full overflow-y-auto"
    >
      <slot name="left-sidebar"></slot>
    </div>
    <!-- Main content -->
    <main
      class="main-content w-full flex-1 flex justify-center overflow-y-auto"
    >
      <slot></slot>
    </main>
    <!-- Right Sidebar -->
    <div
      v-if="!hideRightSidebar"
      class="right-sidebar w-80 bg-white border-l border-gray-200 flex justify-end top-0 h-full overflow-y-auto hidden xl:block"
    >
      <slot name="right-sidebar"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  hideRightSidebar?: boolean;
}>();
</script>

<style>
.main-content::-webkit-scrollbar {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

/* Responsive */
@media (max-width: 1280px) {
  .right-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .sidebar {
    width: 80px;
  }

  .main-content {
    margin-left: 80px;
  }

  .logo-img {
    max-width: 60px;
  }
}
</style>
