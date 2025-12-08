import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { ChannelContent, ChannelHistoricalContent, ChannelPurchaseContentImage, ChannelPurchaseContentImageType,
    ChannelPurchaseContentText, ChannelTicketContentFormat, ChannelTicketContentImage, ChannelTicketContentImageType,
    ChannelTicketContentText, PutChannelPurchaseContentImage, PutChannelTicketContentImage
} from '@admin-clients/cpanel/channels/communication/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of, zip } from 'rxjs';
import { catchError, finalize, map, switchMap } from 'rxjs/operators';
import { ChannelsExtendedApi } from './api/channels-extended.api';
import { ChannelCancellationServices, PutChannelCancellationServices } from './models/cancellation-services.model';
import { ChannelEvent } from './models/channel-event.model';
import { ChannelExternalTool, ChannelExternalToolName } from './models/channel-external-tool.model';
import { ChannelGatewayConfigRequest } from './models/channel-gateway-config-request.model';
import { ChannelGateway } from './models/channel-gateway.model';
import { ChannelPurchaseConfig } from './models/channel-purchase-config.model';
import { ChannelSessionsFilter } from './models/channel-session.model';
import { ChannelSuggestionType } from './models/channel-suggestion-type';
import { TextContent } from './models/channel-text-content';
import { EmailServerConf } from './models/email-server-conf.model';
import { EmailServerTestRequest } from './models/email-server-test-request.model';
import { GetChannelEventsRequest } from './models/get-channel-events-request.model';
import { GetChannelSuggestionsRequest } from './models/get-channel-suggestions-request.model';
import { NotificationEmailTemplate } from './models/notification-email-template.model';
import { PostChannelContentsCloneRequest } from './models/post-channel-contents-clone-request.model';
import { PostChannelContentsCloneResponseItem } from './models/post-channel-contents-clone-response.model';
import { PostChannelGateway } from './models/post-channel-gateway.model';
import { PostChannelSuggestionReq } from './models/post-channel-suggestion-request.model';
import { PutChannelEventRequest } from './models/put-channel-events-request.model';
import { ChannelsExtendedState } from './state/channels-extended.state';

@Injectable({ providedIn: 'root' })
/**
 * @deprecated Try to use ChannelsService instead.
 */
export class ChannelsExtendedService {
    private readonly _channelsApi = inject(ChannelsExtendedApi);
    private readonly _channelsState = inject(ChannelsExtendedState);

    readonly externalTools = Object.freeze({
        load: (channelId: number): void => StateManager.load(
            this._channelsState.externalTools,
            this._channelsApi.getExternalTools(channelId)
        ),
        get$: () => this._channelsState.externalTools.getValue$(),
        loading$: () => this._channelsState.externalTools.isInProgress$(),
        update: (channelId: number, toolName: string, toolConfig: ChannelExternalTool) =>
            StateManager.inProgress(
                this._channelsState.externalTools,
                this._channelsApi.putExternalTool(channelId, toolName, toolConfig)
            ),
        resetDatalayer: (channelId: number, externalTool: ChannelExternalToolName) =>
            StateManager.inProgress(
                this._channelsState.externalTools,
                this._channelsApi.postDatalayerReset(channelId, externalTool)
            )
    });

    readonly gatewayConfiguration = Object.freeze({
        load: (channelId: string, gatewayId: string, configId: string): void => StateManager.load(
            this._channelsState.gatewayConfiguration,
            this._channelsApi.getGatewayConfig(channelId, gatewayId, configId)
        ),
        get$: () => this._channelsState.gatewayConfiguration.getValue$(),
        loading$: () => this._channelsState.gatewayConfiguration.isInProgress$(),
        create: (channelId: string, gatewayId: string, paymentMethodConfig: ChannelGatewayConfigRequest) => StateManager.inProgress(
            this._channelsState.gatewayConfiguration,
            this._channelsApi.postChannelGatewayConfig(channelId, gatewayId, paymentMethodConfig)
        ),
        save: (channelId: string, gatewayId: string, configId: string, gatewayConfig: ChannelGatewayConfigRequest) =>
            StateManager.inProgress(
                this._channelsState.gatewayConfiguration,
                this._channelsApi.putChannelGatewayConfig(channelId, gatewayId, configId, gatewayConfig)
            ),
        delete: (channelId: number, gatewayId: string, configId: string) => StateManager.inProgress(
            this._channelsState.gatewayConfiguration,
            this._channelsApi.deleteChannelGatewayConfig(channelId, gatewayId, configId)
        ),
        clear: () => this._channelsState.gatewayConfiguration.setValue(null)
    });

