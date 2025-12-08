import { ProducerStatus } from './producer-status.model';

export interface Producer {
    id: number;
    name: string;
    status: ProducerStatus;
    nif: string;
    entity: {
        id: number;
        name: string;
    };
    use_simplified_invoice?: boolean;
    social_reason?: string;
    contact?: {
        address?: string;
        country?: {
            code: string;
        };
        country_subdivision?: {
            code: string;
        };
    };
}
