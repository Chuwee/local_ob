import { EventType } from '../../promoters/models/event-type.enum';

export interface TicketAllocationEvent {
    id: number;
    name: string;
    reference: string;
    type: EventType;
    category: {
        id: number;
        code: string;
        description: string;
        custom: {
            id: number;
            code: string;
            description: string;
        };
    };
    entity: {
        id: number;
        name: string;
    };
    promoter: {
        id: number;
        name: string;
    };
}
