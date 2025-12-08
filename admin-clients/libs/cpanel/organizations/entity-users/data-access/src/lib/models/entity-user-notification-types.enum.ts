export enum EntityUserNotificationTypes {
    channelEventRequest = 'CHANNEL_EVENT_REQUEST',
    channelEventEnable = 'CHANNEL_EVENT_ENABLE',
    eventChannelAsk = 'EVENT_CHANNEL_ASK'
}

export const channelMgrNotifications = [
    EntityUserNotificationTypes.channelEventRequest,
    EntityUserNotificationTypes.channelEventEnable
];

export const eventMgrNotifications = [EntityUserNotificationTypes.eventChannelAsk];
