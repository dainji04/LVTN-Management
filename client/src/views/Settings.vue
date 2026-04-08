<template>
  <AuthLayout :hide-right-sidebar="true" :hide-left-sidebar="true">
    <!-- Main Content -->
    <main class="main-content w-full flex-1 overflow-y-auto bg-[#f5f5f5]">
      <div class="max-w-6xl mx-auto py-6 px-4 h-full">
        <div class="settings-wrapper bg-white rounded-2xl shadow-sm overflow-hidden flex h-[calc(100vh-48px)]">

          <!-- ===== LEFT NAV (Column 1) ===== -->
          <div class="settings-left-nav w-56 shrink-0 border-r border-gray-100 flex flex-col py-6 px-3 overflow-y-auto">
            <h1 class="text-2xl font-bold text-gray-900 px-3 mb-6">
              <router-link to="/">
                <LeftOutlined />
              </router-link>
            </h1>

            <!-- Chung -->
            <div class="nav-section mb-4">
              <p class="nav-section-title px-3 mb-2">{{ $t('settingsGeneral') }}</p>
              <button
                v-for="item in generalItems"
                :key="item.key"
                class="nav-item"
                :class="{ 'nav-item-active': activeSection === item.key }"
                @click="selectSection(item.key)"
              >
                <span>{{ $t(item.label) }}</span>
                <RightOutlined class="nav-arrow" />
              </button>
            </div>

            <!-- Bảo mật -->
            <div class="nav-section mb-4">
              <p class="nav-section-title px-3 mb-2">{{ $t('settingsSecurity') }}</p>
              <button
                v-for="item in securityItems"
                :key="item.key"
                class="nav-item"
                :class="{ 'nav-item-active': activeSection === item.key }"
                @click="selectSection(item.key)"
              >
                <span>{{ $t(item.label) }}</span>
                <RightOutlined class="nav-arrow" />
              </button>
            </div>

            <!-- Sinh trắc học -->
            <div class="nav-section mb-4">
              <p class="nav-section-title px-3 mb-2">{{ $t('settingsBiometric') }}</p>
              <p class="text-xs text-gray-400 px-3 mb-3 leading-relaxed">
                {{ $t('settingsBiometricDesc') }}
              </p>
              <div class="flex items-center justify-between px-3 py-1">
                <span class="text-sm font-semibold text-gray-800">{{ $t('settingsBiometric') }}</span>
                <a-switch v-model:checked="biometricEnabled" />
              </div>
            </div>

            <!-- Ngôn ngữ -->
            <div class="nav-section">
              <p class="nav-section-title px-3 mb-2">{{ $t('settingsLanguage') }}</p>
              <div class="px-3 space-y-1">
                <button
                v-for="lang in languages"
                :key="lang.code"
                class="lang-item"
                :class="{ 'lang-item-active': currentLocale === lang.code }"
                @click="changeLang(lang.code)"
                >
                  <span class="lang-flag">{{ lang.flag }}</span>
                  <span class="flex-1 text-left text-sm font-medium">{{ lang.label }}</span>
                  <span v-if="currentLocale === lang.code" class="lang-check">
                    <CheckOutlined />
                  </span>
                </button>
              </div>
            </div>
          </div>

          <!-- ===== MIDDLE PANEL (Column 2) ===== -->
          <div class="settings-middle w-52 shrink-0 border-r border-gray-100 flex flex-col py-6 px-4 overflow-y-auto">
            <h2 class="text-xl font-bold text-gray-900 mb-5">{{ $t('settingsTitle') }}</h2>

            <!-- Chung section -->
            <div class="mid-section mb-5">
              <p class="mid-section-title mb-2">{{ $t('settingsGeneral') }}</p>
              <button
                v-for="item in generalItems"
                :key="item.key"
                class="mid-item"
                :class="{ 'mid-item-active': activeSection === item.key }"
                @click="selectSection(item.key)"
              >
                <span>{{ $t(item.label) }}</span>
                <RightOutlined v-if="item.key === 'profile'" class="text-gray-400 text-xs" />
              </button>
            </div>

            <!-- Bảo mật section -->
            <div class="mid-section">
              <p class="mid-section-title mb-2">{{ $t('settingsSecurity') }}</p>
              <button
                v-for="item in securityItems"
                :key="item.key"
                class="mid-item"
                :class="{ 'mid-item-active': activeSection === item.key }"
                @click="selectSection(item.key)"
              >
                {{ $t(item.label) }}
              </button>
            </div>
          </div>

          <!-- ===== RIGHT CONTENT PANEL (Column 3) ===== -->
          <div class="settings-content flex-1 py-8 px-10 overflow-y-auto">

            <!-- Change Password Content -->
            <div v-if="activeSection === 'password'">
              <h2 class="text-2xl font-bold text-gray-900 mb-6">{{ $t('settingsChangePassword') }}</h2>

              <form @submit.prevent="handleChangePassword" class="max-w-lg space-y-5">
                <!-- Current Password -->
                <div class="form-group">
                  <label class="form-label">{{ $t('settingsCurrentPassword') }}</label>
                  <div class="input-wrapper">
                    <input
                      :type="showCurrentPw ? 'text' : 'password'"
                      v-model="passwordForm.currentPassword"
                      class="form-input"
                      placeholder="••••••••"
                    />
                    <button type="button" class="eye-btn" @click="showCurrentPw = !showCurrentPw">
                      <EyeOutlined v-if="!showCurrentPw" />
                      <EyeInvisibleOutlined v-else />
                    </button>
                  </div>
                </div>

                <!-- New Password -->
                <div class="form-group">
                  <label class="form-label">{{ $t('settingsNewPassword') }}</label>
                  <div class="input-wrapper">
                    <input
                      :type="showNewPw ? 'text' : 'password'"
                      v-model="passwordForm.newPassword"
                      class="form-input"
                      placeholder="••••••••"
                    />
                    <button type="button" class="eye-btn" @click="showNewPw = !showNewPw">
                      <EyeOutlined v-if="!showNewPw" />
                      <EyeInvisibleOutlined v-else />
                    </button>
                  </div>
                </div>

                <!-- Confirm New Password -->
                <div class="form-group">
                  <label class="form-label">{{ $t('settingsConfirmNewPassword') }}</label>
                  <div class="input-wrapper" :class="{ 'input-error': passwordMismatch }">
                    <input
                      :type="showConfirmPw ? 'text' : 'password'"
                      v-model="passwordForm.confirmPassword"
                      class="form-input"
                      placeholder="••••••••"
                    />
                    <button type="button" class="eye-btn" @click="showConfirmPw = !showConfirmPw">
                      <EyeOutlined v-if="!showConfirmPw" />
                      <EyeInvisibleOutlined v-else />
                    </button>
                  </div>
                  <p v-if="passwordMismatch" class="text-sm text-gray-500 mt-1.5">
                    {{ $t('settingsPasswordMismatch') }}
                  </p>
                </div>

                <!-- Submit -->
                <button type="submit" class="submit-btn">
                  {{ $t('settingsChangePassword') }}
                </button>
              </form>
            </div>

            <!-- Profile Content -->
            <div v-else-if="activeSection === 'profile'">
              <h2 class="text-2xl font-bold text-gray-900 mb-6">{{ $t('settingsProfile') }}</h2>
              <div class="max-w-lg space-y-5">
                <div class="form-group">
                  <label class="form-label">{{ $t('settingsLastName') }}</label>
                  <input v-model="profileForm.ho" type="text" class="form-input" :placeholder="$t('settingsLastNamePlaceholder')" />
                </div>
                <div class="form-group">
                  <label class="form-label">{{ $t('settingsFirstName') }}</label>
                  <input v-model="profileForm.ten" type="text" class="form-input" :placeholder="$t('settingsFirstNamePlaceholder')" />
                </div>
                <div class="form-group">
                  <label class="form-label">{{ $t('settingsBio') }}</label>
                  <textarea v-model="profileForm.bio" class="form-input resize-none" rows="3" :placeholder="$t('settingsBioPlaceholder')"></textarea>
                </div>
                <button class="submit-btn" @click="handleSaveProfile">{{ $t('settingsSaveChanges') }}</button>
              </div>
            </div>

            <!-- Contact Support Content -->
            <div v-else-if="activeSection === 'support'">
              <h2 class="text-2xl font-bold text-gray-900 mb-6">{{ $t('settingsContactSupport') }}</h2>
              <div class="max-w-lg space-y-5">
                <div class="form-group">
                  <label class="form-label">{{ $t('settingsSupportTitle') }}</label>
                  <input v-model="supportForm.title" type="text" class="form-input" :placeholder="$t('settingsSupportTitlePlaceholder')" />
                </div>
                <div class="form-group">
                  <label class="form-label">{{ $t('settingsSupportDescription') }}</label>
                  <textarea v-model="supportForm.description" class="form-input resize-none" rows="5" :placeholder="$t('settingsSupportDescPlaceholder')"></textarea>
                </div>
                <button class="submit-btn" @click="handleSendSupport">{{ $t('settingsSendRequest') }}</button>
              </div>
            </div>

            <!-- Privacy Policy Content -->
            <div v-else-if="activeSection === 'privacy'">
              <h2 class="text-2xl font-bold text-gray-900 mb-6">{{ $t('settingsPrivacyPolicy') }}</h2>
              <div class="max-w-2xl prose prose-sm text-gray-600 space-y-4">
                <p class="leading-relaxed">{{ $t('settingsPrivacyIntro') }}</p>
                <h3 class="text-base font-semibold text-gray-800">{{ $t('settingsPrivacyCollectTitle') }}</h3>
                <p class="leading-relaxed">{{ $t('settingsPrivacyCollectBody') }}</p>
                <h3 class="text-base font-semibold text-gray-800">{{ $t('settingsPrivacyUseTitle') }}</h3>
                <p class="leading-relaxed">{{ $t('settingsPrivacyUseBody') }}</p>
                <h3 class="text-base font-semibold text-gray-800">{{ $t('settingsPrivacyProtectTitle') }}</h3>
                <p class="leading-relaxed">{{ $t('settingsPrivacyProtectBody') }}</p>
              </div>
            </div>

          </div>

        </div>
      </div>
    </main>
  </AuthLayout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import type { SupportedLocales } from '../i18n';
