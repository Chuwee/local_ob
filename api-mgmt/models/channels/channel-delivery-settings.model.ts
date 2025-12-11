import { ChannelDeliveryMethod } from './channel-delivery.method.model';
import { EmailContentType } from './email-content-type.enum';

export interface ChannelDeliverySettings {
    use_nfc?: boolean;
    purchase_email_content?: EmailContentType;
    methods?: ChannelDeliveryMethod[];
    b2b_external_download_url?: {
        enabled: boolean;
        target_channel?: {
            id: number;
            name: string;
            url: string;
        };
    };
    receipt_ticket_display: {
        pdf?: boolean;
        passbook?: boolean;
        qr?: boolean;
    };
    checkout_ticket_display?: {
        pdf: boolean;
        passbook: boolean;
    };
}

export interface PutChannelDeliverySettings extends ChannelDeliverySettings {
    b2b_external_download_url?: {
        enabled: boolean;
        target_channel_id?: number;
    };
}
