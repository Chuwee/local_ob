import { mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import {
    AggregatedData, AggregationMetricType, IdName, ResponseAggregatedData
} from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { catchError, combineLatest, filter, finalize, map, Observable, of, switchMap, take } from 'rxjs';
import { Attribute, AttributeScope } from '../models/attributes.model';
import { EntityType } from '../models/entity-type.enum';
import { EntitiesBaseApi } from './api/entities-base.api';
import { CustomizationItem, CustomizationItemTag } from './models/customization-item.model';
import { aggDataEntitiesLicenses } from './models/entities-licenses-aggregated-data';
import { EntitiesUsersLimitsResponse } from './models/entities-users-limits.model';
import { EntityCalendar } from './models/entity-calendar.model';
import { EntityCategory } from './models/entity-categories.model';
import { EntityCustomerTypeReq } from './models/entity-customer-type.model';
import { EntityExternalCapacity } from './models/entity-external-capacity.model';
import { EntityFriends } from './models/entity-friends.model';
import { EntityProfile } from './models/entity-profile.model';
import { EntitySurcharge } from './models/entity-surcharge.model';
import { Entity } from './models/entity.model';
import { GetEntitiesRequest } from './models/get-entities-request.model';
import { GetEntitiesResponse } from './models/get-entities-response.model';
import { LoginAuthConfig } from './models/login-auth-config.model';
import { aggDataMobileLicenses } from './models/mobile-licenses-aggregated-data';
import { PutEntity } from './models/put-entity.model';
import { Tax } from './models/tax.model';
import { EntitiesBaseState } from './state/entities-base.state';

@Injectable({ providedIn: 'root' })
export class EntitiesBaseService {

    // Entities Users Limits
    readonly entitiesUsersLimits = Object.freeze({
        load: (): void => StateManager.load(
            this._entitiesBaseState.entitiesUsersLimits,
            this._entitiesBaseApi.getEntitiesUsersLimit()
        ),
        getEntitiesMetadata$: () => this._entitiesBaseState.entitiesUsersLimits.getValue$()
            .pipe(filter(limits => !!limits), map(limits => this.entitiesBiLimitsAgg(limits))),
        getMobileMetadata$: () => this._entitiesBaseState.entitiesUsersLimits.getValue$()
            .pipe(filter(limits => !!limits), map(limits => this.mobileBiLimitsAgg(limits))),
        error$: () => this._entitiesBaseState.entitiesUsersLimits.getError$(),
        inProgress$: () => this._entitiesBaseState.entitiesUsersLimits.isInProgress$(),
        clear: () => this._entitiesBaseState.entitiesUsersLimits.setValue(null)
    });

    readonly managedEntitiesList = Object.freeze({
        load: (id: number, request: GetEntitiesRequest): void => StateManager.load(
            this._entitiesBaseState.managedEntitiesList,
            this._entitiesBaseApi.getManagedEntities(id, request).pipe(mapMetadata())
        ),
        update$: (id: number, entities: IdName[]) =>
            StateManager.inProgress(
                this._entitiesBaseState.managedEntitiesList,
                this._entitiesBaseApi.putEntity(id, { settings: { managed_entities: entities } })
            ),
        getMetadata$: () => this._entitiesBaseState.managedEntitiesList.getValue$().pipe(map(list => list?.metadata)),
        getData$: () => this._entitiesBaseState.managedEntitiesList.getValue$().pipe(map(entities => entities?.data)),
        error$: () => this._entitiesBaseState.managedEntitiesList.getError$(),
        inProgress$: () => this._entitiesBaseState.managedEntitiesList.isInProgress$(),
        clear: () => this._entitiesBaseState.managedEntitiesList.setValue(null)
    });

    // Surcharges
    readonly surcharges = Object.freeze({
        load: (entityId: number, currencyCode?: string): void => StateManager.load(
            this._entitiesBaseState.entitySurcharges,
            this._entitiesBaseApi.getEntitySurcharges(entityId, currencyCode)
        ),
        update: (entityId: number, surcharges: EntitySurcharge[]): Observable<void> =>
            StateManager.inProgress(
                this._entitiesBaseState.entitySurcharges,
                this._entitiesBaseApi.postEntitySurcharges(entityId, surcharges)
            ),
        get$: () => this._entitiesBaseState.entitySurcharges.getValue$(),
        error$: () => this._entitiesBaseState.entitySurcharges.getError$(),
        inProgress$: () => this._entitiesBaseState.entitySurcharges.isInProgress$(),
        clear: () => this._entitiesBaseState.entitySurcharges.setValue(null)
    });

    readonly entityList = Object.freeze({
        load: (request: GetEntitiesRequest): void => {
            StateManager.load(
                this._entitiesBaseState.entityList,
                this._entitiesBaseApi.getEntities(request).pipe(mapMetadata())
            );
        },
        loadMore: (request: GetEntitiesRequest): void =>
            StateManager.loadMore(request, this._entitiesBaseState.entityList, r => this._entitiesBaseApi.getEntities(r)),
        getData$: (): Observable<Entity[]> => this._entitiesBaseState.entityList.getValue$().pipe(map(entities => entities?.data)),
        getMetadata$: (): Observable<Metadata> => this._entitiesBaseState.entityList.getValue$().pipe(map(list => list?.metadata)),
        clear: (): void => this._entitiesBaseState.entityList.setValue(null),
        inProgress$: (): Observable<boolean> => this._entitiesBaseState.entityList.isInProgress$()
    });

    readonly entityCustomization = Object.freeze({
        load: (id: number): void => {
            StateManager.load(
                this._entitiesBaseState.entityCustomization,
                this._entitiesBaseApi.getEntityCustomization(id)
            );
        },
        getData$: (): Observable<CustomizationItem[]> => this._entitiesBaseState.entityCustomization.getValue$(),
        clear: (): void => this._entitiesBaseState.entityCustomization.setValue(null),
        inProgress$: (): Observable<boolean> => this._entitiesBaseState.entityList.isInProgress$(),
        update$: (id: number, customizationItems: CustomizationItem[]): Observable<void> =>
            StateManager.inProgress(
                this._entitiesBaseState.entityCustomization,
                this._entitiesBaseApi.putEntityCustomization(id, customizationItems)
            ),
        delete$: (id: number, customizationItemTag: CustomizationItemTag): Observable<void> =>
            StateManager.inProgress(
                this._entitiesBaseState.entityCustomization,
                this._entitiesBaseApi.deleteEntityCustomization(id, customizationItemTag)
            )
    });

    readonly entityTypes = Object.freeze({
        load: (id: number): void => {
            StateManager.load(
                this._entitiesBaseState.entityTypes,
                this._entitiesBaseApi.getEntityTypes(id)
            );
        },
        get$: () => this._entitiesBaseState.entityTypes.getValue$(),
        clear: (): void => this._entitiesBaseState.entityTypes.setValue(null),
        loading$: () => this._entitiesBaseState.entityList.isInProgress$()
    });

    readonly entityGateways = Object.freeze({
        loadIfNull: (id: number): void => {
            StateManager.loadIfNull(
                this._entitiesBaseState.entityGateways,
                this._entitiesBaseApi.getEntityGateways(id)
            );
        },
        get$: () => this._entitiesBaseState.entityGateways.getValue$(),
        clear: (): void => this._entitiesBaseState.entityGateways.setValue(null),
        loading$: () => this._entitiesBaseState.entityGateways.isInProgress$()
    });

    // Entity Custom Types
    readonly entityCustomerTypes = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesBaseState.entityCustomerTypes,
            this._entitiesBaseApi.getEntityCustomerTypes(entityId)
        ),
        create: (entityId: number, form: EntityCustomerTypeReq): Observable<void> =>
            StateManager.inProgress(
                this._entitiesBaseState.entityCustomerTypes,
                this._entitiesBaseApi.postEntityCustomerType(entityId, form)
            ),
        update: (entityId: number, customTypeId: number, form: EntityCustomerTypeReq): Observable<void> =>
            StateManager.inProgress(
                this._entitiesBaseState.entityCustomerTypes,
                this._entitiesBaseApi.putEntityCustomerType(entityId, customTypeId, form)
            ),
        delete: (entityId: number, customTypeId: number) => {
            this._entitiesBaseState.entityCustomerTypes.setInProgress(true);
            return this._entitiesBaseApi.deleteEntityCustomerTypes(entityId, customTypeId).pipe(
                finalize(() => this._entitiesBaseState.entityCustomerTypes.setInProgress(false))
            );
        },
        get$: () => this._entitiesBaseState.entityCustomerTypes.getValue$(),
        error$: () => this._entitiesBaseState.entityCustomerTypes.getError$(),
        inProgress$: () => this._entitiesBaseState.entityCustomerTypes.isInProgress$(),
        clear: () => this._entitiesBaseState.entityCustomerTypes.setValue(null)
    });

    // Authentication configuration
    readonly authConfig = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesBaseState.entityAuthConfig,
            this._entitiesBaseApi.getEntityAuthConfig(entityId)
        ),
        update: (entityId: number, config: Partial<LoginAuthConfig>): Observable<void> =>
            StateManager.inProgress(
                this._entitiesBaseState.entityAuthConfig,
                this._entitiesBaseApi.putEntityAuthConfig(entityId, config)
            ),
        get$: () => this._entitiesBaseState.entityAuthConfig.getValue$(),
        inProgress$: () => this._entitiesBaseState.entityAuthConfig.isInProgress$(),
        clear: () => this._entitiesBaseState.entityAuthConfig.setValue(null)
    });

    // Entity Member Counter
    readonly entityMemberCounter = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesBaseState.entityMemberCounter,
            this._entitiesBaseApi.getEntityMemberCounter(entityId)
        ),
        update: (entityId: number, memberCounter: { member_counter: number }): Observable<void> =>
            StateManager.inProgress(
                this._entitiesBaseState.entityMemberCounter,
                this._entitiesBaseApi.putEntityMemberCounter(entityId, memberCounter)
            ),
        get$: () => this._entitiesBaseState.entityMemberCounter.getValue$(),
        error$: () => this._entitiesBaseState.entityMemberCounter.getError$(),
        inProgress$: () => this._entitiesBaseState.entityMemberCounter.isInProgress$(),
        clear: () => this._entitiesBaseState.entityMemberCounter.setValue(null)
    });

    // Entity Friends
    readonly entityFriends = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesBaseState.entityFriends,
            this._entitiesBaseApi.getEntityFriends(entityId)
        ),
        save: (entityId: number, entityFriends: EntityFriends): Observable<void> =>
            StateManager.inProgress(
                this._entitiesBaseState.entityFriends,
                this._entitiesBaseApi.putEntityFriends(entityId, entityFriends)
            ),
        get$: () => this._entitiesBaseState.entityFriends.getValue$(),
        error$: () => this._entitiesBaseState.entityFriends.getError$(),
        loading$: () => this._entitiesBaseState.entityFriends.isInProgress$(),
        clear: () => this._entitiesBaseState.entityFriends.setValue(null)
    });

    // External Venues
    readonly externalVenues = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesBaseState.externalVenues,
            this._entitiesBaseApi.getExternalVenues(entityId)
        ),
        get$: () => this._entitiesBaseState.externalVenues.getValue$(),
        error$: () => this._entitiesBaseState.externalVenues.getError$(),
        loading$: () => this._entitiesBaseState.externalVenues.isInProgress$(),
        clear: () => this._entitiesBaseState.externalVenues.setValue(null)
    });

    // External Venue Templates
    readonly externalVenueTemplates = Object.freeze({
        load: (entityId: number, externalVenueId: string): void => StateManager.load(
            this._entitiesBaseState.externalVenueTemplates,
            this._entitiesBaseApi.getExternalVenueTemplates(entityId, externalVenueId)
        ),
        get$: () => this._entitiesBaseState.externalVenueTemplates.getValue$(),
        error$: () => this._entitiesBaseState.externalVenueTemplates.getError$(),
        loading$: () => this._entitiesBaseState.externalVenueTemplates.isInProgress$(),
        clear: () => this._entitiesBaseState.externalVenueTemplates.setValue(null)
    });

    constructor(
        private _entitiesBaseApi: EntitiesBaseApi,
        private _entitiesBaseState: EntitiesBaseState
    ) { }

    getEntity$(): Observable<Entity> {
        return this._entitiesBaseState.entity.getValue$();
    }

    loadEntity(id: number): void {
        this._entitiesBaseState.entity.setError(null);
        this._entitiesBaseState.entity.setInProgress(true);
        this._entitiesBaseApi.getEntity(id)
            .pipe(
                catchError(error => {
                    this._entitiesBaseState.entity.setError(error);
                    return of(null);
                }),
                finalize(() => this._entitiesBaseState.entity.setInProgress(false))
            )
            .subscribe(entity =>
                this._entitiesBaseState.entity.setValue(entity)
            );
    }

    isEntityLoading$(): Observable<boolean> {
        return this._entitiesBaseState.entity.isInProgress$();
    }

    isEntitySaving$(): Observable<boolean> {
        return this._entitiesBaseState.entity.isInProgress$();
    }

    updateEntity(id: number, updatedEntity: PutEntity): Observable<void> {
        this._entitiesBaseState.entity.setInProgress(true);
        this._entitiesBaseState.entity.setError(null);
        return this._entitiesBaseApi.putEntity(id, updatedEntity)
            .pipe(
                catchError(error => {
                    this._entitiesBaseState.entity.setError(error);
                    throw error;
                }),
                finalize(() => {
                    this._entitiesBaseState.entity.setInProgress(false);
                })
            );
    }

    clearEntity(): void {
        this._entitiesBaseState.entity.setValue(null);
    }

    getCachedEntities$(ids: number[]): Observable<Entity[]> {
        return this._entitiesBaseState.entitiesCache.getItems$(
            ids,
            id => this._entitiesBaseApi.getEntity(id)
        );
    }

    loadEntityTypes(entityId: number): void {
        this._entitiesBaseState.entityTypes.setInProgress(true);
        this._entitiesBaseApi.getEntityTypes(entityId)
            .pipe(
                finalize(() => this._entitiesBaseState.entityTypes.setInProgress(false))
            )
            .subscribe(entity => this._entitiesBaseState.entityTypes.setValue(entity));
    }

    getEntityTypes$(): Observable<EntityType[]> {
        return this._entitiesBaseState.entityTypes.getValue$();
    }

    isEntityTypesLoading$(): Observable<boolean> {
        return this._entitiesBaseState.entityTypes.isInProgress$();
    }

    getEntityAvailableLenguages$(): Observable<string[]> {
        return this.getEntity$().pipe(
            filter(entity => !!(entity?.settings?.languages?.available)),
            map(entity => entity.settings?.languages.available));
    }

    loadEntityTaxes(id: number): void {
        this._entitiesBaseState.entityTaxes.setInProgress(true);
        this._entitiesBaseApi.getEntityTaxes(id)
            .pipe(
                finalize(() => this._entitiesBaseState.entityTaxes.setInProgress(false))
            )
            .subscribe(entityTaxes => this._entitiesBaseState.entityTaxes.setValue(entityTaxes));
    }

    isEntityTaxesLoading(): Observable<boolean> {
        return this._entitiesBaseState.entityTaxes.isInProgress$();
    }

    getEntityTaxes$(): Observable<Tax[]> {
        return this._entitiesBaseState.entityTaxes.getValue$();
    }

    loadAttributes(entityId: number, scope?: AttributeScope): void {
        this._entitiesBaseState.entityAttributes.setError(null);
        this._entitiesBaseState.entityAttributes.setInProgress(true);
        this._entitiesBaseApi.getEntityAttributes(entityId, scope)
            .pipe(
                catchError(error => {
                    this._entitiesBaseState.entityAttributes.setError(error);
                    return of(null);
                }),
                finalize(() => this._entitiesBaseState.entityAttributes.setInProgress(false))
            )
            .subscribe((attributes: Attribute[]) =>
                this._entitiesBaseState.entityAttributes.setValue(attributes)
            );
    }

    getAttributes$(): Observable<Attribute[]> {
        return this._entitiesBaseState.entityAttributes.getValue$();
    }

    clearAttributes(): void {
        this._entitiesBaseState.entityAttributes.setValue(null);
    }

    loadEntityCategories(entityId: number): void {
        this._entitiesBaseState.entityCategories.setInProgress(true);
        this._entitiesBaseApi.getEntityCategories(entityId)
            .pipe(
                finalize(() => this._entitiesBaseState.entityCategories.setInProgress(false))
            )
            .subscribe(entityCategories => this._entitiesBaseState.entityCategories.setValue(entityCategories));
    }

    getEntityCategories$(): Observable<EntityCategory[]> {
        return this._entitiesBaseState.entityCategories.getValue$();
    }

    isEntityCategoriesLoading$(): Observable<boolean> {
        return this._entitiesBaseState.entityCategories.isInProgress$();
    }

    clearEntityCategories(): void {
        this._entitiesBaseState.entityCategories.setValue(null);
    }

    loadEntityCalendars(id: number): void {
        this._entitiesBaseState.entityCalendars.setInProgress(true);
        this._entitiesBaseApi.getEntityCalendars(id)
            .pipe(
                finalize(() => this._entitiesBaseState.entityCalendars.setInProgress(false))
            )
            .subscribe(entityCalendars => this._entitiesBaseState.entityCalendars.setValue(entityCalendars));
    }

    isEntityCalendarsLoading$(): Observable<boolean> {
        return this._entitiesBaseState.entityCalendars.isInProgress$();
    }

    getEntityCalendars$(): Observable<EntityCalendar[]> {
        return this._entitiesBaseState.entityCalendars.getValue$();
    }

    clearEntityCalendars(): void {
        this._entitiesBaseState.entityCalendars.setValue(null);
    }

    loadEntityProfiles(entityId: number): void {
        this._entitiesBaseState.entityProfiles.setInProgress(true);
        this._entitiesBaseApi.getEntityProfiles(entityId)
            .pipe(
                finalize(() => this._entitiesBaseState.entityProfiles.setInProgress(false))
            )
            //If entity doesn't have entityProfiles, API doesn't return empty array
            .subscribe(entityProfiles => this._entitiesBaseState.entityProfiles.setValue(entityProfiles || []));
    }

    getEntityProfiles$(): Observable<EntityProfile[]> {
        return this._entitiesBaseState.entityProfiles.getValue$();
    }

    isEntityProfilesLoading$(): Observable<boolean> {
        return this._entitiesBaseState.entityProfiles.isInProgress$();
    }

    clearEntityProfiles(): void {
        this._entitiesBaseState.entityProfiles.setValue(null);
    }

    loadEntityExternalCapacities(entityId: number): void {
        this._entitiesBaseState.entityExternalCapacities.setInProgress(true);
        this._entitiesBaseApi.getEntityExternalCapacities(entityId)
            .pipe(
                finalize(() => this._entitiesBaseState.entityExternalCapacities.setInProgress(false))
            )
            .subscribe(entityExternalCapacities => this._entitiesBaseState.entityExternalCapacities.setValue(entityExternalCapacities));
    }

    getEntityExternalCapacities$(): Observable<EntityExternalCapacity[]> {
        return this._entitiesBaseState.entityExternalCapacities.getValue$();
    }

    isEntityExternalCapacitiesLoading$(): Observable<boolean> {
        return this._entitiesBaseState.entityExternalCapacities.isInProgress$();
    }

    clearEntityExternalCapacities(): void {
        this._entitiesBaseState.entityExternalCapacities.setValue(null);
    }

    //Server search
    loadServerSearchEntityList(request: GetEntitiesRequest, nextPage = false): void {
        const currentObservable$ = this._entitiesBaseState.entityList.getValue$();
        let result: Observable<GetEntitiesResponse>;
        if (!nextPage) {
            result = this._entitiesBaseApi.getEntities(request).pipe(catchError(() => of(null)));
        } else {
            result = currentObservable$
                .pipe(
                    take(1),
                    switchMap(currentData => {
                        request.offset = currentData.metadata.offset + currentData.data.length;
                        return this._entitiesBaseApi.getEntities(request).pipe(
                            map(nextElements => {
                                nextElements.data = currentData.data.concat(nextElements.data);
                                nextElements.metadata.limit = nextElements.data.length;
                                nextElements.metadata.offset = 0;
                                return nextElements;
                            })
                        );
                    })
                );
        }
        result.subscribe(entities => {
            this._entitiesBaseState.entityList.setValue(entities);
        });
    }

    loadExternalBarcodesEntityOptions(entityId: number): void {
        this._entitiesBaseState.externalBarcodesEntityOptions.setInProgress(true);
        this._entitiesBaseApi.getExternalBarcodesEntityOptions(entityId)
            .pipe(finalize(() => this._entitiesBaseState.externalBarcodesEntityOptions.setInProgress(false)))
            .subscribe(res => this._entitiesBaseState.externalBarcodesEntityOptions.setValue(res));
    }

    getExternalBarcodesEntityOptions$(): Observable<{ id: string; properties: Record<string, string[]> }> {
        return this._entitiesBaseState.externalBarcodesEntityOptions.getValue$();
    }

    getElementEntity$<T extends IdName>(item$: Observable<{ entity: IdName }>, user$: Observable<{ entity: T }>): Observable<Entity | T> {
        return combineLatest([item$, user$])
            .pipe(
                switchMap(([item, user]) => {
                    if (item && user) {
                        if (item.entity.id === user.entity.id) {
                            return of(user.entity);
                        } else {
                            this.clearEntity();
                            this.loadEntity(item.entity.id);
                            return this.getEntity$();
                        }
                    } else {
                        return of(null);
                    }
                })
            );
    }

    private entitiesBiLimitsAgg(limits: EntitiesUsersLimitsResponse): AggregatedData {
        const aggData: ResponseAggregatedData = {
            overall: [
                {
                    name: 'advanced',
                    type: AggregationMetricType.count,
                    value: limits.bi.advanced.total
                },
                {
                    name: 'basic',
                    type: AggregationMetricType.count,
                    value: limits.bi.basic.total
                },
                {
                    name: 'total',
                    type: AggregationMetricType.sum,
                    value: limits.bi.advanced.total + limits.bi.basic.total
                }
            ],
            type: [
                {
                    agg_metric: [
                        { name: 'advanced', type: AggregationMetricType.count, value: limits.bi.advanced.used },
                        { name: 'basic', type: AggregationMetricType.count, value: limits.bi.basic.used },
                        { name: 'total', type: AggregationMetricType.sum, value: limits.bi.advanced.used + limits.bi.basic.used }
                    ],
                    agg_value: 'USED'
                },
                {
                    agg_metric: [
                        { name: 'advanced', type: AggregationMetricType.count, value: limits.bi.advanced.limit },
                        { name: 'basic', type: AggregationMetricType.count, value: limits.bi.basic.limit },
                        { name: 'total', type: AggregationMetricType.sum, value: limits.bi.advanced.limit + limits.bi.basic.limit }
                    ],
                    agg_value: 'LIMIT'
                }
            ]
        };
        return new AggregatedData(aggData, aggDataEntitiesLicenses);
    }

    private mobileBiLimitsAgg(limits: EntitiesUsersLimitsResponse): AggregatedData {
        const aggData: ResponseAggregatedData = {
            overall: [
                {
                    name: 'mobile_used',
                    type: AggregationMetricType.count,
                    value: limits.bi.mobile.used
                },
                {
                    name: 'mobile_limit',
                    type: AggregationMetricType.count,
                    value: limits.bi.mobile.limit
                }
            ]
        };
        return new AggregatedData(aggData, aggDataMobileLicenses);
    }
}