import {
  RightOutlined,
  EyeOutlined,
  EyeInvisibleOutlined,
  CheckOutlined,
  LeftOutlined,
} from '@ant-design/icons-vue';
import AuthLayout from '../layouts/authLayout.vue';
import { useAuthStore } from '../store/authStore';
import { changeLanguages } from '../helpers/changeLanguagesHelper';
import { notificationHelper } from '../helpers/notificationHelper';
import { useI18n } from 'vue-i18n';

const {t} = useI18n();

const authStore = useAuthStore();
const currentUser = authStore.getUser;

// i18n locale switcher
const currentLocale = ref(localStorage.getItem('locale') as 'vi' | 'en' || 'vi');

const changeLang = (lang: 'vi' | 'en') => {
  currentLocale.value = lang;
  changeLanguages(lang);
}

const languages = [
  { code: 'vi' as SupportedLocales, label: 'Tiếng Việt', flag: '🇻🇳' },
  { code: 'en' as SupportedLocales, label: 'English', flag: '🇺🇸' },
];

// Active section state
const activeSection = ref<'profile' | 'support' | 'password' | 'privacy'>('password');

const generalItems: { key: 'profile' | 'support'; label: string }[] = [
  { key: 'profile', label: 'settingsProfile' },
  { key: 'support', label: 'settingsContactSupport' },
];

