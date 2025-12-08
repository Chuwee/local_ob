import { EventChannelContentTextType } from '@admin-clients/cpanel/promoters/events/communication/data-access';

export interface SaleRequestChannelContentText {
    language: string;
    type: EventChannelContentTextType;
    redirect_url?: string;
}
