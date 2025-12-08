import { SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export interface NotificationSessionsListElement {
    id: number;
    name: string;
    dates: {
        start: string;
    };
    type: SessionType;
}
