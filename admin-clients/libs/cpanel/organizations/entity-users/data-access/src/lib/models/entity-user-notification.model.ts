import { EntityUserNotificationTypes } from './entity-user-notification-types.enum';

export interface EntityUserNotification {
    type: EntityUserNotificationTypes;
    enable: boolean;
}
