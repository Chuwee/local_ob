import { NotificationRecipients } from './notification-recipients.model';
import { NotificationSummary } from './notification-summary.model';

export interface NotificationDetail {
    code: string;
    entity: {
        id: number;
        name: string;
    };
    name: string;
    status: string;
    recipients: NotificationRecipients;
    summary: NotificationSummary;
    created_date: string;
}
