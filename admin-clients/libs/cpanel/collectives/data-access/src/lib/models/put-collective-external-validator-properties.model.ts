export interface PutCollectiveExternalValidatorProperties {
    external_validator_properties: {
        user: string;
        password: string;
    };
    entity_id?: number;
}
