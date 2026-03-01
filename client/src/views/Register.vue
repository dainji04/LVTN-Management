<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { helperApi } from '../helpers/apiHelper';
import { Button } from 'ant-design-vue';
import { notificationHelper } from '../helpers/notificationHelper';
import { authHelper } from '../helpers/authHelper';

const isLoading = ref(false);
const router = useRouter();
const emailError = ref('');
const passwordError = ref('');
const confirmPasswordError = ref('');
const usernameError = ref('');
const fullNameError = ref('');

const typePassword = ref('password');
const typeConfirmPassword = ref('password');

const userInfo = reactive({
    fullName: '',
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    dateOfBirth: '',
    agreeToTerms: false,
});

const validateRegisterForm = async (
    fullName: string,
    username: string,
    email: string,
    password: string,
    confirmPassword: string,
    agreeToTerms: boolean
) => {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/;

    // Reset errors
    fullNameError.value = '';
    usernameError.value = '';
    emailError.value = '';
    passwordError.value = '';
    confirmPasswordError.value = '';

    let isValid = true;

    // Validate full name
    if (!fullName.trim()) {
        fullNameError.value = 'Họ và tên không được để trống';
        isValid = false;
    } else if (fullName.trim().length < 2) {
        fullNameError.value = 'Họ và tên phải có ít nhất 2 ký tự';
        isValid = false;
    }

    // Validate username
    if (!username.trim()) {
        usernameError.value = 'Tên người dùng không được để trống';
        isValid = false;
    } else if (!usernameRegex.test(username)) {
        usernameError.value = 'Tên người dùng phải có 3-20 ký tự, chỉ chứa chữ cái, số và dấu gạch dưới';
        isValid = false;
    }

    // Validate email
    if (!email.trim()) {
        emailError.value = 'Email không được để trống';
        isValid = false;
    } else if (!emailRegex.test(email)) {
        emailError.value = 'Email không hợp lệ';
        isValid = false;
    }

    // Validate password
    if (!password) {
        passwordError.value = 'Mật khẩu không được để trống';
        isValid = false;
    } else if (!passwordRegex.test(password)) {
        passwordError.value = 'Mật khẩu phải có ít nhất 8 ký tự, 1 chữ cái viết hoa, 1 chữ cái viết thường, 1 số và 1 ký tự đặc biệt';
        isValid = false;
    }

    // Validate confirm password
    if (!confirmPassword) {
        confirmPasswordError.value = 'Vui lòng xác nhận mật khẩu';
        isValid = false;
    } else if (password !== confirmPassword) {
        confirmPasswordError.value = 'Mật khẩu xác nhận không khớp';
        isValid = false;
    }

    // Validate terms agreement
    if (!agreeToTerms) {
        notificationHelper('warning', 'Vui lòng đồng ý với điều khoản sử dụng');
        isValid = false;
    }

    return isValid;
};

const submitForm = async () => {
    try {
        const isValid = await validateRegisterForm(
            userInfo.fullName,
            userInfo.username,
            userInfo.email,
            userInfo.password,
            userInfo.confirmPassword,
            userInfo.agreeToTerms
        );
        if (!isValid) return;

        isLoading.value = true;
        const response = await helperApi('register', {
            method: 'POST',
            body: {
                fullName: userInfo.fullName.trim(),
                username: userInfo.username.trim(),
                email: userInfo.email.trim(),
                password: userInfo.password,
                dateOfBirth: userInfo.dateOfBirth || null,
            },
        });

        if (response.status) {
            // If registration returns token and user, auto-login
            if (response.data?.token && response.data?.user) {
                authHelper.setAuth(response.data.token, response.data.user);
            }
            notificationHelper('success', 'Đăng ký thành công!');
            router.push('/');
        } else {
            notificationHelper('error', response.message || 'Đăng ký thất bại');
        }
    } catch (error: any) {
        console.error(error);
        // Handle specific error messages
        if (error.message) {
            notificationHelper('error', error.message);
        } else {
            notificationHelper('error', 'Đăng ký thất bại. Vui lòng thử lại.');
        }
    } finally {
        setTimeout(() => {
            isLoading.value = false;
        }, 3000);
    }
};

const togglePasswordVisibility = (field: 'password' | 'confirmPassword') => {
    if (field === 'password') {
        typePassword.value = typePassword.value === 'password' ? 'text' : 'password';
    } else {
        typeConfirmPassword.value = typeConfirmPassword.value === 'password' ? 'text' : 'password';
    }
};
</script>

