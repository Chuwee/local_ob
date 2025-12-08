import { B2bUserType } from './b2b-client.model';

export interface B2bSeatReduced {
    id?: number;
    event?: {
        id?: number;
        name?: string;
    };
    session?: {
        id?: number;
        name?: string;
        date?: string;
    };
    seat?: {
        venue_name?: string;
        sector_name?: string;
        row_name?: string;
        seat_name?: string;
        seat_id?: number;
        status?: B2bSeatStatus;
    };
    channel?: {
        id?: number;
        name?: string;
    };
    price?: number;
    type?: B2bSeatType;
}

export enum B2bSeatType {
    published = 'PUBLISHED',
    unpublished = 'UNPUBLISHED'
}
export enum B2bSeatStatus {
    available = 'AVAILABLE',
    sold = 'SOLD',
    blockedPromoter = 'BLOCKED_PROMOTER',
    blockedSystem = 'BLOCKED_SYSTEM',
    kill = 'KILL',
    issued = 'ISSUED',
    validated = 'VALIDATED',
    refunded = 'REFUNDED',
    cancelled = 'CANCELLED',
    blockedPresale = 'BLOCKED_PRESALE',
    blockedSale = 'BLOCKED_SALE',
    invitation = 'INVITATION',
    blockedSeasonTicket = 'BLOCKED_SEASON_TICKET',
    blockedExternal = 'BLOCKED_EXTERNAL',
    deletedExternal = 'DELETED_EXTERNAL'
}
export enum B2bSeatUserType {
    b2b = 'B2B',
    cpanel = 'CPANEL'
}

export interface B2bPublisher {
    user_type?: B2bUserType;
    client_entity_id?: number;
    client_id?: number;
    client_name?: string;
    user_id?: number;
    username?: string;
}
export interface B2bSeat extends B2bSeatReduced {
    entity_id?: number;
    source_quota_id?: number;
    target_quota_id?: number;
    source_price_type_id?: number;
    target_price_type_id?: number;
    date?: string;
    publisher?: B2bPublisher;
    historic?: B2bSeatHistoricEntry[];
}

export interface B2bSeatHistoricEntry {
    publisher?: B2bPublisher;
    source_quota_id?: number;
    target_quota_id?: number;
    source_price_type_id?: number;
    target_price_type_id?: number;
    date?: string;
    price?: number;
    type?: B2bSeatType;
}
