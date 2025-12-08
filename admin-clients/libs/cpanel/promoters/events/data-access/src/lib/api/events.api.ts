import { buildHttpParams, getRangeParam } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import {
    CustomerTypeAssignation, PutCustomerTypeAssignation, Rate, RateRestrictions, RatesExternalType
} from '@admin-clients/cpanel/promoters/shared/data-access';
import { AttributeWithValues, PutAttribute } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { Id } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EventAttendantField, GetEventAttendantFields } from '../models/attendants-fields.model';
import { EventAdditionalConfig } from '../models/event-additional-config.model';
import { EventExternalBarcodes } from '../models/event-external-barcodes.model';
import { EventPrice } from '../models/event-price.model';
import { EventSurcharge } from '../models/event-surcharge.model';
import { Event } from '../models/event.model';
import { GetEventPricesRequest } from '../models/get-event-prices.request';
import { GetEventsRequest } from '../models/get-events-request.model';
import { GetEventsResponse } from '../models/get-events-response.model';
import { PostEventRate } from '../models/post-event-rate.model';
import { PostEvent } from '../models/post-event.model';
import { PutEventPrice } from '../models/put-event-price.model';
import { PutEvent } from '../models/put-event.model';
import { PostRateGroup, PutRateGroup, RateGroup } from '../models/rate-group.model';

@Injectable()
export class EventsApi {
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #EVENTS_API = `${this.#BASE_API}/mgmt-api/v1/events`;
    readonly #http = inject(HttpClient);

