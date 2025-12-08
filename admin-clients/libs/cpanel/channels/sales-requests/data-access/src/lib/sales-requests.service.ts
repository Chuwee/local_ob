import { Metadata, mapMetadata, getMetadata, getListData, StateManager } from '@OneboxTM/utils-state';
import {
    ChannelSurcharge, ChannelCommission, AdditionalCondition
} from '@admin-clients/cpanel/channels/data-access';
import { ContentLinkRequest } from '@admin-clients/cpanel/shared/data-access';
import { ExportRequest, ExportResponse, FilterOption } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable, Provider } from '@angular/core';
import { concat, EMPTY, forkJoin, Observable, of, Subject, tap, zip } from 'rxjs';
import { catchError, finalize, map, mapTo, switchMap, take, takeUntil } from 'rxjs/operators';
import { SalesRequestsApi } from './api/sales-requests.api';
import { GetFilterRequest } from './models/get-filter-request';
import { GetSaleRequestChannelSessionLinksRequest } from './models/get-sale-request-channel-session-links-request';
import { GetSaleRequestPriceTypesRequest } from './models/get-sale-request-price-types-request';
import { GetSaleRequestPromotionsRequest } from './models/get-sale-request-promotions-request';
import { GetSaleRequestSessionsRequest } from './models/get-sale-request-sessions-request';
import { GetSalesRequestsRequest } from './models/get-sales-requests-request';
import { GetSalesRequestsResponse } from './models/get-sales-requests-response';
import { PutSaleRequestGateways } from './models/put-sale-request-gateways.model';
import { PutSaleRequestPurchaseContentImage } from './models/put-sale-request-purchase-content-image.model';
import { PutSaleRequestTicketContentImage } from './models/put-sale-request-ticket-content-image.model';
import { SaleRequestAdditionalBannerTexts } from './models/sale-request-additional-banner-texts.model';
import { SaleRequestBenefitsContentBadge } from './models/sale-request-benefits-content-badge.model';
import { SaleRequestConfigurationModel } from './models/sale-request-configuration.model';
import { SaleRequestDeliveryConditions } from './models/sale-request-delivery-conditions.model';
import { SaleRequestEmailContentTexts } from './models/sale-request-email-content-texts.model';
import { SaleRequestGateway, SaleRequestGatewayBenefits } from './models/sale-request-gateway.model';
import { SaleRequestPriceTypes, SaleRequestPriceTypesResponse } from './models/sale-request-price-types.model';
import { SaleRequestPromotion } from './models/sale-request-promotion.model';
import { SaleRequestPurchaseContentImageType } from './models/sale-request-purchase-content-image-type.enum';
import { SaleRequestPurchaseContentImage } from './models/sale-request-purchase-content-image.model';
import { SaleRequestPurchaseContentText } from './models/sale-request-purchase-content-text.model';
import { SaleRequestSession, SaleRequestSessionResponse } from './models/sale-request-session.model';
import { PutSaleRequestSurchargeTaxes } from './models/sale-request-surcharge-taxes.model';
import { SaleRequestTicketContentFormat } from './models/sale-request-ticket-content-format.enum';
import { SaleRequestTicketContentImageType } from './models/sale-request-ticket-content-image-type.enum';
import { SaleRequestTicketContentImage } from './models/sale-request-ticket-content-image.model';
import { SaleRequestTicketContentTemplate } from './models/sale-request-ticket-content-template.model';
import { SaleRequestTicketContentText } from './models/sale-request-ticket-content-text.model';
import { SaleRequest } from './models/sale-request.model';
import { SalesRequestsFilterField } from './models/sales-requests-filter-field.enum';
import { SalesRequestListElementModel } from './models/sales-requests-list-element.model';
import { SalesRequestsStatus } from './models/sales-requests-status.model';
import { SalesRequestsState } from './state/sales-requests.state';

export const provideSalesRequestService = (): Provider => [
    SalesRequestsService,
    SalesRequestsState,
    SalesRequestsApi
];

@Injectable({
    providedIn: 'root'
})
export class SalesRequestsService {
    readonly #salesRequestsApi = inject(SalesRequestsApi);
    readonly #salesRequestsState = inject(SalesRequestsState);

