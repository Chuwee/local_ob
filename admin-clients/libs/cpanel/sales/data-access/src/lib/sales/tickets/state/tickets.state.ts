import { StateProperty } from '@OneboxTM/utils-state';
import { TicketsBaseState } from '@admin-clients/shared/common/data-access';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { GetFilterResponse } from '../../models/get-filter-response.model';
import { GetFilterSessionDataResponse } from '../models/get-filter-session-data-response.model';
import { TicketRelocation } from '../models/ticket-relocation.model';

@Injectable()
export class TicketsState extends TicketsBaseState {
    readonly filterCurrencyList = new StateProperty<GetFilterResponse>();
    readonly filterChannelEntityList = new StateProperty<GetFilterResponse>();
    readonly filterEventEntityList = new StateProperty<GetFilterResponse>();
    readonly filterChannelList = new StateProperty<GetFilterResponse>();
    readonly filterEventList = new StateProperty<GetFilterResponse>();
    readonly filterSessionList = new StateProperty<GetFilterResponse>();
    readonly filterSectorList = new StateProperty<GetFilterSessionDataResponse>();
    readonly filterPriceTypeList = new StateProperty<GetFilterSessionDataResponse>();
    readonly filterClientList = new StateProperty<GetFilterResponse>();
    readonly ticketRelocations = new StateProperty<TicketRelocation[]>()
    readonly channelEntityCache = new ItemCache<FilterOption>();
    readonly eventEntityCache = new ItemCache<FilterOption>();
    readonly channelCache = new ItemCache<FilterOption>();
    readonly eventCache = new ItemCache<FilterOption>();
    readonly sessionCache = new ItemCache<FilterOption>();
    readonly sectorCache = new ItemCache<FilterOption>();
    readonly priceTypeCache = new ItemCache<FilterOption>();
    readonly clientCache = new ItemCache<FilterOption>();
    readonly currencyCache = new ItemCache<FilterOption>();
}
