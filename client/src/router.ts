import { createRouter, createWebHistory } from 'vue-router';
import Home from './views/Home.vue';
import { authHelper } from './helpers/authHelper';

const routes = [
    { 
        path: '/', 
        name: 'Home', 
        component: Home,
        meta: { requiresAuth: true }
    },
    { 
        path: '/login', 
        name: 'Login', 
        component: () => import('./views/Login.vue'),
        meta: { requiresGuest: true }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        redirect: '/',
    },
];

export const router = createRouter({
    history: createWebHistory(),
    routes,
});

// Navigation guard: redirect based on authentication status
router.beforeEach((to, _from, next) => {
    const isAuthenticated = authHelper.isAuthenticatedRef.value;
    const requiresAuth = to.meta?.requiresAuth ?? false;
    const requiresGuest = to.meta?.requiresGuest ?? false;
    
    // If route requires authentication and user is not logged in
    if (requiresAuth && !isAuthenticated) {
        next({ name: 'Login' });
        return;
    }

    // If route requires guest (like login) and user is already logged in
    if (requiresGuest && isAuthenticated) {
        next({ name: 'Home' });
        return;
    }

    // Otherwise, proceed normally
    next();
});