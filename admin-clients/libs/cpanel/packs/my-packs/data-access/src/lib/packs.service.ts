import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { EventChannelContentImageRequest, EventChannelContentText } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { TicketContentImageRequest, TicketContentText } from '@admin-clients/cpanel/promoters/events/data-access';
import { fetchAll } from '@admin-clients/shared/utility/utils';
import { inject, Injectable, Provider } from '@angular/core';
import { catchError, finalize, map, Observable, of, switchMap, takeUntil, zip } from 'rxjs';
import { PacksApi } from './api/packs.api';
import { CreatePackItemRequest } from './models/create-pack-item.model';
import { CreatePackRequest } from './models/create-pack.model';
import { GetPackSubItemsRequest } from './models/get-pack-subitems-request.model';
import { GetPacksRequest } from './models/get-packs-request.model';
import { GetPacksResponse } from './models/get-packs-response.model';
import { PutPackChannel } from './models/pack-channel.model';
import { PutPackPriceTypes } from './models/pack-price-types.model';
import { CreateRateRequest } from './models/pack-rate.model';
import { Pack } from './models/pack.model';
import { PutPackItem } from './models/put-pack-item.model';
import { PutPackPrice } from './models/put-pack-price.model';
import { PutPackSubItems } from './models/put-pack-sub-items.model';
import { PutPack } from './models/put-pack.model';
import { PacksState } from './state/packs.state';

export const providePacksService = (): Provider => [
    PacksApi,
    PacksState,
    PacksService
];

@Injectable({
    providedIn: 'root'
})
export class PacksService {
    readonly #api = inject(PacksApi);
    readonly #state = inject(PacksState);

