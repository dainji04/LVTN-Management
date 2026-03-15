<script setup lang="ts">
import { useAuthStore } from "../store/authStore";

const authStore = useAuthStore();

const logout = () => {
  authStore.logout();
};
</script>

<template>
  <nav>
    <ul>
      <li>
        <a href="/">Home</a>
      </li>
      <li>
        <a href="/about">About</a>
      </li>
      <li>
        <a href="/contact">Contact</a>
      </li>
      <li>
        <a href="/login">Login</a>
      </li>
      <li>
        <a href="/profile">Profile</a>
      </li>
      <li>
        <a href="/settings">Settings</a>
      </li>
      <li>
        <a href="#" @click="logout">Logout</a>
      </li>
    </ul>
  </nav>
</template>

<style scoped>
nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f0f0;
  padding: 10px;
}
ul {
  display: flex;
  list-style: none;
  align-items: center;
}
li {
  margin-right: 20px;
}
a {
  text-decoration: none;
  color: #333;
}
a:hover {
  color: #000;
}
a:active {
  color: #000;
}
a:visited {
  color: #000;
}
a:focus {
  color: #000;
}
</style>
