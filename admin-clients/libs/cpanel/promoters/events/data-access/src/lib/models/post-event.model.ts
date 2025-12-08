import { EventAvetConnection } from './event-avet-connection.enum';

export interface PostEvent {
    name: string;
    type: string;
    entity_id: number;
    producer_id: number;
    category_id: number;
    currency_code?: string;
    reference?: string;
    additional_config?: {
        avet_config?: EventAvetConnection;
        avet_competition_id?: number;
        venue_template_id?: number;
        external_event_id?: string;
        inventory_provider?: string;
        standalone?: boolean;
    };
}
