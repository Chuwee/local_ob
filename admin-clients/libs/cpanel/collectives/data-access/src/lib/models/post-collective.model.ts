import { CollectiveType } from './collective-type.enum';
import { CollectiveValidationMethod } from './collective-validation-method.enum';

export interface PostCollective {
    entity_id?: number;

    name: string;

    type: CollectiveType;

    /**
     * Validation Method
     *
     * Could be empty when Collective Type is Venue
     */
    validation_method?: CollectiveValidationMethod;

    external_validator?: string;

    description?: string;
}
