export enum ChannelType {
    web = 'WEB',
    webBoxOffice = 'WEB_BOX_OFFICE',
    webB2B = 'WEB_B2B',
    webSubscribers = 'WEB_SUBSCRIBERS',
    boxOffice = 'BOX_OFFICE',
    external = 'EXTERNAL',
    members = 'MEMBERS'
}

export const channelWebTypes = [
    ChannelType.web,
    ChannelType.webBoxOffice,
    ChannelType.webB2B,
    ChannelType.webSubscribers
];

export const channelVoucherWebTypes = [
    ChannelType.web,
    ChannelType.webBoxOffice
];

export const channelAfterPromotionTypes = [
    ChannelType.web,
    ChannelType.webBoxOffice
];

export const channelOperativeTypes = channelWebTypes.concat([ChannelType.boxOffice, ChannelType.members]);
export const channelTypesPaymentMethods = channelWebTypes.concat(ChannelType.members);
