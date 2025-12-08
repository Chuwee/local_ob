export interface PutSaleRequestGateways {
    custom: boolean;
    channel_gateways?: {
        gateway_sid: string;
        configuration_sid: string;
        active: boolean;
        default: boolean;
    }[];
}
