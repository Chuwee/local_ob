import { mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { PutCustomerTypeAssignation, Rate, RateRestrictions } from '@admin-clients/cpanel/promoters/shared/data-access';
import { EventType, PutAttribute } from '@admin-clients/shared/common/data-access';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EventsApi } from './api/events.api';
import { EventAttendantField } from './models/attendants-fields.model';
import { EventAvetConnection } from './models/event-avet-connection.enum';
import { EventExternalBarcodes } from './models/event-external-barcodes.model';
import { EventSurcharge } from './models/event-surcharge.model';
import { Event } from './models/event.model';
import { GetEventPricesRequest } from './models/get-event-prices.request';
import { GetEventsRequest } from './models/get-events-request.model';
import { PostEventRate } from './models/post-event-rate.model';
import { PostEvent } from './models/post-event.model';
import { PutEventPrice } from './models/put-event-price.model';
import { PutEvent } from './models/put-event.model';
import { PostRateGroup, PutRateGroup } from './models/rate-group.model';
import { EventsState } from './state/events.state';

@Injectable()
export class EventsService {
    private readonly _api = inject(EventsApi);
    private readonly _state = inject(EventsState);

    readonly ratesGroup = Object.freeze({
        load: (eventId: number, type?: 'RATE') => StateManager.load(
            this._state.ratesGroup,
            this._api.getRatesGroup(eventId, type)
        ),
        get$: () => this._state.ratesGroup.getValue$(),
        loading$: () => this._state.ratesGroup.isInProgress$(),
        create: (eventId: number, payload: PostRateGroup) => StateManager.inProgress(
            this._state.ratesGroup,
            this._api.postRatesGroup(eventId, payload)
        ),
        update: (eventId: number, payload: PutRateGroup) => StateManager.inProgress(
            this._state.ratesGroup,
            this._api.putRateGroup(eventId, payload)
        ),
        updateMany: (eventId: number, payload: PutRateGroup[]) => StateManager.inProgress(
            this._state.ratesGroup,
            this._api.putRatesGroup(eventId, payload)
        ),
        delete: (eventId: number, rateGroupId: number) => StateManager.inProgress(
            this._state.ratesGroup,
            this._api.deleteRateGroup(eventId, rateGroupId)
        ),
        clear: () => this._state.ratesGroup.setValue(null)
    });

    // SGA products are the same as rates group but with a different type
    readonly sgaProducts = Object.freeze({
        load: (eventId: number) => StateManager.load(
            this._state.sgaProducts,
            this._api.getRatesGroup(eventId, 'PRODUCT')
        ),
        get$: () => this._state.sgaProducts.getValue$(),
        loading$: () => this._state.sgaProducts.isInProgress$(),
        clear: () => this._state.sgaProducts.setValue(null),
        update: (eventId: number, payload: PutRateGroup[]) => StateManager.inProgress(
            this._state.sgaProducts,
            this._api.putRatesGroup(eventId, payload)
        )
    });

    readonly eventsList = Object.freeze({
        load: (request: GetEventsRequest) => StateManager.load(
            this._state.eventsList,
            this._api.getEvents(request).pipe(mapMetadata())
        ),
        loadMore: (request: GetEventsRequest) =>
            StateManager.loadMore(request, this._state.eventsList, r => this._api.getEvents(r)),
        getData$: () => this._state.eventsList.getValue$().pipe(map(events => events?.data)),
        getMetadata$: () => this._state.eventsList.getValue$().pipe(map(r => r?.metadata)),
        loading$: () => this._state.eventsList.isInProgress$(),
        clear: () => this._state.eventsList.setValue(null)
    });

    readonly event = Object.freeze({
        load: (eventId: string) => StateManager.load(
            this._state.event,
            this._api.getEvent(eventId)
        ),
        get$: (): Observable<Event> => this._state.event.getValue$(),
        inProgress$: () => this._state.event.isInProgress$(),
        create: (event: PostEvent) => StateManager.inProgress(
            this._state.event,
            this._api.postEvent(event).pipe(map(result => result.id))
        ),
        update: (id: number, event: PutEvent) => StateManager.inProgress(
            this._state.event,
            this._api.putEvent(id, event)
        ),
        delete: (eventId: string) => StateManager.inProgress(
            this._state.event,
            this._api.deleteEvent(eventId)
        ),
        clear: () => this._state.event.setValue(null),
        error$: () => this._state.event.getError$()
    });

