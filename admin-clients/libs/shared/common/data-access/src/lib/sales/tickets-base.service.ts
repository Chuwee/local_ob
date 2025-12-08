import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import {
    AggregatedData,
    AggregationMetrics,
    combineAggregatedData,
    ExportRequest
} from '@admin-clients/shared/data-access/models';
import { runWithRetriesIfNull$ } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import moment from 'moment/moment';
import { combineLatest, Observable, concat, filter, map, mergeMap, of, switchMap, take, toArray } from 'rxjs';
import { TicketsBaseApi } from './api/tickets-base.api';
import { GetTicketActionsRequest } from './models/get-ticket-actions-request.model';
import { GetTicketsRequest } from './models/get-tickets-request.model';
import { OrderItemDetails } from './models/order-item-details.model';
import { OrderType } from './models/order-type.enum';
import { TicketDetailType } from './models/ticket-detail-type.enum';
import { TicketDetail } from './models/ticket-detail.model';
import { aggDataTicket } from './models/tickets-aggregated-data';
import {
    DeleteTicketTransferRequest,
    PostTicketReleaseRequest,
    PostTicketTransferRequest
} from './models/transfer-ticket-request.model';
import { TicketsBaseState } from './state/tickets-base.state';
import { RenewalDetails } from './models/ticket-seat-management-data.model';

@Injectable()
export class TicketsBaseService {
    readonly currencyAggregatedData = Object.freeze({
        load: (request: GetTicketsRequest) => {
            const req = this.prepareTicketRequest(request);

            StateManager.load(
                this._ticketsBaseState.aggregations,
                this._ticketsBaseApi.getAggregations(req)
            );
        },
        loading$: () => this._ticketsBaseState.aggregations.isInProgress$(),
        clear: () => this._ticketsBaseState.aggregations.setValue(null),
        getCombined$: (aggDataMetrics = aggDataTicket) => combineLatest([
            this._ticketsBaseState.aggregations.getValue$().pipe(filter(Boolean)),
            this._ticketsBaseState.ticketList.getValue$().pipe(
                map(tickets => tickets?.aggregated_data),
                filter(Boolean)
            )
        ]).pipe(
            map(([currencyAggregatedData, aggregatedData]) =>
                combineAggregatedData(currencyAggregatedData, aggregatedData, aggDataMetrics)
            )
        )
    });

    readonly weeklyticketListAggregatedData = Object.freeze({
        load: (request: GetTicketsRequest, lastDay: moment.Moment) => {
            const baseRequest = this.prepareTicketRequest(request);

            StateManager.load(
                this._ticketsBaseState.weeklyTicketListAggregations,
                concat(
                    ...Array(7).fill(null).map((_, daysAgo) => {
                        const dayRequest: GetTicketsRequest = {
                            ...baseRequest,
                            purchase_date_from: lastDay.clone().subtract(daysAgo, 'days').startOf('day').toJSON(),
                            purchase_date_to: lastDay.clone().subtract(daysAgo, 'days').endOf('day').toJSON()
                        };
                        return this._ticketsBaseApi.getTickets(dayRequest);
                    })
                ).pipe(
                    mergeMap((response, index) =>
                        // TODO: To unify the code, assemble the response as
                        //       the weeklyCurrencyAggregatedData function of the orders.service
                        of({
                            aggData: response,
                            weekDay: lastDay.clone().subtract(index, 'days').isoWeekday()
                        })
                    ),
                    toArray()
                )
            );
        },
        get$: () => this._ticketsBaseState.weeklyTicketListAggregations.getValue$(),
        clear: () => {
            this._ticketsBaseState.weeklyTicketListAggregations.setValue(null);
        },
        loading$: () => this._ticketsBaseState.weeklyTicketListAggregations.isInProgress$()
    });

    readonly ticketList = Object.freeze({
        load: (request: GetTicketsRequest, relevance = false): void => {

            if (relevance && request.q) {
                request.sort = null; // ordenación default por relevancia en búsqueda por parámetro q
            }

            const req = this.prepareTicketRequest(request);

            StateManager.load(
                this._ticketsBaseState.ticketList,
                this._ticketsBaseApi.getTickets(req).pipe(mapMetadata())
            );
        },
        getData$: () => this._ticketsBaseState.ticketList.getValue$().pipe(getListData()),
        getMetaData$: () => this._ticketsBaseState.ticketList.getValue$().pipe(getMetadata()),
        getAggregatedData$: (aggregatedMetric: AggregationMetrics) => this._ticketsBaseState.ticketList.getValue$()
            .pipe(map(tickets => tickets?.aggregated_data && new AggregatedData(tickets.aggregated_data, aggregatedMetric))),
        export: (request: GetTicketsRequest, data: ExportRequest) => {
            request.type = request.type?.length ? request.type : [TicketDetailType.seat, TicketDetailType.group];

            return StateManager.inProgress(
                this._ticketsBaseState.exportTickets,
                this._ticketsBaseApi.exportTickets(request, data)
            );
        },
        exportActions: (request: GetTicketActionsRequest, data: ExportRequest) => StateManager.inProgress(
            this._ticketsBaseState.exportTicketActions,
            this._ticketsBaseApi.exportTicketActions(request, data)
        ),
        error$: () => this._ticketsBaseState.ticketList.getError$(),
        loading$: () => this._ticketsBaseState.ticketList.isInProgress$(),
        loadingExport$: () => this._ticketsBaseState.exportTickets.isInProgress$(),
        clear: () => this._ticketsBaseState.ticketList.setValue(null)
    });

