import { StateProperty } from '@OneboxTM/utils-state';
import { AggregatedData, ResponseAggregatedData } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { GetStateHistoryResponse } from '../models/get-state-history-response.model';
import { GetTicketsResponse } from '../models/get-tickets-response.model';
import { TicketAttendeeHistory } from '../models/ticket-detail-attendees-history.model';
import { TicketDetail } from '../models/ticket-detail.model';
import { RenewalDetails } from '../models/ticket-seat-management-data.model';

@Injectable()
export class TicketsBaseState {
    readonly aggregations = new StateProperty<ResponseAggregatedData>();
    readonly weeklyTicketListAggregations = new StateProperty<{ aggData: GetTicketsResponse; weekDay: number }[]>();
    readonly ticketList = new StateProperty<GetTicketsResponse>();
    readonly exportTickets = new StateProperty<boolean>();
    readonly exportTicketActions = new StateProperty<boolean>();
    readonly ticketDetail = new StateProperty<TicketDetail>();
    readonly ticketLink = new StateProperty();
    readonly ticketAttendeeHistory = new StateProperty<TicketAttendeeHistory[]>();
    readonly ticketStateHistory = new StateProperty<GetStateHistoryResponse>();
    readonly ticketAttendeeFields = new StateProperty<Map<string, string>>();
    readonly ticketAttendantEdit = new StateProperty<boolean>();
    readonly ticketTransfer = new StateProperty<void>();
    readonly transferLink = new StateProperty<boolean>();
    readonly ticketDeleteTransfer = new StateProperty<void>();
    readonly ticketResendEmail = new StateProperty();
    readonly ticketRelease = new StateProperty<void>();
    readonly ticketDeleteRelease = new StateProperty<void>();
    readonly weekAggregates = new StateProperty<{ aggData: AggregatedData; weekDay: number }[]>();
    readonly renewalDetails = new StateProperty<RenewalDetails>();
}
