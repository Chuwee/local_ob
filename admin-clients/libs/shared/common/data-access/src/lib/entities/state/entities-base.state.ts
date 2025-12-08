import { StateProperty } from '@OneboxTM/utils-state';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { Attribute } from '../../models/attributes.model';
import { EntityType } from '../../models/entity-type.enum';
import { CustomizationItem } from '../models/customization-item.model';
import { EntitiesUsersLimitsResponse } from '../models/entities-users-limits.model';
import { EntityCalendar } from '../models/entity-calendar.model';
import { EntityCategory } from '../models/entity-categories.model';
import { EntityCustomerType } from '../models/entity-customer-type.model';
import { EntityExternalCapacity } from '../models/entity-external-capacity.model';
import { EntityExternalVenue } from '../models/entity-external-venue.model';
import { EntityFriends } from '../models/entity-friends.model';
import { EntityGateway } from '../models/entity-gateway.model';
import { EntityProfile } from '../models/entity-profile.model';
import { EntitySurcharge } from '../models/entity-surcharge.model';
import { Entity } from '../models/entity.model';
import { ExternalInvetories } from '../models/external-inventories.model';
import { GetEntitiesResponse } from '../models/get-entities-response.model';
import { LoginAuthConfig } from '../models/login-auth-config.model';
import { Tax } from '../models/tax.model';

@Injectable({ providedIn: 'root' })
export class EntitiesBaseState {
    readonly entity = new StateProperty<Entity>();
    readonly entityList = new StateProperty<GetEntitiesResponse>();
    readonly managedEntitiesList = new StateProperty<GetEntitiesResponse>();
    readonly entityCustomization = new StateProperty<CustomizationItem[]>();
    readonly entitySaving = new StateProperty<Entity>();
    readonly entityTaxes = new StateProperty<Tax[]>();
    readonly entityGateways = new StateProperty<EntityGateway[]>();
    readonly entityTypes = new StateProperty<EntityType[]>();
    readonly entityCategories = new StateProperty<EntityCategory[]>();
    readonly entitiesUsersLimits = new StateProperty<EntitiesUsersLimitsResponse>();
    readonly entitySurcharges = new StateProperty<EntitySurcharge[]>();
    readonly entityAttributes = new StateProperty<Attribute[]>();
    readonly entityCalendars = new StateProperty<EntityCalendar[]>();
    readonly entityProfiles = new StateProperty<EntityProfile[]>();
    readonly entityExternalCapacities = new StateProperty<EntityExternalCapacity[]>();
    readonly externalBarcodesEntityOptions = new StateProperty<{ id: string; properties: Record<string, string[]> }>();
    readonly entitiesCache = new ItemCache<Entity>();
    readonly entityCustomerTypes = new StateProperty<EntityCustomerType[]>();
    readonly entityAuthConfig = new StateProperty<LoginAuthConfig>();
    readonly entityMemberCounter = new StateProperty<{ member_counter: number }>();
    readonly entityFriends = new StateProperty<EntityFriends>();
    readonly externalVenues = new StateProperty<EntityExternalVenue[]>();
    readonly externalVenueTemplates = new StateProperty<ExternalInvetories[]>();
}
