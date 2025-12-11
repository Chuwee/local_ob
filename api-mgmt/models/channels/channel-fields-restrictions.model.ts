/* eslint-disable no-useless-escape */
export enum ChannelFieldsRestrictions {
    channelNameLength = 200,
    channelEntityManagerLength = 150,
    channelEntityOwnerLength = 150,
    channelWebLength = 250,
    channelSurnameLength = 200,
    channelPositionLength = 200,
    channelEmailLength = 150,
    channelPhoneLength = 25,
    channelPhonePattern = '^[+]{0,1}[\s0-9]*$',
    channelUrlLength = 100,
    channelUrlPattern = '[\.a-zA-Z0-9_-]*',
    channelPurchaseMaxLimit = 150,
    channelBookingMaxLimit = 150,
    channelIssueMaxLimit = 150,
    additionalConditionNameLength = 50,
    additionalConditionChannelTextLength = 5000 // ??
}
