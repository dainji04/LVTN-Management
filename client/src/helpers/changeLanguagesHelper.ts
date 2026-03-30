import i18n from '../i18n'

export const changeLanguages = (lang: 'en' | 'vi') => {
    (i18n.global as any).locale = lang;
    localStorage.setItem('lang', lang);
}