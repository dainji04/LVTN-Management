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

const typePassword = ref('password');

const userInfo = reactive({
    email: '',
    password: '',
    remember: false,
});

const validateLoginForm = async(email: string, password: string) => {
    // const isValid = await validate(rules, userInfo); // validate form using vee-validate library
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    // password is required a special character, a number, a uppercase letter, a lowercase letter
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    

    if (!email && !password) {
        emailError.value = 'Email không được để trống';
        passwordError.value = 'Mật khẩu không được để trống';
        return false;
    }

    if (!emailRegex.test(email)) {
        emailError.value = 'Email không hợp lệ';
        return false;
    }

    if (!passwordRegex.test(password)) {
        passwordError.value = 'Mật khẩu phải có ít nhất 8 ký tự, 1 chữ cái viết hoa, 1 chữ cái viết thường, 1 số và 1 ký tự đặc biệt';
        return false;
    }

    return true;
}

const submitForm = async() => {
    emailError.value = '';
    passwordError.value = '';
    try {
        // validate form
        const isValid = await validateLoginForm(userInfo.email, userInfo.password);
        if (!isValid) return;

        isLoading.value = true;
        const response = await helperApi('login', {
            method: 'POST',
            body: userInfo,
        });

        if (response.status) {
            // Save token and user data
            if (response.data?.token && response.data?.user) {
                authHelper.setAuth(response.data.token, response.data.user);
            }
            notificationHelper('success', 'Đăng nhập thành công');
            router.push('/');
        } else {
            notificationHelper('error', response.message);
        }
    } catch (error) {
        console.error(error);
        notificationHelper('error', 'Đăng nhập thất bại');
    } finally {
        setTimeout(() => {
            isLoading.value = false;
        },3000);
    }
}

const togglePasswordVisibility = () => {
    if (typePassword.value === 'password') {
        typePassword.value = 'text';
    } else {
        typePassword.value = 'password';
    }
}

</script>

<template>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <h2>Sign In</h2>
                <p>Enter your credentials to access your account</p>
            </div>
            
            <form class="login-form" id="loginForm" @submit.prevent="submitForm">
                <div class="form-group">
                    <div class="input-wrapper">
                        <input v-model="userInfo.email" type="text" id="email" name="email" required autocomplete="email">
                        <label for="email">Email Address</label>
                    </div>
                    <span class="error-message" id="emailError" v-if="emailError">{{ emailError }}</span>
                </div>

                <div class="form-group">
                    <div class="input-wrapper password-wrapper">
                        <input v-model="userInfo.password" type="password" id="password" name="password" required autocomplete="current-password">
                        <label for="password">Password</label>
                        <button @click="togglePasswordVisibility" type="button" class="password-toggle" id="passwordToggle" aria-label="Toggle password visibility">
                            <span class="eye-icon"></span>
                        </button>
                    </div>
                    <span class="error-message" id="passwordError" v-if="passwordError">{{ passwordError }}</span>
                </div>

                <div class="form-options">
                    <label class="remember-wrapper">
                        <input v-model="userInfo.remember" type="checkbox" id="remember" name="remember">
                        <span class="checkbox-label">
                            <span class="checkmark"></span>
                            Remember me
                        </span>
                    </label>
                    <router-link to="/forgot-password" class="forgot-password">Forgot password?</router-link>
                </div>

                <Button block size="large" type="primary" :loading="isLoading" @click="submitForm">Sign In</Button>
            </form>

            <div class="signup-link">
                <p>
                    Don't have an account?
                    <router-link to="/register">Sign Up</router-link>
                </p>
            </div>

            <div class="success-message" id="successMessage">
                <div class="success-icon">✓</div>
                <h3>Login Successful!</h3>
                <p>Redirecting to your dashboard...</p>
            </div>
        </div>
    </div>
</template>

<style scoped>
.login-container {
    width: 100%;
    min-width: 400px;
    max-width: 500px;
}

.login-card {
    background: white;
    border-radius: 12px;
    padding: 32px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.login-header {
    text-align: center;
    margin-bottom: 32px;
}

.login-header h2 {
    font-size: 1.875rem;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 8px;
}

.login-header p {
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
.input-wrapper input.has-value + label {
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

/* Form Options */
.form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    flex-wrap: wrap;
    gap: 12px;
}

.remember-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
}

.remember-wrapper input[type="checkbox"] {
    display: none;
}

.checkbox-label {
    color: #64748b;
    font-size: 0.875rem;
    cursor: pointer;
    user-select: none;
    display: flex;
    align-items: center;
    gap: 8px;
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
}

.remember-wrapper input[type="checkbox"]:checked ~ .checkbox-label .checkmark {
    background: #6366f1;
    border-color: #6366f1;
}

.remember-wrapper input[type="checkbox"]:checked ~ .checkbox-label .checkmark::after {
    content: '✓';
    color: white;
    font-size: 10px;
    font-weight: bold;
}

.forgot-password {
    color: #6366f1;
    text-decoration: none;
    font-size: 0.875rem;
    font-weight: 500;
    transition: color 0.2s ease;
}

.forgot-password:hover {
    color: #4f46e5;
}

/* Button */
.login-btn {
    width: 100%;
    background: #6366f1;
    border: none;
    border-radius: 8px;
    padding: 12px 24px;
    color: white;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
    margin-bottom: 24px;
}

.login-btn:hover {
    background: #4f46e5;
}

.login-btn:active {
    transform: translateY(1px);
}

/* Loading State */
.login-btn.loading {
    pointer-events: none;
    background: #a5a6f6;
}

.btn-text {
    transition: opacity 0.2s ease;
}

.btn-loader {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 16px;
    height: 16px;
    border: 2px solid transparent;
    border-top: 2px solid white;
    border-radius: 50%;
    opacity: 0;
    animation: spin 1s linear infinite;
    transition: opacity 0.2s ease;
}

.login-btn.loading .btn-text {
    opacity: 0;
}

.login-btn.loading .btn-loader {
    opacity: 1;
}

/* Signup Link */
.signup-link {
    text-align: center;
    margin-top: 20px;
}

.signup-link p {
    color: #64748b;
    font-size: 0.875rem;
}

.signup-link a {
    color: #6366f1;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s ease;
}

.signup-link a:hover {
    color: #4f46e5;
}

/* Success Message */
.success-message {
    display: none;
    text-align: center;
    padding: 32px 20px;
    opacity: 0;
    transform: translateY(20px);
    transition: all 0.3s ease;
}

.success-message.show {
    display: block;
    opacity: 1;
    transform: translateY(0);
}

.success-icon {
    width: 48px;
    height: 48px;
    background: #22c55e;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    color: white;
    margin: 0 auto 16px;
    animation: successPulse 0.5s ease;
}

.success-message h3 {
    color: #1e293b;
    font-size: 1.25rem;
    margin-bottom: 8px;
}

.success-message p {
    color: #64748b;
    font-size: 0.875rem;
}

/* Animations */
@keyframes spin {
    0% { transform: translate(-50%, -50%) rotate(0deg); }
    100% { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes successPulse {
    0% { transform: scale(0); }
    50% { transform: scale(1.1); }
    100% { transform: scale(1); }
}

/* Mobile Responsive */
@media (max-width: 480px) {
    .login-card {
        padding: 24px;
        margin: 10px;
    }
    
    .login-header h2 {
        font-size: 1.5rem;
    }
    
    .form-options {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
    }
}
</style>