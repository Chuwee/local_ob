import { SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export enum NotificationSessionsScope {
    all = 'ALL',
    restricted = 'RESTRICTED'
}

export interface NotificationSessions {
    type: NotificationSessionsScope;
    sessions: {
        id: number;
        name: string;
        date?: string;
        type?: SessionType;
    }[];
}