    readonly eventPrices = Object.freeze({
        load: (eventId: string, tplId: string, request?: GetEventPricesRequest) => StateManager.load(
            this._state.eventPrices,
            this._api.getEventPrices(eventId, tplId, request)
        ),
        get$: () => this._state.eventPrices.getValue$(),
        inProgress$: () => this._state.eventPrices.isInProgress$(),
        update: (eventId: string, tplId: string, eventPrices: PutEventPrice[]) => StateManager.inProgress(
            this._state.eventPrices,
            this._api.putEventPrices(eventId, tplId, eventPrices)
        ),
        clear: () => this._state.eventPrices.setValue(null),
        error$: () => this._state.eventPrices.getError$()
    });

    readonly eventRates = Object.freeze({
        load: (eventId: string) => StateManager.load(
            this._state.eventRates,
            this._api.getEventRates(eventId)
        ),
        get$: () => this._state.eventRates.getValue$(),
        inProgress$: () => this._state.eventRates.isInProgress$(),
        create: (eventId: string, eventRate: PostEventRate) => StateManager.inProgress(
            this._state.eventRates,
            this._api.postEventRate(eventId, eventRate).pipe(map((result: { id: number }) => ({
                id: result.id,
                name: eventRate.name
            })))
        ),
        update: (eventId: string, eventRates: Partial<Rate>[]) => StateManager.inProgress(
            this._state.eventRates,
            this._api.putEventRates(eventId, eventRates)
        ),
        delete: (eventId: string, rateId: number) => StateManager.inProgress(
            this._state.eventRates,
            this._api.deleteEventRate(eventId, rateId)
        ),
        clear: () => this._state.eventRates.setValue(null),
        error$: () => this._state.eventRates.getError$()
    });

    readonly eventRatesExternalTypes = Object.freeze({
        load: (eventId: string) => StateManager.load(
            this._state.eventRatesExternalTypes,
            this._api.getEventRatesExternalTypes(eventId)
        ),
        get$: () => this._state.eventRatesExternalTypes.getValue$(),
        loading$: () => this._state.eventRatesExternalTypes.isInProgress$(),
        clear: () => this._state.eventRatesExternalTypes.setValue(null),
        error$: () => this._state.eventRatesExternalTypes.getError$()
    });

    readonly ratesRestrictions = Object.freeze({
        load: (eventId: number) => StateManager.load(
            this._state.eventRateRestriction,
            this._api.getEventRatesRestrictions(eventId)
        ),
        get$: () => this._state.eventRateRestriction.getValue$().pipe(map(res => res?.data || [])),
        inProgress$: () => this._state.eventRateRestriction.isInProgress$(),
        update: (eventId: number, rateId: number, restrictions: Partial<RateRestrictions>) => StateManager.inProgress(
            this._state.eventRateRestriction,
            this._api.putEventRateRestrictions(eventId, rateId, restrictions)
        ),
        delete: (eventId: number, rateId: number) => StateManager.inProgress(
            this._state.eventRateRestriction,
            this._api.deleteEventRateRestrictions(eventId, rateId)
        ),
        clear: () => this._state.eventRateRestriction.setValue(null),
        error$: () => this._state.eventRateRestriction.getError$()
    });

    readonly eventSurcharges = Object.freeze({
        load: (eventId: string) => StateManager.load(
            this._state.eventSurcharges,
            this._api.getEventSurcharges(eventId)
        ),
        get$: () => this._state.eventSurcharges.getValue$(),
        inProgress$: () => this._state.eventSurcharges.isInProgress$(),
        create: (eventId: string, surcharges: EventSurcharge[]) => StateManager.inProgress(
            this._state.eventSurcharges,
            this._api.postEventSurcharges(eventId, surcharges)
        ),
        clear: () => this._state.eventSurcharges.setValue(null),
        error$: () => this._state.eventSurcharges.getError$()
    });

