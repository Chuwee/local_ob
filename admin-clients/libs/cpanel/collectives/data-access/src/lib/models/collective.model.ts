import { CollectiveExternalValidatorAuthType } from './collective-external-validator-auth-type.enum';
import { CollectiveScope } from './collective-scope.enum';
import { CollectiveStatus } from './collective-status.enum';
import { CollectiveType } from './collective-type.enum';
import { CollectiveValidationMethod } from './collective-validation-method.enum';

export interface Collective {
    id: number;
    name: string;
    status?: CollectiveStatus;
    scope: CollectiveScope;
    type: CollectiveType;
    validation_method: CollectiveValidationMethod;
    generic?: boolean;
    entity?: {
        id: number;
        name: string;
        operator?: {
            id: number;
            name: string;
        };
    };
    external_validator?: {
        external_validator_name?: string;
        external_validator_authentication?: CollectiveExternalValidatorAuthType;
        external_validator_properties?: {
            user: string;
            password: string;
        };
    };
    show_usages: boolean;
    description: string;
}
