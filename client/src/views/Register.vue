<template>
    <div class="register-container w-full min-h-screen bg-white flex items-center justify-center py-8 px-4 sm:py-12">
        <div class="register-card w-full max-w-2xl bg-white rounded-lg p-6 sm:p-8">
            <!-- Back Button -->
            <button @click="goBack" class="mb-6 flex items-center text-gray-600 hover:text-black transition-colors">
                <LeftOutlined class="mr-2" />
            </button>

            <!-- Title and Subtitle -->
            <h1 class="text-2xl sm:text-3xl font-bold text-black mb-2">
                {{ $t('getStartedOnAloChat') }}
            </h1>
            <p class="text-sm sm:text-base text-gray-600 mb-6 sm:mb-8">
                {{ $t('createAccountDescription') }}
            </p>

            <form @submit.prevent="submitForm" class="space-y-4 sm:space-y-6">
                <!-- Name Fields (Họ/Tên) -->
                <div class="form-group">
                    <label class="block text-sm font-medium text-black mb-2">
                        {{ $t('name') }}<span class="text-primary">*</span>
                    </label>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div class="form-group-inner">
                            <input
                                class="input-field"
                                type="text"
                                :placeholder="$t('lastName')"
                                v-model="formData.lastName"
                                @input="lastNameTouched = true"
                            />
                            <p class="error-message" v-if="lastNameError">{{ lastNameError }}</p>
                        </div>
                        <div class="form-group-inner">
                            <input
                                class="input-field"
                                type="text"
                                :placeholder="$t('firstName')"
                                v-model="formData.firstName"
                                @input="firstNameTouched = true"
                            />
                            <p class="error-message" v-if="firstNameError">{{ firstNameError }}</p>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label class="block text-sm font-medium text-black mb-2">
                        {{ $t('username') }}<span class="text-primary">*</span>
                    </label>
                    <div class="form-group-inner">
                        <input
                            class="input-field"
                            type="text"
                            :placeholder="$t('username')"
                            v-model="formData.username"
                            @input="usernameTouched = true"
                        />
                        <p class="error-message" v-if="usernameError">{{ usernameError }}</p>
                    </div>
                </div>

                <!-- Date of Birth -->
                <div class="form-group">
                    <label class="block text-sm font-medium text-black mb-2">
                        {{ $t('dateOfBirth') }}<span class="text-primary">*</span>
                    </label>
                    <div class="grid grid-cols-3 gap-2 sm:gap-4">
                        <div>
                            <div class="form-group-inner relative">
                                <select
                                    class="input-field appearance-none pr-8"
                                    v-model="formData.day"
                                    @change="dayTouched = true"
                                >
                                    <option value="">{{ $t('day') }}</option>
                                    <option v-for="d in 31" :key="d" :value="d">{{ d }}</option>
                                </select>
                                <DownOutlined class="select-icon" />
                            </div>
                            <p class="error-message" v-if="dateOfBirthError">{{ dateOfBirthError }}</p>
                        </div>
                        <div>
                            <div class="form-group-inner relative">
                                <select
                                    class="input-field appearance-none pr-8"
                                    v-model="formData.month"
                                    @change="monthTouched = true"
                                >
                                    <option value="">{{ $t('month') }}</option>
                                    <option v-for="m in 12" :key="m" :value="m">{{ m }}</option>
                                </select>
                                <DownOutlined class="select-icon" />
                            </div>
                            <p class="error-message" v-if="dateOfBirthError">{{ dateOfBirthError }}</p>
                        </div>
                        <div>
                            <div class="form-group-inner relative">
                                <select
                                    class="input-field appearance-none pr-8"
                                    v-model="formData.year"
                                    @change="yearTouched = true"
                                >
                                    <option value="">{{ $t('year') }}</option>
                                    <option v-for="y in years" :key="y" :value="y">{{ y }}</option>
                                </select>
                                <DownOutlined class="select-icon" />
                            </div>
                            <p class="error-message" v-if="dateOfBirthError">{{ dateOfBirthError }}</p>
                        </div>
                    </div>
                </div>

                <!-- Gender -->
                <div class="form-group">
                    <label class="block text-sm font-medium text-black mb-2">
                        {{ $t('gender') }}<span class="text-primary">*</span>
                    </label>
                    <div class="form-group-inner relative">
                        <select
                            class="input-field appearance-none pr-8"
                            v-model="formData.gender"
                            @change="genderTouched = true"
                        >
                            <option value="">{{ $t('selectGender') }}</option>
                            <option value="male">Nam</option>
                            <option value="female">Nữ</option>
                            <option value="other">Khác</option>
                        </select>
                        <DownOutlined class="select-icon" />
                    </div>
                    <p class="error-message" v-if="genderError">{{ genderError }}</p>
                </div>

                <!-- Mobile or Email -->
                <div class="form-group">
                    <label class="block text-sm font-medium text-black mb-2">
                        {{ $t('email') }}<span class="text-primary">*</span>
                    </label>
                    <div class="form-group-inner">
                        <input
                            class="input-field"
                            type="text"
                            :placeholder="$t('emailPlaceholder')"
                            v-model="formData.email"
                            @input="emailTouched = true"
                        />
                    </div>
                    <p class="text-xs text-gray-600 mt-2">
                        {{ $t('notificationInfo') }}
                    </p>
                    <p class="error-message" v-if="emailError">{{ emailError }}</p>
                </div>

                <div class="form-group">
                    <label class="block text-sm font-medium text-black mb-2">
                        {{ $t('phoneNumber') }}
                    </label>
                    <div class="form-group-inner">
                        <input
                            class="input-field"
                            type="text"
                            :placeholder="$t('phoneNumberPlaceholder')"
                            v-model="formData.phoneNumber"
                            @input="phoneNumberTouched = true"
                        />
                    </div>
                    <p class="text-xs text-gray-600 mt-2">
                        {{ $t('notificationInfo') }}
                    </p>
                    <p class="error-message" v-if="phoneNumberError">{{ phoneNumberError }}</p>
                </div>

                <!-- Password -->
                <div class="form-group">
                    <label class="block text-sm font-medium text-black mb-2">
                        {{ $t('password') }}<span class="text-primary">*</span>
                    </label>
                    <div class="form-group-inner">
                        <input
                            class="input-field"
                            type="password"
                            :placeholder="$t('passwordPlaceholder')"
                            v-model="formData.password"
                            @input="passwordTouched = true"
                        />
                    </div>
                    <p class="text-xs text-gray-600 mt-2">
                        {{ $t('contactInfoNotice') }}
                    </p>
                    <p class="error-message" v-if="passwordError">{{ passwordError }}</p>
                </div>

                <!-- Terms and Privacy Info -->
                <div class="text-xs text-gray-600 mb-4 sm:mb-6 space-y-2">
                    <p>{{ $t('termsAndPrivacy') }}</p>
                    <p>{{ $t('dataUsage') }}</p>
                </div>

                <!-- Submit Button -->
                <Button
                    :classes="'w-full round py-3 px-4 text-white mb-4'"
                    :type="'primary'"
                    :is-disabled="isLoading"
                    :html-type="'submit'"
                >
                    <span v-if="!isLoading">{{ $t('submit') }}</span>
                    <span v-else>Loading...</span>
                </Button>

                <!-- Already have account link -->
                <div class="text-center">
                    <router-link
                        to="/login"
                        class="text-sm text-gray-600 hover:text-primary transition-colors"
                    >
                        {{ $t('alreadyHaveAccount') }}
                    </router-link>
                </div>
            </form>
        </div>

        <!-- Copyright -->
        <div class="absolute bottom-4 left-4 text-xs text-gray-600 hidden sm:block">
            {{ $t('copyright') }}
        </div>
    </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { LeftOutlined, DownOutlined } from '@ant-design/icons-vue';
