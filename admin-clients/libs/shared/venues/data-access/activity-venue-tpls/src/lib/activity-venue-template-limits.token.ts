import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { PriceTypeAvailability } from './models/price-type-availability.model';
import { VenueTemplateQuotaCapacity } from './models/venue-template-quota-capacity.model';

export interface ActivityLimitsComponentService {

    loadActivityQuotaCapacity(
        { venueTemplateId, eventId, sessionId }: {
            venueTemplateId?: number;
            eventId?: number;
            sessionId?: number;
        }): void;

    updateActivityQuotaCapacity(
        { venueTemplateId, eventId, sessionId }: {
            venueTemplateId?: number;
            eventId?: number;
            sessionId?: number;
        },
        quotaCapacities: VenueTemplateQuotaCapacity[]
    ): Observable<void>;

    clearVenueTemplateQuotaCapacity();

    isActivityQuotaCapacityInProgress$(): Observable<boolean>;

    getActivityQuotaCapacity$(): Observable<VenueTemplateQuotaCapacity[]>;

    getPriceTypeAvailability$(): Observable<PriceTypeAvailability[]>;
}

export const ACTIVITY_LIMITS_SERVICE = new InjectionToken<ActivityLimitsComponentService>('ACTIVITY_LIMITS_SERVICE');
