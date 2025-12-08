import { getListData, getMetadata, StateManager } from '@OneboxTM/utils-state';
import { ChannelSurcharge, ChannelPriceSimulation, ChannelCommission } from '@admin-clients/cpanel/channels/data-access';
import { ChannelB2bAssignations } from '@admin-clients/cpanel/promoters/shared/data-access';
import { Id, PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { catchError, filter, finalize, map, take, takeUntil } from 'rxjs/operators';
import { SeasonTicketChannelsApi } from './api/season-ticket-channels.api';
import { SeasonTicketChannelLink } from './models/season-ticket-channel-content.model';
import { SeasonTicketChannel } from './models/season-ticket-channel.model';
import { UpdateSeasonTicketChannelsRequest } from './models/update-season-ticket-channels-request.model';
import { SeasonTicketChannelsState } from './state/season-ticket-channels.state';

@Injectable({
    providedIn: 'root'
})
export class SeasonTicketChannelsService {
    readonly #state = inject(SeasonTicketChannelsState);
    readonly #api = inject(SeasonTicketChannelsApi);

    readonly #loadChannelStarted = new Subject<void>();

    readonly channelSurcharges = Object.freeze({
        load: (id: number, channelId: number) => StateManager.load(
            this.#state.channelSurcharges,
            this.#api.getSeasonTicketChannelChannelSurcharges(id, channelId)
        ),
        get$: () => this.#state.channelSurcharges.getValue$(),
        loading$: () => this.#state.channelSurcharges.isInProgress$(),
        clear: () => this.#state.channelSurcharges.setValue(null)
    });

    readonly seasonTicketChannelList = Object.freeze({
        load: (seasonTicketId: number, request: PageableFilter) => StateManager.load(
            this.#state.seasonTicketChannelList,
            this.#api.getSeasonTicketChannelsList(seasonTicketId, request)
        ),
        getData$: () => this.#state.seasonTicketChannelList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.seasonTicketChannelList.getValue$().pipe(getMetadata()),
        add: (seasonTicketId: number, channelId: number) => StateManager.inProgress(
            this.#state.seasonTicketChannelList,
            this.#api.postSeasonTicketChannel(seasonTicketId, channelId)
        ),
        update: (selectedSeasonTicketChannel: SeasonTicketChannel) =>
            this.updateSeasonTicketChannelListSelectedStatus(selectedSeasonTicketChannel),
        delete: (seasonTicketId: number, channelId: number) => StateManager.inProgress(
            this.#state.seasonTicketChannelList,
            this.#api.deleteSeasonTicketChannel(seasonTicketId, channelId)
        ),
        loading$: () => this.#state.seasonTicketChannelList.isInProgress$(),
        clear: () => this.#state.seasonTicketChannelList.setValue(null)
    });

    readonly b2bAssignations = Object.freeze({
        load: (id: number, channelId: number) => StateManager.load(
            this.#state.b2bAssignations,
            this.#api.getB2bAssignations(id, channelId)
        ),
        get$: () => this.#state.b2bAssignations.getValue$(),
        update: (seasonTicketId: number, channelId: number, request: ChannelB2bAssignations<Id>) => StateManager.inProgress(
            this.#state.b2bAssignations,
            this.#api.putB2bAssignations(seasonTicketId, channelId, request)
        ),
        loading$: () => this.#state.b2bAssignations.isInProgress$(),
        clear: () => this.#state.b2bAssignations.setValue(null)
    });

    updateSeasonTicketChannelListSelectedStatus(selectedSeasonTicketChannel: SeasonTicketChannel): void {
        this.#state.seasonTicketChannelList.setInProgress(true);
        this.#state.seasonTicketChannelList.getValue$()
            .pipe(
                filter(channels => !!channels?.data),
                take(1),
                finalize(() => this.#state.seasonTicketChannelList.setInProgress(false))
            )
            .subscribe(channels => {
                channels.data = channels.data.map(seasonTicketChannel => {
                    if (seasonTicketChannel.channel.id === selectedSeasonTicketChannel.channel.id) {
                        seasonTicketChannel.status = selectedSeasonTicketChannel.status;
                    }
                    return seasonTicketChannel;
                });
                this.#state.seasonTicketChannelList.setValue(channels);
            });
    }

    loadSeasonTicketChannel(seasonTicketChannelId: number, channelId: number, keepPreviousChannel = false): void {
        if (!keepPreviousChannel) {
            this.clearSeasonTicketChannel();
        }
        this.#loadChannelStarted.next();
        this.#state.setSeasonTicketChannelInProgress(true);
        this.#state.setSeasonTicketChannelError(null);
        this.#api.getSeasonTicketChannel(seasonTicketChannelId, channelId)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketChannelError(error);
                    return of(null);
                }),
                finalize(() =>
                    this.#state.setSeasonTicketChannelInProgress(false)
                ),
                takeUntil(this.#loadChannelStarted)
            )
            .subscribe(channel =>
                this.#state.setSeasonTicketChannel(channel)
            );
    }

    clearSeasonTicketChannel(): void {
        this.#state.setSeasonTicketChannel(null);
    }

    getSeasonTicketChannel$(): Observable<SeasonTicketChannel> {
        return this.#state.getSeasonTicketChannel$();
    }

    updateSeasonTicketChannel(seasonTicketId: number, channelId: number, request: UpdateSeasonTicketChannelsRequest): Observable<void> {
        this.#state.setSeasonTicketChannelInProgress(true);
        return this.#api.updateSeasonTicketChannel(seasonTicketId, channelId, request)
            .pipe(finalize(() => this.#state.setSeasonTicketChannelInProgress(false))
            );
    }

    requestSeasonTicketChannel(seasonTicketId: number, seasonTicketChannelId: number): Observable<void> {
        return this.#api.postRequestSeasonTicketChannel(seasonTicketId, seasonTicketChannelId);
    }

    isSeasonTicketChannelInProgress$(): Observable<boolean> {
        return this.#state.isSeasonTicketChannelInProgress$();
    }

    isSeasonTicketChannelLinkLoading$(): Observable<boolean> {
        return this.#state.isSeasonTicketChannelLinkLoading$();
    }

    isTicketPdfPreviewDownloading$(): Observable<boolean> {
        return this.#state.isTicketPdfPreviewDownloading$();
    }

    isSeasonTicketChannelRequestAccepted$(): Observable<boolean> {
        return this.#state.getSeasonTicketChannel$().pipe(
            filter(seasonTicketChannel => !!seasonTicketChannel),
            map(seasonTicketChannel => seasonTicketChannel.status.request === 'ACCEPTED')
        );
    }

    loadSeasonTicketChannelLink(seasonTicketId: number, channelId: number): void {
        this.#state.setSeasonTicketChannelLinkError(null);
        this.#state.setSeasonTicketChannelLinkLoading(true);
        this.#api.getSeasonTicketChannelLink(seasonTicketId, channelId)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketChannelLinkError(error);
                    return of(null);
                }),
                finalize(() =>
                    this.#state.setSeasonTicketChannelLinkLoading(false)
                )
            )
            .subscribe(communication =>
                this.#state.setSeasonTicketChannelLink(communication)
            );
    }

    getSeasonTicketChannelLanguages$(): Observable<SeasonTicketChannel['settings']['languages']> {
        return this.getSeasonTicketChannel$()
            .pipe(
                filter(eventChannel => !!eventChannel),
                map(eventChannel => eventChannel.settings.languages)
            );
    }

    clearSeasonTicketChannelLink(): void {
        this.#state.setSeasonTicketChannelLink(null);
    }

    downloadTicketPdfPreview$(seasonTicketId: number, channelId: number, language: string): Observable<{ url: string }> {
        this.#state.setTicketPdfPreviewDownloading(true);
        return this.#api.downloadTicketPdfPreview(seasonTicketId, channelId, language)
            .pipe(finalize(() => this.#state.setTicketPdfPreviewDownloading(false))
            );
    }

    getSeasonTicketChannelLink$(): Observable<SeasonTicketChannelLink> {
        return this.#state.getSeasonTicketChannelLink$();
    }

    getSeasonTicketChannelLinkError$(): Observable<HttpErrorResponse> {
        return this.#state.getSeasonTicketChannelLinkError$();
    }

    loadSeasonTicketChannelSurcharges(seasonTicketId: number, channelId: number): void {
        this.#state.setSeasonTicketChannelSurchargesError(null);
        this.#state.setSeasonTicketChannelSurchargesLoading(true);
        this.#api.getSeasonTicketChannelSurcharges(seasonTicketId, channelId)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketChannelSurchargesError(error);
                    return of(null);
                }),
                finalize(() =>
                    this.#state.setSeasonTicketChannelSurchargesLoading(false)
                )
            )
            .subscribe(surcharges =>
                this.#state.setSeasonTicketChannelSurcharges(surcharges)
            );
    }

    getSeasonTicketChannelChannelSurcharges$(): Observable<ChannelSurcharge[]> {
        return this.#state.getSeasonTicketChannelChannelSurcharges$();
    }

    getSeasonTicketChannelChannelSurchargesError$(): Observable<HttpErrorResponse> {
        return this.#state.getSeasonTicketChannelChannelSurchargesError$();
    }

    isSeasonTicketChannelChannelSurchargesLoading$(): Observable<boolean> {
        return this.#state.isSeasonTicketChannelChannelSurchargesLoading$();
    }

    clearSeasonTicketChannelChannelSurcharges(): void {
        this.#state.setSeasonTicketChannelChannelSurcharges(null);
    }

    clearSeasonTicketChannelSurcharges(): void {
        this.#state.setSeasonTicketChannelSurcharges(null);
    }

    getSeasonTicketChannelPriceSimulation$(): Observable<ChannelPriceSimulation[]> {
        return this.#state.getSeasonTicketChannelPriceSimulation$();
    }

    getSeasonTicketChannelPriceSimulationError$(): Observable<HttpErrorResponse> {
        return this.#state.getSeasonTicketChannelPriceSimulationError$();
    }

    isSeasonTicketChannelPriceSimulationLoading$(): Observable<boolean> {
        return this.#state.isSeasonTicketChannelPriceSimulationLoading$();
    }

    clearSeasonTicketChannelPriceSimulation(): void {
        this.#state.setSeasonTicketChannelPriceSimulation(null);
    }

    loadSeasonTicketChannelPriceSimulation(seasonTicketId: number, channelId: number): void {
        this.#state.setSeasonTicketChannelPriceSimulation(null);
        this.#state.setSeasonTicketChannelPriceSimulationLoading(true);
        this.#api.getSeasonTicketChannelPriceSimulation(seasonTicketId, channelId)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketChannelPriceSimulationError(
                        error
                    );
                    return of(null);
                }),
                finalize(() =>
                    this.#state.setSeasonTicketChannelPriceSimulationLoading(
                        false
                    )
                )
            )
            .subscribe(surcharges =>
                this.#state.setSeasonTicketChannelPriceSimulation(surcharges)
            );
    }

    getSeasonTicketChannelSurcharges$(): Observable<ChannelSurcharge[]> {
        return this.#state.getSeasonTicketChannelSurcharges$();
    }

    getSeasonTicketChannelSurchargesError$(): Observable<HttpErrorResponse> {
        return this.#state.getSeasonTicketChannelSurchargesError$();
    }

    isSeasonTicketChannelSurchargesLoading$(): Observable<boolean> {
        return this.#state.isSeasonTicketChannelSurchargesLoading$();
    }

    isSeasonTicketChannelSurchargesSaving$(): Observable<boolean> {
        return this.#state.isSeasonTicketChannelSurchargesSaving$();
    }

    saveSeasonTicketChannelSurcharges(seasonTicketId: number, channelId: number, surcharges: ChannelSurcharge[]): Observable<any> {
        this.#state.setSeasonTicketChannelSurchargesError(null);
        this.#state.setSeasonTicketChannelSurchargesSaving(true);
        return this.#api.postSeasonTicketChannelSurcharges(seasonTicketId, channelId, surcharges)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketChannelSurchargesError(error);
                    throw error;
                }),
                finalize(() =>
                    this.#state.setSeasonTicketChannelSurchargesSaving(false)
                )
            );
    }

    getSeasonTicketChannelCommissions$(): Observable<ChannelCommission[]> {
        return this.#state.getSeasonTicketChannelCommissions$();
    }

    clearSeasonTicketChannelCommissions(): void {
        this.#state.setSeasonTicketChannelCommissions(null);
    }

    isSeasonTicketChannelCommissionsLoading$(): Observable<boolean> {
        return this.#state.isSeasonTicketChannelCommissionsLoading$();
    }

    loadSeasonTicketChannelCommissions(seasonTicketId: number, channelId: number): void {
        this.#state.setSeasonTicketChannelCommissionsError(null);
        this.#state.setSeasonTicketChannelCommissionsLoading(true);
        this.#api.getSeasonTicketChannelCommissions(seasonTicketId, channelId)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketChannelCommissionsError(error);
                    return of(null);
                }),
                finalize(() =>
                    this.#state.setSeasonTicketChannelCommissionsLoading(false)
                )
            )
            .subscribe(commissions =>
                this.#state.setSeasonTicketChannelCommissions(commissions)
            );
    }

}
