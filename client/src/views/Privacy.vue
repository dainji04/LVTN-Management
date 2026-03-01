<template>
    <div class="privacy-container">
        <div class="privacy-card">
            <div class="privacy-header">
                <h1>Privacy Policy</h1>
                <p class="last-updated">Last updated: {{ new Date().toLocaleDateString('vi-VN') }}</p>
            </div>

            <div class="privacy-content">
                <section>
                    <h2>1. Information We Collect</h2>
                    <p>We collect information that you provide directly to us, including:</p>
                    <ul>
                        <li>Account information (name, username, email, password)</li>
                        <li>Profile information and content you post</li>
                        <li>Messages and communications with other users</li>
                        <li>Device information and usage data</li>
                    </ul>
                </section>

                <section>
                    <h2>2. How We Use Your Information</h2>
                    <p>We use the information we collect to:</p>
                    <ul>
                        <li>Provide, maintain, and improve our services</li>
                        <li>Create and manage your account</li>
                        <li>Send you notifications and updates</li>
                        <li>Ensure platform security and prevent fraud</li>
                        <li>Comply with legal obligations</li>
                    </ul>
                </section>

                <section>
                    <h2>3. Information Sharing</h2>
                    <p>
                        We do not sell your personal information. We may share your information only in the following circumstances:
                    </p>
                    <ul>
                        <li>With your consent</li>
                        <li>To comply with legal requirements</li>
                        <li>To protect our rights and safety</li>
                        <li>With service providers who assist us in operating our platform</li>
                    </ul>
                </section>

                <section>
                    <h2>4. Data Security</h2>
                    <p>
                        We implement appropriate security measures to protect your personal information. However, no method of transmission over the internet is 100% secure.
                    </p>
                </section>

                <section>
                    <h2>5. Your Rights</h2>
                    <p>You have the right to:</p>
                    <ul>
                        <li>Access your personal information</li>
                        <li>Update or correct your information</li>
                        <li>Delete your account and data</li>
                        <li>Opt-out of certain communications</li>
                    </ul>
                </section>

                <section>
                    <h2>6. Cookies and Tracking</h2>
                    <p>
                        We use cookies and similar technologies to enhance your experience, analyze usage, and assist with our marketing efforts.
                    </p>
                </section>

                <section>
                    <h2>7. Children's Privacy</h2>
                    <p>
                        Our platform is not intended for users under the age of 13. We do not knowingly collect personal information from children under 13.
                    </p>
                </section>

                <section>
                    <h2>8. Changes to This Policy</h2>
                    <p>
                        We may update this privacy policy from time to time. We will notify you of any changes by posting the new policy on this page.
                    </p>
                </section>

                <section>
                    <h2>9. Contact Us</h2>
                    <p>
                        If you have any questions about this privacy policy, please contact us through our support channels.
                    </p>
                </section>
            </div>

            <div class="privacy-footer">
                <router-link to="/register" class="back-link">← Back to Registration</router-link>
            </div>
        </div>
    </div>
</template>

<style scoped>
.privacy-container {
    width: 100%;
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
}

.privacy-card {
    background: white;
    border-radius: 12px;
    padding: 40px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.privacy-header {
    text-align: left;
    margin-bottom: 40px;
    padding-bottom: 20px;
    border-bottom: 2px solid #e2e8f0;
}

.privacy-header h1 {
    font-size: 2rem;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 8px;
}

.last-updated {
    color: #64748b;
    font-size: 0.875rem;
}

.privacy-content {
    line-height: 1.8;
    color: #334155;
}

.privacy-content section {
    margin-bottom: 32px;
}

.privacy-content h2 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 12px;
}

.privacy-content p {
    margin-bottom: 12px;
    color: #475569;
}

.privacy-content ul {
    margin-left: 24px;
    margin-top: 8px;
    margin-bottom: 12px;
}

.privacy-content li {
    margin-bottom: 8px;
    color: #475569;
}

.privacy-footer {
    margin-top: 40px;
    padding-top: 20px;
    border-top: 2px solid #e2e8f0;
    text-align: left;
}

.back-link {
    color: #6366f1;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s ease;
    display: inline-flex;
    align-items: left;
    gap: 8px;
}

.back-link:hover {
    color: #4f46e5;
    text-decoration: underline;
}

@media (max-width: 768px) {
    .privacy-container {
        padding: 10px;
    }

    .privacy-card {
        padding: 24px;
    }

    .privacy-header h1 {
        font-size: 1.5rem;
    }

    .privacy-content h2 {
        font-size: 1.125rem;
    }
}
</style>
