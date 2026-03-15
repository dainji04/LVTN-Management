<template>
    <a-dropdown :placement="directionClass" class="cursor-pointer">
        <a class="ant-dropdown-link" @click.prevent>
            <MenuOutlined />
        </a>
        <template #overlay>
            <a-menu>
                <a-menu-item key="1">
                    <div class="flex items-center gap-2">
                        <UserOutlined />
                        <span>{{ $t('profile') }}</span>
                    </div>
                </a-menu-item>
                <a-menu-item key="2">
                    <div class="flex items-center gap-2">
                        <SettingOutlined />
                        <span>{{ $t('settings') }}</span>
                    </div>
                </a-menu-item>
                <a-menu-item key="3" @click="handleLogout" class="flex items-center gap-2">
                    <div class="flex items-center gap-2">
                        <LogoutOutlined />
                        <span>{{ $t('logout') }}</span>
                    </div>
                </a-menu-item>
            </a-menu>
        </template>
    </a-dropdown>
</template>
<script lang="ts" setup>
import { LogoutOutlined, MenuOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons-vue';
import { computed } from 'vue';
import { useAuthStore } from '../store/authStore';
import { useRouter } from 'vue-router';
import { notificationHelper } from '../helpers/notificationHelper';
import { useI18n } from 'vue-i18n';

const props = defineProps<{
    direction?: 'topRight' | 'topLeft' | 'bottomRight' | 'bottomLeft';
}>();

const { t } = useI18n();

const authStore = useAuthStore();
const router = useRouter();
const directionClass = computed(() => {
    return props.direction || 'topRight';
});

const handleLogout = async () => {
    const isLogoutSuccess = await authStore.logout();
    if (isLogoutSuccess) {
        notificationHelper("success", t("logoutSuccess"));
        setTimeout(() => {
            router.push("/login");
        }, 1000);
    } else {
        notificationHelper("error", t("logoutFailed") + ' ' + t("pleaseTryAgain"));
        setTimeout(() => {
            router.push("/");
        }, 1000);
    }
};
</script>