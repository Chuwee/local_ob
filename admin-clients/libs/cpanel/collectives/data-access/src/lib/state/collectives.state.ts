import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { CollectiveEntity } from '../models/collective-entities.model';
import { CollectiveExternalValidator } from '../models/collective-external-validator.model';
import { Collective } from '../models/collective.model';
import { GetCollectiveCodesResponse } from '../models/get-collective-codes-response.model';
import { GetCollectivesResponse } from '../models/get-collectives-response.model';

@Injectable({
    providedIn: 'root'
})
export class CollectivesState {

    readonly collectivesList = new StateProperty<GetCollectivesResponse>();

    readonly collective = new StateProperty<Collective>();

    readonly collectiveSaving = new StateProperty<void>();

    readonly collectiveEntities = new StateProperty<CollectiveEntity[]>();

    readonly collectiveEntitiesSaving = new StateProperty<void>();

    readonly collectiveExternalValidators = new StateProperty<CollectiveExternalValidator[]>();

    // External validator properties: user and password
    readonly collectiveExternalValidatorPropertiesSaving = new StateProperty<void>();

    readonly collectiveCodes = new StateProperty<GetCollectiveCodesResponse>();

    readonly collectiveCodeSaving = new StateProperty<void>();

    readonly collectiveCodesSaving = new StateProperty<void>();

    readonly collectiveCodesDeleting = new StateProperty<void>();

    readonly exportCollectiveCodes = new StateProperty<void>();

}
