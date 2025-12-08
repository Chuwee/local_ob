import { NotificationRecipients } from './notification-recipients.model';

export interface PutNotificationEmail {
    code?: string;
    name?: string;
    recipients?: NotificationRecipients;
}
