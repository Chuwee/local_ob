import { buildHttpParams } from '@OneboxTM/utils-http';
import {
    EventChannelContentText, EventChannelContentImage, EventChannelContentImageRequest, EventChannelContentImageType
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    TicketContentText, TicketContentImage, TicketContentImageRequest, TicketContentImageType
} from '@admin-clients/cpanel/promoters/events/data-access';
import { ContentLinkRequest } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CreatePackItemRequest } from '../models/create-pack-item.model';
import { CreatePackRequest } from '../models/create-pack.model';
import { GetPackChannelsResponse } from '../models/get-pack-channels-response.model';
import { GetPackSubItemsRequest } from '../models/get-pack-subitems-request.model';
import { GetPackSubItemsResponse } from '../models/get-pack-subitems-response.model';
import { GetPacksRequest } from '../models/get-packs-request.model';
import { GetPacksResponse } from '../models/get-packs-response.model';
import { PackChannel, PutPackChannel } from '../models/pack-channel.model';
import { PackPriceTypes, PutPackPriceTypes } from '../models/pack-price-types.model';
import { PackPrice } from '../models/pack-price.model';
import { CreateRateRequest, PackRate } from '../models/pack-rate.model';
import { Pack, PackItem } from '../models/pack.model';
import { PutPackItem } from '../models/put-pack-item.model';
import { PutPackPrice } from '../models/put-pack-price.model';
import { PutPackSubItems } from '../models/put-pack-sub-items.model';
import { PutPack } from '../models/put-pack.model';

@Injectable({
    providedIn: 'root'
})
export class PacksApi {
    readonly #http = inject(HttpClient);
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #MGMT_API = `${this.#BASE_API}/mgmt-api/v1`;
    readonly #PACKS = 'packs';
    readonly #CONTENTS = '/contents';
    readonly #TICKET_CONTENTS = '/ticket-contents';
    readonly #CHANNELS_SEGMENT = '/channels';

    //PACKS LIST
    getPacksList(request: GetPacksRequest): Observable<GetPacksResponse> {
        const params = buildHttpParams(request);
        return this.#http.get<GetPacksResponse>(`${this.#MGMT_API}/${this.#PACKS}`, { params });
    }

    //PACK
    getPack(packId: number): Observable<Pack> {
        return this.#http.get<Pack>(`${this.#MGMT_API}/${this.#PACKS}/${packId}`);
    }

    postPack(req: CreatePackRequest): Observable<Pack> {
        return this.#http.post<Pack>(`${this.#MGMT_API}/${this.#PACKS}`, req);
    }

    putPack(packId: number, params: PutPack): Observable<void> {
        return this.#http.put<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}`, params);
    }

