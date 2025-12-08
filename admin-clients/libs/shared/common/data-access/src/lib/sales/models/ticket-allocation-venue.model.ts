export interface TicketAllocationVenue {
    id: number;
    name: string;
    entity: {
        id: number;
        name: string;
    };
    city: string;
}
