import { StateProperty } from '@OneboxTM/utils-state';
import { ExternalInvetories, InventoryProviders } from '@admin-clients/shared/common/data-access';
import { Injectable } from '@angular/core';
import { ExternalEntityConfiguration } from '../models/configuration.model';
import { ExternalCapacity } from '../models/external-capacity.model';
import { ExternalPeriodicities } from '../models/external-periodicities.model';
import { ExternalRoles } from '../models/external-roles.model';

@Injectable({
    providedIn: 'root'
})
export class ExternalEntitiesState {
    readonly capacities = new StateProperty<ExternalCapacity[]>();
    readonly configuration = new StateProperty<ExternalEntityConfiguration>();
    readonly link = new StateProperty<void>();
    readonly clubCodes = new StateProperty<string[]>();
    readonly periodicities = new StateProperty<ExternalPeriodicities[]>();
    readonly roles = new StateProperty<ExternalRoles[]>();
    readonly terms = new StateProperty<object[]>();
    readonly inventories = new StateProperty<ExternalInvetories[]>();
    readonly inventoryProviders = new StateProperty<InventoryProviders>();
}