    readonly eventAdditionalConfig = Object.freeze({
        load: (eventId: string) => StateManager.load(
            this._state.eventAdditionalConfig,
            this._api.getEventAdditionalConfig(eventId)
        ),
        get$: () => this._state.eventAdditionalConfig.getValue$(),
        loading$: () => this._state.eventAdditionalConfig.isInProgress$(),
        clear: () => this._state.eventAdditionalConfig.setValue(null),
        error$: () => this._state.eventAdditionalConfig.getError$()
    });

    readonly eventAttributes = Object.freeze({
        load: (eventId: number, fullLoad?: boolean) => StateManager.load(
            this._state.eventAttributes,
            this._api.getEventAttributes(eventId, fullLoad)
        ),
        get$: () => this._state.eventAttributes.getValue$(),
        update: (eventId: number, attributes: PutAttribute[]) => StateManager.inProgress(
            this._state.eventAttributes,
            this._api.putEventAttributes(eventId, attributes)
        ),
        loading$: () => this._state.eventAttributes.isInProgress$(),
        clear: () => this._state.eventAttributes.setValue(null),
        error$: () => this._state.eventAttributes.getError$()
    });

    readonly eventAttendantFields = Object.freeze({
        load: (eventId: number) => StateManager.load(
            this._state.eventAttendantFields,
            this._api.getEventAttendantFields(eventId)
        ),
        get$: () => this._state.eventAttendantFields.getValue$(),
        getData$: () => this._state.eventAttendantFields.getValue$().pipe(map(fields => fields?.data)),
        create: (eventId: number, attendantFields: EventAttendantField[]) => StateManager.inProgress(
            this._state.eventAttendantFields,
            this._api.postEventAttendantFields(eventId, attendantFields)
        ),
        loading$: () => this._state.eventAttendantFields.isInProgress$(),
        clear: () => this._state.eventAttendantFields.setValue(null),
        error$: () => this._state.eventAttendantFields.getError$()
    });

    readonly eventExternalBarcodes = Object.freeze({
        load: (eventId: number) => StateManager.load(
            this._state.eventExternalBarcodes,
            this._api.getEventExternalBarcodes(eventId)
        ),
        get$: () => this._state.eventExternalBarcodes.getValue$(),
        update: (eventId: number, externalBarcodes: Partial<EventExternalBarcodes>) => StateManager.inProgress(
            this._state.eventExternalBarcodes,
            this._api.putEventExternalBarcodeConfig(eventId, externalBarcodes)
        ),
        inProgress$: () => this._state.eventExternalBarcodes.isInProgress$(),
        clear: () => this._state.eventExternalBarcodes.setValue(null),
        error$: () => this._state.eventExternalBarcodes.getError$()
    });

    readonly customerTypesAssignation = Object.freeze({
        load: (eventId: number) => StateManager.load(
            this._state.eventCustomerTypesAssignation,
            this._api.getEventCustomerTypesAssignation(eventId)
        ),
        update: (eventId: number, customerTypesAssignation: PutCustomerTypeAssignation[]) => StateManager.inProgress(
            this._state.eventCustomerTypesAssignation, this._api.putEventCustomerTypesAssignation(eventId, customerTypesAssignation)
        ),
        get$: () => this._state.eventCustomerTypesAssignation.getValue$(),
        inProgress$: () => this._state.eventCustomerTypesAssignation.isInProgress$(),
        clear: () => this._state.eventCustomerTypesAssignation.setValue(null),
        error$: () => this._state.eventCustomerTypesAssignation.getError$()
    });

    getEvents$(ids: number[]): Observable<Event[]> {
        return this._state.eventsCache.getItems$(ids, id => (this._api.getEvent(id.toString())));
    }

    static isAvet(event: Event): boolean {
        return event?.type === EventType.avet;
    }

    static isAvetSocket(event: Event): boolean {
        return event?.type === EventType.avet && event?.additional_config?.avet_config === EventAvetConnection.socket;
    }

    static isAvetWS(event: Event): boolean {
        return event?.type === EventType.avet && event?.additional_config?.avet_config === EventAvetConnection.ws;
    }
}