    readonly channelSuggestions = Object.freeze({
        load: (channelId: number, filters?: GetChannelSuggestionsRequest): void => StateManager.load(
            this._channelsState.channelSuggestions,
            this._channelsApi.getChannelSuggestions(channelId, filters).pipe(mapMetadata())
        ),
        get$: () => this._channelsState.channelSuggestions.getValue$(),
        getData$: () => this._channelsState.channelSuggestions.getValue$().pipe(getListData()),
        getMetadata$: () => this._channelsState.channelSuggestions.getValue$().pipe(getMetadata()),
        delete: (
            channelId: number, source: { type: ChannelSuggestionType; id: number }, target: { type: ChannelSuggestionType; id: number }
        ) => {
            this._channelsState.channelSuggestions.setInProgress(true);
            return this._channelsApi.deleteChannelSuggestion(channelId, source, target);
        },
        deleteSourceTargets: (channelId: number, source: { type: ChannelSuggestionType; id: number }) => {
            this._channelsState.channelSuggestions.setInProgress(true);
            return this._channelsApi.deleteSourceTargets(channelId, source);
        },
        save: (channelId: number, type: ChannelSuggestionType, id: number, req: PostChannelSuggestionReq[]) =>
            StateManager.inProgress(
                this._channelsState.channelSuggestions,
                this._channelsApi.postChannelSuggestion(channelId, type, id, req)
            ),
        loading$: () => this._channelsState.channelSuggestions.isInProgress$(),
        clear: () => this._channelsState.channelSuggestions.setValue(null)
    });

    readonly channelSessions = Object.freeze({
        load: (channelId: number, eventId: number, filters?: ChannelSessionsFilter): void => StateManager.load(
            this._channelsState.channelSessions,
            this._channelsApi.getChannelSessions(channelId, eventId, filters).pipe(mapMetadata())
        ),
        get$: () => this._channelsState.channelSessions.getValue$(),
        getData$: () => this._channelsState.channelSessions.getValue$().pipe(getListData()),
        getMetadata$: () => this._channelsState.channelSessions.getValue$().pipe(getMetadata()),
        loading$: () => this._channelsState.channelSessions.isInProgress$(),
        clear: () => this._channelsState.channelSessions.setValue(null)
    });

    // CHANNEL EVENTS

    loadChannelEvents(channelId: number, filter?: GetChannelEventsRequest): void {
        this._channelsState.setChannelEventsLoading(true);
        this._channelsApi.getChannelEvents(channelId, filter)
            .pipe(
                mapMetadata(),
                finalize(() => this._channelsState.setChannelEventsLoading(false))
            )
            .subscribe(events =>
                this._channelsState.setChannelEvents(events)
            );
    }

    getChannelEventsData$(): Observable<ChannelEvent[]> {
        return this._channelsState.getChannelEvents$().pipe(getListData());
    }

    getChannelEventsMetadata$(): Observable<Metadata> {
        return this._channelsState.getChannelEvents$().pipe(getMetadata());
    }

    isChannelEventsLoading$(): Observable<boolean> {
        return this._channelsState.isChannelEventsLoading$();
    }

    clearChannelEvents(): void {
        this._channelsState.setChannelEvents(null);
    }

    saveChannelEvents(channelId: number, req: PutChannelEventRequest[]): Observable<void> {
        this._channelsState.setChannelCatalogSaving(true);
        return this._channelsApi.putChannelEvents(channelId, req)
            .pipe(finalize(() => this._channelsState.setChannelCatalogSaving(false)));
    }

    isChannelCatalogSaving$(): Observable<boolean> {
        return this._channelsState.isChannelCatalogSaving$();
    }

    // COMMUNICATION

    cloneContents(channelId: number, reqBody: PostChannelContentsCloneRequest): Observable<PostChannelContentsCloneResponseItem[]> {
        this._channelsState.setCloneContentsSaving(true);
        return this._channelsApi.postContentsClone(channelId, reqBody)
            .pipe(
                catchError(error => { throw error; }),
                finalize(() => this._channelsState.setCloneContentsSaving(false))
            );
    }

