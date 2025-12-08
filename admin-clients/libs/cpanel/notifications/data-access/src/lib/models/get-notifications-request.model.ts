import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { NotificationStatus } from './notification-status.enum';

export interface GetNotificationsRequest extends PageableFilter {
    name?: string;
    entity_id?: number;
    status?: NotificationStatus[];
    sent_date?: string;
}
