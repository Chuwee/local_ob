import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Attribute, AttributeScope } from '../../models/attributes.model';
import { EntityType } from '../../models/entity-type.enum';
import { CustomizationItem, CustomizationItemTag } from '../models/customization-item.model';
import { EntitiesUsersLimitsResponse } from '../models/entities-users-limits.model';
import { EntityCalendar } from '../models/entity-calendar.model';
import { EntityCategory } from '../models/entity-categories.model';
import { EntityCustomerType, EntityCustomerTypeReq } from '../models/entity-customer-type.model';
import { EntityExternalCapacity } from '../models/entity-external-capacity.model';
import { EntityExternalVenue } from '../models/entity-external-venue.model';
import { EntityFriends } from '../models/entity-friends.model';
import { EntityGateway } from '../models/entity-gateway.model';
import { EntityProfile } from '../models/entity-profile.model';
import { EntitySurcharge } from '../models/entity-surcharge.model';
import { Entity } from '../models/entity.model';
import { ExternalInvetories } from '../models/external-inventories.model';
import { GetEntitiesRequest } from '../models/get-entities-request.model';
import { GetEntitiesResponse } from '../models/get-entities-response.model';
import { LoginAuthConfig } from '../models/login-auth-config.model';
import { PutEntity } from '../models/put-entity.model';
import { Tax } from '../models/tax.model';

@Injectable({ providedIn: 'root' })
export class EntitiesBaseApi {

    private readonly BASE_API = inject(APP_BASE_API);
    protected readonly ENTITIES_API = `${this.BASE_API}/mgmt-api/v1/entities`;
    protected readonly ENTITY_CATEGORIES_API = `${this.BASE_API}/mgmt-api/v1/entity-categories`;

    protected readonly http = inject(HttpClient);

    getEntities(request: GetEntitiesRequest): Observable<GetEntitiesResponse> {
        request.limit ??= 20;
        const params = buildHttpParams(request);
        return this.http.get<GetEntitiesResponse>(this.ENTITIES_API, { params });
    }

    getManagedEntities(entityId: number, request: GetEntitiesRequest): Observable<GetEntitiesResponse> {
        request.limit ??= 999;
        const params = buildHttpParams(request);
        return this.http.get<GetEntitiesResponse>(`${this.ENTITIES_API}/${entityId}/managed-entities`, { params });
    }

    getEntity(entityId: number): Observable<Entity> {
        return this.http.get<Entity>(`${this.ENTITIES_API}/${entityId}`);
    }