    readonly packsList = Object.freeze({
        load: (request: GetPacksRequest) => StateManager.load(
            this.#state.packsList,
            this.#api.getPacksList(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.packsList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.packsList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.packsList.isInProgress$(),
        clear: () => this.#state.packsList.setValue(null)
    });

    readonly pack = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.pack,
            this.#api.getPack(packId)
        ),
        get$: () => this.#state.pack.getValue$(),
        getError$: () => this.#state.pack.getError$(),
        loading$: () => this.#state.pack.isInProgress$(),
        create: (req: CreatePackRequest) => StateManager.inProgress(
            this.#state.pack,
            this.#api.postPack(req)
        ),
        update: (packId: number, params: PutPack) => StateManager.inProgress(
            this.#state.pack,
            this.#api.putPack(packId, params)
        ),
        delete: (packId: number) => StateManager.inProgress(
            this.#state.pack,
            this.#api.deletePack(packId)
        ),
        clear: () => this.#state.pack.setValue(null),
        channels: Object.freeze({
            load: (packId: number) => StateManager.load(
                this.#state.channels,
                this.#api.getPackChannels(packId)
            ),
            post: (packId: number, channels: number[]) => StateManager.inProgress(
                this.#state.channels,
                this.#api.postPackChannels(packId, channels)
            ),
            getData$: () => this.#state.channels.getValue$().pipe(getListData()),
            getMetadata$: () => this.#state.channels.getValue$().pipe(getMetadata()),
            loading$: () => this.#state.channels.isInProgress$(),
            clear: () => this.#state.channels.setValue(null),
            error$: () => this.#state.channels.getError$()
        }),
        channel: Object.freeze({
            load: (packId: number, channelId: number): void => StateManager.load(
                this.#state.channel,
                this.#api.getPackChannel(packId, channelId)
            ),
            delete: (packId: number, channelId: number) =>
                StateManager.inProgress(
                    this.#state.channel,
                    this.#api.deletePackChannel(packId, channelId)
                ),
            get$: () => this.#state.channel.getValue$(),
            error$: () => this.#state.channel.getError$(),
            loading$: () => this.#state.channel.isInProgress$(),
            update: (packId: number, channelId: number, params: PutPackChannel) => StateManager.inProgress(
                this.#state.channel,
                this.#api.putPackChannel(packId, channelId, params)
            ),
            clear: () => this.#state.channel.setValue(null),
            request: (packId: number, channelId: number) => StateManager.inProgress(
                this.#state.channel,
                this.#api.postRequestPackChannel(packId, channelId)
            )
        }),
        previewLinks: Object.freeze({
            load: (packId: number, channelId: number) => StateManager.load(
                this.#state.packPreviewLinks,
                this.#api.getPacksPreviewLinks(packId, channelId)
            ),
            get$: () => this.#state.packPreviewLinks.getValue$(),
            loading$: () => this.#state.packPreviewLinks.isInProgress$(),
            clear: () => this.#state.packPreviewLinks.setValue(null)
        })
    });

    readonly packItems = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packItems,
            this.#api.getPackItems(packId)
        ),
        get$: () => this.#state.packItems.getValue$(),
        getError$: () => this.#state.packItems.getError$(),
        loading$: () => this.#state.packItems.isInProgress$(),
        create: (packId: number, req: CreatePackItemRequest[]) => StateManager.inProgress(
            this.#state.packItems,
            this.#api.postPackItems(packId, req)
        ),
        update: (packId: number, itemId: number, params: PutPackItem) => StateManager.inProgress(
            this.#state.packItems,
            this.#api.putPackItems(packId, itemId, params)
        ),
        delete: (packId: number, itemId: number) => StateManager.inProgress(
            this.#state.packItems,
            this.#api.deletePackItems(packId, itemId)
        ),
        clear: () => this.#state.packItems.setValue(null)
    });

    readonly packSubItems = Object.freeze({
        load: (packId: number, itemId: number, request: GetPackSubItemsRequest) => StateManager.load(
            this.#state.packSubItems,
            this.#api.getPackSubItems(packId, itemId, request).pipe(mapMetadata())
        ),
        loadMore: (packId: number, itemId: number, request: GetPackSubItemsRequest) => StateManager.loadMore(
            request,
            this.#state.packSubItems,
            r => this.#api.getPackSubItems(packId, itemId, r)
        ),
        getData$: () => this.#state.packSubItems.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.packSubItems.getValue$().pipe(getMetadata()),
        getError$: () => this.#state.packSubItems.getError$(),
        loading$: () => this.#state.packSubItems.isInProgress$(),
        update: (packId: number, itemId: number, params: PutPackSubItems) => StateManager.inProgress(
            this.#state.packSubItems,
            this.#api.putPackSubItems(packId, itemId, params)
        ),
        clear: () => this.#state.packSubItems.setValue(null)
    });

    readonly packPrices = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packPrices,
            this.#api.getPackPrices(packId)
        ),
        get$: () => this.#state.packPrices.getValue$(),
        inProgress$: () => this.#state.packPrices.isInProgress$(),
        update: (packId: number, packPrices: PutPackPrice[]) => StateManager.inProgress(
            this.#state.packPrices,
            this.#api.putPackPrices(packId, packPrices)
        ),
        clear: () => this.#state.packPrices.setValue(null),
        error$: () => this.#state.packPrices.getError$()
    });

    readonly packRates = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packRates,
            this.#api.getPackRates(packId)
        ),
        refresh: (packId: number) => StateManager.inProgress(
            this.#state.packRates,
            this.#api.refreshPackRates(packId)
        ),
        get$: () => this.#state.packRates.getValue$(),
        inProgress$: () => this.#state.packRates.isInProgress$(),
        create: (packId: number, req: CreateRateRequest) => StateManager.inProgress(
            this.#state.packRates,
            this.#api.postPackRates(packId, req)
        ),
        clear: () => this.#state.packRates.setValue(null),
        error$: () => this.#state.packRates.getError$()
    });

    readonly priceTypes = Object.freeze({
        load: (packId: number, itemId: number) => StateManager.load(
            this.#state.packPriceTypes,
            this.#api.getPackPriceTypes(packId, itemId)
        ),
        get$: () => this.#state.packPriceTypes.getValue$(),
        loading$: () => this.#state.packPriceTypes.isInProgress$(),
        update: (packId: number, itemId: number, req: PutPackPriceTypes) => StateManager.inProgress(
            this.#state.packPriceTypes,
            this.#api.putPackPriceTypes(packId, itemId, req)
        ),
        clear: () => this.#state.packPriceTypes.setValue(null),
        error$: () => this.#state.packPriceTypes.getError$()
    });

    readonly packTexts = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packTexts,
            this.#api.getPackTexts(packId)
        ),
        get$: () => this.#state.packTexts.getValue$(),
        save: (packId: number, contents: EventChannelContentText[]) => StateManager.inProgress(
            this.#state.packTexts,
            this.#api.postPackTexts(packId, contents)
        ),
        loading$: () => this.#state.packTexts.isInProgress$(),
        clear: () => this.#state.packTexts.setValue(null)
    });

    readonly packImages = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packImages,
            this.#api.getPackImages(packId)
        ),
        get$: () => this.#state.packImages.getValue$(),
        save: (packId: number, contents: EventChannelContentImageRequest[]) => StateManager.inProgress(
            this.#state.packImages,
            this.#api.postPackImages(packId, contents)
        ),
        delete: (packId: number, imagesToDelete: EventChannelContentImageRequest[]) => StateManager.inProgress(
            this.#state.packImages,
            zip(...imagesToDelete.map(request =>
                this.#api.deletePackImage(packId, request.language, request.type, request.position)
            )).pipe(
                switchMap(() => of(null)),
                catchError(() => of(null))
            )),
        loading$: () => this.#state.packImages.isInProgress$(),
        clear: () => this.#state.packImages.setValue(null)
    });

    readonly packTicketTexts = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packTicketTexts,
            this.#api.getPackTicketTexts(packId)
        ),
        get$: () => this.#state.packTicketTexts.getValue$(),
        save: (packId: number, contents: TicketContentText[]) => StateManager.inProgress(
            this.#state.packTicketTexts,
            this.#api.postPackTicketTexts(packId, contents)
        ),
        loading$: () => this.#state.packTicketTexts.isInProgress$(),
        clear: () => this.#state.packTicketTexts.setValue(null)
    });

    readonly packTicketImages = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packTicketImages,
            this.#api.getPackTicketImages(packId)
        ),
        get$: () => this.#state.packTicketImages.getValue$(),
        save: (packId: number, contents: TicketContentImageRequest[]) => StateManager.inProgress(
            this.#state.packTicketImages,
            this.#api.postPackTicketImages(packId, contents)
        ),
        delete: (packId: number, imagesToDelete: TicketContentImageRequest[]) => StateManager.inProgress(
            this.#state.packTicketImages,
            zip(...imagesToDelete.map(request =>
                this.#api.deletePackTicketImage(packId, request.language, request.type)
            )).pipe(
                switchMap(() => of(null)),
                catchError(() => of(null))
            )),
        loading$: () => this.#state.packTicketImages.isInProgress$(),
        clear: () => this.#state.packTicketImages.setValue(null)
    });

    readonly packPrinterTexts = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packPrinterTexts,
            this.#api.getPackPrinterTexts(packId)
        ),
        get$: () => this.#state.packPrinterTexts.getValue$(),
        save: (packId: number, contents: TicketContentText[]) => StateManager.inProgress(
            this.#state.packPrinterTexts,
            this.#api.postPackPrinterTexts(packId, contents)
        ),
        loading$: () => this.#state.packPrinterTexts.isInProgress$(),
        clear: () => this.#state.packPrinterTexts.setValue(null)
    });

    readonly packPrinterImages = Object.freeze({
        load: (packId: number) => StateManager.load(
            this.#state.packPrinterImages,
            this.#api.getPackPrinterImages(packId)
        ),
        get$: () => this.#state.packPrinterImages.getValue$(),
        save: (packId: number, contents: TicketContentImageRequest[]) => StateManager.inProgress(
            this.#state.packPrinterImages,
            this.#api.postPackPrinterImages(packId, contents)
        ),
        delete: (packId: number, imagesToDelete: TicketContentImageRequest[]) => StateManager.inProgress(
            this.#state.packPrinterImages,
            zip(...imagesToDelete.map(request =>
                this.#api.deletePackPrinterImage(packId, request.language, request.type)
            )).pipe(
                switchMap(() => of(null)),
                catchError(() => of(null))
            )),
        loading$: () => this.#state.packPrinterImages.isInProgress$(),
        clear: () => this.#state.packPrinterImages.setValue(null)
    });

    loadAllPacks(request?: Partial<GetPacksRequest>): void {
        this.#state.allPacks.triggerCancellation();
        this.#state.allPacks.setInProgress(true);
        const req: GetPacksRequest = Object.assign({ offset: 0, limit: 999 }, request);
        fetchAll((offset: number) => this.#api.getPacksList({ ...req, offset }))
            .pipe(
                finalize(() => this.#state.allPacks.setInProgress(false)),
                takeUntil(this.#state.allPacks.getCancellation$())
            ).subscribe(result => this.#state.allPacks.setValue(result));
    }

    getAllPacks$(): Observable<GetPacksResponse> {
        return this.#state.allPacks.getValue$();
    }

    getAllPacksData$(): Observable<Pack[]> {
        return this.#state.allPacks.getValue$().pipe(map(packs => packs?.data));
    }

    isAllPacksLoading$(): Observable<boolean> {
        return this.#state.allPacks.isInProgress$();
    }

    clearAllPacks(): void {
        this.#state.allPacks.setValue(null);
    }

}