    getEvents(request: GetEventsRequest): Observable<GetEventsResponse> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            type: request.type,
            entity_id: request.entityId,
            producer_id: request.producerId,
            venue_id: request.venueId,
            country: request.country,
            city: request.city,
            include_archived: request.includeArchived,
            fields: request.fields,
            status: request.status,
            start_date: getRangeParam(request.startDate, request.endDate),
            currency_code: request.currency
        });
        return this.#http.get<GetEventsResponse>(this.#EVENTS_API, { params });
    }

    postEvent(event: PostEvent): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(this.#EVENTS_API, event);
    }

    getEvent(eventId: string): Observable<Event> {
        return this.#http.get<Event>(`${this.#EVENTS_API}/${eventId}`);
    }

    putEvent(id: number, event: PutEvent): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${id}`, event);
    }

    deleteEvent(eventId: string): Observable<void> {
        return this.#http.delete<void>(`${this.#EVENTS_API}/${eventId}`);
    }

    getEventPrices(eventId: string, tplId: string, request?: GetEventPricesRequest): Observable<EventPrice[]> {
        const params = buildHttpParams({
            session_id: request?.sessionId,
            rate_group_id: request?.rateGroupId,
            rate_group_product_id: request?.productId
        });
        return this.#http.get<EventPrice[]>(
            `${this.#EVENTS_API}/${eventId}/venue-templates/${tplId}/prices`, { params });
    }

    putEventPrices(eventId: string, tplId: string, prices: PutEventPrice[]): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${eventId}/venue-templates/${tplId}/prices`, prices);
    }

    getEventRates(eventId: string): Observable<Rate[]> {
        return this.#http.get<Rate[]>(`${this.#EVENTS_API}/${eventId}/rates`);
    }

    getEventRatesExternalTypes(eventId: string): Observable<RatesExternalType[]> {
        return this.#http.get<RatesExternalType[]>(`${this.#EVENTS_API}/${eventId}/rates/external-types`);
    }

    putEventRates(eventId: string, rates: Partial<Rate>[]): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${eventId}/rates`, rates);
    }

    postEventRate(eventId: string, rate: PostEventRate): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.#EVENTS_API}/${eventId}/rates`, rate);
    }

    putEventRate(eventId: string, rate: Partial<Rate>): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${eventId}/rates/${rate.id}`, {
            name: rate.name,
            default: rate.default,
            restrictive_access: rate.restrictive_access
        });
    }

    deleteEventRate(eventId: string, rateId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#EVENTS_API}/${eventId}/rates/${rateId}`);
    }

    getEventRatesRestrictions(eventId: number): Observable<ListResponse<RateRestrictions>> {
        return this.#http.get<ListResponse<RateRestrictions>>(`${this.#EVENTS_API}/${eventId}/rates/restrictions`);
    }

    putEventRateRestrictions(eventId: number, rateId: number, restrictions: Partial<RateRestrictions>): Observable<void> {
        return this.#http.post<void>(`${this.#EVENTS_API}/${eventId}/rates/${rateId}/restrictions`, restrictions);
    }

    deleteEventRateRestrictions(eventId: number, rateId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#EVENTS_API}/${eventId}/rates/${rateId}/restrictions`);
    }

    getEventSurcharges(eventId: string): Observable<EventSurcharge[]> {
        return this.#http.get<EventSurcharge[]>(`${this.#EVENTS_API}/${eventId}/surcharges`);
    }

    postEventSurcharges(eventId: string, surcharges: EventSurcharge[]): Observable<void> {
        return this.#http.post<void>(`${this.#EVENTS_API}/${eventId}/surcharges`, surcharges);
    }

    getEventAdditionalConfig(eventId: string): Observable<EventAdditionalConfig> {
        return this.#http.get<EventAdditionalConfig>(`${this.#EVENTS_API}/${eventId}/additional-config`);
    }

    getEventAttributes(eventId: number, fullLoad = false): Observable<AttributeWithValues[]> {
        const params = buildHttpParams({ full_load: fullLoad ? true : undefined });
        return this.#http.get<AttributeWithValues[]>(`${this.#EVENTS_API}/${eventId}/attributes`, { params });
    }

    putEventAttributes(eventId: number, attributes: PutAttribute[]): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${eventId}/attributes`, attributes);
    }

    getEventAttendantFields(eventId: number): Observable<GetEventAttendantFields> {
        return this.#http.get<GetEventAttendantFields>(`${this.#EVENTS_API}/${eventId}/fields`);
    }

    postEventAttendantFields(eventId: number, attendantFields: EventAttendantField[]): Observable<void> {
        return this.#http.post<void>(`${this.#EVENTS_API}/${eventId}/fields`, attendantFields);
    }

    getEventExternalBarcodes(eventId: number): Observable<EventExternalBarcodes> {
        return this.#http.get<EventExternalBarcodes>(`${this.#EVENTS_API}/${eventId}/external-barcodes/config`);
    }

    putEventExternalBarcodeConfig(eventId: number, externalBarcodes: Partial<EventExternalBarcodes>): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${eventId}/external-barcodes/config`, externalBarcodes);
    }

    getRatesGroup(eventId: number, type?: 'PRODUCT' | 'RATE'): Observable<RateGroup[]> {
        const params = buildHttpParams({ type });
        return this.#http.get<RateGroup[]>(`${this.#EVENTS_API}/${eventId}/rates-group`, { params });
    }

    postRatesGroup(eventId: number, payload: PostRateGroup): Observable<Id> {
        return this.#http.post<Id>(`${this.#EVENTS_API}/${eventId}/rates-group`, payload);
    }

    putRateGroup(eventId: number, payload: PutRateGroup): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${eventId}/rates-group/${payload.id}`, payload);
    }

    putRatesGroup(eventId: number, payload: PutRateGroup[]): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${eventId}/rates-group`, payload);
    }

    deleteRateGroup(eventId: number, rateGroupId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#EVENTS_API}/${eventId}/rates-group/${rateGroupId}`);
    }

    getEventCustomerTypesAssignation(eventId: number): Observable<CustomerTypeAssignation[]> {
        return this.#http.get<CustomerTypeAssignation[]>(`${this.#EVENTS_API}/${eventId}/customer-types`);
    }

    putEventCustomerTypesAssignation(eventId: number, customerTypeAssignation: PutCustomerTypeAssignation[]): Observable<void> {
        return this.#http.put<void>(`${this.#EVENTS_API}/${eventId}/customer-types`, customerTypeAssignation);
    }
}