    readonly ticketDetail = Object.freeze({
        load: (orderCode: string, ticketId: string) => StateManager.load(
            this._ticketsBaseState.ticketDetail,
            this._ticketsBaseApi.getTicket(orderCode, ticketId)
        ),
        get$: () => this._ticketsBaseState.ticketDetail.getValue$(),
        link: {
            get$: () => StateManager.inProgress(
                this._ticketsBaseState.ticketLink,
                this._ticketsBaseState.ticketDetail.getValue$().pipe(
                    filter(ticketDetail => ticketDetail !== null),
                    take(1),
                    switchMap(ticketDetail => this.tryToGetTicketLink$(ticketDetail.order.code, ticketDetail.id))
                )
            ),
            loading$: () => this._ticketsBaseState.ticketLink.isInProgress$()
        },
        stateHistory: {
            load: (orderCode: string, ticketId: string) => StateManager.load(
                this._ticketsBaseState.ticketStateHistory,
                this._ticketsBaseApi.getTicketStateHistory(orderCode, ticketId).pipe(mapMetadata())
            ),
            get$: () => this._ticketsBaseState.ticketStateHistory.getValue$(),
            getData$: () => this._ticketsBaseState.ticketStateHistory.getValue$().pipe(map(ticketState => ticketState?.data)),
            getMetaData$: () => this._ticketsBaseState.ticketStateHistory.getValue$().pipe(map(ticketState => ticketState?.metadata)),
            error$: () => this._ticketsBaseState.ticketStateHistory.getError$(),
            loading$: () => this._ticketsBaseState.ticketStateHistory.isInProgress$(),
            clear: () => this._ticketsBaseState.ticketStateHistory.setValue(null)
        },
        attendeeHistory: {
            load: (orderCode: string, ticketId: number) => StateManager.load(
                this._ticketsBaseState.ticketAttendeeHistory,
                this._ticketsBaseApi.getTicketAttendeeHistory$(orderCode, ticketId)
            ),
            get$: () => this._ticketsBaseState.ticketAttendeeHistory.getValue$(),
            error$: () => this._ticketsBaseState.ticketAttendeeHistory.getError$(),
            loading$: () => this._ticketsBaseState.ticketAttendeeHistory.isInProgress$(),
            clear: () => this._ticketsBaseState.ticketAttendeeHistory.setValue(null)
        },
        attendeeFields: {
            load: (orderCode: string, ticketId: number) => StateManager.load(
                this._ticketsBaseState.ticketAttendeeFields,
                this._ticketsBaseApi.getTicketAttendeeFields$(orderCode, ticketId).pipe(
                    map(fields => new Map(Object.keys(fields).map(fieldKey => [fieldKey, fields[fieldKey]])))
                )
            ),
            get$: () => this._ticketsBaseState.ticketAttendeeFields.getValue$(),
            loading$: () => this._ticketsBaseState.ticketAttendeeFields.isInProgress$(),
            clear: () => this._ticketsBaseState.ticketAttendeeFields.setValue(null)
        },
        editTicketAttendant$: (orderCode: string, ticketId: string, attendantData: Record<string, string>) => StateManager.inProgress(
            this._ticketsBaseState.ticketAttendantEdit,
            this._ticketsBaseApi.postTicketAttendant(orderCode, ticketId, attendantData)),
        set: (ticketDetail: TicketDetail) => this._ticketsBaseState.ticketDetail.setValue(ticketDetail),
        error$: () => this._ticketsBaseState.ticketDetail.getError$(),
        loading$: () => this._ticketsBaseState.ticketDetail.isInProgress$(),
        clear: () => this._ticketsBaseState.ticketDetail.setValue(null),
        renewalDetails: {
            update: (orderCode: string, ticketId: string, renewalDetails: RenewalDetails) => StateManager.inProgress(
                this._ticketsBaseState.renewalDetails,
                this._ticketsBaseApi.putRenewalDetails(orderCode, ticketId, renewalDetails)
            ),
            loading$: () => this._ticketsBaseState.renewalDetails.isInProgress$()
        }
    });

