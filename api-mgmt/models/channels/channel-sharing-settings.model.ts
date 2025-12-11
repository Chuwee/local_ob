export type PaymentSetting = {
    default: boolean;
    active: boolean;
    gateway_sid: string;
    conf_sid: string;
};

export interface ChannelSharingSettings {
    allow_booking_sharing?: boolean;
    booking_checkout?: {
        enabled?: boolean;
        channel_id?: number;
        channel_name?: string;
        payment_settings?: PaymentSetting[];
    };
}