const securityItems: { key: 'password' | 'privacy'; label: string }[] = [
  { key: 'password', label: 'settingsChangePassword' },
  { key: 'privacy', label: 'settingsPrivacyPolicy' },
];

const selectSection = (key: typeof activeSection.value) => {
  activeSection.value = key;
};

// Biometric toggle
const biometricEnabled = ref(false);

// Password form
const showCurrentPw = ref(false);
const showNewPw = ref(false);
const showConfirmPw = ref(false);

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const passwordMismatch = computed(() => {
  return (
    passwordForm.value.confirmPassword.length > 0 &&
    passwordForm.value.newPassword !== passwordForm.value.confirmPassword
  );
});

const handleChangePassword = () => {
  if (passwordMismatch.value) return;
  if (!passwordForm.value.currentPassword || !passwordForm.value.newPassword) {
    notificationHelper('warning',t('toastFillAllFields'));

    return;
  }
  // TODO: call API to change password
  notificationHelper('success',t('toastChangePasswordSuccess'));
  passwordForm.value = { currentPassword: '', newPassword: '', confirmPassword: '' };
};

// Profile form
const profileForm = ref({
  ho: currentUser?.ho ?? '',
  ten: currentUser?.ten ?? '',
  bio: currentUser?.gioiThieu ?? '',
});