    readonly #filterFieldRequests = {
        [SalesRequestsFilterField.channels]: {},
        [SalesRequestsFilterField.eventEntity]: {},
        [SalesRequestsFilterField.channelEntity]: {}
    };

    readonly #cancelSalesRequestsList = new Subject<void>();
    readonly #cancelSalesRequestSessions = new Subject<void>();
    readonly #cancelSalesRequestPriceTypes = new Subject<void>();

    readonly salesRequestSessionLinks = Object.freeze({
        load: (request: GetSaleRequestChannelSessionLinksRequest): void => StateManager.load(
            this.#salesRequestsState.salesRequestSessionLinks,
            this.#salesRequestsApi.getChannelContentSessionLinks(request).pipe(mapMetadata())
        ),
        get$: () => this.#salesRequestsState.salesRequestSessionLinks.getValue$().pipe(getListData()),
        getSessionLinksMetadata$: () => this.#salesRequestsState.salesRequestSessionLinks.getValue$().pipe(getMetadata()),
        error$: () => this.#salesRequestsState.salesRequestSessionLinks.getError$(),
        inProgress$: () => this.#salesRequestsState.salesRequestSessionLinks.isInProgress$(),
        clear: () => this.#salesRequestsState.salesRequestSessionLinks.setValue(null)
    });

    readonly saleRequests = Object.freeze({
        load: (filters?: GetSalesRequestsRequest): void => StateManager.load(
            this.#salesRequestsState.saleRequests,
            this.#salesRequestsApi.getSalesRequests(filters).pipe(mapMetadata())
        ),
        getData$: () => this.#salesRequestsState.saleRequests.getValue$().pipe(map(saleRequests => saleRequests?.data || [])),
        getMetadata$: () => this.#salesRequestsState.saleRequests.getValue$().pipe(getMetadata()),
        loading$: () => this.#salesRequestsState.saleRequests.isInProgress$(),
        clear: () => this.#salesRequestsState.saleRequests.setValue(null)
    });

    readonly saleRequestSessions = Object.freeze({
        load: (filters?: GetSaleRequestSessionsRequest): void => StateManager.load(
            this.#salesRequestsState.saleRequestSessions,
            this.#salesRequestsApi.getSaleRequestSessions(filters).pipe(mapMetadata())
        ),
        get$: () => this.#salesRequestsState.saleRequestSessions.getValue$(),
        getData$: () => this.#salesRequestsState.saleRequestSessions.getValue$().pipe(
            map(saleRequestSessions => saleRequestSessions?.data || [])
        ),
        getMetadata$: () => this.#salesRequestsState.saleRequestSessions.getValue$().pipe(getMetadata()),
        loading$: () => this.#salesRequestsState.saleRequestSessions.isInProgress$(),
        clear: () => this.#salesRequestsState.saleRequestSessions.setValue(null)
    });

    readonly salesRequestPriceSimulation = Object.freeze({
        load: (saleRequestId: number): void => StateManager.load(
            this.#salesRequestsState.saleRequestPriceSimulation,
            this.#salesRequestsApi.getSaleRequestPriceSimulation(saleRequestId)
        ),
        get$: () => this.#salesRequestsState.saleRequestPriceSimulation.getValue$(),
        error$: () => this.#salesRequestsState.saleRequestPriceSimulation.getError$(),
        inProgress$: () => this.#salesRequestsState.saleRequestPriceSimulation.isInProgress$(),
        clear: () => this.#salesRequestsState.saleRequestPriceSimulation.setValue(null),
        export: (saleRequestId: number, request: ExportRequest): Observable<ExportResponse> => StateManager.inProgress(
            this.#salesRequestsState.saleRequestPriceSimulation,
            this.#salesRequestsApi.postSaleRequestPriceSimulationExport(saleRequestId, request)
        )
    });

    readonly gatewayBenefits = Object.freeze({
        load: (saleRequestId: number, gatewaySid: string, configurationSid: string) => StateManager.load(
            this.#salesRequestsState.gatewayBenefits,
            this.#salesRequestsApi.getSaleRequestGatewayBenefits(saleRequestId, gatewaySid, configurationSid)
        ),
        get$: () => this.#salesRequestsState.gatewayBenefits.getValue$(),
        inProgress$: () => this.#salesRequestsState.gatewayBenefits.isInProgress$(),
        clear: () => this.#salesRequestsState.gatewayBenefits.setValue(null),
        update: (saleRequestId: number, gateway: string, config: string, request: SaleRequestGatewayBenefits) => StateManager.inProgress(
            this.#salesRequestsState.gatewayBenefits,
            this.#salesRequestsApi.postSaleRequestGatewayBenefits(saleRequestId, gateway, config, request)
                .pipe(tap(value => this.#salesRequestsState.gatewayBenefits.setValue(value)))
        )
    });

    readonly paymentMethodsBenefitsContents = Object.freeze({
        load: (saleRequestId: number) => StateManager.load(
            this.#salesRequestsState.paymentMethodsBenefitsContents,
            this.#salesRequestsApi.getPaymentMethodsBenefitsContents(saleRequestId)
        ),
        get$: () => this.#salesRequestsState.paymentMethodsBenefitsContents.getValue$(),
        inProgress$: () => this.#salesRequestsState.paymentMethodsBenefitsContents.isInProgress$(),
        clear: () => this.#salesRequestsState.paymentMethodsBenefitsContents.setValue(null),
        update: (saleRequestId: number, data: SaleRequestBenefitsContentBadge[]) => StateManager.inProgress(
            this.#salesRequestsState.paymentMethodsBenefitsContents,
            this.#salesRequestsApi.postPaymentMethodsBenefitsContents(saleRequestId, data)
        )
    });

    readonly surchargeTaxes = Object.freeze({
        load: (channelId: number) =>
            StateManager.load(
                this.#salesRequestsState.saleRequestSurchargeTaxes,
                this.#salesRequestsApi.getSaleRequestSurchargeTaxes(channelId)
            ),
        update: (channelId: number, data: PutSaleRequestSurchargeTaxes) =>
            StateManager.inProgress(
                this.#salesRequestsState.saleRequestSurchargeTaxes,
                this.#salesRequestsApi.putSaleRequestSurchargeTaxes(channelId, data)
            ),
        clear: () => this.#salesRequestsState.saleRequestSurchargeTaxes.setValue(null),
        loading$: () => this.#salesRequestsState.saleRequestSurchargeTaxes.isInProgress$(),
        get$: () => this.#salesRequestsState.saleRequestSurchargeTaxes.getValue$()
    });

    cancelSalesRequestsList(): void {
        this.#cancelSalesRequestsList.next();
    }

    loadSalesRequestsList(request: GetSalesRequestsRequest): void {
        this.#salesRequestsState.setSalesRequestsListLoading(true);
        this.#salesRequestsApi.getSalesRequests(request)
            .pipe(
                catchError(() => of(null)),
                takeUntil(this.#cancelSalesRequestsList),
                finalize(() => this.#salesRequestsState.setSalesRequestsListLoading(false))
            )
            .subscribe(salesRequests =>
                this.#salesRequestsState.setSalesRequestsList(salesRequests)
            );
    }

    //Server search
    loadServerSearchSalesRequestList(request: GetSalesRequestsRequest, nextPage = false): void {
        const currentObservable$ = this.#salesRequestsState.getSalesRequestsList$();
        let result: Observable<GetSalesRequestsResponse>;
        if (!nextPage) {
            result = this.#salesRequestsApi.getSalesRequests(request).pipe(catchError(() => of(null)));
        } else {
            result = currentObservable$
                .pipe(
                    take(1),
                    switchMap(currentData => {
                        request.offset = currentData.metadata.offset + currentData.data.length;
                        return this.#salesRequestsApi.getSalesRequests(request).pipe(
                            map(nextElements => {
                                nextElements.data = currentData.data.concat(nextElements.data);
                                nextElements.metadata.limit = nextElements.data.length;
                                nextElements.metadata.offset = 0;
                                return nextElements;
                            })
                        );
                    })
                );
        }
        result.subscribe(salesRequests => {
            this.#salesRequestsState.setSalesRequestsList(salesRequests);
        });
    }

    getSalesRequestsListData$(): Observable<SalesRequestListElementModel[]> {
        return this.#salesRequestsState.getSalesRequestsList$()
            .pipe(map(salesRequests => salesRequests?.data));
    }

    getSalesRequestsListMetadata$(): Observable<Metadata> {
        return this.#salesRequestsState.getSalesRequestsList$()
            .pipe(map(salesRequests => salesRequests?.metadata
                && Object.assign(new Metadata(), salesRequests.metadata)));
    }

    isSalesRequestsListLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSalesRequestsListLoading$();
    }

    clearSalesRequestsList(): void {
        this.#salesRequestsState.setSalesRequestsList(null);
    }

    loadAllSalesRequests(request?: GetSalesRequestsRequest): void {
        const loadingComplete = new Subject<void>();
        const pageSize = 50;
        const req: GetSalesRequestsRequest = { ...request, offset: 0, limit: pageSize };
        loadingComplete.next();
        this.#salesRequestsState.setAllSalesRequestsListLoading(true);
        this.#salesRequestsState.setAllSalesRequestsList(null);
        this.#salesRequestsApi.getSalesRequests(req)
            .pipe(
                switchMap(saleReqResponse => {
                    const saleReqResponses: Observable<GetSalesRequestsResponse>[] = [];
                    for (let incrOffset = pageSize; incrOffset < saleReqResponse.metadata.total; incrOffset += pageSize) {
                        saleReqResponses.push(
                            this.#salesRequestsApi.getSalesRequests(Object.assign(req, {
                                offset: incrOffset,
                                limit: pageSize
                            })).pipe(takeUntil(loadingComplete))
                        );
                    }
                    return concat(...saleReqResponses)
                        .pipe(
                            mapMetadata(),
                            finalize(() => {
                                saleReqResponse.metadata.limit = saleReqResponse.metadata.total;
                                this.#salesRequestsState.setAllSalesRequestsList(saleReqResponse);
                                this.#salesRequestsState.setAllSalesRequestsListLoading(false);
                            }),
                            map(incrGetSaleReqResponse => saleReqResponse.data.push(...incrGetSaleReqResponse.data)),
                            takeUntil(loadingComplete)
                        );
                }),
                takeUntil(loadingComplete))
            .subscribe();
    }

    getAllSalesRequests$(): Observable<SalesRequestListElementModel[]> {
        return this.#salesRequestsState.getAllSalesRequestsList$().pipe(getListData());
    }

    getAllSalesRequestsMetadata$(): Observable<Metadata> {
        return this.#salesRequestsState.getAllSalesRequestsList$().pipe(getMetadata());
    }

    loadFilter(saleRequestField: SalesRequestsFilterField, request: GetFilterRequest): void {
        request.limit = 500;
        if (!this.#isTheSameRequest(this.#filterFieldRequests[saleRequestField], request)) {
            for (const [key, value] of Object.entries(request)) {
                this.#filterFieldRequests[saleRequestField][key] = value;
            }
            switch (saleRequestField) {
                case SalesRequestsFilterField.channelEntity:
                    this.loadFilterChannelEntityList(request);
                    break;
                case SalesRequestsFilterField.eventEntity:
                    this.loadFilterEventEntityList(request);
                    break;
                case SalesRequestsFilterField.channels:
                    this.loadFilterChannelList(request);
                    break;
            }
        }
    }

    loadFilterChannelList(request: GetFilterRequest): void {
        this.#salesRequestsApi.getFilterOptions$('channels', request)
            .pipe(catchError(() => of(null)))
            .subscribe(channels =>
                this.#salesRequestsState.setFilterChannelList(channels)
            );
    }

    getFilterChannelListData$(): Observable<FilterOption[]> {
        return this.#salesRequestsState.getFilterChannelList$()
            .pipe(map(channels => channels?.data));
    }

    loadFilterChannelEntityList(request: GetFilterRequest): void {
        this.#salesRequestsApi.getFilterOptions$('channel-entities', request)
            .pipe(catchError(() => of(null)))
            .subscribe(entities =>
                this.#salesRequestsState.setFilterChannelEntityList(entities)
            );
    }

    getFilterChannelEntityListData$(): Observable<FilterOption[]> {
        return this.#salesRequestsState.getFilterChannelEntityList$()
            .pipe(map(entities => entities?.data));
    }

    loadFilterEventEntityList(request: GetFilterRequest): void {
        this.#salesRequestsApi.getFilterOptions$('event-entities', request)
            .pipe(catchError(() => of(null)))
            .subscribe(entities =>
                this.#salesRequestsState.setFilterEventEntityList(entities)
            );
    }

    getFilterEventEntityListData$(): Observable<FilterOption[]> {
        return this.#salesRequestsState.getFilterEventEntityList$()
            .pipe(map(entities => entities?.data));
    }

    loadSaleRequest(saleRequestId: number): void {
        this.#salesRequestsState.setSaleRequestError(null);
        this.#salesRequestsState.setSaleRequestLoading(true);
        this.#salesRequestsApi.getSaleRequest(saleRequestId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestLoading(false))
            )
            .subscribe(channel => this.#salesRequestsState.setSaleRequest(channel));
    }

    updateSaleRequestStatus(saleRequestId: number, status: SalesRequestsStatus): Observable<{ status: SalesRequestsStatus }> {
        return this.#salesRequestsApi.putSaleRequestStatus(saleRequestId, status);
    }

    clearSaleRequest(): void {
        this.#salesRequestsState.setSaleRequest(null);
    }

    getSaleRequest$(): Observable<SaleRequest> {
        return this.#salesRequestsState.getSaleRequest$();
    }

    getSaleRequestError$(): Observable<HttpErrorResponse> {
        return this.#salesRequestsState.getSaleRequestError$();
    }

    isSaleRequestLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestLoading$();
    }

    loadSaleRequestSurcharges(saleRequestId: number): void {
        this.#salesRequestsState.setSaleRequestSurchargesError(null);
        this.#salesRequestsState.setSaleRequestSurchargesLoading(true);
        this.#salesRequestsApi.getSaleRequestSurcharges(saleRequestId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestSurchargesError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestSurchargesLoading(false))
            )
            .subscribe(surcharges =>
                this.#salesRequestsState.setSaleRequestSurcharges(surcharges)
            );
    }

    getSaleRequestSurcharges$(): Observable<ChannelSurcharge[]> {
        return this.#salesRequestsState.getSaleRequestSurcharges$();
    }

    getSaleRequestSurchargesError$(): Observable<HttpErrorResponse> {
        return this.#salesRequestsState.getSaleRequestSurchargesError$();
    }

    isSaleRequestSurchargesLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestSurchargesLoading$();
    }

    isSaleRequestSurchargesSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestSurchargesSaving$();
    }

    clearSaleRequestSurcharges(): void {
        this.#salesRequestsState.setSaleRequestSurcharges(null);
    }

    saveSaleRequestSurcharges(saleRequestId: number, surcharges: ChannelSurcharge[]): Observable<void> {
        this.#salesRequestsState.setSaleRequestSurchargesError(null);
        this.#salesRequestsState.setSaleRequestSurchargesSaving(true);
        return this.#salesRequestsApi.postSaleRequestSurcharges(saleRequestId, surcharges)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestSurchargesError(error);
                    throw error;
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestSurchargesSaving(false))
            );
    }

    isSaleRequestCommissionsSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestCommissionsSaving$();
    }

    isSaleRequestCommissionsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestCommissionsLoading$();
    }

    getSaleRequestCommissions$(): Observable<ChannelCommission[]> {
        return this.#salesRequestsState.getSaleRequestCommissions$();
    }

    clearSaleRequestCommissions(): void {
        this.#salesRequestsState.setSaleRequestCommissions(null);
    }

    saveSaleRequestCommissions(saleRequestId: number, commissions: ChannelCommission[]): Observable<void> {
        this.#salesRequestsState.setSaleRequestCommissionsError(null);
        this.#salesRequestsState.setSaleRequestCommissionsSaving(true);
        return this.#salesRequestsApi.postSaleRequestCommissions(saleRequestId, commissions)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestCommissionsError(error);
                    throw error;
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestCommissionsSaving(false))
            );
    }

    loadSaleRequestCommissions(saleRequestId: number): void {
        this.#salesRequestsState.setSaleRequestCommissionsError(null);
        this.#salesRequestsState.setSaleRequestCommissionsLoading(true);
        this.#salesRequestsApi.getSaleRequestCommissions(saleRequestId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestCommissionsError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestCommissionsLoading(false))
            )
            .subscribe(commissions =>
                this.#salesRequestsState.setSaleRequestCommissions(commissions)
            );
    }

    isSaleRequestConfigurationSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestConfigurationSaving$();
    }

    isSaleRequestConfigurationLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestConfigurationLoading$();
    }

    getSaleRequestConfiguration$(): Observable<SaleRequestConfigurationModel> {
        return this.#salesRequestsState.getSaleRequestConfiguration$();
    }

    clearSaleRequestConfiguration(): void {
        this.#salesRequestsState.setSaleRequestConfiguration(null);
    }

    saveSaleRequestConfigurationCustomCategory(saleRequestId: number, categoryId: number): Observable<void> {
        this.#salesRequestsState.setSaleRequestConfigurationSaving(true);
        this.#salesRequestsState.setSaleRequestConfigurationError(null);
        return this.#salesRequestsApi.putSaleRequestConfigurationCustomCategory(saleRequestId, categoryId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestConfigurationError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestConfigurationSaving(false))
            );
    }

    saveSaleRequestConfigurationSubscriptionList(saleRequestId: number, subscriptionListId: number): Observable<void> {
        this.#salesRequestsState.setSaleRequestConfigurationSaving(true);
        this.#salesRequestsState.setSaleRequestConfigurationError(null);
        return this.#salesRequestsApi.putSaleRequestConfigurationSubscriptionList(saleRequestId, subscriptionListId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestConfigurationError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestConfigurationSaving(false))
            );
    }

    saveSaleRequestConfigurationRefundAllowed(saleRequestId: number, allowRefund: boolean): Observable<void> {
        this.#salesRequestsState.setSaleRequestConfigurationSaving(true);
        this.#salesRequestsState.setSaleRequestConfigurationError(null);
        return this.#salesRequestsApi.postSaleRequestConfigurationRefundAllowed(saleRequestId, allowRefund)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestConfigurationError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestConfigurationSaving(false))
            );
    }

    loadSaleRequestConfiguration(saleRequestId: number): void {
        this.#salesRequestsState.setSaleRequestConfigurationError(null);
        this.#salesRequestsState.setSaleRequestConfigurationLoading(true);
        this.#salesRequestsApi.getSaleRequestConfigurationRefundAllowed(saleRequestId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestConfigurationError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestConfigurationLoading(false))
            )
            .subscribe(configuration =>
                this.#salesRequestsState.setSaleRequestConfiguration(configuration)
            );
    }

    getSaleRequestSessions$(): Observable<SaleRequestSession[]> {
        return this.#salesRequestsState.getSaleRequestSessions$().pipe(getListData());
    }

    getSaleRequestSessionsMetadata$(): Observable<Metadata> {
        return this.#salesRequestsState.getSaleRequestSessions$().pipe(
            map(list => list?.metadata && new Metadata(list.metadata))
        );
    }

    getSaleRequestSessionsError$(): Observable<HttpErrorResponse> {
        return this.#salesRequestsState.getSaleRequestSessionsError$();
    }

    clearSaleRequestSessions(): void {
        this.#salesRequestsState.setSaleRequestSessions(null);
    }

    isSaleRequestSessionsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestSessionsLoading$();
    }

    cancelSaleRequestSessions(): void {
        this.#cancelSalesRequestSessions.next();
    }

    loadSaleRequestSessions(request: GetSaleRequestSessionsRequest): void {
        this.#salesRequestsState.setSaleRequestSessionsError(null);
        this.#salesRequestsState.setSaleRequestSessionsLoading(true);
        this.#salesRequestsApi.getSaleRequestSessions(request)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestSessionsError(error);
                    return EMPTY;
                }),
                takeUntil(this.#cancelSalesRequestSessions),
                finalize(() => this.#salesRequestsState.setSaleRequestSessionsLoading(false))
            )
            .subscribe(sessions =>
                this.#salesRequestsState.setSaleRequestSessions(sessions)
            );
    }

    getAllSaleRequestSessions$(): Observable<SaleRequestSession[]> {
        return this.#salesRequestsState.getAllSessionsList$().pipe(getListData());
    }

    getAllSaleRequestSessionsMetadata$(): Observable<Metadata> {
        return this.#salesRequestsState.getAllSessionsList$().pipe(getMetadata());
    }

    loadAllSaleRequestSessions(request?: Partial<GetSaleRequestSessionsRequest>): void {
        const loadingCompleteSessionList = new Subject<void>();
        const pageSize = 999;
        const req: GetSaleRequestSessionsRequest = { ...request, offset: 0, limit: pageSize };
        loadingCompleteSessionList.next();
        this.#salesRequestsState.setAllSessionsListLoading(true);
        this.#salesRequestsState.setAllSessionsList(null);
        this.#salesRequestsApi.getSaleRequestSessions(req)
            .pipe(
                switchMap(getSessionResponse => {
                    const getSessionsResponses: Observable<SaleRequestSessionResponse>[] = [];
                    for (let incrOffset = pageSize; incrOffset < getSessionResponse.metadata.total; incrOffset += pageSize) {
                        getSessionsResponses.push(
                            this.#salesRequestsApi.getSaleRequestSessions(Object.assign(req, {
                                offset: incrOffset,
                                limit: pageSize
                            })).pipe(takeUntil(loadingCompleteSessionList))
                        );
                    }
                    return concat(...getSessionsResponses)
                        .pipe(
                            mapMetadata(),
                            finalize(() => {
                                getSessionResponse.metadata.limit = getSessionResponse.metadata.total;
                                this.#salesRequestsState.setAllSessionsList(getSessionResponse);
                                this.#salesRequestsState.setAllSessionsListLoading(false);
                            }),
                            map(incrGetSessionResponse => getSessionResponse.data.push(...incrGetSessionResponse.data)),
                            takeUntil(loadingCompleteSessionList)
                        );
                }),
                takeUntil(loadingCompleteSessionList))
            .subscribe();
    }

    getSaleRequestPriceTypes$(): Observable<SaleRequestPriceTypes[]> {
        return this.#salesRequestsState.getSaleRequestPriceTypes$().pipe(getListData());
    }

    getSaleRequestPriceTypesMetadata$(): Observable<Metadata> {
        return this.#salesRequestsState.getSaleRequestPriceTypes$().pipe(getMetadata());
    }

    getSaleRequestPriceTypesError$(): Observable<HttpErrorResponse> {
        return this.#salesRequestsState.getSaleRequestPriceTypesError$();
    }

    clearSaleRequestPriceTypes(): void {
        this.#salesRequestsState.setSaleRequestPriceTypes(null);
    }

    isSaleRequestPriceTypesLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestPriceTypesLoading$();
    }

    cancelSaleRequestPriceTypes(): void {
        this.#cancelSalesRequestPriceTypes.next();
    }

    loadSaleRequestPriceTypes(request: GetSaleRequestPriceTypesRequest): void {
        this.#salesRequestsState.setSaleRequestPriceTypesError(null);
        this.#salesRequestsState.setSaleRequestPriceTypesLoading(true);
        this.#salesRequestsApi.getSaleRequestPriceTypes(request)
            .pipe(
                mapMetadata(),
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestPriceTypesError(error);
                    return EMPTY;
                }),
                takeUntil(this.#cancelSalesRequestPriceTypes),
                finalize(() => this.#salesRequestsState.setSaleRequestPriceTypesLoading(false))
            )
            .subscribe(pricetypes =>
                this.#salesRequestsState.setSaleRequestPriceTypes(pricetypes)
            );
    }

    getAllSaleRequestPriceTypes$(): Observable<SaleRequestPriceTypes[]> {
        return this.#salesRequestsState.getAllPriceTypesList$().pipe(getListData());
    }

    loadAllSaleRequestPriceTypes(request?: Partial<GetSaleRequestPriceTypesRequest>): void {
        const loadingComplete = new Subject<void>();
        const pageSize = 999;
        const req: GetSaleRequestPriceTypesRequest = { ...request, offset: 0, limit: pageSize };
        loadingComplete.next();
        this.#salesRequestsState.setAllPriceTypesListLoading(true);
        this.#salesRequestsState.setAllPriceTypesList(null);
        this.#salesRequestsApi.getSaleRequestPriceTypes(req)
            .pipe(
                switchMap(getPriceTypeResponse => {
                    const getPriceTypesResponses: Observable<SaleRequestPriceTypesResponse>[] = [];
                    for (let incrOffset = pageSize; incrOffset < getPriceTypeResponse.metadata.total; incrOffset += pageSize) {
                        getPriceTypesResponses.push(
                            this.#salesRequestsApi.getSaleRequestPriceTypes(Object.assign(req, {
                                offset: incrOffset,
                                limit: pageSize
                            })).pipe(takeUntil(loadingComplete))
                        );
                    }
                    return concat(...getPriceTypesResponses)
                        .pipe(
                            mapMetadata(),
                            finalize(() => {
                                getPriceTypeResponse.metadata.limit = getPriceTypeResponse.metadata.total;
                                this.#salesRequestsState.setAllPriceTypesList(getPriceTypeResponse);
                                this.#salesRequestsState.setAllPriceTypesListLoading(false);
                            }),
                            map(incrGetPriceTypesResponse => getPriceTypeResponse.data.push(...incrGetPriceTypesResponse.data)),
                            takeUntil(loadingComplete)
                        );
                }),
                takeUntil(loadingComplete))
            .subscribe();
    }

    getSaleRequestPromotions$(): Observable<SaleRequestPromotion[]> {
        return this.#salesRequestsState.getSaleRequestPromotions$().pipe(getListData());
    }

    getSaleRequestPromotionsMetadata$(): Observable<Metadata> {
        return this.#salesRequestsState.getSaleRequestPromotions$().pipe(getMetadata());
    }

    getSaleRequestPromotionsError$(): Observable<HttpErrorResponse> {
        return this.#salesRequestsState.getSaleRequestPromotionsError$();
    }

    clearSaleRequestPromotions(): void {
        this.#salesRequestsState.setSaleRequestPromotions(null);
    }

    isSaleRequestPromotionsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestPromotionsLoading$();
    }

    loadSaleRequestPromotions(request: GetSaleRequestPromotionsRequest): void {
        this.#salesRequestsState.setSaleRequestPromotionsError(null);
        this.#salesRequestsState.setSaleRequestPromotionsLoading(true);
        this.#salesRequestsApi.getSaleRequestPromotions(request)
            .pipe(
                catchError(error => {
                    mapMetadata(),
                        this.#salesRequestsState.setSaleRequestPromotionsError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestPromotionsLoading(false))
            )
            .subscribe(promotions =>
                this.#salesRequestsState.setSaleRequestPromotions(promotions)
            );
    }

    clearSaleRequestPaymentMethods(): void {
        this.#salesRequestsState.setSaleRequestPaymentMethods(null);
    }

    loadSaleRequestPaymentMethods(saleRequestId: number): void {
        this.#salesRequestsState.setSaleRequestPaymentMethodsError(null);
        this.#salesRequestsState.setSaleRequestPaymentMethodsLoading(true);
        this.#salesRequestsApi.getSaleRequestPaymentMethods(saleRequestId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestPaymentMethodsError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestPaymentMethodsLoading(false))
            )
            .subscribe(paymentMethod =>
                this.#salesRequestsState.setSaleRequestPaymentMethods(paymentMethod)
            );
    }

    getSaleRequestPaymentMethods$(): Observable<SaleRequestGateway> {
        return this.#salesRequestsState.getSaleRequestPaymentMethods$();
    }

    isSaleRequestPaymentMethodsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestPaymentMethodsLoading$();
    }

    isSaleRequestPaymentMethodsSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestPaymentMethodsSaving$();
    }

    saveSaleRequestPaymentMethods(saleRequestId: number, saleRequestGateways: PutSaleRequestGateways): Observable<void> {
        this.#salesRequestsState.setSaleRequestPaymentMethodsError(null);
        this.#salesRequestsState.setSaleRequestPaymentMethodsSaving(true);
        return this.#salesRequestsApi.putSaleRequestPaymentMethods(saleRequestId, saleRequestGateways)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestPaymentMethodsError(error);
                    throw error;
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestPaymentMethodsSaving(false))
            );
    }

    loadSaleRequestPurchaseContentImages(saleRequestId: number, language?: string, type?: SaleRequestPurchaseContentImageType): void {
        this.#salesRequestsState.setPurchaseContentImagesLoading(true);
        this.#salesRequestsApi.getPurchaseContentImages(saleRequestId, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setPurchaseContentImagesLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setPurchaseContentImages(contents)
            );
    }

    getSaleRequestPurchaseContentImages$(): Observable<SaleRequestPurchaseContentImage[]> {
        return this.#salesRequestsState.getPurchaseContentImages$();
    }

    isSaleRequestPurchaseContentImagesLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isPurchaseContentImagesLoading$();
    }

    saveSaleRequestPurchaseContentImages(saleRequestId: number, imagesToSave: PutSaleRequestPurchaseContentImage[]): Observable<void> {
        this.#salesRequestsState.setPurchaseContentImagesSaving(true);
        return this.#salesRequestsApi.postPurchaseContentImages(saleRequestId, imagesToSave)
            .pipe(finalize(() => this.#salesRequestsState.setPurchaseContentImagesSaving(false)));
    }

    isSaleRequestPurchaseContentImagesSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isPurchaseContentImagesSaving$();
    }

    deleteSaleRequestPurchaseContentImages(saleRequestId: number, imagesToDelete: PutSaleRequestPurchaseContentImage[]): Observable<void> {
        this.#salesRequestsState.setPurchaseContentImagesRemoving(true);
        return zip(...imagesToDelete.map(request => this.#salesRequestsApi
            .deletePurchaseContentImage(saleRequestId, request.language, request.type))
        )
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setPurchaseContentImagesRemoving(false))
            );
    }

    isSaleRequestPurchaseContentImagesRemoving$(): Observable<boolean> {
        return this.#salesRequestsState.isPurchaseContentImagesRemoving$();
    }

    clearSaleRequestPurchaseContentImages(): void {
        this.#salesRequestsState.setPurchaseContentImages(null);
    }

    loadSaleRequestPurchaseContentTexts(saleRequestId: number): void {
        this.#salesRequestsState.setPurchaseContentTextsLoading(true);
        this.#salesRequestsApi.getPurchaseContentTexts(saleRequestId)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setPurchaseContentTextsLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setPurchaseContentTexts(contents)
            );
    }

    getSaleRequestPurchaseContentTexts$(): Observable<SaleRequestPurchaseContentText[]> {
        return this.#salesRequestsState.getPurchaseContentTexts$();
    }

    isSaleRequestPurchaseContentTextsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isPurchaseContentTextsLoading$();
    }

    saveSaleRequestPurchaseContentTexts(saleRequestId: number, textsToSave: SaleRequestPurchaseContentText[]): Observable<void> {
        this.#salesRequestsState.setPurchaseContentTextsSaving(true);
        return this.#salesRequestsApi.postPurchaseContentTexts(saleRequestId, textsToSave)
            .pipe(finalize(() => this.#salesRequestsState.setPurchaseContentTextsSaving(false)));
    }

    isSaleRequestPurchaseContentTextsSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isPurchaseContentTextsSaving$();
    }

    clearSaleRequestPurchaseContentTexts(): void {
        this.#salesRequestsState.setPurchaseContentTexts(null);
    }

    loadSaleRequestChannelContentLinks(saleRequestId: number): void {
        this.#salesRequestsState.setChannelContentLinksLoading(true);
        this.#salesRequestsApi.getChannelContentLinks(saleRequestId)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setChannelContentLinksLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setChannelContentLinks(contents)
            );
    }

    getSaleRequestChannelContentLinks$(): Observable<ContentLinkRequest[]> {
        return this.#salesRequestsState.getChannelContentLinks$();
    }

    isSaleRequestChannelContentLinksLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isChannelContentLinksLoading$();
    }

    clearSaleRequestChannelContentLinks(): void {
        this.#salesRequestsState.setChannelContentLinks(null);
    }

    loadTicketPdfContentImages(salesRequestId: number, language?: string, type?: SaleRequestTicketContentImageType): void {
        this.#salesRequestsState.setTicketPdfContentImagesLoading(true);
        this.#salesRequestsApi.getTicketContentImages(salesRequestId, SaleRequestTicketContentFormat.pdf, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setTicketPdfContentImagesLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setTicketPdfContentImages(contents)
            );
    }

    clearTicketPdfContentImages(): void {
        this.#salesRequestsState.setTicketPdfContentImages(null);
    }

    getTicketPdfContentImages$(): Observable<SaleRequestTicketContentImage[]> {
        return this.#salesRequestsState.getTicketPdfContentImages$();
    }

    isTicketPdfContentImagesLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPdfContentImagesLoading$();
    }

    saveTicketPdfContentImages(salesRequestId: number, imagesToSave: PutSaleRequestTicketContentImage[]): Observable<void> {
        this.#salesRequestsState.setTicketPdfContentImagesSaving(true);
        return this.#salesRequestsApi.postTicketContentImages(salesRequestId, SaleRequestTicketContentFormat.pdf, imagesToSave)
            .pipe(finalize(() => this.#salesRequestsState.setTicketPdfContentImagesSaving(false)));
    }

    isTicketPdfContentImagesSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPdfContentImagesSaving$();
    }

    deleteTicketPdfContentImages(salesRequestId: number, imagesToDelete: PutSaleRequestTicketContentImage[]): Observable<void> {
        this.#salesRequestsState.setTicketPdfContentImagesRemoving(true);
        return zip(...imagesToDelete.map(request => this.#salesRequestsApi
            .deleteTicketContentImage(salesRequestId, SaleRequestTicketContentFormat.pdf, request.language, request.type))
        )
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setTicketPdfContentImagesRemoving(false))
            );
    }

    isTicketPdfContentImagesRemoving$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPdfContentImagesRemoving$();
    }

    loadTicketPdfContentTemplate(salesRequestId: number): void {
        this.#salesRequestsState.setTicketPdfContentTemplateLoading(true);
        this.#salesRequestsApi.getTicketContentTemplate(salesRequestId, SaleRequestTicketContentFormat.pdf)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setTicketPdfContentTemplateLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setTicketPdfContentTemplate(contents)
            );
    }

    clearTicketPdfContentTemplate(): void {
        this.#salesRequestsState.setTicketPdfContentTemplate(null);
    }

    getTicketPdfContentTemplate$(): Observable<SaleRequestTicketContentTemplate> {
        return this.#salesRequestsState.getTicketPdfContentTemplate$();
    }

    isTicketPdfContentTemplateLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPdfContentTemplateLoading$();
    }

    loadTicketPdfContentTexts(saleRequestId: number): void {
        this.#salesRequestsState.setTicketPdfContentTextsLoading(true);
        this.#salesRequestsApi.getTicketContentTexts(saleRequestId, SaleRequestTicketContentFormat.pdf)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setTicketPdfContentTextsLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setTicketPdfContentTexts(contents)
            );
    }

    clearTicketPdfContentTexts(): void {
        this.#salesRequestsState.setTicketPdfContentTexts(null);
    }

    getTicketPdfContentTexts$(): Observable<SaleRequestTicketContentText[]> {
        return this.#salesRequestsState.getTicketPdfContentTexts$();
    }

    isTicketPdfContentTextsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPdfContentTextsLoading$();
    }

    loadTicketPrinterContentImages(salesRequestId: number, language?: string, type?: SaleRequestTicketContentImageType): void {
        this.#salesRequestsState.setTicketPrinterContentImagesLoading(true);
        this.#salesRequestsApi.getTicketContentImages(salesRequestId, SaleRequestTicketContentFormat.printer, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setTicketPrinterContentImagesLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setTicketPrinterContentImages(contents)
            );
    }

    clearTicketPrinterContentImages(): void {
        this.#salesRequestsState.setTicketPrinterContentImages(null);
    }

    getTicketPrinterContentImages$(): Observable<SaleRequestTicketContentImage[]> {
        return this.#salesRequestsState.getTicketPrinterContentImages$();
    }

    isTicketPrinterContentImagesLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPrinterContentImagesLoading$();
    }

    saveTicketPrinterContentImages(salesRequestId: number, imagesToSave: PutSaleRequestTicketContentImage[]): Observable<void> {
        this.#salesRequestsState.setTicketPrinterContentImagesSaving(true);
        return this.#salesRequestsApi.postTicketContentImages(salesRequestId, SaleRequestTicketContentFormat.printer, imagesToSave)
            .pipe(finalize(() => this.#salesRequestsState.setTicketPrinterContentImagesSaving(false)));
    }

    isTicketPrinterContentImagesSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPrinterContentImagesSaving$();
    }

    deleteTicketPrinterContentImages(salesRequestId: number, imagesToDelete: PutSaleRequestTicketContentImage[]): Observable<void> {
        this.#salesRequestsState.setTicketPrinterContentImagesRemoving(true);
        return zip(...imagesToDelete.map(request => this.#salesRequestsApi
            .deleteTicketContentImage(salesRequestId, SaleRequestTicketContentFormat.printer, request.language, request.type))
        )
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setTicketPrinterContentImagesRemoving(false))
            );
    }

    isTicketPrinterContentImagesRemoving$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPrinterContentImagesRemoving$();
    }

    loadTicketPrinterContentTemplate(salesRequestId: number): void {
        this.#salesRequestsState.setTicketPrinterContentTemplateLoading(true);
        this.#salesRequestsApi.getTicketContentTemplate(salesRequestId, SaleRequestTicketContentFormat.printer)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setTicketPrinterContentTemplateLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setTicketPrinterContentTemplate(contents)
            );
    }

    clearTicketPrinterContentTemplate(): void {
        this.#salesRequestsState.setTicketPrinterContentTemplate(null);
    }

    getTicketPrinterContentTemplate$(): Observable<SaleRequestTicketContentTemplate> {
        return this.#salesRequestsState.getTicketPrinterContentTemplate$();
    }

    isTicketPrinterContentTemplateLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPrinterContentTemplateLoading$();
    }

    loadTicketPrinterContentTexts(saleRequestId: number): void {
        this.#salesRequestsState.setTicketPrinterContentTextsLoading(true);
        this.#salesRequestsApi.getTicketContentTexts(saleRequestId, SaleRequestTicketContentFormat.printer)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setTicketPrinterContentTextsLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setTicketPrinterContentTexts(contents)
            );
    }

    clearTicketPrinterContentTexts(): void {
        this.#salesRequestsState.setTicketPrinterContentTexts(null);
    }

    getTicketPrinterContentTexts$(): Observable<SaleRequestTicketContentText[]> {
        return this.#salesRequestsState.getTicketPrinterContentTexts$();
    }

    isTicketPrinterContentTextsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPrinterContentTextsLoading$();
    }

    downloadTicketPdfPreview$(saleRequestId: number, language: string): Observable<{ url: string }> {
        this.#salesRequestsState.setTicketPdfPreviewDownloading(true);

        return this.#salesRequestsApi.downloadTicketPdfPreview$(saleRequestId, SaleRequestTicketContentFormat.pdf, language).pipe(
            finalize(() => this.#salesRequestsState.setTicketPdfPreviewDownloading(false))
        );
    }

    isTicketPdfPreviewDownloading$(): Observable<boolean> {
        return this.#salesRequestsState.isTicketPdfPreviewDownloading$();
    }

    //Delivery Conditions
    isSaleRequestDeliveryConditionsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestDeliveryConditionsLoading$();
    }

    isSaleRequestDeliveryConditionsSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestDeliveryConditionsSaving$();
    }

    clearSaleRequestDeliveryConditions(): void {
        this.#salesRequestsState.setSaleRequestDeliveryConditions(null);
    }

    getSaleRequestDeliveryConditions$(): Observable<SaleRequestDeliveryConditions> {
        return this.#salesRequestsState.getSaleRequestDeliveryConditions$();
    }

    loadSaleRequestDeliveryConditions(saleRequestId: number): void {
        this.#salesRequestsState.setSaleRequestDeliveryConditionsError(null);
        this.#salesRequestsState.setSaleRequestDeliveryConditionsLoading(true);
        this.#salesRequestsApi.getSaleRequestDeliveryConditions(saleRequestId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestDeliveryConditionsError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestDeliveryConditionsLoading(false))
            )
            .subscribe(deliveryConditions =>
                this.#salesRequestsState.setSaleRequestDeliveryConditions(deliveryConditions)
            );
    }

    saveSaleRequestDeliveryConditions(saleRequestId: number, deliveryConditions: SaleRequestDeliveryConditions): Observable<void> {
        this.#salesRequestsState.setSaleRequestDeliveryConditionsError(null);
        this.#salesRequestsState.setSaleRequestDeliveryConditionsSaving(true);
        return this.#salesRequestsApi.putSaleRequestDeliveryConditions(saleRequestId, deliveryConditions)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestDeliveryConditionsError(error);
                    throw error;
                }),
                finalize(() => {
                    this.#salesRequestsState.setSaleRequestDeliveryConditionsSaving(false);
                    this.#salesRequestsState.setSaleRequestDeliveryConditions(deliveryConditions);
                }
                )
            );
    }

    //Additional Conditions
    isSaleRequestAdditionalConditionsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestAdditionalConditionsLoading$();
    }

    isSaleRequestAdditionalConditionsSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestAdditionalConditionsSaving$();
    }

    clearSaleRequestAdditionalConditions(): void {
        this.#salesRequestsState.setSaleRequestAdditionalConditions(null);
    }

    getSaleRequestAdditionalConditions$(): Observable<AdditionalCondition[]> {
        return this.#salesRequestsState.getSaleRequestAdditionalConditions$();
    }

    loadSaleRequestAdditionalConditions(saleRequestId: number): void {
        this.#salesRequestsState.setSaleRequestAdditionalConditionsError(null);
        this.#salesRequestsState.setSaleRequestAdditionalConditionsLoading(true);
        this.#salesRequestsApi.getSaleRequestAdditionalConditions(saleRequestId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestAdditionalConditionsError(error);
                    return of(null);
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestAdditionalConditionsLoading(false))
            )
            .subscribe(additionalConditions =>
                this.#salesRequestsState.setSaleRequestAdditionalConditions(additionalConditions)
            );
    }

    updateSaleRequestAdditionalConditions(saleRequestId: number, additionalConditions: AdditionalCondition[]): Observable<void> {
        this.#salesRequestsState.setSaleRequestAdditionalConditionsError(null);
        this.#salesRequestsState.setSaleRequestAdditionalConditionsSaving(true);
        return forkJoin(additionalConditions.map(condition =>
            this.#salesRequestsApi.putAdditionalCondition(saleRequestId, condition.id, {
                ...condition,
                id: undefined
            })
        ))
            .pipe(
                mapTo(undefined),
                catchError(error => {
                    this.#salesRequestsState.setSaleRequestAdditionalConditionsError(error);
                    throw error;
                }),
                finalize(() => this.#salesRequestsState.setSaleRequestAdditionalConditionsSaving(false))
            );
    }

    createAdditionalCondition(saleRequestId: number, additionalCondition: AdditionalCondition): Observable<{ id: number }> {
        this.#salesRequestsState.setAdditionalConditionSaving(true);
        this.#salesRequestsState.setAdditionalConditionError(null);
        return this.#salesRequestsApi.postAdditionalCondition(saleRequestId, additionalCondition)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setAdditionalConditionError(error);
                    throw error;
                }),
                finalize(() => this.#salesRequestsState.setAdditionalConditionSaving(false))
            );
    }

    updateAdditionalCondition(channelId: number, adCondId: number, additionalCondition: AdditionalCondition): Observable<void> {
        this.#salesRequestsState.setAdditionalConditionSaving(true);
        this.#salesRequestsState.setAdditionalConditionError(null);
        return this.#salesRequestsApi.putAdditionalCondition(channelId, adCondId, additionalCondition)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setAdditionalConditionError(error);
                    throw error;
                }),
                finalize(() => this.#salesRequestsState.setAdditionalConditionSaving(false))
            );
    }

    isAdditionalConditionSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isAdditionalConditionSaving$();
    }

    getAdditionalConditionError$(): Observable<HttpErrorResponse> {
        return this.#salesRequestsState.getAdditionalConditionError$();
    }

    isAdditionalConditionsInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this.#salesRequestsState.isAdditionalConditionsLoading$(),
            this.#salesRequestsState.isAdditionalConditionSaving$()
        ]);
    }

    deleteAdditionalCondition(channelId: number, conditionId: number): Observable<void> {
        this.#salesRequestsState.setAdditionalConditionSaving(true);
        this.#salesRequestsState.setAdditionalConditionError(null);
        return this.#salesRequestsApi.deleteAdditionalCondition(channelId, conditionId)
            .pipe(
                catchError(error => {
                    this.#salesRequestsState.setAdditionalConditionError(error);
                    throw error;
                }),
                finalize(() => this.#salesRequestsState.setAdditionalConditionSaving(false))
            );
    }

    loadEmailContentText(salesRequestId: number): void {
        this.#salesRequestsState.setTicketPdfContentImagesLoading(true);
        this.#salesRequestsApi.getEmailContentText(salesRequestId)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setSaleRequestEmailContentTextsLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setSaleRequestEmailContentTexts(contents)
            );
    }

    clearEmailContentText(): void {
        this.#salesRequestsState.setSaleRequestEmailContentTexts(null);
    }

    getEmailContentText$(): Observable<SaleRequestEmailContentTexts[]> {
        return this.#salesRequestsState.getSaleRequestEmailContentTexts$();
    }

    isEmailContentTextLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestEmailContentTextsLoading$();
    }

    saveEmailContentText(salesRequestId: number, textsToSave: SaleRequestEmailContentTexts[]): Observable<void> {
        this.#salesRequestsState.setSaleRequestEmailContentTextsSaving(true);
        return this.#salesRequestsApi.postEmailContentText(salesRequestId, textsToSave)
            .pipe(finalize(() => this.#salesRequestsState.setSaleRequestEmailContentTextsSaving(false)));
    }

    isEmailContentTextSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestEmailContentTextsSaving$();
    }

    loadSaleRequestAdditionalBannerTexts(saleRequestId: number, language?: string): void {
        this.#salesRequestsState.setSaleRequestAdditionalBannerTextsLoading(true);
        this.#salesRequestsApi.getAdditionalBannerTexts(saleRequestId, language)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this.#salesRequestsState.setSaleRequestAdditionalBannerTextsLoading(false))
            )
            .subscribe(contents =>
                this.#salesRequestsState.setSaleRequestAdditionalBannerTexts(contents)
            );
    }

    getSaleRequestAdditionalBannerTexts$(): Observable<SaleRequestAdditionalBannerTexts[]> {
        return this.#salesRequestsState.getSaleRequestAdditionalBannerTexts$();
    }

    isSaleRequestAdditionalBannerTextsLoading$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestAdditionalBannerTextsLoading$();
    }

    saveSaleRequestAdditionalBannerTexts(saleRequestId: number, textsToSave: SaleRequestAdditionalBannerTexts[]): Observable<void> {
        this.#salesRequestsState.setSaleRequestAdditionalBannerTextsSaving(true);
        return this.#salesRequestsApi.postAdditionalBannerTexts(saleRequestId, textsToSave)
            .pipe(finalize(() => this.#salesRequestsState.setSaleRequestAdditionalBannerTextsSaving(false)));
    }

    isSaleRequestAdditionalBannerTextsSaving$(): Observable<boolean> {
        return this.#salesRequestsState.isSaleRequestAdditionalBannerTextsSaving$();
    }

    clearSaleRequestAdditionalBannerTexts(): void {
        this.#salesRequestsState.setSaleRequestAdditionalBannerTexts(null);
    }

    #isTheSameRequest(request: GetFilterRequest, requestNew: GetFilterRequest): boolean {
        for (const [key, value] of Object.entries(request)) {
            if (requestNew[key] !== value) {
                return false;
            }
        }
        for (const [key, value] of Object.entries(requestNew)) {
            if (request[key] !== value) {
                return false;
            }
        }
        return true;
    }
}
