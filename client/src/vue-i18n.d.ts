import { DefineLocaleMessage } from 'vue-i18n'
import { MessageSchema } from './i18n'

declare module 'vue-i18n' {
    export interface DefineLocaleMessage extends MessageSchema {}
}