const handleSaveProfile = () => {
  // TODO: call API to save profile
  notificationHelper('success',t('toastSaveProfileSuccess'));
};

// Support form
const supportForm = ref({
  title: '',
  description: '',
});

const handleSendSupport = () => {
  if (!supportForm.value.title || !supportForm.value.description) {
    notificationHelper('warning',t('toastFillAllFields'));
    return;
  }
  notificationHelper('success',t('toastSendSupportSuccess'));
  supportForm.value = { title: '', description: '' };
};
</script>

<style scoped>
/* Left nav */
.nav-section-title {
  @apply text-xs font-semibold text-gray-400 uppercase tracking-wider;
}

.nav-item {
  @apply w-full flex items-center justify-between text-sm font-semibold text-gray-700
  rounded-xl px-3 py-2.5 mb-0.5 transition-all duration-150 hover:bg-gray-100 text-left;
}

.nav-arrow {
  @apply text-gray-400 text-xs opacity-0 transition-opacity duration-150;
}

.nav-item:hover .nav-arrow {
  @apply opacity-100;
}

.nav-item-active {
  @apply bg-gray-100 text-gray-900;
}

.nav-item-active .nav-arrow {
  @apply opacity-100;
}

/* Middle panel */
.mid-section-title {
  @apply text-xs font-semibold text-gray-400 uppercase tracking-wider;
}

.mid-item {
  @apply w-full flex items-center justify-between text-sm text-gray-700 font-medium
  rounded-xl px-3 py-2.5 mb-0.5 text-left transition-all duration-150 hover:bg-gray-100;
}

.mid-item-active {
  @apply bg-gray-100 text-gray-900 font-semibold;
}

/* Right content */
.form-group {
  @apply flex flex-col gap-1.5;
}

.form-label {
  @apply text-sm font-medium text-gray-700;
}

.form-input {
  @apply w-full border border-gray-200 rounded-xl px-4 py-3 text-gray-800 text-sm
  focus:outline-none focus:border-pink-400 focus:ring-2 focus:ring-pink-100 transition-all;
}

.input-wrapper {
  @apply relative flex items-center;
}

.input-wrapper .form-input {
  @apply pr-11;
}

.input-wrapper.input-error .form-input {
  @apply border-red-300 focus:border-red-400 focus:ring-red-100;
}

.eye-btn {
  @apply absolute right-3 text-gray-400 hover:text-gray-600 transition-colors;
}

.submit-btn {
  @apply w-full bg-gradient-to-r from-pink-400 to-rose-400 text-white font-semibold
  py-3.5 rounded-xl hover:from-pink-500 hover:to-rose-500 active:scale-[0.99]
  transition-all duration-200 shadow-sm hover:shadow-md mt-2;
}

/* Language switcher */
.lang-item {
  @apply w-full flex items-center gap-2.5 rounded-xl px-3 py-2 mb-0.5
  transition-all duration-150 hover:bg-gray-100 cursor-pointer;
}

.lang-item-active {
  @apply bg-pink-50;
}

.lang-flag {
  @apply text-base leading-none;
}

.lang-check {
  @apply text-pink-500 text-xs;
}
</style>
