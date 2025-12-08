import { StateManager } from '@OneboxTM/utils-state';
import { EventChannelContentImageRequest, EventChannelContentText } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { TicketContentImageRequest, TicketContentText } from '@admin-clients/cpanel/promoters/events/data-access';
import { inject, Injectable } from '@angular/core';
import { catchError, of, switchMap, zip } from 'rxjs';
import { PacksApi } from './api/packs.api';
import { CreatePackItemRequest, CreateRateRequest, PutPackItem, PutPackPrice, PutPackPriceTypes } from './models';
import { CreatePackRequest } from './models/create-pack.model';
import { PutPack } from './models/put-pack.model';
import { PacksState } from './state/packs.state';

@Injectable({
    providedIn: 'root'
})
export class PacksService {
    private readonly _state = inject(PacksState);
    private readonly _api = inject(PacksApi);

    readonly packList = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this._state.packList,
            this._api.getPacksList(channelId)
        ),
        get$: () => this._state.packList.getValue$(),
        loading$: () => this._state.packList.isInProgress$(),
        clear: () => this._state.packList.setValue(null)
    });

    readonly pack = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.pack,
            this._api.getPack(channelId, packId)
        ),
        get$: () => this._state.pack.getValue$(),
        getError$: () => this._state.pack.getError$(),
        loading$: () => this._state.pack.isInProgress$(),
        create: (channelId: number, req: CreatePackRequest) => StateManager.inProgress(
            this._state.pack,
            this._api.postPack(channelId, req)
        ),
        update: (channelId: number, packId: number, params: PutPack) => StateManager.inProgress(
            this._state.pack,
            this._api.putPack(channelId, packId, params)
        ),
        delete: (channelId: number, packId: number) => StateManager.inProgress(
            this._state.pack,
            this._api.deletePack(channelId, packId)
        ),
        clear: () => this._state.pack.setValue(null)
    });

    readonly packItems = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packItems,
            this._api.getPackItems(channelId, packId)
        ),
        get$: () => this._state.packItems.getValue$(),
        getError$: () => this._state.packItems.getError$(),
        loading$: () => this._state.packItems.isInProgress$(),
        create: (channelId: number, packId: number, req: CreatePackItemRequest[]) => StateManager.inProgress(
            this._state.packItems,
            this._api.postPackItems(channelId, packId, req)
        ),
        update: (channelId: number, packId: number, itemId: number, params: PutPackItem) => StateManager.inProgress(
            this._state.packItems,
            this._api.putPackItems(channelId, packId, itemId, params)
        ),
        delete: (channelId: number, packId: number, itemId: number) => StateManager.inProgress(
            this._state.packItems,
            this._api.deletePackItems(channelId, packId, itemId)
        ),
        clear: () => this._state.packItems.setValue(null)
    });

    readonly packPrices = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packPrices,
            this._api.getPackPrices(channelId, packId)
        ),
        get$: () => this._state.packPrices.getValue$(),
        inProgress$: () => this._state.packPrices.isInProgress$(),
        update: (channelId: number, packId: number, packPrices: PutPackPrice[]) => StateManager.inProgress(
            this._state.packPrices,
            this._api.putPackPrices(channelId, packId, packPrices)
        ),
        clear: () => this._state.packPrices.setValue(null),
        error$: () => this._state.packPrices.getError$()
    });

    readonly packRates = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packRates,
            this._api.getPackRates(channelId, packId)
        ),
        refresh: (channelId: number, packId: number) => StateManager.inProgress(
            this._state.packRates,
            this._api.refreshPackRates(channelId, packId)
        ),
        get$: () => this._state.packRates.getValue$(),
        inProgress$: () => this._state.packRates.isInProgress$(),
        create: (channelId: number, packId: number, req: CreateRateRequest) => StateManager.inProgress(
            this._state.packRates,
            this._api.postPackRates(channelId, packId, req)
        ),
        clear: () => this._state.packRates.setValue(null),
        error$: () => this._state.packRates.getError$()
    });

    readonly priceTypes = Object.freeze({
        load: (channelId: number, packId: number, itemId: number) => StateManager.load(
            this._state.packPriceTypes,
            this._api.getPackPriceTypes(channelId, packId, itemId)
        ),
        get$: () => this._state.packPriceTypes.getValue$(),
        loading$: () => this._state.packPriceTypes.isInProgress$(),
        update: (channelId: number, packId: number, itemId: number, req: PutPackPriceTypes) => StateManager.inProgress(
            this._state.packPriceTypes,
            this._api.putPackPriceTypes(channelId, packId, itemId, req)
        ),
        clear: () => this._state.packPriceTypes.setValue(null),
        error$: () => this._state.packPriceTypes.getError$()
    });

    readonly packTexts = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packTexts,
            this._api.getPackTexts(channelId, packId)
        ),
        get$: () => this._state.packTexts.getValue$(),
        save: (channelId: number, packId: number, contents: EventChannelContentText[]) => StateManager.inProgress(
            this._state.packTexts,
            this._api.postPackTexts(channelId, packId, contents)
        ),
        loading$: () => this._state.packTexts.isInProgress$(),
        clear: () => this._state.packTexts.setValue(null)
    });

    readonly packImages = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packImages,
            this._api.getPackImages(channelId, packId)
        ),
        get$: () => this._state.packImages.getValue$(),
        save: (channelId: number, packId: number, contents: EventChannelContentImageRequest[]) => StateManager.inProgress(
            this._state.packImages,
            this._api.postPackImages(channelId, packId, contents)
        ),
        delete: (channelId: number, packId: number, imagesToDelete: EventChannelContentImageRequest[]) => StateManager.inProgress(
            this._state.packImages,
            zip(...imagesToDelete.map(request =>
                this._api.deletePackImage(channelId, packId, request.language, request.type, request.position)
            )).pipe(
                switchMap(() => of(null)),
                catchError(() => of(null))
            )),
        loading$: () => this._state.packImages.isInProgress$(),
        clear: () => this._state.packImages.setValue(null)
    });

    readonly packTicketTexts = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packTicketTexts,
            this._api.getPackTicketTexts(channelId, packId)
        ),
        get$: () => this._state.packTicketTexts.getValue$(),
        save: (channelId: number, packId: number, contents: TicketContentText[]) => StateManager.inProgress(
            this._state.packTicketTexts,
            this._api.postPackTicketTexts(channelId, packId, contents)
        ),
        loading$: () => this._state.packTicketTexts.isInProgress$(),
        clear: () => this._state.packTicketTexts.setValue(null)
    });

    readonly packTicketImages = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packTicketImages,
            this._api.getPackTicketImages(channelId, packId)
        ),
        get$: () => this._state.packTicketImages.getValue$(),
        save: (channelId: number, packId: number, contents: TicketContentImageRequest[]) => StateManager.inProgress(
            this._state.packTicketImages,
            this._api.postPackTicketImages(channelId, packId, contents)
        ),
        delete: (channelId: number, packId: number, imagesToDelete: TicketContentImageRequest[]) => StateManager.inProgress(
            this._state.packTicketImages,
            zip(...imagesToDelete.map(request =>
                this._api.deletePackTicketImage(channelId, packId, request.language, request.type)
            )).pipe(
                switchMap(() => of(null)),
                catchError(() => of(null))
            )),
        loading$: () => this._state.packTicketImages.isInProgress$(),
        clear: () => this._state.packTicketImages.setValue(null)
    });

    readonly packPrinterTexts = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packPrinterTexts,
            this._api.getPackPrinterTexts(channelId, packId)
        ),
        get$: () => this._state.packPrinterTexts.getValue$(),
        save: (channelId: number, packId: number, contents: TicketContentText[]) => StateManager.inProgress(
            this._state.packPrinterTexts,
            this._api.postPackPrinterTexts(channelId, packId, contents)
        ),
        loading$: () => this._state.packPrinterTexts.isInProgress$(),
        clear: () => this._state.packPrinterTexts.setValue(null)
    });

    readonly packPrinterImages = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packPrinterImages,
            this._api.getPackPrinterImages(channelId, packId)
        ),
        get$: () => this._state.packPrinterImages.getValue$(),
        save: (channelId: number, packId: number, contents: TicketContentImageRequest[]) => StateManager.inProgress(
            this._state.packPrinterImages,
            this._api.postPackPrinterImages(channelId, packId, contents)
        ),
        delete: (channelId: number, packId: number, imagesToDelete: TicketContentImageRequest[]) => StateManager.inProgress(
            this._state.packPrinterImages,
            zip(...imagesToDelete.map(request =>
                this._api.deletePackPrinterImage(channelId, packId, request.language, request.type)
            )).pipe(
                switchMap(() => of(null)),
                catchError(() => of(null))
            )),
        loading$: () => this._state.packPrinterImages.isInProgress$(),
        clear: () => this._state.packPrinterImages.setValue(null)
    });

    readonly packPreviewLinks = Object.freeze({
        load: (channelId: number, packId: number) => StateManager.load(
            this._state.packPreviewLinks,
            this._api.getPacksPreviewLinks(channelId, packId)
        ),
        get$: () => this._state.packPreviewLinks.getValue$(),
        loading$: () => this._state.packPreviewLinks.isInProgress$(),
        clear: () => this._state.packPreviewLinks.setValue(null)
    });
}