    isCloneContentsSaving$(): Observable<boolean> {
        return this._channelsState.isCloneContentsSaving$();
    }

    loadTextContents(channelId: number, languageId: string, newLiterals?: boolean): void {
        this._channelsState.setTextContentsError(null);
        this._channelsState.setTextContentsLoading(true);
        this._channelsApi.getTextContents(channelId, languageId, newLiterals)
            .pipe(
                map(contents => contents?.sort((a, b) => a.key.localeCompare(b.key))),
                catchError(error => {
                    this._channelsState.setTextContentsError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setTextContentsLoading(false))
            )
            .subscribe(textContents =>
                this._channelsState.setTextContents(textContents)
            );
    }

    updateTextContents(channelId: number, languageId: string, textContents: TextContent[], newLiterals?: boolean): Observable<void> {
        this._channelsState.setTextContentsSaving(true);
        this._channelsState.setTextContentsError(null);
        return this._channelsApi.postTextContents(channelId, languageId, textContents, newLiterals)
            .pipe(
                catchError(error => {
                    this._channelsState.setTextContentsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setTextContentsSaving(false))
            );
    }

    isTextContentsInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsState.isTextContentsLoading$(),
            this._channelsState.isTextContentsSaving$()
        ]);
    }

    clearTextContents$(): void {
        this._channelsState.setTextContents(null);
    }

    getTextContentsError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getTextContentsError$();
    }

    getTextContents$(): Observable<TextContent[]> {
        return this._channelsState.getTextContents$();
    }

    loadContents(channelId: number, category: string, languageId?: string): void {
        this._channelsState.setContentsError(null);
        this._channelsState.setContentsLoading(true);
        this._channelsApi.getContents(channelId, category, languageId)
            .pipe(
                catchError(error => {
                    this._channelsState.setContentsError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setContentsLoading(false))
            )
            .subscribe(contents =>
                this._channelsState.setContents(contents)
            );
    }

    loadHistoricalContent(channelId: number, contentId: number, languageId: string): void {
        this._channelsState.setHistoricalContentError(null);
        this._channelsState.setHistoricalContentLoading(true);
        this._channelsApi.getHistoricalContent(channelId, contentId, languageId)
            .pipe(
                catchError(error => {
                    this._channelsState.setHistoricalContentError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setHistoricalContentLoading(false))
            )
            .subscribe(content =>
                this._channelsState.setHistoricalContent(content)
            );
    }

    updateContents(channelId: number, category: string, contents: ChannelContent[], languageId?: string): Observable<void> {
        this._channelsState.setContentsSaving(true);
        this._channelsState.setContentsError(null);
        return this._channelsApi.putContents(channelId, category, contents, languageId)
            .pipe(
                catchError(error => {
                    this._channelsState.setContentsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setContentsSaving(false))
            );
    }

    updateProfiledContents(channelId: number, contentId: string, contents: ChannelContent[]): Observable<void> {
        this._channelsState.setProfiledContentsSaving(true);
        this._channelsState.setContentsError(null);
        return this._channelsApi.putProfiledContents(channelId, contentId, contents)
            .pipe(
                catchError(error => {
                    this._channelsState.setContentsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setProfiledContentsSaving(false))
            );
    }

    isContentsInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsState.isContentsLoading$(),
            this._channelsState.isContentsSaving$(),
            this._channelsState.isProfiledContentsSaving$(),
            this._channelsState.isHistoricalContentLoading$()
        ]);
    }

    clearContents$(): void {
        this._channelsState.setContents(null);
        this._channelsState.setHistoricalContent(null);
    }

    getContentsError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getContentsError$();
    }

    getContents$(): Observable<ChannelContent[]> {
        return this._channelsState.getContents$();
    }

    getHistoricalContent$(): Observable<ChannelHistoricalContent[]> {
        return this._channelsState.getHistoricalContent$();
    }

    loadTicketPdfContentImages(channelId: number, language?: string, type?: ChannelTicketContentImageType): void {
        this._channelsState.setTicketPdfContentImagesLoading(true);
        this._channelsApi.getTicketContentImages(channelId, ChannelTicketContentFormat.pdf, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._channelsState.setTicketPdfContentImagesLoading(false))
            )
            .subscribe(contents =>
                this._channelsState.setTicketPdfContentImages(contents)
            );
    }

    getTicketPdfContentImages$(): Observable<ChannelTicketContentImage[]> {
        return this._channelsState.getTicketPdfContentImages$();
    }

    isTicketPdfContentImagesLoading$(): Observable<boolean> {
        return this._channelsState.isTicketPdfContentImagesLoading$();
    }

    saveTicketPdfContentImages(channelId: number, imagesToSave: PutChannelTicketContentImage[]): Observable<void> {
        this._channelsState.setTicketPdfContentImagesSaving(true);
        return this._channelsApi.putTicketContentImages(channelId, ChannelTicketContentFormat.pdf, imagesToSave)
            .pipe(finalize(() => this._channelsState.setTicketPdfContentImagesSaving(false)));
    }

    isTicketPdfContentImagesSaving$(): Observable<boolean> {
        return this._channelsState.isTicketPdfContentImagesSaving$();
    }

    deleteTicketPdfContentImages(channelId: number, imagesToDelete: PutChannelTicketContentImage[]): Observable<void> {
        this._channelsState.setTicketPdfContentImagesRemoving(true);
        return zip(...imagesToDelete.map(request => this._channelsApi
            .deleteTicketContentImage(channelId, ChannelTicketContentFormat.pdf, request.language, request.type))
        )
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() => this._channelsState.setTicketPdfContentImagesRemoving(false))
            );
    }

    isTicketPdfContentImagesRemoving$(): Observable<boolean> {
        return this._channelsState.isTicketPdfContentImagesRemoving$();
    }

    loadTicketPrinterContentImages(channelId: number, language?: string, type?: ChannelTicketContentImageType): void {
        this._channelsState.setTicketPrinterContentImagesLoading(true);
        this._channelsApi.getTicketContentImages(channelId, ChannelTicketContentFormat.printer, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._channelsState.setTicketPrinterContentImagesLoading(false))
            )
            .subscribe(contents =>
                this._channelsState.setTicketPrinterContentImages(contents)
            );
    }

    getTicketPrinterContentImages$(): Observable<ChannelTicketContentImage[]> {
        return this._channelsState.getTicketPrinterContentImages$();
    }

    isTicketPrinterContentImagesLoading$(): Observable<boolean> {
        return this._channelsState.isTicketPrinterContentImagesLoading$();
    }

    saveTicketPrinterContentImages(channelId: number, imagesToSave: PutChannelTicketContentImage[]): Observable<void> {
        this._channelsState.setTicketPrinterContentImagesSaving(true);
        return this._channelsApi.putTicketContentImages(channelId, ChannelTicketContentFormat.printer, imagesToSave)
            .pipe(finalize(() => this._channelsState.setTicketPrinterContentImagesSaving(false)));
    }

    isTicketPrinterContentImagesSaving$(): Observable<boolean> {
        return this._channelsState.isTicketPrinterContentImagesSaving$();
    }

    deleteTicketPrinterContentImages(channelId: number, imagesToDelete: PutChannelTicketContentImage[]): Observable<void> {
        this._channelsState.setTicketPrinterContentImagesRemoving(true);
        return zip(...imagesToDelete.map(request => this._channelsApi
            .deleteTicketContentImage(channelId, ChannelTicketContentFormat.printer, request.language, request.type))
        )
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() => this._channelsState.setTicketPrinterContentImagesRemoving(false))
            );
    }

    isTicketPrinterContentImagesRemoving$(): Observable<boolean> {
        return this._channelsState.isTicketPrinterContentImagesRemoving$();
    }

    loadTicketPassbookContentTexts(channelId: number): void {
        this._channelsState.setTicketPassbookContentTextsLoading(true);
        this._channelsApi.getTicketContentTexts(channelId, ChannelTicketContentFormat.passbook)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._channelsState.setTicketPassbookContentTextsLoading(false))
            )
            .subscribe(contents =>
                this._channelsState.setTicketPassbookContentTexts(contents)
            );
    }

    getTicketPassbookContentTexts$(): Observable<ChannelTicketContentText[]> {
        return this._channelsState.getTicketPassbookContentTexts$();
    }

    isTicketPassbookContentTextsLoading$(): Observable<boolean> {
        return this._channelsState.isTicketPassbookContentTextsLoading$();
    }

    saveTicketPassbookContentTexts(channelId: number, textsToSave: ChannelTicketContentText[]): Observable<void> {
        this._channelsState.setTicketPassbookContentTextsSaving(true);
        return this._channelsApi.putTicketContentTexts(channelId, ChannelTicketContentFormat.passbook, textsToSave)
            .pipe(finalize(() => this._channelsState.setTicketPassbookContentTextsSaving(false)));
    }

    isTicketPassbookContentTextsSaving$(): Observable<boolean> {
        return this._channelsState.isTicketPassbookContentTextsSaving$();
    }

    clearTicketPassbookContentTexts(): void {
        this._channelsState.setTicketPassbookContentTexts(null);
    }

    loadTicketPassbookContentImages(channelId: number,
        language?: string, type?: ChannelTicketContentImageType): void {
        this._channelsState.setTicketPassbookContentImagesLoading(true);
        this._channelsApi.getTicketContentImages(channelId, ChannelTicketContentFormat.passbook, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._channelsState.setTicketPassbookContentImagesLoading(false))
            )
            .subscribe(contents =>
                this._channelsState.setTicketPassbookContentImages(contents)
            );
    }

    getTicketPassbookContentImages$(): Observable<ChannelTicketContentImage[]> {
        return this._channelsState.getTicketPassbookContentImages$();
    }

    isTicketPassbookContentImagesLoading$(): Observable<boolean> {
        return this._channelsState.isTicketPassbookContentImagesLoading$();
    }

    saveTicketPassbookContentImages(channelId: number,
        imagesToSave: PutChannelTicketContentImage[]): Observable<void> {
        this._channelsState.setTicketPassbookContentImagesSaving(true);
        return this._channelsApi
            .putTicketContentImages(channelId, ChannelTicketContentFormat.passbook, imagesToSave)
            .pipe(finalize(() => this._channelsState.setTicketPassbookContentImagesSaving(false)));
    }

    isTicketPassbookContentImagesSaving$(): Observable<boolean> {
        return this._channelsState.isTicketPassbookContentImagesSaving$();
    }

    deleteTicketPassbookContentImages(channelId: number,
        imagesToDelete: PutChannelTicketContentImage[]): Observable<void> {
        this._channelsState.setTicketPassbookContentImagesRemoving(true);
        return zip(...imagesToDelete
            .map(request => this._channelsApi
                .deleteTicketContentImage(channelId, ChannelTicketContentFormat.passbook, request.language, request.type)))
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() => this._channelsState.setTicketPassbookContentImagesRemoving(false))
            );
    }

    isTicketPassbookContentImagesRemoving$(): Observable<boolean> {
        return this._channelsState.isTicketPassbookContentImagesRemoving$();
    }

    clearTicketPassbookContentImages(): void {
        this._channelsState.setTicketPassbookContentImages(null);
    }

    loadPurchaseContentImages(channelId: number, language?: string, type?: ChannelPurchaseContentImageType): void {
        this._channelsState.setPurchaseContentImagesLoading(true);
        this._channelsApi.getPurchaseContentImages(channelId, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._channelsState.setPurchaseContentImagesLoading(false))
            )
            .subscribe(contents =>
                this._channelsState.setPurchaseContentImages(contents)
            );
    }

    getPurchaseContentImages$(): Observable<ChannelPurchaseContentImage[]> {
        return this._channelsState.getPurchaseContentImages$();
    }

    isPurchaseContentImagesLoading$(): Observable<boolean> {
        return this._channelsState.isPurchaseContentImagesLoading$();
    }

    savePurchaseContentImages(channelId: number, imagesToSave: PutChannelPurchaseContentImage[]): Observable<void> {
        this._channelsState.setPurchaseContentImagesSaving(true);
        return this._channelsApi.postPurchaseContentImages(channelId, imagesToSave)
            .pipe(finalize(() => this._channelsState.setPurchaseContentImagesSaving(false)));
    }

    isPurchaseContentImagesSaving$(): Observable<boolean> {
        return this._channelsState.isPurchaseContentImagesSaving$();
    }

    deletePurchaseContentImages(channelId: number, imagesToDelete: PutChannelPurchaseContentImage[]): Observable<void> {
        this._channelsState.setPurchaseContentImagesRemoving(true);
        return zip(...imagesToDelete.map(request => this._channelsApi
            .deletePurchaseContentImage(channelId, request.language, request.type))
        )
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() => this._channelsState.setPurchaseContentImagesRemoving(false))
            );
    }

    isPurchaseContentImagesRemoving$(): Observable<boolean> {
        return this._channelsState.isPurchaseContentImagesRemoving$();
    }

    loadPurchaseContentTexts(channelId: number): void {
        this._channelsState.setPurchaseContentTextsLoading(true);
        this._channelsApi.getPurchaseContentTexts(channelId)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._channelsState.setPurchaseContentTextsLoading(false))
            )
            .subscribe(contents =>
                this._channelsState.setPurchaseContentTexts(contents)
            );
    }

    getPurchaseContentTexts$(): Observable<ChannelPurchaseContentText[]> {
        return this._channelsState.getPurchaseContentTexts$();
    }

    isPurchaseContentTextsLoading$(): Observable<boolean> {
        return this._channelsState.isPurchaseContentTextsLoading$();
    }

    savePurchaseContentTexts(channelId: number, textsToSave: ChannelPurchaseContentText[]): Observable<void> {
        this._channelsState.setPurchaseContentTextsSaving(true);
        return this._channelsApi.postPurchaseContentTexts(channelId, textsToSave)
            .pipe(finalize(() => this._channelsState.setPurchaseContentTextsSaving(false)));
    }

    isPurchaseContentTextsSaving$(): Observable<boolean> {
        return this._channelsState.isPurchaseContentTextsSaving$();
    }

    // CONFIGURATION

    clearChannelPaymentMethods(): void {
        this._channelsState.setChannelPaymentMethods(null);
    }

    loadChannelPaymentMethods(channelId: string): void {
        this._channelsState.setChannelPaymentMethodsError(null);
        this._channelsState.setChannelPaymentMethodsLoading(true);
        this._channelsApi.getPaymentMethods(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelPaymentMethodsError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setChannelPaymentMethodsLoading(false))
            )
            .subscribe(paymentMethod =>
                this._channelsState.setChannelPaymentMethods(paymentMethod)
            );
    }

    getChannelPaymentMethods$(): Observable<ChannelGateway[]> {
        return this._channelsState.getChannelPaymentMethods$();
    }

    isChannelPaymentMethodsLoading$(): Observable<boolean> {
        return this._channelsState.isChannelPaymentMethodsLoading$();
    }

    isChannelPaymentMethodsSaving$(): Observable<boolean> {
        return this._channelsState.isChannelPaymentMethodsSaving$();
    }

    saveChannelPaymentMethods(channelId: string, paymentsMethods: PostChannelGateway[]): Observable<void> {
        this._channelsState.setChannelPaymentMethodsError(null);
        this._channelsState.setChannelPaymentMethodsSaving(true);
        return this._channelsApi.putChannelPaymentMethods(channelId, paymentsMethods)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelPaymentMethodsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setChannelPaymentMethodsSaving(false))
            );
    }

    loadPurchaseConfig(channelId: number): void {
        this._channelsState.setPurchaseConfigError(null);
        this._channelsState.setPurchaseConfigLoading(true);
        this._channelsApi.getPurchaseConfig(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setPurchaseConfigError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setPurchaseConfigLoading(false))
            )
            .subscribe(config =>
                this._channelsState.setPurchaseConfig(config)
            );
    }

    getPurchaseConfig$(): Observable<ChannelPurchaseConfig> {
        return this._channelsState.getPurchaseConfig$();
    }

    clearPurchaseConfig(): void {
        this._channelsState.setPurchaseConfig(null);
    }

    isPurchaseConfigLoading$(): Observable<boolean> {
        return this._channelsState.isPurchaseConfigLoading$();
    }

    isPurchaseConfigSaving$(): Observable<boolean> {
        return this._channelsState.isPurchaseConfigSaving$();
    }

    updatePurchaseConfig(channelId: number, config: ChannelPurchaseConfig): Observable<void> {
        this._channelsState.setPurchaseConfigSavingError(null);
        this._channelsState.setPurchaseConfigSaving(true);
        return this._channelsApi.putPurchaseConfig(channelId, config)
            .pipe(
                catchError(error => {
                    this._channelsState.setPurchaseConfigSavingError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setPurchaseConfigSaving(false))
            );
    }

    // notification email server config

    clearEmailServerConfig(): void {
        this._channelsState.setEmailServerConfig(null);
        this._channelsState.setEmailServerConfigError(null);
    }

    loadEmailServerConfig(channelId: number): void {
        this._channelsState.setEmailServerConfigLoading(true);
        this._channelsState.setEmailServerConfigError(null);
        this._channelsApi.getNotificationsEmailServer(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setEmailServerConfigError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setEmailServerConfigLoading(false))
            )
            .subscribe(notificationServerConfig => this._channelsState.setEmailServerConfig(notificationServerConfig));
    }

    getEmailServerConfig$(): Observable<EmailServerConf> {
        return this._channelsState.getEmailServerConfig();
    }

    isEmailServerConfigLoading$(): Observable<boolean> {
        return this._channelsState.isEmailServerConfigLoading();
    }

    getEmailServerConfigError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getEmailServerConfigError();
    }

    updateEmailServerConfig(channelId: number, conf: EmailServerConf): Observable<void> {
        this._channelsState.setEmailServerConfigUpdating(true);
        return this._channelsApi.putNotificationsEmailServer(channelId, conf)
            .pipe(finalize(() => this._channelsState.setEmailServerConfigUpdating(false)));
    }

    isEmailServerConfigUpdating$(): Observable<boolean> {
        return this._channelsState.isEmailServerConfigUpdating();
    }

    sendEmailServerTest(channelId: number, email: EmailServerTestRequest): Observable<boolean> {
        this._channelsState.setEmailServerConfigTesting(true);
        this._channelsState.setEmailServerTestError(null);
        return this._channelsApi.postEmailServerTest(channelId, email)
            .pipe(
                switchMap(() => of(true)),
                catchError(error => {
                    this._channelsState.setEmailServerTestError(error);
                    return of(false);
                }),
                finalize(() => this._channelsState.setEmailServerConfigTesting(false))
            );
    }

    isEmailServerConfigTesting$(): Observable<boolean> {
        return this._channelsState.isEmailServerConfigTesting();
    }

    getEmailServerConfigTestingError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getEmailServerTestError();
    }

    // notification email server templates

    clearNotificationEmailTemplates(): void {
        this._channelsState.setNotificationEmailTemplates(null);
        this._channelsState.setNotificationEmailTemplatesError(null);
    }

    loadNotificationEmailTemplates(channelId: number): void {
        this._channelsState.setNotificationEmailTemplatesLoading(true);
        this._channelsState.setNotificationEmailTemplatesError(null);
        this._channelsApi.getNotificationsEmailTemplates(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setNotificationEmailTemplatesError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setNotificationEmailTemplatesLoading(false))
            )
            .subscribe(notificationServerTemplates => this._channelsState.setNotificationEmailTemplates(notificationServerTemplates));
    }

    getNotificationEmailTemplates$(): Observable<NotificationEmailTemplate[]> {
        return this._channelsState.getNotificationEmailTemplates();
    }

    isNotificationEmailTemplatesLoading$(): Observable<boolean> {
        return this._channelsState.isNotificationEmailTemplatesLoading();
    }

    getNotificationEmailTemplatesError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getNotificationEmailTemplatesError();
    }

    updateNotificationEmailTemplates(channelId: number, templates: NotificationEmailTemplate[]): Observable<void> {
        this._channelsState.setNotificationEmailTemplatesUpdating(true);
        return this._channelsApi.putNotificationsEmailTemplates(channelId, templates)
            .pipe(finalize(() => this._channelsState.setNotificationEmailTemplatesUpdating(false)));
    }

    isNotificationEmailTemplatesUpdating$(): Observable<boolean> {
        return this._channelsState.isNotificationEmailTemplatesUpdating();
    }

    // CANCELLATION SERVICES

    loadCancellationServices(channelId: number): void {
        this._channelsState.setCancellationServicesError(null);
        this._channelsState.setCancellationServicesLoading(true);
        this._channelsApi.getCancellationServices(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setCancellationServicesError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setCancellationServicesLoading(false))
            ).subscribe(cancellationServices =>
                this._channelsState.setCancellationServices(cancellationServices)
            );
    }

    isCancellationServicesLoading$(): Observable<boolean> {
        return this._channelsState.isCancellationServicesLoading$();
    }

    getCancellationServices$(): Observable<ChannelCancellationServices> {
        return this._channelsState.getCancellationServices$();
    }

    updateCancellationServices(channelId: number, data: PutChannelCancellationServices): Observable<void> {
        return this._channelsApi.putCancellationServices(channelId, data);
    }

    clearCancellationServices(): void {
        this._channelsState.setCancellationServices(null);
    }
}