    readonly ticketTransfer = Object.freeze({
        transfer$: (request: PostTicketTransferRequest) => StateManager.inProgress(
            this._ticketsBaseState.ticketTransfer,
            this._ticketsBaseApi.transfer(request)),
        getPdf$: (code: string, itemId: number, sessionId: number) => StateManager.inProgress(
            this._ticketsBaseState.transferLink,
            this.tryToGetTransferLink$(code, itemId, sessionId)),
        delete$: (request: DeleteTicketTransferRequest) =>
            StateManager.inProgress(
                this._ticketsBaseState.ticketDeleteTransfer,
                this._ticketsBaseApi.deleteTransfer(request)
            )
        ,
        deleteLoading$: () => this._ticketsBaseState.ticketDeleteTransfer.isInProgress$(),
        resendEmail$: (code: string, itemId: number, sessionId: number) => StateManager.inProgress(
            this._ticketsBaseState.ticketResendEmail,
            this._ticketsBaseApi.resendTransferEmail(code, itemId, sessionId)),
        resendEmailLoading$: () => this._ticketsBaseState.ticketResendEmail.isInProgress$(),
        linkLoading$: () => this._ticketsBaseState.transferLink.isInProgress$(),
        error$: () => this._ticketsBaseState.transferLink.getError$(),
        loading$: () => this._ticketsBaseState.ticketTransfer.isInProgress$(),
        clear: () => this._ticketsBaseState.ticketTransfer.setValue(null)
    });

    readonly ticketRelease = Object.freeze({
        release$: (request: PostTicketReleaseRequest) => StateManager.inProgress(
            this._ticketsBaseState.ticketRelease,
            this._ticketsBaseApi.release(request)),
        delete$: (request: DeleteTicketTransferRequest) =>
            StateManager.inProgress(
                this._ticketsBaseState.ticketDeleteRelease,
                this._ticketsBaseApi.deleteRelease(request)
            )
        ,
        deleteLoading$: () => this._ticketsBaseState.ticketDeleteRelease.isInProgress$(),
        loading$: () => this._ticketsBaseState.ticketRelease.isInProgress$(),
        clear: () => this._ticketsBaseState.ticketRelease.setValue(null)
    });

    constructor(
        private _ticketsBaseApi: TicketsBaseApi,
        private _ticketsBaseState: TicketsBaseState
    ) { }

    getLastSessionSeatOrderItem(request: GetTicketsRequest): Observable<OrderItemDetails> {
        const req = this.prepareTicketRequest(request);
        return this._ticketsBaseApi.getTickets(req).pipe(
            getListData(),
            map(tickets => tickets?.filter(ticket => ticket?.order.type !== OrderType.refund)),
            filter(tickets => tickets?.length > 0),
            map(tickets => this.getNewestOrderItem(tickets))
        );
    }

    private getNewestOrderItem(items: OrderItemDetails[]): OrderItemDetails {
        return items.reduce((newest, current) => {
            if (!current?.order?.date) return newest;
            if (!newest) return current;
            return moment(current.order.date).isAfter(newest.order.date) ? current : newest;
        });
    }

    /**
     * Prepares a GetTicketsRequest with default values used throughout the ticket module.
     *
     * - Removes channel_entity_id if channel_id is present.
     * - Removes event_entity_id if event_id is present.
     * - Defaults `type` to [SEAT, GROUP] if not provided.
     * - Clears `sort` if searching by relevance (when relevance is true and `q` is present).
     *
     * @param request The original GetTicketsRequest object
     * @param relevance If true and `q` is present, removes `sort` to enforce relevance ordering
     * @returns A new GetTicketsRequest with normalized values
     */
    private prepareTicketRequest(request: GetTicketsRequest): GetTicketsRequest {
        return {
            ...request,
            channel_entity_id: request.channel_id?.length ? undefined : request.channel_entity_id,
            event_entity_id: request.event_id?.length ? undefined : request.event_entity_id,
            type: request.type?.length ? request.type : [TicketDetailType.seat, TicketDetailType.group]
        };
    }

    private tryToGetTransferLink$(code: string, itemId: number, sessionId: number): Observable<string> {
        return runWithRetriesIfNull$(() => this._ticketsBaseApi.getTransferPdf(code, itemId, sessionId), 5, 1000)
            .pipe(map(response => {
                if (response) {
                    return response.tickets[0].download_link;
                } else {
                    return null;
                }
            }));
    }

    private tryToGetTicketLink$(code: string, id: number): Observable<string> {
        return runWithRetriesIfNull$(() => this._ticketsBaseApi.getTicketPdf(code, id), 3, 1000)
            .pipe(map(response => {
                if (response && Array.isArray(response.tickets) && response.tickets.length) {
                    return response.tickets[0].download_link;
                }
                return null;
            }));
    }
}
