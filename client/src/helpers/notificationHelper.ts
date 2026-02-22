import { notification } from "ant-design-vue";

type NotificationType = 'success' | 'error' | 'warning' | 'info';

export const notificationHelper = (
        type: NotificationType = 'success',
        message: string = '', 
        description: string = '') => {
    notification[type]({
        message: message,
        description: description,
    });
}