import { NotificationEmailTemplateType } from './notification-email-template-type.enum';

export interface NotificationEmailTemplate {
    type: NotificationEmailTemplateType;
    from: string;
    cco: string;
    alias: string;
}
