import { mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { ChannelCommission, ChannelPriceSimulation, ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import {
    EventChannelContentImageRequest, EventChannelContentImageType
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { ContentLinkRequest } from '@admin-clients/cpanel/shared/data-access';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize, map, take } from 'rxjs/operators';
import { EventChannelsApi } from './api/event-channels.api';
import { B2BPublishConfigurationRequest } from './models/b2b-publish-configuration.model';
import { EventChannelAttendantsLink } from './models/event-channel-attendants-link.model';
import {
    EventChannelB2bAssignations,
    PutEventChannelB2bAssignations
} from './models/event-channel-b2b-assignations.model';
import { EventChannelTicketContentFormat } from './models/event-channel-ticket-content-format.enum';
import { EventChannelTicketTemplateType } from './models/event-channel-ticket-template-type.enum';
import { EventChannelToFavoriteRequest } from './models/event-channel-to-favorite-request.model';
import { EventChannel } from './models/event-channel.model';
import { GetEventChannelsCandidatesRequest } from './models/event-channels-candidates.model';
import { EventChannelsRequest } from './models/event-channels-request.model';
import { GetEventChannelSessionLinksRequest } from './models/get-event-channel-session-links-request';
import { UpdateEventChannelsRequest } from './models/update-event-channels-request.model';
import { EventChannelsState } from './state/event-channels.state';

@Injectable()
export class EventChannelsService {
    private readonly _eventChannelsState = inject(EventChannelsState);
    private readonly _eventChannelApi = inject(EventChannelsApi);

    readonly eventChannelsList = Object.freeze({
        load: (eventId: number, request: EventChannelsRequest): void => StateManager.load(
            this._eventChannelsState.eventChannelList,
            this._eventChannelApi.getEventChannelsList(eventId, request).pipe(mapMetadata())
        ),
        loadMore: (eventId: number, request: EventChannelsRequest) =>
            StateManager.loadMore(request, this._eventChannelsState.eventChannelList,
                r => this._eventChannelApi.getEventChannelsList(eventId, r).pipe(mapMetadata())),
        getData$: () => this._eventChannelsState.eventChannelList.getValue$()
            .pipe(map(value => value?.data)),
        getMetaData$: () => this._eventChannelsState.eventChannelList.getValue$()
            .pipe(map(r => r?.metadata)),
        error$: () => this._eventChannelsState.eventChannelList.getError$(),
        inProgress$: () => this._eventChannelsState.eventChannelList.isInProgress$(),
        clear: () => this._eventChannelsState.eventChannelList.setValue(null),
        updateEventChannelStatus: (targetEventChannel: EventChannel) =>
            this._eventChannelsState.eventChannelList.getValue$().pipe(take(1))
                .subscribe(list => {
                    const listEventChannel = list?.data.find(eventChannel => eventChannel.channel.id === targetEventChannel.channel.id);
                    if (listEventChannel) {
                        listEventChannel.status = targetEventChannel.status;
                        this._eventChannelsState.eventChannelList.setValue(list);
                    }
                })
    });

    readonly eventChannel = Object.freeze({
        load: (eventId: number, channelId: number): void => StateManager.load(
            this._eventChannelsState.eventChannel,
            this._eventChannelApi.getEventChannel(eventId, channelId)
        ),
        cancelLoad: () => this._eventChannelsState.eventChannel.triggerCancellation(),
        get$: () => this._eventChannelsState.eventChannel.getValue$(),
        error$: () => this._eventChannelsState.eventChannel.getError$(),
        inProgress$: () => this._eventChannelsState.eventChannel.isInProgress$(),
        clear: () => this._eventChannelsState.eventChannel.setValue(null)
    });

    readonly eventChannelPublishedSessionLinks = Object.freeze({
        load: (request: GetEventChannelSessionLinksRequest): void => StateManager.load(
            this._eventChannelsState.eventChannelPublishedSessionLinks,
            this._eventChannelApi.getEventChannelSessionLinks(request).pipe(mapMetadata())
        ),
        getData$: () => this._eventChannelsState.eventChannelPublishedSessionLinks.getValue$()
            .pipe(map(sessionLinks => sessionLinks?.data)),
        getMetadata$: () => this._eventChannelsState.eventChannelPublishedSessionLinks.getValue$()
            .pipe(map(r => r?.metadata)),
        error$: () => this._eventChannelsState.eventChannelPublishedSessionLinks.getError$(),
        inProgress$: () => this._eventChannelsState.eventChannelPublishedSessionLinks.isInProgress$(),
        clear: () => this._eventChannelsState.eventChannelPublishedSessionLinks.setValue(null)
    });

    readonly eventChannelUnpublishedSessionLinks = Object.freeze({
        load: (request: GetEventChannelSessionLinksRequest): void => StateManager.load(
            this._eventChannelsState.eventChannelUnpublishedSessionLinks,
            this._eventChannelApi.getEventChannelSessionLinks(request).pipe(mapMetadata())
        ),
        getData$: () => this._eventChannelsState.eventChannelUnpublishedSessionLinks.getValue$()
            .pipe(map(sessionLinks => sessionLinks?.data)),
        getMetadata$: () => this._eventChannelsState.eventChannelUnpublishedSessionLinks.getValue$()
            .pipe(map(r => r?.metadata)),
        error$: () => this._eventChannelsState.eventChannelUnpublishedSessionLinks.getError$(),
        inProgress$: () => this._eventChannelsState.eventChannelUnpublishedSessionLinks.isInProgress$(),
        clear: () => this._eventChannelsState.eventChannelUnpublishedSessionLinks.setValue(null)
    });

    readonly ticketTemplates = Object.freeze({
        load: (eventId: number, channelId: number) => StateManager.load(
            this._eventChannelsState.ticketTemplate,
            this._eventChannelApi.getEventChannelTicketTemplates(eventId, channelId)),
        save: (eventId: number, channelId: number, templateId: number,
            type: EventChannelTicketTemplateType, format: EventChannelTicketContentFormat) => StateManager.inProgress(
                this._eventChannelsState.ticketTemplate,
                this._eventChannelApi.postEventChannelTicketTemplates(eventId, channelId, templateId, type, format)
            ),
        get$: () => this._eventChannelsState.ticketTemplate.getValue$(),
        error$: () => this._eventChannelsState.ticketTemplate.getError$(),
        inProgress$: () => this._eventChannelsState.ticketTemplate.isInProgress$(),
        clear: () => this._eventChannelsState.ticketTemplate.setValue(null)
    });

    readonly eventChannelsCandidatesList = Object.freeze({
        load: (eventId: number, request: GetEventChannelsCandidatesRequest): void => StateManager.load(
            this._eventChannelsState.eventChannelsCandidatesList,
            this._eventChannelApi.getEventChannelsCandidatesList(eventId, request).pipe(mapMetadata())
        ),
        getData$: () => this._eventChannelsState.eventChannelsCandidatesList.getValue$()
            .pipe(map(value => value?.data)),
        getMetaData$: () => this._eventChannelsState.eventChannelsCandidatesList.getValue$()
            .pipe(map(r => r?.metadata)),
        inProgress$: () => this._eventChannelsState.eventChannelsCandidatesList.isInProgress$(),
        clear: () => this._eventChannelsState.eventChannelsCandidatesList.setValue(null)
    });

    readonly b2bPublishConfiguration = Object.freeze({
        load: (eventId: number, channelId: number, venueTemplateId: number): void => StateManager.load(
            this._eventChannelsState.b2bPublishConfiguration,
            this._eventChannelApi.getB2bPublishConfiguration(eventId, channelId, venueTemplateId)
        ),
        save: (eventId: number, channelId: number, venueTemplateId: number, req: B2BPublishConfigurationRequest) => StateManager.inProgress(
            this._eventChannelsState.b2bPublishConfiguration,
            this._eventChannelApi.putB2bPublishConfiguration(eventId, channelId, venueTemplateId, req)
        ),
        get$: () => this._eventChannelsState.b2bPublishConfiguration.getValue$(),
        error$: () => this._eventChannelsState.b2bPublishConfiguration.getError$(),
        inProgress$: () => this._eventChannelsState.b2bPublishConfiguration.isInProgress$(),
        clear: () => this._eventChannelsState.b2bPublishConfiguration.setValue(null)
    });

    readonly eventChannelSquareImages = Object.freeze({
        load: (eventId: number, channelId: number): void => StateManager.load(
            this._eventChannelsState.eventChannelSquareImages,
            this._eventChannelApi.getEventChannelContentSquareImages(eventId, channelId)
        ),
        save: (eventId: number, channelId: number, images: EventChannelContentImageRequest[]): Observable<void> => StateManager.inProgress(
            this._eventChannelsState.eventChannelSquareImages,
            this._eventChannelApi.postEventChannelContentSquareImages(eventId, channelId, images)
        ),
        delete: (
            eventId: number, channelId: number, language: string, type: EventChannelContentImageType, position: number
        ): Observable<void> => StateManager.inProgress(
            this._eventChannelsState.eventChannelSquareImages,
            this._eventChannelApi.deleteEventChannelContentSquareImage(eventId, channelId, language, type, position)
        ),
        get$: () => this._eventChannelsState.eventChannelSquareImages.getValue$(),
        error$: () => this._eventChannelsState.eventChannelSquareImages.getError$(),
        inProgress$: () => this._eventChannelsState.eventChannelSquareImages.isInProgress$(),
        clear: () => this._eventChannelsState.eventChannelSquareImages.setValue([])
    });

    readonly eventSessionSquareImages = Object.freeze({
        load: (eventId: number, channelId: number, sessionId: number): void => StateManager.load(
            this._eventChannelsState.eventSessionSquareImages,
            this._eventChannelApi.getEventSessionContentSquareImages(eventId, channelId, sessionId)
        ),
        save: (eventId: number, channelId: number, sessionId: number, images: EventChannelContentImageRequest[]): Observable<void> =>
            StateManager.inProgress(
                this._eventChannelsState.eventSessionSquareImages,
                this._eventChannelApi.postEventSessionContentSquareImages(eventId, channelId, sessionId, images)
            ),
        delete: (
            eventId: number, channelId: number, sessionId: number, language: string, type: EventChannelContentImageType, position: number
        ): Observable<void> => StateManager.inProgress(
            this._eventChannelsState.eventSessionSquareImages,
            this._eventChannelApi.deleteEventSessionContentSquareImage(eventId, channelId, sessionId, language, type, position)
        ),
        deleteAll: (eventId: number, channelId: number, sessionId: number): Observable<void> => StateManager.inProgress(
            this._eventChannelsState.eventSessionSquareImages,
            this._eventChannelApi.deleteAllEventSessionContentSquareImages(eventId, channelId, sessionId)
        ),
        get$: () => this._eventChannelsState.eventSessionSquareImages.getValue$(),
        error$: () => this._eventChannelsState.eventSessionSquareImages.getError$(),
        inProgress$: () => this._eventChannelsState.eventSessionSquareImages.isInProgress$(),
        clear: () => this._eventChannelsState.eventSessionSquareImages.setValue([])
    });

    readonly eventSessionSquareImagesConfig = Object.freeze({
        load: (eventId: number, channelId: number): void => StateManager.load(
            this._eventChannelsState.eventSessionSquareImagesConfig,
            this._eventChannelApi.getEventSessionContentSquareImagesConfig(eventId, channelId)
        ),
        get$: () => this._eventChannelsState.eventSessionSquareImagesConfig.getValue$(),
        error$: () => this._eventChannelsState.eventSessionSquareImagesConfig.getError$(),
        inProgress$: () => this._eventChannelsState.eventSessionSquareImagesConfig.isInProgress$(),
        clear: () => this._eventChannelsState.eventSessionSquareImagesConfig.setValue(null)
    });

    addEventChannel(eventId, channelId: number): Observable<void> {
        this._eventChannelsState.eventChannelList.setInProgress(true);
        return this._eventChannelApi.postEventChannel(eventId, channelId)
            .pipe(finalize(() => this._eventChannelsState.eventChannelList.setInProgress(false)));
    }

    updateEventChannel(eventId: number, channelId: number, request: UpdateEventChannelsRequest): Observable<void> {
        this._eventChannelsState.eventChannel.setInProgress(true);
        return this._eventChannelApi.putEventChannel(eventId, channelId, request)
            .pipe(finalize(() => this._eventChannelsState.eventChannel.setInProgress(false)));
    }

    requestEventChannel(eventId: number, eventChannelId: number): Observable<void> {
        return this._eventChannelApi.postRequestEventChannel(eventId, eventChannelId);
    }

    deleteEventChannel(eventId: number, channelId: number): Observable<void> {
        this._eventChannelsState.eventChannelList.setInProgress(true);
        return this._eventChannelApi.deleteEventChannel(eventId, channelId)
            .pipe(finalize(() => this._eventChannelsState.eventChannelList.setInProgress(false)));
    }

    loadEventChannelSurcharges(eventId: number, channelId: number): void {
        this._eventChannelsState.eventChannelSurcharges.setError(null);
        this._eventChannelsState.eventChannelSurcharges.setInProgress(true);
        this._eventChannelApi.getEventChannelSurcharges(eventId, channelId)
            .pipe(
                catchError(error => {
                    this._eventChannelsState.eventChannelSurcharges.setError(error);
                    return of(null);
                }),
                finalize(() => this._eventChannelsState.eventChannelSurcharges.setInProgress(false))
            )
            .subscribe(surcharges =>
                this._eventChannelsState.eventChannelSurcharges.setValue(surcharges)
            );
    }

    getEventChannelSurcharges$(): Observable<ChannelSurcharge[]> {
        return this._eventChannelsState.eventChannelSurcharges.getValue$();
    }

    getEventChannelSurchargesError$(): Observable<HttpErrorResponse> {
        return this._eventChannelsState.eventChannelSurcharges.getError$();
    }

    isEventChannelSurchargesLoading$(): Observable<boolean> {
        return this._eventChannelsState.eventChannelSurcharges.isInProgress$();
    }

    isEventChannelSurchargesSaving$(): Observable<boolean> {
        return this._eventChannelsState.eventChannelSurchargesSaving.isInProgress$();
    }

    clearEventChannelSurcharges(): void {
        this._eventChannelsState.eventChannelSurcharges.setValue(null);
    }

    saveChannelSurcharges(eventId: number, channelId: number, surcharges: ChannelSurcharge[]): Observable<void> {
        this._eventChannelsState.eventChannelSurcharges.setError(null);
        this._eventChannelsState.eventChannelSurchargesSaving.setInProgress(true);
        return this._eventChannelApi.postEventChannelSurcharges(eventId, channelId, surcharges)
            .pipe(
                catchError(error => {
                    this._eventChannelsState.eventChannelSurcharges.setError(error);
                    throw error;
                }),
                finalize(() => this._eventChannelsState.eventChannelSurchargesSaving.setInProgress(false))
            );
    }

    loadEventChannelChannelSurcharges(eventId: number, channelId: number): void {
        this._eventChannelsState.eventChannelChannelSurcharges.setError(null);
        this._eventChannelsState.eventChannelChannelSurcharges.setInProgress(true);
        this._eventChannelApi.getEventChannelChannelSurcharges(eventId, channelId)
            .pipe(
                catchError(error => {
                    this._eventChannelsState.eventChannelChannelSurcharges.setError(error);
                    return of(null);
                }),
                finalize(() => this._eventChannelsState.eventChannelChannelSurcharges.setInProgress(false))
            )
            .subscribe(surcharges =>
                this._eventChannelsState.eventChannelChannelSurcharges.setValue(surcharges)
            );
    }

    getEventChannelChannelSurcharges$(): Observable<ChannelSurcharge[]> {
        return this._eventChannelsState.eventChannelChannelSurcharges.getValue$();
    }

    getEventChannelChannelSurchargesError$(): Observable<HttpErrorResponse> {
        return this._eventChannelsState.eventChannelChannelSurcharges.getError$();
    }

    isEventChannelChannelSurchargesLoading$(): Observable<boolean> {
        return this._eventChannelsState.eventChannelChannelSurcharges.isInProgress$();
    }

    clearEventChannelChannelSurcharges(): void {
        this._eventChannelsState.eventChannelChannelSurcharges.setValue(null);
    }

    loadEventChannelPriceSimulation(eventId: number, channelId: number): void {
        this._eventChannelsState.eventChannelPriceSimulation.setError(null);
        this._eventChannelsState.eventChannelPriceSimulation.setInProgress(true);
        this._eventChannelApi.getEventChannelPriceSimulation(eventId, channelId)
            .pipe(
                catchError(error => {
                    this._eventChannelsState.eventChannelPriceSimulation.setError(error);
                    return of(null);
                }),
                finalize(() => this._eventChannelsState.eventChannelPriceSimulation.setInProgress(false))
            )
            .subscribe(surcharges =>
                this._eventChannelsState.eventChannelPriceSimulation.setValue(surcharges)
            );
    }

    getEventChannelPriceSimulation$(): Observable<ChannelPriceSimulation[]> {
        return this._eventChannelsState.eventChannelPriceSimulation.getValue$();
    }

    getEventChannelPriceSimulationError$(): Observable<HttpErrorResponse> {
        return this._eventChannelsState.eventChannelPriceSimulation.getError$();
    }

    isEventChannelPriceSimulationLoading$(): Observable<boolean> {
        return this._eventChannelsState.eventChannelPriceSimulation.isInProgress$();
    }

    clearEventChannelPriceSimulation(): void {
        this._eventChannelsState.eventChannelPriceSimulation.setValue(null);
    }

    loadEventChannelCommissions(eventId: number, channelId: number): void {
        this._eventChannelsState.eventChannelCommissions.setError(null);
        this._eventChannelsState.eventChannelCommissions.setInProgress(true);
        this._eventChannelApi.getEventChannelCommissions(eventId, channelId)
            .pipe(
                catchError(error => {
                    this._eventChannelsState.eventChannelCommissions.setError(error);
                    return of(null);
                }),
                finalize(() => this._eventChannelsState.eventChannelCommissions.setInProgress(false))
            )
            .subscribe(commissions =>
                this._eventChannelsState.eventChannelCommissions.setValue(commissions)
            );
    }

    isEventChannelCommissionsLoading$(): Observable<boolean> {
        return this._eventChannelsState.eventChannelCommissions.isInProgress$();
    }

    getEventChannelCommissions$(): Observable<ChannelCommission[]> {
        return this._eventChannelsState.eventChannelCommissions.getValue$();
    }

    clearEventChannelCommissions(): void {
        this._eventChannelsState.eventChannelCommissions.setValue(null);
    }

    getContentLinkRequest$(): Observable<ContentLinkRequest[]> {
        return this._eventChannelsState.eventChannelLinks.getValue$();
    }

    getContentLinkRequestError$(): Observable<HttpErrorResponse> {
        return this._eventChannelsState.eventChannelLinks.getError$();
    }

    clearContentLinkRequest(): void {
        this._eventChannelsState.eventChannelLinks.setValue(null);
    }

    isContentLinkRequestLoading$(): Observable<boolean> {
        return this._eventChannelsState.eventChannelLinks.isInProgress$();
    }

    loadContentLinkRequest(eventId: number, channelId: number): void {
        this._eventChannelsState.eventChannelLinks.setError(null);
        this._eventChannelsState.eventChannelLinks.setInProgress(true);
        this._eventChannelApi.getContentLinkRequest(eventId, channelId)
            .pipe(
                catchError(error => {
                    this._eventChannelsState.eventChannelLinks.setError(error);
                    return of(null);
                }),
                finalize(() => this._eventChannelsState.eventChannelLinks.setInProgress(false))
            )
            .subscribe(communication =>
                this._eventChannelsState.eventChannelLinks.setValue(communication)
            );
    }

    downloadTicketPdfPreview$(eventId: number, channelId: number, language: string): Observable<{ url: string }> {
        this._eventChannelsState.ticketPdfPreviewDownloading.setInProgress(true);
        return this._eventChannelApi.downloadTicketPdfPreview(eventId, channelId, language).pipe(
            finalize(() => this._eventChannelsState.ticketPdfPreviewDownloading.setInProgress(false))
        );
    }

    isTicketPdfPreviewDownloading$(): Observable<boolean> {
        return this._eventChannelsState.ticketPdfPreviewDownloading.isInProgress$();
    }

    // CHANNEL EDIT ATTENDANTS URLS
    loadEventChannelAttendantsLinks(eventId: number, channelId: number): void {
        this._eventChannelsState.eventChannelAttendantsLinks.setError(null);
        this._eventChannelsState.eventChannelAttendantsLinks.setInProgress(true);
        this._eventChannelApi.getEventChannelAttendantsLinks(eventId, channelId)
            .pipe(
                catchError(error => {
                    this._eventChannelsState.eventChannelAttendantsLinks.setError(error);
                    return of(null);
                }),
                finalize(() => this._eventChannelsState.eventChannelAttendantsLinks.setInProgress(false))
            )
            .subscribe(links =>
                this._eventChannelsState.eventChannelAttendantsLinks.setValue(links)
            );
    }

    getEventChannelAttendantsLinks$(): Observable<EventChannelAttendantsLink[]> {
        return this._eventChannelsState.eventChannelAttendantsLinks.getValue$();
    }

    getEventChannelAttendantsLinksError$(): Observable<HttpErrorResponse> {
        return this._eventChannelsState.eventChannelAttendantsLinks.getError$();
    }

    clearEventChannelAttendantsLinks(): void {
        this._eventChannelsState.eventChannelAttendantsLinks.setValue(null);
    }

    isEventChannelAttendantsLinksLoading$(): Observable<boolean> {
        return this._eventChannelsState.eventChannelAttendantsLinks.isInProgress$();
    }

    // ADD CHANNEL TO FAVORITE
    updateFavoriteChannel(eventId: number, channelId: number, request: EventChannelToFavoriteRequest): Observable<void> {
        this._eventChannelsState.channelToFavoriteSaving.setInProgress(true);
        return this._eventChannelApi.putFavoriteChannel(eventId, channelId, request)
            .pipe(finalize(() => this._eventChannelsState.channelToFavoriteSaving.setInProgress(false)));
    }

    isChannelToFavoriteSaving(): Observable<boolean> {
        return this._eventChannelsState.channelToFavoriteSaving.isInProgress$();
    }

    // B2B
    loadB2bAssignations(eventId: number, channelId: number): void {
        this._eventChannelsState.b2bAssignations.setInProgress(true);
        this._eventChannelsState.b2bAssignations.setError(null);
        this._eventChannelApi.getB2bAssignations(eventId, channelId)
            .pipe(
                catchError(error => {
                    this._eventChannelsState.b2bAssignations.setError(error);
                    throw error; // propagates error to subscriber error callback
                }),
                finalize(() => this._eventChannelsState.b2bAssignations.setInProgress(false))
            )
            .subscribe(assignations => this._eventChannelsState.b2bAssignations.setValue(assignations));
    }

    clearB2bAssignations(): void {
        return this._eventChannelsState.b2bAssignations.setValue(null);
    }

    isB2bAssignationsInProgress$(): Observable<boolean> {
        return this._eventChannelsState.b2bAssignations.isInProgress$();
    }

    getB2bAssignations$(): Observable<EventChannelB2bAssignations> {
        return this._eventChannelsState.b2bAssignations.getValue$();
    }

    updateB2bAssignations(eventId: number, channelId: number, request: PutEventChannelB2bAssignations): Observable<void> {
        this._eventChannelsState.b2bAssignations.setInProgress(true);
        return this._eventChannelApi.putB2bAssignations(eventId, channelId, request)
            .pipe(finalize(() => this._eventChannelsState.b2bAssignations.setInProgress(false)));
    }

}
