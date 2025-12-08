import { NotificationContentType } from './notification-content-type.model';

export interface NotificationContent {
    language: string;
    type: NotificationContentType;
    value: string;
}