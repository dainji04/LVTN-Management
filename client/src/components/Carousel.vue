<template>
    <a-carousel :after-change="onChange" class="w-full h-[400px]">
        <template #prevArrow>
            <div class="custom-slick-arrow" style="left: 10px; z-index: 1">
                <left-circle-outlined />
            </div>
        </template>
        <template #nextArrow>
            <div class="custom-slick-arrow" style="right: 10px">
                <right-circle-outlined />
            </div>
        </template>
    </a-carousel>
</template>
<script lang="ts" setup>
const onChange = (current: number) => {
    console.log(current);
};
</script>
<style scoped>
/* For demo */
:deep(.slick-slide) {
    text-align: center;
    height: 160px;
    line-height: 160px;
    background: #364d79;
    overflow: hidden;
}

:deep(.slick-slide h3) {
    color: #fff;
}
</style>