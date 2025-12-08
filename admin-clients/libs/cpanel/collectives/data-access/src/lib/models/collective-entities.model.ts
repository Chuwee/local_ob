export interface CollectiveEntity {
    id: number;
    name: string;
    enabled: boolean;
    external_validator_properties?: {
        user: string;
        password?: string;
    };
}
