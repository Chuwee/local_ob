import { StateProperty } from '@OneboxTM/utils-state';
import { EventChannelContentImage, EventChannelContentText } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { TicketContentImage, TicketContentText } from '@admin-clients/cpanel/promoters/events/data-access';
import { ContentLinkRequest } from '@admin-clients/cpanel/shared/data-access';
import { Injectable } from '@angular/core';
import { PackPrice, PackPriceTypes, PackRate } from '../models';
import { Pack, PackItem } from '../models/pack.model';

@Injectable({
    providedIn: 'root'
})
export class PacksState {
    readonly packList = new StateProperty<Pack[]>();
    readonly pack = new StateProperty<Pack>();
    readonly packItems = new StateProperty<PackItem[]>();
    readonly packPrices = new StateProperty<PackPrice[]>();
    readonly packRates = new StateProperty<PackRate[]>();
    readonly packPriceTypes = new StateProperty<PackPriceTypes>();
    readonly packTexts = new StateProperty<EventChannelContentText[]>();
    readonly packImages = new StateProperty<EventChannelContentImage[]>();
    readonly packTicketTexts = new StateProperty<TicketContentText[]>();
    readonly packTicketImages = new StateProperty<TicketContentImage[]>();
    readonly packPrinterTexts = new StateProperty<TicketContentText[]>();
    readonly packPrinterImages = new StateProperty<TicketContentImage[]>();
    readonly packPreviewLinks = new StateProperty<ContentLinkRequest[]>();
}
