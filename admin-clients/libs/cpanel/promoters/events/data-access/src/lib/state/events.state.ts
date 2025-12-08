import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { CustomerTypeAssignation, Rate, RateRestrictions, RatesExternalType } from '@admin-clients/cpanel/promoters/shared/data-access';
import { AttributeWithValues } from '@admin-clients/shared/common/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { GetEventAttendantFields } from '../models/attendants-fields.model';
import { EventAdditionalConfig } from '../models/event-additional-config.model';
import { EventExternalBarcodes } from '../models/event-external-barcodes.model';
import { EventPrice } from '../models/event-price.model';
import { EventSurcharge } from '../models/event-surcharge.model';
import { Event } from '../models/event.model';
import { GetEventsResponse } from '../models/get-events-response.model';
import { RateGroup } from '../models/rate-group.model';

@Injectable()
export class EventsState {
    readonly ratesGroup = new StateProperty<RateGroup[]>();
    readonly sgaProducts = new StateProperty<RateGroup[]>();
    readonly eventsList = new StateProperty<GetEventsResponse>();
    readonly event = new StateProperty<Event>();
    readonly eventPrices = new StateProperty<EventPrice[]>();
    readonly eventRates = new StateProperty<Rate[]>();
    readonly eventRatesWithRestrictions = new StateProperty<ListResponse<IdName>>();
    readonly eventRateRestriction = new StateProperty<ListResponse<RateRestrictions>>();
    readonly eventSurcharges = new StateProperty<EventSurcharge[]>();
    readonly eventAdditionalConfig = new StateProperty<EventAdditionalConfig>();
    readonly eventAttributes = new StateProperty<AttributeWithValues[]>();
    readonly eventAttendantFields = new StateProperty<GetEventAttendantFields>();
    readonly eventExternalBarcodes = new StateProperty<EventExternalBarcodes>();
    readonly eventsCache = new ItemCache<Event>();
    readonly eventCustomerTypesAssignation = new StateProperty<CustomerTypeAssignation[]>();
    readonly eventRatesExternalTypes = new StateProperty<RatesExternalType[]>();
}
