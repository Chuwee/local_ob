import { buildHttpParams } from '@OneboxTM/utils-http';
import {
    EventChannelContentImage, EventChannelContentImageRequest, EventChannelContentImageType, EventChannelContentText
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    TicketContentImage, TicketContentImageRequest, TicketContentImageType, TicketContentText
} from '@admin-clients/cpanel/promoters/events/data-access';
import { ContentLinkRequest } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
    CreatePackItemRequest, CreatePackRequest, CreateRateRequest, Pack, PackItem, PackPrice, PackPriceTypes,
    PackRate, PutPack, PutPackItem, PutPackPrice, PutPackPriceTypes
} from '../models';

@Injectable({
    providedIn: 'root'
})
export class PacksApi {
    readonly #http = inject(HttpClient);
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CHANNELS_API = `${this.BASE_API}/mgmt-api/v1/channels`;
    private readonly PACKS = '/packs';
    private readonly CONTENTS = '/contents';
    private readonly TICKET_CONTENTS = '/ticket-contents';

    getPacksList(channelId: number): Observable<Pack[]> {
        return this.#http.get<Pack[]>(`${this.CHANNELS_API}/${channelId}${this.PACKS}`);
    }

    getPack(channelId: number, packId: number): Observable<Pack> {
        return this.#http.get<Pack>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}`);
    }

    postPack(channelId: number, req: CreatePackRequest): Observable<Pack> {
        return this.#http.post<Pack>(`${this.CHANNELS_API}/${channelId}${this.PACKS}`, req);
    }

    putPack(channelId: number, packId: number, params: PutPack): Observable<void> {
        return this.#http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}`, params);
    }

    deletePack(channelId: number, packId: number): Observable<void> {
        return this.#http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}`);
    }

    //Pack items
    getPackItems(channelId: number, packId: number): Observable<PackItem[]> {
        return this.#http.get<PackItem[]>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/items`);
    }

    postPackItems(channelId: number, packId: number, req: CreatePackItemRequest[]): Observable<Pack> {
        return this.#http.post<Pack>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/items`, req);
    }

    putPackItems(channelId: number, packId: number, itemId: number, params: PutPackItem): Observable<void> {
        return this.#http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/items/${itemId}`, params);
    }

    deletePackItems(channelId: number, packId: number, itemId: number): Observable<void> {
        return this.#http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/items/${itemId}`);
    }

    //Pack Prices
    getPackPrices(channelId: number, packId: number): Observable<PackPrice[]> {
        return this.#http.get<PackPrice[]>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/prices`);
    }

    putPackPrices(channelId: number, packId: number, prices: PutPackPrice[]): Observable<void> {
        return this.#http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/prices`, prices);
    }

    //Pack rates
    getPackRates(channelId: number, packId: number): Observable<PackRate[]> {
        return this.#http.get<PackRate[]>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/rates`);
    }

    postPackRates(channelId: number, packId: number, rates: CreateRateRequest): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/rates`, rates);
    }

    refreshPackRates(channelId: number, packId: number): Observable<void> {
        return this.#http.post<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/rates/refresh`, {});
    }

    //Pack price types
    getPackPriceTypes(channelId: number, packId: number, itemId: number): Observable<PackPriceTypes> {
        return this.#http.get<PackPriceTypes>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/items/${itemId}/price-types`
        );
    }

    putPackPriceTypes(channelId: number, packId: number, itemId: number, req: PutPackPriceTypes): Observable<void> {
        return this.#http.put<void>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/items/${itemId}/price-types`, req
        );
    }

    //Communication elements
    getPackTexts(channelId: number, packId: number): Observable<EventChannelContentText[]> {
        return this.#http.get<EventChannelContentText[]>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.CONTENTS}/texts`);
    }

    postPackTexts(channelId: number, packId: number, contents: EventChannelContentText[]): Observable<void> {
        return this.#http.post<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.CONTENTS}/texts`, contents);
    }

    getPackImages(channelId: number, packId: number): Observable<EventChannelContentImage[]> {
        return this.#http.get<EventChannelContentImage[]>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.CONTENTS}/images`
        );
    }

    postPackImages(channelId: number, packId: number, contents: EventChannelContentImageRequest[]): Observable<void> {
        return this.#http.post<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.CONTENTS}/images`, contents);
    }

    deletePackImage(
        channelId: number, packId: number, language: string, type: EventChannelContentImageType, position: number
    ): Observable<void> {
        const params = buildHttpParams({ position });
        return this.#http.delete<void>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.CONTENTS}/images/languages/${language}/types/${type}`,
            { params }
        );
    }

    //Ticket pdf contents
    getPackTicketTexts(channelId: number, packId: number): Observable<TicketContentText[]> {
        return this.#http.get<TicketContentText[]>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PDF/texts`
        );
    }

    postPackTicketTexts(channelId: number, packId: number, contents: TicketContentText[]): Observable<void> {
        return this.#http.post<void>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PDF/texts`, contents);
    }

    getPackTicketImages(channelId: number, packId: number): Observable<TicketContentImage[]> {
        return this.#http.get<TicketContentImage[]>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PDF/images`
        );
    }

    postPackTicketImages(channelId: number, packId: number, contents: TicketContentImageRequest[]): Observable<void> {
        return this.#http.post<void>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PDF/images`, contents
        );
    }

    deletePackTicketImage(channelId: number, packId: number, language: string, type: TicketContentImageType): Observable<void> {
        return this.#http.delete<void>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PDF/images/languages/${language}/types/${type}`
        );
    }

    //Ticket printer contents
    getPackPrinterTexts(channelId: number, packId: number): Observable<TicketContentText[]> {
        return this.#http.get<TicketContentText[]>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PRINTER/texts`
        );
    }

    postPackPrinterTexts(channelId: number, packId: number, contents: TicketContentText[]): Observable<void> {
        return this.#http.post<void>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PRINTER/texts`, contents
        );
    }

    getPackPrinterImages(channelId: number, packId: number): Observable<TicketContentImage[]> {
        return this.#http.get<TicketContentImage[]>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PRINTER/images`
        );
    }

    postPackPrinterImages(channelId: number, packId: number, contents: TicketContentImageRequest[]): Observable<void> {
        return this.#http.post<void>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PRINTER/images`, contents
        );
    }

    deletePackPrinterImage(channelId: number, packId: number, language: string, type: TicketContentImageType): Observable<void> {
        return this.#http.delete<void>(
            `${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}${this.TICKET_CONTENTS}/PRINTER/images/languages/${language}/types/${type}`
        );
    }

    // Pack Preview Links
    getPacksPreviewLinks(channelId: number, packId: number): Observable<ContentLinkRequest[]> {
        return this.#http.get<ContentLinkRequest[]>(`${this.CHANNELS_API}/${channelId}${this.PACKS}/${packId}/funnel-urls`);
    }
}
