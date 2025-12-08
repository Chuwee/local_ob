import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    ActivityGroupsComponentService, ActivityLimitsComponentService, ActivityPriceTypesGatesComponentService, PriceTypeAvailability,
    SessionActivityGroupsConfig, SessionPriceType, VenueTemplateQuotaCapacity
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { EventSessionsService } from './sessions.service';

@Injectable()
export class SessionCapacityActivityService
    implements ActivityLimitsComponentService, ActivityPriceTypesGatesComponentService, ActivityGroupsComponentService {

    constructor(
        private _eventSessionsSrv: EventSessionsService,
        private _venueTemplatesService: VenueTemplatesService
    ) {
    }

    clearVenueTemplateQuotaCapacity(): void {
        this._venueTemplatesService.clearVenueTemplatePriceTypes();
        this._eventSessionsSrv.clearQuotaCapacities();
    }

    getActivityQuotaCapacity$(): Observable<VenueTemplateQuotaCapacity[]> {
        return this._eventSessionsSrv.getQuotaCapacities$();
    }

    isActivityQuotaCapacityInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._venueTemplatesService.isVenueTemplatePriceTypesLoading$(),
            this._venueTemplatesService.isVenueTemplateQuotasLoading$(),
            this._eventSessionsSrv.isQuotaCapacitiesInProgress$(),
            this._eventSessionsSrv.isPriceTypeAvailabilityInProgress$()
        ]);
    }

    loadActivityQuotaCapacity(
        { venueTemplateId, eventId, sessionId }: { venueTemplateId?: number; eventId?: number; sessionId?: number }
    ): void {
        this._venueTemplatesService.loadVenueTemplatePriceTypes(venueTemplateId);
        this._venueTemplatesService.loadVenueTemplateQuotas(venueTemplateId);
        this._eventSessionsSrv.loadQuotaCapacities(eventId, sessionId);
        this._eventSessionsSrv.loadPriceTypeAvailability(eventId, sessionId);
    }

    updateActivityQuotaCapacity(
        { venueTemplateId, eventId, sessionId }: {
            venueTemplateId?: number;
            eventId?: number;
            sessionId?: number;
        },
        quotaCapacities: VenueTemplateQuotaCapacity[]
    ): Observable<void> {
        return this._eventSessionsSrv.updateQuotaCapacities(eventId, sessionId, quotaCapacities);
    }

    getPriceTypeAvailability$(): Observable<PriceTypeAvailability[]> {
        return this._eventSessionsSrv.getPriceTypeAvailability$();
    }

    clearActivityPriceTypesGates(): void {
        this._venueTemplatesService.clearVenueTemplatePriceTypes();
    }

    getActivityPriceTypes$(): Observable<VenueTemplatePriceType[] | SessionPriceType[]> {
        return this._eventSessionsSrv.getSessionPriceTypes$();
    }

    isActivityPriceTypesGatesLoading$(): Observable<boolean> {
        return this._eventSessionsSrv.isSessionPriceTypesLoading$();
    }

    loadActivityPriceTypes({
        venueTemplateId,
        eventId,
        sessionId
    }: { venueTemplateId?: number; eventId?: number; sessionId?: number }): void {
        this._eventSessionsSrv.loadSessionPriceTypes(eventId, sessionId);
        this._venueTemplatesService.loadVenueTemplateGates(venueTemplateId);
    }

    updatePriceTypesGates(
        {
            venueTemplateId,
            eventId,
            sessionId
        }: { venueTemplateId?: number; eventId?: number; sessionId?: number },
        priceTypes: SessionPriceType[]
    ): Observable<void> {
        return this._eventSessionsSrv.updateSessionPriceTypes(eventId, sessionId, priceTypes);
    }

    isActivityPriceTypesGatesSaving$(): Observable<boolean> {
        return this._eventSessionsSrv.isSessionPriceTypesSaving$();
    }

    clearVenueTemplateGroupsConfig(): void {
        this._eventSessionsSrv.clearSessionActivityGroupConfig();
    }

    getActivityGroupsConfig$(): Observable<SessionActivityGroupsConfig> {
        return this._eventSessionsSrv.getSessionActivityGroupsConfig$();
    }

    isActivityGroupsConfigInProgress$(): Observable<boolean> {
        return this._eventSessionsSrv.isSessionActivityGroupsConfigLoading$();
    }

    loadActivityGroupsConfig({
        venueTemplateId,
        eventId,
        sessionId
    }: { venueTemplateId?: number; eventId?: number; sessionId?: number }): void {
        this._eventSessionsSrv.loadSessionActivityGroupsConfig(eventId, sessionId);
    }

    updateActivityGroupsConfig(
        {
            venueTemplateId,
            eventId,
            sessionId
        }: { venueTemplateId?: number; eventId?: number; sessionId?: number },
        groupsConfigs: SessionActivityGroupsConfig
    ): Observable<void> {
        return this._eventSessionsSrv.updateSessionActivityGroupsConfig(eventId, sessionId, groupsConfigs);
    }

}
