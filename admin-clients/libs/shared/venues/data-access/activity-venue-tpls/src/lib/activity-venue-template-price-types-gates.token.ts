import { VenueTemplatePriceType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { SessionPriceType } from './models/session-price-type.model';

export interface ActivityPriceTypesGatesComponentService {

    loadActivityPriceTypes(
        { venueTemplateId, eventId, sessionId }: {
            venueTemplateId?: number;
            eventId?: number;
            sessionId?: number;
        }): void;

    updatePriceTypesGates(
        { venueTemplateId, eventId, sessionId }: {
            venueTemplateId?: number;
            eventId?: number;
            sessionId?: number;
        },
        priceTypes: VenueTemplatePriceType[] | SessionPriceType[]
    ): Observable<void>;

    clearActivityPriceTypesGates();

    isActivityPriceTypesGatesLoading$(): Observable<boolean>;

    isActivityPriceTypesGatesSaving$(): Observable<boolean>;

    getActivityPriceTypes$(): Observable<VenueTemplatePriceType[] | SessionPriceType[]>;
}

export const ACTIVITY_PRICE_TYPES_GATES_SERVICE
    = new InjectionToken<ActivityPriceTypesGatesComponentService>('ACTIVITY_PRICE_TYPES_GATES_SERVICE');
