<template>
    <div class="terms-container">
        <div class="terms-card">
            <div class="terms-header">
                <h1>Terms of Service</h1>
                <p class="last-updated">Last updated: {{ new Date().toLocaleDateString('vi-VN') }}</p>
            </div>

            <div class="terms-content">
                <section>
                    <h2>1. Acceptance of Terms</h2>
                    <p>
                        By accessing and using this social media platform, you accept and agree to be bound by the terms and provision of this agreement.
                    </p>
                </section>

                <section>
                    <h2>2. User Accounts</h2>
                    <p>
                        You are responsible for maintaining the confidentiality of your account and password. You agree to accept responsibility for all activities that occur under your account.
                    </p>
                </section>

                <section>
                    <h2>3. User Conduct</h2>
                    <p>You agree not to:</p>
                    <ul>
                        <li>Post any content that is illegal, harmful, or violates others' rights</li>
                        <li>Harass, abuse, or harm other users</li>
                        <li>Impersonate any person or entity</li>
                        <li>Spam or send unsolicited messages</li>
                        <li>Violate any applicable laws or regulations</li>
                    </ul>
                </section>

                <section>
                    <h2>4. Content Ownership</h2>
                    <p>
                        You retain ownership of the content you post. By posting content, you grant us a license to use, display, and distribute your content on our platform.
                    </p>
                </section>

                <section>
                    <h2>5. Account Termination</h2>
                    <p>
                        We reserve the right to suspend or terminate your account at any time for violations of these terms or for any other reason we deem necessary.
                    </p>
                </section>

                <section>
                    <h2>6. Limitation of Liability</h2>
                    <p>
                        Our platform is provided "as is" without warranties of any kind. We are not liable for any damages arising from your use of the platform.
                    </p>
                </section>

                <section>
                    <h2>7. Changes to Terms</h2>
                    <p>
                        We reserve the right to modify these terms at any time. Continued use of the platform after changes constitutes acceptance of the new terms.
                    </p>
                </section>
            </div>

            <div class="terms-footer">
                <router-link to="/register" class="back-link">← Back to Registration</router-link>
            </div>
        </div>
    </div>
</template>

<style scoped>
.terms-container {
    width: 100%;
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
}

.terms-card {
    background: white;
    border-radius: 12px;
    padding: 40px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.terms-header {
    text-align: center;
    margin-bottom: 40px;
    padding-bottom: 20px;
    border-bottom: 2px solid #e2e8f0;
}

.terms-header h1 {
    font-size: 2rem;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 8px;
}

.last-updated {
    color: #64748b;
    font-size: 0.875rem;
}

.terms-content {
    line-height: 1.8;
    color: #334155;
}

.terms-content section {
    margin-bottom: 32px;
}

.terms-content h2 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 12px;
}

.terms-content p {
    margin-bottom: 12px;
    color: #475569;
}

.terms-content ul {
    margin-left: 24px;
    margin-top: 8px;
    margin-bottom: 12px;
}

.terms-content li {
    margin-bottom: 8px;
    color: #475569;
}

.terms-footer {
    margin-top: 40px;
    padding-top: 20px;
    border-top: 2px solid #e2e8f0;
    text-align: center;
}

.back-link {
    color: #6366f1;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 8px;
}

.back-link:hover {
    color: #4f46e5;
    text-decoration: underline;
}

@media (max-width: 768px) {
    .terms-container {
        padding: 10px;
    }

    .terms-card {
        padding: 24px;
    }

    .terms-header h1 {
        font-size: 1.5rem;
    }

    .terms-content h2 {
        font-size: 1.125rem;
    }
}
</style>