    deletePack(packId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}`);
    }

    //PACK ITEMS
    getPackItems(packId: number): Observable<PackItem[]> {
        return this.#http.get<PackItem[]>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/items`);
    }

    postPackItems(packId: number, req: CreatePackItemRequest[]): Observable<Pack> {
        return this.#http.post<Pack>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/items`, req);
    }

    putPackItems(packId: number, itemId: number, params: PutPackItem): Observable<void> {
        return this.#http.put<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/items/${itemId}`, params);
    }

    deletePackItems(packId: number, itemId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/items/${itemId}`);
    }

    //PACK SUBITEMS
    getPackSubItems(packId: number, itemId: number, request?: Partial<GetPackSubItemsRequest>): Observable<GetPackSubItemsResponse> {
        const params = buildHttpParams(request);
        return this.#http.get<GetPackSubItemsResponse>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/items/${itemId}/subitems`, { params });
    }

    putPackSubItems(packId: number, itemId: number, params: PutPackSubItems): Observable<void> {
        return this.#http.put<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/items/${itemId}/subitems`, params);
    }

    //PACK PRICES
    getPackPrices(packId: number): Observable<PackPrice[]> {
        return this.#http.get<PackPrice[]>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}/prices`);
    }

    putPackPrices(packId: number, prices: PutPackPrice[]): Observable<void> {
        return this.#http.put<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/prices`, prices);
    }

    //PACK RATES
    getPackRates(packId: number): Observable<PackRate[]> {
        return this.#http.get<PackRate[]>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}/rates`);
    }

    postPackRates(packId: number, rates: CreateRateRequest): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/rates`, rates);
    }

    refreshPackRates(packId: number): Observable<void> {
        return this.#http.post<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}/rates/refresh`, {});
    }

    //PACK PRICE TYPES
    getPackPriceTypes(packId: number, itemId: number): Observable<PackPriceTypes> {
        return this.#http.get<PackPriceTypes>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}/items/${itemId}/price-types`
        );
    }

    putPackPriceTypes(packId: number, itemId: number, req: PutPackPriceTypes): Observable<void> {
        return this.#http.put<void>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}/items/${itemId}/price-types`, req
        );
    }

    //PACK COMMUNICATION
    getPackTexts(packId: number): Observable<EventChannelContentText[]> {
        return this.#http.get<EventChannelContentText[]>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CONTENTS}/texts`);
    }

    postPackTexts(packId: number, contents: EventChannelContentText[]): Observable<void> {
        return this.#http.post<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CONTENTS}/texts`, contents);
    }

    getPackImages(packId: number): Observable<EventChannelContentImage[]> {
        return this.#http.get<EventChannelContentImage[]>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CONTENTS}/images`
        );
    }

    postPackImages(packId: number, contents: EventChannelContentImageRequest[]): Observable<void> {
        return this.#http.post<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CONTENTS}/images`, contents);
    }

    deletePackImage(
        packId: number, language: string, type: EventChannelContentImageType, position: number
    ): Observable<void> {
        const params = buildHttpParams({ position });
        return this.#http.delete<void>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CONTENTS}/images/languages/${language}/types/${type}`,
            { params }
        );
    }

    //Ticket pdf contents
    getPackTicketTexts(packId: number): Observable<TicketContentText[]> {
        return this.#http.get<TicketContentText[]>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PDF/texts`
        );
    }

    postPackTicketTexts(packId: number, contents: TicketContentText[]): Observable<void> {
        return this.#http.post<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PDF/texts`, contents);
    }

    getPackTicketImages(packId: number): Observable<TicketContentImage[]> {
        return this.#http.get<TicketContentImage[]>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PDF/images`
        );
    }

    postPackTicketImages(packId: number, contents: TicketContentImageRequest[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PDF/images`, contents
        );
    }

    deletePackTicketImage(packId: number, language: string, type: TicketContentImageType): Observable<void> {
        return this.#http.delete<void>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PDF/images/languages/${language}/types/${type}`
        );
    }

    //Ticket printer contents
    getPackPrinterTexts(packId: number): Observable<TicketContentText[]> {
        return this.#http.get<TicketContentText[]>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PRINTER/texts`
        );
    }

    postPackPrinterTexts(packId: number, contents: TicketContentText[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PRINTER/texts`, contents
        );
    }

    getPackPrinterImages(packId: number): Observable<TicketContentImage[]> {
        return this.#http.get<TicketContentImage[]>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PRINTER/images`
        );
    }

    postPackPrinterImages(packId: number, contents: TicketContentImageRequest[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PRINTER/images`, contents
        );
    }

    deletePackPrinterImage(packId: number, language: string, type: TicketContentImageType): Observable<void> {
        return this.#http.delete<void>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#TICKET_CONTENTS}/PRINTER/images/languages/${language}/types/${type}`
        );
    }

    //PACK CHANNELS
    getPackChannels(packId: number): Observable<GetPackChannelsResponse> {
        return this.#http.get<GetPackChannelsResponse>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CHANNELS_SEGMENT}`);
    }

    postPackChannels(packId: number, channels: number[]): Observable<{ channel_ids: number[] }> {
        const body = { channel_ids: channels };
        return this.#http.post<{ channel_ids: number[] }>(
            `${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CHANNELS_SEGMENT}`,
            body
        );
    }

    //CHANNEL
    getPackChannel(packId: number, channelId: number): Observable<PackChannel> {
        return this.#http.get<PackChannel>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CHANNELS_SEGMENT}/${channelId}`);
    }

    deletePackChannel(packId: number, channelId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CHANNELS_SEGMENT}/${channelId}`);
    }

    postRequestPackChannel(packId: number, channelId: number): Observable<void> {
        return this.#http.post<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CHANNELS_SEGMENT}/${channelId}/request-approval`, {});
    }

    putPackChannel(packId: number, channelId: number, params: PutPackChannel): Observable<void> {
        return this.#http.put<void>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CHANNELS_SEGMENT}/${channelId}`, params);
    }

    // PACK PREVIEW LINKS
    getPacksPreviewLinks(packId: number, channelId: number): Observable<ContentLinkRequest[]> {
        return this.#http.get<ContentLinkRequest[]>(`${this.#MGMT_API}/${this.#PACKS}/${packId}${this.#CHANNELS_SEGMENT}/${channelId}/funnel-urls`);
    }

}
