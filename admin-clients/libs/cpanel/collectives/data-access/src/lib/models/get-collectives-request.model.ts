import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { CollectiveStatus } from './collective-status.enum';
import { CollectiveType } from './collective-type.enum';
import { CollectiveValidationMethod } from './collective-validation-method.enum';

export interface GetCollectivesRequest extends PageableFilter {
    entity_id?: number;
    type?: CollectiveType;
    validation_method?: CollectiveValidationMethod[];
    status?: CollectiveStatus;
}
