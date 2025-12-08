import { CollectiveValidationMethod } from './collective-validation-method.enum';

export interface CollectiveCode {
    code: string;
    validation_method: CollectiveValidationMethod;
    key?: string;
    validity_period?: {
        from: string;
        to: string;
    };
    usage?: {
        limit: number;
        current: number;
    };
}
