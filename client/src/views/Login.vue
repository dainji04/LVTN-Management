<template>
    <div class="login-container w-full px-[100px] py-[20px]">
        <div class="login-card flex flex-col justify-center h-[90%] grid grid-cols-[3fr_2fr]">
            <div class="left h-full bg-[#FFF0F4] flex justify-space-between items-center gap-4 px-8">
                <h1 class="text-4xl font-bold leading-normal">
                    {{ $t('welcomeToOurSocialApp') }}
                </h1>
                <img class="h-[90%] object-cover" src="../assets/thumbnail-login.png" />
            </div>
            <div class="right flex flex-col justify-center px-20">

                <h1 class="text-2xl font-bold text-center mb-4">
                    {{ $t('welcomeBack') }}
                </h1>

                <form @submit.prevent="submitForm">
                    <div class="form-group grid gap-1" :class="{ 'has-value': userInfo.email }">
                        <input class="input round border-black border p-2 " type="email" id="email" v-model="userInfo.email" @input="emailTouched = true" />
                        <label class="label" for="email">{{ $t('email') }}</label>
                    </div>
                    <span class="error-message" v-if="emailTouched && emailErrorMessage">{{ emailErrorMessage }}</span>

                    <div class="form-group grid gap-1 mt-4" :class="{ 'has-value': userInfo.password }">
                        <input class="input round border-black border p-2  " type="password" id="password" v-model="userInfo.password" @input="passwordTouched = true" />
                        <label class="label" for="password">{{ $t('password') }}</label>
                    </div>
                    <span class="error-message" v-if="passwordTouched && passwordErrorMessage">{{ passwordErrorMessage }}</span>

                    <Button :classes="'w-full round py-2 px-4 text-white mt-6'" :type="'primary'" :is-disabled="isLoading">
                        <span v-if="!isLoading" class="text-white">{{ $t('login') }}</span>
                        <span v-else class="text-white">Loading...</span>
                    </Button>
                </form>
                
                <!-- Forgot password -->
                <div class="flex justify-center items-center mt-4">
                    <router-link to="/forgot-password" class="text-sm font-bold block">{{ $t('forgotPassword') }}</router-link>
                </div>

                <!-- register -->
                <Button :classes="'w-full round py-2 px-4 mt-12'" :border="true" :type="'secondary'">
                    <span>{{ $t('createNewAccount') }}</span>
                </Button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
// import { helperApi } from '../helpers/apiHelper';
import { notificationHelper } from '../helpers/notificationHelper';
// import { authHelper } from '../helpers/authHelper';
import Button from '../components/button.vue';

import { useI18n } from 'vue-i18n';
const { t } = useI18n();

const isLoading = ref(false);
const router = useRouter();
const emailError = ref('');
const passwordError = ref('');

const typePassword = ref('password');

const emailTouched = ref(false);
const passwordTouched = ref(false);

const userInfo = reactive({
    email: '',
    password: '',
    remember: false,
});

const emailErrorMessage = computed<string>(() => {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!userInfo.email) {
        return t('emailRequired');
    }

    if (!emailRegex.exec(userInfo.email)) {
        return t('emailInvalid');
    }

    return '';
});

const passwordErrorMessage = computed<string>(() => {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    if (!userInfo.password) {
        return t('passwordRequired');
    }

    if (!passwordRegex.exec(userInfo.password)) {
        return t('passwordMinLength') + t('passwordSpecialChar') + t('passwordNumber') + t('passwordUppercase');
    }

    return '';
});

const submitForm = async() => {
    console.log('call')
    // Mark fields as touched when form is submitted
    emailTouched.value = true;
    passwordTouched.value = true;
    
    emailError.value = '';
    passwordError.value = '';
    try {
        // validate form
        if (emailErrorMessage.value || passwordErrorMessage.value) {
            notificationHelper('error', t('loginFailed') + ' ' + t('pleaseCheckYourLoginInfo'));
            return;
        }

        // show message validate form
        isLoading.value = true;
        notificationHelper('success', 'Đăng nhập thành công');
        
        setTimeout(()=>{
            router.push('/');
            isLoading.value = false;
        }, 3000)
        // const response = await helperApi('login', {
        //     method: 'POST',
        //     body: userInfo,
        // });

        // if (response.status) {
        //     // Save token and user data
        //     if (response.data?.token && response.data?.user) {
        //         authHelper.setAuth(response.data.token, response.data.user);
        //     }
        //     notificationHelper('success', 'Đăng nhập thành công');
        //     router.push('/');
        // } else {
        //     notificationHelper('error', response.message);
        // }
    } catch (error) {
        console.error(error);
        notificationHelper('error', 'Đăng nhập thất bại');
    } finally {
        // setTimeout(() => {
        //     isLoading.value = false;
        // },3000);
    }
}

// const togglePasswordVisibility = () => {
//     if (typePassword.value === 'password') {
//         typePassword.value = 'text';
//     } else {
//         typePassword.value = 'password';
//     }
// }

</script>

<style scoped>
.round {
    @apply rounded-2xl;
}

.error-message {
    @apply text-red-500 text-sm mt-1 pl-2;
}

.login-card {
    background: white;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.form-group {
    position: relative;
}

.label {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    pointer-events: none;
    transition: all 0.3s ease;
    z-index: 2;
    background: white;
    padding: 0 4px;
}

.input {
    position: relative;
    z-index: 1;
    padding: 12px 16px;
}

.input:focus ~ .label,
.form-group.has-value .label {
    top: 0;
    transform: translateY(-12px);
    font-size: 0.875rem;
}
</style>