
export enum NotificationChannelsScope {
    all = 'ALL',
    restricted = 'RESTRICTED'
}

export interface NotificationChannels {
    type: NotificationChannelsScope;
    channels: {
        id: number;
        name: string;
    }[];
}
