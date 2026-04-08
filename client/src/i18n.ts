import { createI18n } from 'vue-i18n'
import en from './i18n/locales/en.json'
import vi from './i18n/locales/vi.json'

export type MessageSchema = typeof en
export type SupportedLocales = 'en' | 'vi'

const i18n = createI18n<[MessageSchema], SupportedLocales>({
    legacy: false,
    globalInjection: true,
    locale: localStorage.getItem('locale') || 'vi',
    fallbackLocale: 'en',
    messages: {
        en,
        vi
    }
})

export default i18n