import { NotificationSessionsScope } from './notification-sessions.model';

export interface PutNotificationSessions {
    type: NotificationSessionsScope;
    sessions: number[];
}
