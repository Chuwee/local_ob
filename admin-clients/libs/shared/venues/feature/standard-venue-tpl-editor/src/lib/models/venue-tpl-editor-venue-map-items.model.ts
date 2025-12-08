import { Aisle, NotNumberedZone, Row, Seat, Sector } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';

export type EdVenueTplItem = EdSector | EdRow | EdSeat | Aisle | EdNotNumberedZone;

export interface EdVenueMap {
    sectors: EdSector[];
}

export interface EdSector extends Omit<Sector, 'rows' | 'notNumberedZone' | 'record' | 'type' | 'venueTemplate'>, ItemStatus {
    rows?: EdRow[];
    notNumberedZones?: EdNotNumberedZone[];
}

export interface EdRow extends Omit<Row, 'seats'>, ItemStatus {
    seats: EdSeat[];
}

export interface EdSeat extends Omit<Seat,
    'status' | 'quota' | 'priceType' | 'blockingReason' | 'visibility' | 'accessibility' | 'gate' | 'linkable' | 'notAssignableReason'
    | 'linked' | 'sessionPack' | 'seatRecord' | 'firstCustomTag' | 'secondCustomTag' | 'ticketId' | 'sectorName' | 'rowName'
>, ItemStatus {}

export interface EdNotNumberedZone extends Omit<NotNumberedZone,
    'priceType' | 'gate' | 'visibility' | 'accessibility' | 'statusCounters' | 'blockingReasonCounters' | 'sessionPackCounters'
    | 'quotaCounters' | 'record' | 'linkableSeats' | 'firstCustomTag' | 'secondCustomTag' | 'sectorName'
>, ItemStatus {
    initialCapacity?: number;
}

export interface EdVenueMapMaps {
    sectors: Map<number, EdSector>;
    rows: Map<number, EdRow>;
    seats: Map<number, EdSeat>;
    nnzs: Map<number, EdNotNumberedZone>;
}

interface ItemStatus {
    create?: boolean;
    modify?: boolean;
    delete?: boolean;
}
