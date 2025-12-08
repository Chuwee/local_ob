import { ListResponse } from '@OneboxTM/utils-state';
import { ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { NotificationEmails } from './notification-emails.model';

export interface GetNotificationsResponse extends ListResponse<NotificationEmails> {
    aggregated_data: ResponseAggregatedData;
}