<template>
    <div class="register-container">
        <div class="register-card">
            <div class="register-header">
                <h2>Create Account</h2>
                <p>Join our community and start connecting</p>
            </div>

            <form class="register-form" @submit.prevent="submitForm">
                <div class="form-group">
                    <div class="input-wrapper">
                        <input
                            v-model="userInfo.fullName"
                            type="text"
                            id="fullName"
                            name="fullName"
                            required
                            autocomplete="name"
                        />
                        <label for="fullName">Full Name</label>
                    </div>
                    <span class="error-message" v-if="fullNameError">{{ fullNameError }}</span>
                </div>

                <div class="form-group">
                    <div class="input-wrapper">
                        <input
                            v-model="userInfo.username"
                            type="text"
                            id="username"
                            name="username"
                            required
                            autocomplete="username"
                        />
                        <label for="username">Username</label>
                    </div>
                    <span class="error-message" v-if="usernameError">{{ usernameError }}</span>
                </div>

                <div class="form-group">
                    <div class="input-wrapper">
                        <input
                            v-model="userInfo.email"
                            type="email"
                            id="email"
                            name="email"
                            required
                            autocomplete="email"
                        />
                        <label for="email">Email Address</label>
                    </div>
                    <span class="error-message" v-if="emailError">{{ emailError }}</span>
                </div>

                <div class="form-group">
                    <div class="input-wrapper">
                        <input
                            v-model="userInfo.dateOfBirth"
                            type="date"
                            id="dateOfBirth"
                            name="dateOfBirth"
                            autocomplete="bday"
                        />
                        <label for="dateOfBirth">Date of Birth (Optional)</label>
                    </div>
                </div>

                <div class="form-group">
                    <div class="input-wrapper password-wrapper">
                        <input
                            v-model="userInfo.password"
                            :type="typePassword"
                            id="password"
                            name="password"
                            required
                            autocomplete="new-password"
                        />
                        <label for="password">Password</label>
                        <button
                            @click="togglePasswordVisibility('password')"
                            type="button"
                            class="password-toggle"
                            aria-label="Toggle password visibility"
                        >
                            <span class="eye-icon" :class="{ 'show-password': typePassword === 'text' }"></span>
                        </button>
                    </div>
                    <span class="error-message" v-if="passwordError">{{ passwordError }}</span>
                </div>

                <div class="form-group">
                    <div class="input-wrapper password-wrapper">
                        <input
                            v-model="userInfo.confirmPassword"
                            :type="typeConfirmPassword"
                            id="confirmPassword"
                            name="confirmPassword"
                            required
                            autocomplete="new-password"
                        />
                        <label for="confirmPassword">Confirm Password</label>
                        <button
                            @click="togglePasswordVisibility('confirmPassword')"
                            type="button"
                            class="password-toggle"
                            aria-label="Toggle confirm password visibility"
                        >
                            <span class="eye-icon" :class="{ 'show-password': typeConfirmPassword === 'text' }"></span>
                        </button>
                    </div>
                    <span class="error-message" v-if="confirmPasswordError">{{ confirmPasswordError }}</span>
                </div>

                <div class="form-group">
                    <label class="terms-wrapper">
                        <input v-model="userInfo.agreeToTerms" type="checkbox" id="agreeToTerms" name="agreeToTerms" required />
                        <span class="checkbox-label">
                            <span class="checkmark"></span>
                            I agree to the <a href="/terms" target="_blank">Terms of Service</a> and <a href="/privacy" target="_blank">Privacy Policy</a>
                        </span>
                    </label>
                </div>

                <Button block size="large" type="primary" :loading="isLoading" @click="submitForm">
                    Create Account
                </Button>
            </form>

            <div class="login-link">
                <p>
                    Already have an account?
                    <router-link to="/login">Sign In</router-link>
                </p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.register-container {
    width: 100%;
    min-width: 400px;
    max-width: 500px;
}