    putEntity(id: number, updatedEntity: PutEntity): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${id}`, updatedEntity);
    }

    putEntityCustomization(id: number, customizationItems: CustomizationItem[]): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${id}/custom-contents`, customizationItems);
    }

    deleteEntityCustomization(id: number, customizationItemTag: CustomizationItemTag): Observable<void> {
        return this.http.delete<void>(`${this.ENTITIES_API}/${id}/custom-contents/${customizationItemTag}`);
    }

    getEntityCustomization(id: number): Observable<CustomizationItem[]> {
        return this.http.get<CustomizationItem[]>(`${this.ENTITIES_API}/${id}/custom-contents`);
    }

    getEntitiesUsersLimit(): Observable<EntitiesUsersLimitsResponse> {
        return this.http.get<EntitiesUsersLimitsResponse>(`${this.ENTITIES_API}/user-limits`);
    }

    getEntityAttributes(entityId: number, scope: AttributeScope = null): Observable<Attribute[]> {
        const params = buildHttpParams({ scope });
        return this.http.get<Attribute[]>(`${this.ENTITIES_API}/${entityId}/attributes`, { params });
    }

    getEntityCalendars(entityId: number): Observable<EntityCalendar[]> {
        return this.http.get<EntityCalendar[]>(`${this.ENTITIES_API}/${entityId}/calendars`);
    }

    getEntityProfiles(entityId: number): Observable<EntityProfile[]> {
        return this.http.get<EntityProfile[]>(`${this.ENTITIES_API}/${entityId}/profiles`);
    }

    getEntityGateways(entityId: number): Observable<EntityGateway[]> {
        return this.http.get<EntityGateway[]>(`${this.ENTITIES_API}/${entityId}/gateways`);
    }

    getEntityTaxes(entityId: number): Observable<Tax[]> {
        return this.http.get<Tax[]>(`${this.ENTITIES_API}/${entityId}/taxes`);
    }

    getEntityTypes(entityId: number): Observable<EntityType[]> {
        return this.http.get<EntityType[]>(`${this.ENTITIES_API}/${entityId}/types`);
    }

    getEntityCategories(entityId: number): Observable<EntityCategory[]> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.http.get<EntityCategory[]>(`${this.ENTITY_CATEGORIES_API}`, { params });
    }

    getExternalVenues(entityId: number): Observable<EntityExternalVenue[]> {
        return this.http.get<EntityExternalVenue[]>(`${this.ENTITIES_API}/${entityId}/external-venues`);
    }

    getExternalVenueTemplates(entityId: number, externalVenueId: string): Observable<ExternalInvetories[]> {
        return this.http.get<ExternalInvetories[]>(
            `${this.ENTITIES_API}/${entityId}/external-venues/${externalVenueId}/external-venue-templates`
        );
    }

    getEntitySurcharges(entityId: number, currencyCode: string): Observable<EntitySurcharge[]> {
        const params = buildHttpParams({ currency_code: currencyCode });
        return this.http.get<EntitySurcharge[]>(`${this.ENTITIES_API}/${entityId}/surcharges`, { params });
    }

    postEntitySurcharges(entityId: number, surcharges: EntitySurcharge[]): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/surcharges`, surcharges);
    }

    getEntityExternalCapacities(entityId: number): Observable<EntityExternalCapacity[]> {
        return this.http.get<EntityExternalCapacity[]>(`${this.ENTITIES_API}/${entityId}/loaded-external-capacities`);
    }

    getExternalBarcodesEntityOptions(entityId: number): Observable<{ id: string; properties: Record<string, string[]> }> {
        return this.http.get<{ id: string; properties: Record<string, string[]> }>(`${this.ENTITIES_API}/external-barcodes/${entityId}`);
    }

    getEntityCustomerTypes(entityId: number): Observable<EntityCustomerType[]> {
        return this.http.get<EntityCustomerType[]>(`${this.ENTITIES_API}/${entityId}/customer-types`);
    }

    postEntityCustomerType(entityId: number, customType: EntityCustomerTypeReq): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/customer-types`, customType);
    }

    putEntityCustomerType(entityId: number, customTypeId: number, customType: EntityCustomerTypeReq): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/customer-types/${customTypeId}`, customType);
    }

    deleteEntityCustomerTypes(entityId: number, customTypeId: number): Observable<void> {
        return this.http.delete<void>(`${this.ENTITIES_API}/${entityId}/customer-types/${customTypeId}`);
    }

    getEntityAuthConfig(entityId: number): Observable<LoginAuthConfig> {
        return this.http.get<LoginAuthConfig>(`${this.ENTITIES_API}/${entityId}/auth-config`);
    }

    putEntityAuthConfig(entityId: number, config: Partial<LoginAuthConfig>): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/auth-config`, config);
    }

    getEntityMemberCounter(entityId: number): Observable<{ member_counter: number }> {
        return this.http.get<{ member_counter: number }>(`${this.ENTITIES_API}/${entityId}/member-counter`);
    }

    putEntityMemberCounter(entityId: number, memberCounter: { member_counter: number }): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/member-counter`, memberCounter);
    }

    getEntityFriends(entityId: number): Observable<EntityFriends> {
        return this.http.get<EntityFriends>(`${this.ENTITIES_API}/${entityId}/friends`);
    }

    putEntityFriends(entityId: number, entityFriends: EntityFriends): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/friends`, entityFriends);
    }

}