import { useI18n } from 'vue-i18n';
import type { RegisterForm } from '../types/registerUser';
import { useAuthStore } from '../store/authStore';
import { notificationHelper } from '../helpers/notificationHelper';

const { t } = useI18n();
const router = useRouter();

const isLoading = ref(false);

// Form data
const formData: RegisterForm = reactive({
    lastName: '',
    firstName: '',
    username: '',
    day: 1,
    month: 1,
    year: 2000,
    gender: '',
    email: '',
    phoneNumber: '',
    password: '',
});

// Touched states for validation
const lastNameTouched = ref(false);
const firstNameTouched = ref(false);
const usernameTouched = ref(false);
const dayTouched = ref(false);
const monthTouched = ref(false);
const yearTouched = ref(false);
const genderTouched = ref(false);
const emailTouched = ref(false);
const phoneNumberTouched = ref(false);
const passwordTouched = ref(false);

// Generate years for dropdown (1900 to current year)
const currentYear = new Date().getFullYear();
const years = Array.from({ length: currentYear - 1899 }, (_, i) => currentYear - i);

// Validation
const lastNameError = computed<string>(() => {
    if (!lastNameTouched.value) return '';
    if (!formData.lastName.trim()) return t('emailRequired'); // Reuse translation
    return '';
});

