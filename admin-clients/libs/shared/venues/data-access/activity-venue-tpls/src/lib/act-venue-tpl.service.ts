import { ActivityGroupsConfig, VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { Injectable } from '@angular/core';
import { combineLatest, EMPTY, forkJoin, Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { ActivityGroupsComponentService } from './activity-venue-template-groups.token';
import { ActivityLimitsComponentService } from './activity-venue-template-limits.token';
import { ActivityPriceTypesGatesComponentService } from './activity-venue-template-price-types-gates.token';
import { ActVenueTplsApi } from './api/act-venue-tpls.api';
import { PriceTypeAvailability } from './models/price-type-availability.model';
import { VenueTemplateQuotaCapacity } from './models/venue-template-quota-capacity.model';
import { ActVenueTplsState } from './state/act-venue-tpls.state';

@Injectable()
export class ActVenueTplService
    implements ActivityLimitsComponentService, ActivityPriceTypesGatesComponentService, ActivityGroupsComponentService {

    constructor(
        private _venueTemplateSrv: VenueTemplatesService,
        private _actVenueTplsApi: ActVenueTplsApi,
        private _actVenueTplsState: ActVenueTplsState
    ) { }

    clearActivityVenueTemplateData(): void {
        this._venueTemplateSrv.clearVenueTemplateQuotas();
        this._actVenueTplsState.quotaCapacities.setValue(null);
    }

    loadActivityQuotaCapacity(
        { venueTemplateId }: { venueTemplateId?: number }): void {
        this._actVenueTplsState.quotaCapacities.setInProgress(true);
        this._actVenueTplsApi.getTemplateQuotaCapacity(venueTemplateId)
            .pipe(
                finalize(() => this._actVenueTplsState.quotaCapacities.setInProgress(false))
            )
            .subscribe(quotas => this._actVenueTplsState.quotaCapacities.setValue(quotas));
    }

    clearVenueTemplateQuotaCapacity(): void {
        this._actVenueTplsState.quotaCapacities.setValue(null);
    }

    getActivityQuotaCapacity$(): Observable<VenueTemplateQuotaCapacity[]> {
        return this._actVenueTplsState.quotaCapacities.getValue$();
    }

    isActivityQuotaCapacityInProgress$(): Observable<boolean> {
        return this._actVenueTplsState.quotaCapacities.isInProgress$();
    }

    updateActivityQuotaCapacity(
        { venueTemplateId }: { venueTemplateId?: number },
        quotaCapacities: VenueTemplateQuotaCapacity[]
    ): Observable<void> {
        this._actVenueTplsState.quotaCapacitiesUpdate.setInProgress(true);
        return this._actVenueTplsApi.updateTemplateQuotaCapacity(venueTemplateId, quotaCapacities)
            .pipe(
                finalize(() => this._actVenueTplsState.quotaCapacitiesUpdate.setInProgress(false))
            );
    }

    // ActivityLimitsService

    getPriceTypeAvailability$(): Observable<PriceTypeAvailability[]> {
        return EMPTY;
    }

    // ActivityPriceTypesGatesService

    clearActivityPriceTypesGates(): void {
        this._venueTemplateSrv.clearVenueTemplatePriceTypes();
    }

    getActivityPriceTypes$(): Observable<VenueTemplatePriceType[]> {
        return this._venueTemplateSrv.getVenueTemplatePriceTypes$();
    }

    loadActivityPriceTypes({ venueTemplateId }: { venueTemplateId: number }): void {
        this._venueTemplateSrv.loadVenueTemplatePriceTypes(venueTemplateId);
    }

    isActivityPriceTypesGatesLoading$(): Observable<boolean> {
        return this._venueTemplateSrv.isVenueTemplatePriceTypesLoading$();
    }

    updatePriceTypesGates(
        { venueTemplateId }: { venueTemplateId: number },
        priceTypes: VenueTemplatePriceType[]
    ): Observable<void> {
        return forkJoin(priceTypes.map(priceType => this._venueTemplateSrv.updateVenueTemplatePriceType(venueTemplateId, priceType)))
            .pipe(map(() => null));
    }

    isActivityPriceTypesGatesSaving$(): Observable<boolean> {
        return this._venueTemplateSrv.isVenueTemplatePriceTypeSaving$();
    }

    //GROUPS

    clearVenueTemplateGroupsConfig(): void {
        this._venueTemplateSrv.venueTpl.clear();
    }

    getActivityGroupsConfig$(): Observable<ActivityGroupsConfig | null> {
        return this._venueTemplateSrv.venueTpl.get$()
            .pipe(map(venueTemplate => venueTemplate?.groups ?? null));
    }

    isActivityGroupsConfigInProgress$(): Observable<boolean> {
        return combineLatest([
            this._venueTemplateSrv.venueTpl.inProgress$(),
            this._venueTemplateSrv.isVenueTemplateSaving$()
        ])
            .pipe(map(loadings => loadings.some(loading => !!loading)));
    }

    loadActivityGroupsConfig({
        venueTemplateId,
        eventId,
        sessionId
    }: { venueTemplateId?: number; eventId?: number; sessionId?: number }): void {
        this._venueTemplateSrv.venueTpl.load(venueTemplateId);
    }

    updateActivityGroupsConfig(
        {
            venueTemplateId,
            eventId,
            sessionId
        }: { venueTemplateId?: number; eventId?: number; sessionId?: number },
        groupsConfigs: ActivityGroupsConfig
    ): Observable<void> {
        return this._venueTemplateSrv.updateVenueTemplate(venueTemplateId,
            {
                groups: groupsConfigs
            });
    }
}
