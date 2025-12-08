import { ActivityTicketType } from './activity/activity-ticket-type.enum';

export interface VenueTemplatePriceType {
    id: number;
    name?: string;
    code?: string;
    color?: string;
    default?: boolean;
    priority?: number;
    rate?: {
        name: string;
        id: number;
    };
    ticketType?: ActivityTicketType;
    additional_config?: {
        restrictive_access: boolean;
        gate_id: number;
    };
}

export type PostVenueTemplatePriceType = Omit<VenueTemplatePriceType, 'id'>;
