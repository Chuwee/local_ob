import { EventChannelContentImageType } from '@admin-clients/cpanel/promoters/events/communication/data-access';

export interface SaleRequestChannelContentImage {
    language: string;
    type: EventChannelContentImageType;
    position?: number;
    image_url?: string;
}
