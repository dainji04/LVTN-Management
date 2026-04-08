import i18n from '../i18n'

export const changeLanguages = (lang: 'en' | 'vi') => {
    (i18n.global as any).locale.value = lang;
    localStorage.setItem('locale', lang);
    console.log((i18n.global as any).locale);
    return (i18n.global as any).locale;
}