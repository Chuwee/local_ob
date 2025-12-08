import { StateProperty } from '@OneboxTM/utils-state';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import {
    GetB2bClientsResponse, GetB2bConditionsClientsResponse,
    B2bConditions, B2bClient, B2bClientUser, B2bConditionsClient, GetB2bClientUsersResponse, B2bClientBalance,
    GetB2bClientTransactionsResponse, B2bClientTransactionsExportResponse, GetB2bSeatsListResponse, B2bSeat, GetB2bSeatsFiltersResponse
} from '../models';

@Injectable()
export class B2bState {
    readonly b2bClientsList = new StateProperty<GetB2bClientsResponse>();
    readonly b2bConditionsClients = new StateProperty<GetB2bConditionsClientsResponse>();
    readonly allB2bConditionsClients = new StateProperty<GetB2bConditionsClientsResponse>();
    readonly b2bConditions = new StateProperty<B2bConditions>();
    readonly b2bClient = new StateProperty<B2bClient>();
    readonly b2bClientUsersList = new StateProperty<GetB2bClientUsersResponse>();
    readonly b2bClientUser = new StateProperty<B2bClientUser>();
    readonly b2bClientUserPassword = new StateProperty<void>();
    readonly b2bConditionsClient = new StateProperty<B2bConditionsClient>();
    readonly b2bClientBalance = new StateProperty<B2bClientBalance>();
    readonly b2bClientTransactionsList = new StateProperty<GetB2bClientTransactionsResponse>();
    readonly b2bClientTransactionsExport = new StateProperty<B2bClientTransactionsExportResponse>();
    readonly b2bClientBalanceOperation = new StateProperty<void>();
    readonly b2bSeatsList = new StateProperty<GetB2bSeatsListResponse>();
    readonly exportB2bSeatsList = new StateProperty<boolean>();
    readonly b2bSeat = new StateProperty<B2bSeat>();
    readonly b2bSeatFilterEventList = new StateProperty<GetB2bSeatsFiltersResponse>();
    readonly b2bSeatFilterSessionList = new StateProperty<GetB2bSeatsFiltersResponse>();
    readonly clientsCache = new ItemCache<IdName>();
    readonly b2bClientUserApiKey = new StateProperty<string>();
}