const firstNameError = computed<string>(() => {
    if (!firstNameTouched.value) return '';
    if (!formData.firstName.trim()) return t('emailRequired');
    return '';
});

const usernameError = computed<string>(() => {
    if (!usernameTouched.value) return '';
    if (!formData.username.trim()) return t('usernameRequired');
    if (formData.username.length < 6) return t('usernameMinLength');
    if (formData.username.length > 30) return t('usernameMaxLength');
    const usernameRegex = /^[a-zA-Z0-9_]+$/;
    if (!usernameRegex.test(formData.username)) {
        return t('usernameInvalid');
    }
    return '';
});

const dateOfBirthError = computed<string>(() => {
    if (!dayTouched.value || !monthTouched.value || !yearTouched.value) return '';
    if (!formData.day || !formData.month || !formData.year) return t('dateOfBirthRequired');
    return '';
});

const genderError = computed<string>(() => {
    if (!genderTouched.value) return '';
    if (!formData.gender) return t('genderRequired');
    return '';
});

const emailError = computed<string>(() => {
    if (!emailTouched.value) return '';
    if (!formData.email.trim()) return t('emailRequired');
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(formData.email)) {
        return t('emailInvalid');
    }
    return '';
});

const phoneNumberError = computed<string>(() => {
    if (!phoneNumberTouched.value) return '';
    if (!formData.phoneNumber.trim()) return t('phoneRequired');
    const phoneRegex = /^[0-9]{10,11}$/;
    if (!phoneRegex.test(formData.phoneNumber)) {
        return t('phoneInvalid');
    }
    return '';
});

const passwordError = computed<string>(() => {
    if (!passwordTouched.value) return '';
    if (!formData.password) return t('passwordRequired');
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    if (!passwordRegex.test(formData.password)) {
        return t('passwordMinLength') + ' ' + t('passwordSpecialChar') + ' ' + t('passwordNumber') + ' ' + t('passwordUppercase');
    }
    return '';
});

const goBack = () => {
    router.back();
};

const submitForm = async () => {
    // Mark all fields as touched
    lastNameTouched.value = true;
    firstNameTouched.value = true;
    dayTouched.value = true;
    monthTouched.value = true;
    yearTouched.value = true;
    genderTouched.value = true;
    emailTouched.value = true;
    phoneNumberTouched.value = true;
    passwordTouched.value = true;

    // Validate
    if (
        lastNameError.value ||
        firstNameError.value ||
        dateOfBirthError.value ||
        genderError.value ||
        emailError.value ||
        phoneNumberError.value ||
        passwordError.value
    ) {
        return;
    }

    isLoading.value = true;
    const authStore = useAuthStore();
    try {
        const response = await authStore.register(formData);
        if (response === true) {
            notificationHelper('success', t('registerSuccess'));
            setTimeout(() => {
                router.push('/login');
            }, 1000);
        } else if (typeof response === 'string') {
            notificationHelper('error', response);
        }
    } catch (error: any) {
        console.log(error);
    } finally {
        isLoading.value = false;
    }
};
</script>

<style scoped>
.register-container {
    position: relative;
}

.register-card {
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-group {
    margin-bottom: 1rem;
}

.input-field {
    width: 100%;
    padding: 12px 16px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    font-size: 14px;
    transition: border-color 0.2s;
    background: white;
}

.input-field:focus {
    outline: none;
    border-color: #FF436D;
}

.input-field::placeholder {
    color: #9ca3af;
}

.form-group-inner {
    position: relative;
}

.select-icon {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    pointer-events: none;
    color: #6b7280;
}

.error-message {
    @apply text-red-500 text-sm mt-1 pl-2;
}

.round {
    @apply rounded-2xl;
}

/* Responsive adjustments */
@media (max-width: 640px) {
    .register-card {
        padding: 1.5rem;
    }
    
    .input-field {
        font-size: 16px; /* Prevents zoom on iOS */
    }
}
</style>
