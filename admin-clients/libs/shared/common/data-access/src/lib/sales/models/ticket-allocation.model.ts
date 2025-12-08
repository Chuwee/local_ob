import { Price } from './price.model';
import { TicketAccessibility } from './ticket-accessibility.enum';
import { TicketAllocationEvent } from './ticket-allocation-event.model';
import { TicketAllocationRow } from './ticket-allocation-row.model';
import { TicketAllocationSession } from './ticket-allocation-session.model';
import { TicketAllocationType } from './ticket-allocation-type.enum';
import { TicketAllocationVenue } from './ticket-allocation-venue.model';
import { TicketVisibility } from './ticket-visibility.enum';

export interface TicketAllocation {
    type: TicketAllocationType;
    event: TicketAllocationEvent;
    session: TicketAllocationSession;
    venue: TicketAllocationVenue;
    row: TicketAllocationRow;
    accessibility: TicketAccessibility;
    visibility: TicketVisibility;
    seat: {
        name: string;
        id: number;
    };
    price_type: {
        id: number;
        name: string;
    };
    sector: {
        id: number;
        name: string;
    };
    not_numbered_area: {
        id: number;
        name: string;
    };
    access: {
        id: number;
        name: string;
    };
    quota: {
        id: number;
        name: string;
    };
    price?: Partial<Price>;
}
