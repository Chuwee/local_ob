import { Entity } from '@admin-clients/shared/common/data-access';
import { NotificationStatus } from './notification-status.enum';
import { NotificationSummary } from './notification-summary.model';

export interface NotificationEmails {
    code: string;
    entity: Entity;
    name: string;
    status: NotificationStatus;
    summary: NotificationSummary;
    created_date: string;
}
