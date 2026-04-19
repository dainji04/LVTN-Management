import { createRouter, createWebHistory } from "vue-router";
import Home from "./views/Home.vue";
// import { authHelper } from './helpers/authHelper';
import { useAuthStore } from "./store/authStore";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("./views/Login.vue"),
    meta: { requiresGuest: true }
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("./views/Register.vue"),
    meta: { requiresGuest: true }
  },
  {
    path: "/terms",
    name: "Terms",
    component: () => import("./views/Terms.vue"),
    meta: { requiresGuest: true }
  },
  {
    path: "/privacy",
    name: "Privacy",
    component: () => import("./views/Privacy.vue"),
    meta: { requiresGuest: true }
  },
  {
    path: "/users",
    name: "Users",
    component: () => import("./views/Users.vue"),
  },
  {
    path: "/users/:id",
    name: "UserDetail",
    component: () => import("./views/UserDetail.vue"),
  },
  {
    path: "/users/:id/edit",
    name: "UserEdit",
    component: () => import("./views/UserEdit.vue"),
  },
  {
    path: "/group/:idGroup",
    name: "Group",
    component: () => import("./views/DetailGroup.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/profile",
    name: "Profile",
    component: () => import("./views/Profile.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/groups",
    name: "Groups",
    component: () => import("./views/Groups.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/notifications",
    name: "Notifications",
    component: () => import("./views/Notifications.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/settings",
    name: "Settings",
    component: () => import("./views/Settings.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/messages",
    name: "Messages",
    component: () => import("./views/Messages.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/create",
    name: "Create",
    component: () => import("./views/Create.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/search",
    name: "Search",
    component: () => import("./views/Search.vue"),
    meta: { requiresAuth: true }
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    redirect: "/",
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navigation guard: redirect based on authentication status
router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = authStore.isAuthenticated;
  const requiresAuth = to.meta?.requiresAuth ?? false;
  const requiresGuest = to.meta?.requiresGuest ?? false;

  // If route requires authentication and user is not logged in
  if (requiresAuth && !isAuthenticated) {
    next({ name: "Login" });
    return;
  }

  // If route requires guest (like login) and user is already logged in
  if (requiresGuest && isAuthenticated) {
    next({ name: "Home" });
    return;
  }
  // Otherwise, proceed normally
  next();
});
