import { StateProperty } from '@OneboxTM/utils-state';
import { EventChannelContentImage, EventChannelContentText } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { TicketContentImage, TicketContentText } from '@admin-clients/cpanel/promoters/events/data-access';
import { ContentLinkRequest } from '@admin-clients/cpanel/shared/data-access';
import { Injectable } from '@angular/core';
import { GetPackChannelsResponse } from '../models/get-pack-channels-response.model';
import { GetPackSubItemsResponse } from '../models/get-pack-subitems-response.model';
import { GetPacksResponse } from '../models/get-packs-response.model';
import { PackChannel } from '../models/pack-channel.model';
import { PackPriceTypes } from '../models/pack-price-types.model';
import { PackPrice } from '../models/pack-price.model';
import { PackRate } from '../models/pack-rate.model';
import { Pack, PackItem } from '../models/pack.model';

@Injectable({
    providedIn: 'root'
})
export class PacksState {
    readonly packsList = new StateProperty<GetPacksResponse>();
    readonly pack = new StateProperty<Pack>();
    readonly packItems = new StateProperty<PackItem[]>();
    readonly packSubItems = new StateProperty<GetPackSubItemsResponse>();
    readonly packPrices = new StateProperty<PackPrice[]>();
    readonly packRates = new StateProperty<PackRate[]>();
    readonly packPriceTypes = new StateProperty<PackPriceTypes>();
    readonly packTexts = new StateProperty<EventChannelContentText[]>();
    readonly packImages = new StateProperty<EventChannelContentImage[]>();
    readonly packTicketTexts = new StateProperty<TicketContentText[]>();
    readonly packTicketImages = new StateProperty<TicketContentImage[]>();
    readonly packPrinterTexts = new StateProperty<TicketContentText[]>();
    readonly packPrinterImages = new StateProperty<TicketContentImage[]>();
    readonly channels = new StateProperty<GetPackChannelsResponse>();
    readonly channel = new StateProperty<PackChannel>();
    readonly packPreviewLinks = new StateProperty<ContentLinkRequest[]>();
    readonly allPacks = new StateProperty<GetPacksResponse>();
}