.register-card {
    background: white;
    border-radius: 12px;
    padding: 32px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.register-header {
    text-align: center;
    margin-bottom: 32px;
}

.register-header h2 {
    font-size: 1.875rem;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 8px;
}

.register-header p {
    color: #64748b;
    font-size: 0.875rem;
}

/* Form Styles */
.form-group {
    margin-bottom: 20px;
}

.input-wrapper {
    position: relative;
    display: flex;
    flex-direction: column;
}

.input-wrapper input {
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 8px;
    padding: 12px 16px 8px 16px;
    color: #1e293b;
    font-size: 16px;
    transition: all 0.2s ease;
    width: 100%;
    outline: none;
}

.input-wrapper input[type="date"] {
    padding-top: 12px;
    padding-bottom: 12px;
}

.input-wrapper input::placeholder {
    color: transparent;
}

.input-wrapper label {
    position: absolute;
    left: 16px;
    top: 12px;
    color: #64748b;
    font-size: 16px;
    transition: all 0.2s ease;
    pointer-events: none;
    transform-origin: left top;
    background-color: #fff;
}

.input-wrapper input:focus,
.input-wrapper input:valid,
.input-wrapper input.has-value {
    border-color: #6366f1;
}

.input-wrapper input:focus + label,
.input-wrapper input:valid + label,
.input-wrapper input.has-value + label,
.input-wrapper input[type="date"] + label {
    transform: translateY(-25px) scale(0.875);
    color: #6366f1;
    font-weight: 500;
}

/* Password Toggle */
.password-wrapper {
    position: relative;
}

.password-wrapper input {
    padding-right: 48px;
}

.password-toggle {
    position: absolute;
    right: 12px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    cursor: pointer;
    padding: 8px;
    color: #64748b;
    transition: color 0.2s ease;
}

.password-toggle:hover {
    color: #1e293b;
}

.eye-icon {
    display: block;
    width: 20px;
    height: 20px;
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%2364748b' stroke-width='1.5'%3e%3cpath stroke-linecap='round' stroke-linejoin='round' d='M15 12a3 3 0 11-6 0 3 3 0 016 0z'/%3e%3cpath stroke-linecap='round' stroke-linejoin='round' d='M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z'/%3e%3c/svg%3e");
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;
    transition: background-image 0.2s ease;
}

.eye-icon.show-password {
    background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%2364748b' stroke-width='1.5'%3e%3cpath stroke-linecap='round' stroke-linejoin='round' d='M3.98 8.223A10.477 10.477 0 001.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.45 10.45 0 0112 4.5c4.756 0 8.773 3.162 10.065 7.498a10.523 10.523 0 01-4.293 5.774M6.228 6.228L3 3m3.228 3.228l3.65 3.65m7.894 7.894L21 21m-3.228-3.228l-3.65-3.65m0 0a3 3 0 11-4.243-4.243m4.242 4.242L9.88 9.88'/%3e%3c/svg%3e");
}

/* Error Messages */
.error-message {
    display: block;
    color: #ef4444;
    font-size: 0.75rem;
    font-weight: 500;
    margin-top: 4px;
    margin-left: 4px;
    transform: translateY(-4px);
    transition: all 0.2s ease;
    white-space: pre-line;
    text-align: left;
}

.error-message.show {
    opacity: 1;
    transform: translateY(0);
}

.form-group.error .input-wrapper input {
    border-color: #ef4444;
}

/* Terms Checkbox */
.terms-wrapper {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    cursor: pointer;
    margin-bottom: 0;
}

.terms-wrapper input[type="checkbox"] {
    display: none;
}

.terms-wrapper .checkbox-label {
    color: #64748b;
    font-size: 0.875rem;
    cursor: pointer;
    user-select: none;
    display: flex;
    align-items: flex-start;
    gap: 8px;
    line-height: 1.5;
}

.terms-wrapper .checkbox-label a {
    color: #6366f1;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s ease;
}

.terms-wrapper .checkbox-label a:hover {
    color: #4f46e5;
    text-decoration: underline;
}

.checkmark {
    width: 16px;
    height: 16px;
    border: 2px solid #d1d5db;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    flex-shrink: 0;
    background: white;
    margin-top: 2px;
}

.terms-wrapper input[type="checkbox"]:checked ~ .checkbox-label .checkmark {
    background: #6366f1;
    border-color: #6366f1;
}

.terms-wrapper input[type="checkbox"]:checked ~ .checkbox-label .checkmark::after {
    content: '✓';
    color: white;
    font-size: 10px;
    font-weight: bold;
}

/* Login Link */
.login-link {
    text-align: center;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #e2e8f0;
}

.login-link p {
    color: #64748b;
    font-size: 0.875rem;
}

.login-link a {
    color: #6366f1;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s ease;
}

.login-link a:hover {
    color: #4f46e5;
    text-decoration: underline;
}

/* Mobile Responsive */
@media (max-width: 480px) {
    .register-card {
        padding: 24px;
        margin: 10px;
    }

    .register-header h2 {
        font-size: 1.5rem;
    }

    .terms-wrapper .checkbox-label {
        font-size: 0.8125rem;
    }
}
</style>
