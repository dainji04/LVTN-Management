<template>
  <div class="login-container w-full px-[100px] py-[20px]">
    <div class="login-card h-screen grid grid-cols-[3fr_2fr]">
      <div
        class="left h-screen bg-[#FFF0F4] flex justify-space-between items-center gap-4 px-8"
      >
        <h1 class="text-4xl font-bold leading-normal">
          {{ $t("welcomeToOurSocialApp") }}
        </h1>
        <img class="h-[90%] object-cover" src="../assets/thumbnail-login.png" />
      </div>
      <div class="right h-screen flex flex-col justify-center px-20">
        <h1 class="text-2xl font-bold text-center mb-4">
          {{ $t("welcomeBack") }}
        </h1>

        <form @submit.prevent="submitForm">
          <div
            class="form-group grid gap-1"
            :class="{ 'has-value': userInfo.email }"
          >
            <input
              class="input round border-black border p-2"
              type="email"
              id="email"
              v-model="userInfo.email"
              @input="emailTouched = true"
            />
            <label class="label" for="email">{{ $t("email") }}</label>
          </div>
          <span
            class="error-message"
            v-if="emailTouched && emailErrorMessage"
            >{{ emailErrorMessage }}</span
          >

          <div
            class="form-group grid gap-1 mt-4"
            :class="{ 'has-value': userInfo.password }"
          >
            <input
              class="input round border-black border p-2"
              type="password"
              id="password"
              v-model="userInfo.password"
              @input="passwordTouched = true"
            />
            <label class="label" for="password">{{ $t("password") }}</label>
          </div>
          <span
            class="error-message"
            v-if="passwordTouched && passwordErrorMessage"
            >{{ passwordErrorMessage }}</span
          >

          <Button
            :classes="'w-full round py-2 px-4 text-white mt-6'"
            :type="'primary'"
            :is-disabled="isLoading"
            html-type="submit"
          >
            <span v-if="!isLoading" class="text-white">{{ $t("login") }}</span>
            <span v-else class="text-white">Loading...</span>
          </Button>
        </form>

        <!-- Forgot password -->
        <div class="flex justify-center items-center mt-4">
          <router-link to="/forgot-password" class="text-sm font-bold block">{{
            $t("forgotPassword")
          }}</router-link>
        </div>

        <!-- register -->
        <router-link to="/register">
          <Button
            :classes="'w-full round py-2 px-4 mt-12'"
            :border="true"
            :type="'secondary'"
          >
            <span>{{ $t("createNewAccount") }}</span>
          </Button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { notificationHelper } from "../helpers/notificationHelper";
import { useAuthStore } from "../store/authStore";

import { useI18n } from "vue-i18n";
const { t } = useI18n();

const isLoading = ref(false);
const router = useRouter();
const emailError = ref("");
const passwordError = ref("");

// const typePassword = ref('password');

const emailTouched = ref(false);
const passwordTouched = ref(false);

const userInfo = reactive({
  email: "",
  password: "",
  remember: false,
});

const emailErrorMessage = computed<string>(() => {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!userInfo.email) {
    return t("emailRequired");
  }

  if (!emailRegex.exec(userInfo.email)) {
    return t("emailInvalid");
  }

  return "";
});

const passwordErrorMessage = computed<string>(() => {
  // const passwordRegex =
  //   /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

  if (!userInfo.password) {
    return t("passwordRequired");
  }

  // if (!passwordRegex.exec(userInfo.password)) {
  //   return (
  //     t("passwordMinLength") +
  //     "\n" +
  //     t("passwordSpecialChar") +
  //     "\n" +
  //     t("passwordNumber") +
  //     "\n" +
  //     t("passwordUppercase")
  //   );
  // }

  return "";
});

const authStore = useAuthStore();
const submitForm = async () => {
  console.log("Form submitted");
  // Mark fields as touched when form is submitted
  emailTouched.value = true;
  passwordTouched.value = true;

  emailError.value = "";
  passwordError.value = "";
  try {
    // validate form
    if (emailErrorMessage.value || passwordErrorMessage.value) {
      notificationHelper(
        "error",
        t("loginFailed") + " " + t("pleaseCheckYourLoginInfo")
      );
      return;
    }

    isLoading.value = true;
    const isLoginSuccess = await authStore.login(
      userInfo.email,
      userInfo.password
    );
    if (!isLoginSuccess) {
      notificationHelper("error", t("loginFailed"));
    } else {
      notificationHelper("success", t("loginSuccess"));
      setTimeout(() => {
        router.push("/");
      }, 1000);
    }
  } catch (error) {
    notificationHelper("error", "Đăng nhập thất bại");
  } finally {
    isLoading.value = false;
  }
};
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

/* mobile */
@media only screen and (max-width: 768px) {
  .login-container {
    padding: 20px;
  }
  .login-card {
    grid-template-columns: 1fr;
  }
  .login-card .right {
    padding: 20px;
  }
  .login-card .left {
    display: none;
  }
}
</style>
