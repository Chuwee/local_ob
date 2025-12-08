import { TicketAllocationType } from '@admin-clients/shared/common/data-access';
import { SeasonTicketRenewalMappingStatus } from './season-ticket-renewal-mapping-status.enum';
import { SeasonTicketRenewalStatus } from './season-ticket-renewal-status.enum';

export interface SeasonTicketRenewal {
    id: string;
    user_id: string;
    member_id: string;
    email: string;
    name: string;
    surname: string;
    entity_id?: number;
    entity_name?: string;
    historic_rate?: string;
    actual_rate: string;
    historic_seat: SeasonTicketRenewalSeat;
    actual_seat?: SeasonTicketRenewalSeat;
    birthday: string;
    balance?: number;
    order_code?: string;
    auto_renewal?: boolean;
    renewal_substatus?: string;
    renewal_status: SeasonTicketRenewalStatus;
    mapping_status: SeasonTicketRenewalMappingStatus;
}

export interface SeasonTicketRenewalSeat {
    seat_type?: TicketAllocationType;
    not_numbered_zone_id?: number;
    sector: string;
    sector_id: number;
    row?: string;
    row_id?: number;
    seat: string;
    seat_id: number;
    price_zone: string;
    not_numbered_zone?: string;
}
