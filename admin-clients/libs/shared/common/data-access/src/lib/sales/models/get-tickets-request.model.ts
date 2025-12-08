import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { EventType } from '../../promoters/models/event-type.enum';
import { TicketDetailType } from './ticket-detail-type.enum';
import { TicketOriginMarket } from './ticket-origin-market.type';
import { TicketState } from './ticket-state.enum';

export interface GetTicketsRequest extends PageableFilter {
    id?: number[];
    state?: TicketState;
    session_start_date_from?: string;
    session_start_date_to?: string;
    channel_id?: number[];
    channel_entity_id?: string;
    event_id?: number[];
    event_entity_id?: string;
    client_entity_id?: number[];
    session_id?: number[];
    sector_id?: number[];
    price_type_id?: number[];
    purchase_date_from?: string;
    purchase_date_to?: string;
    ticket_type?: string;
    validation?: string;
    print?: string;
    customer_id?: string;
    event_type?: EventType;
    currency_code?: string;
    type?: TicketDetailType[];
    product_id?: number[];
    origin_market?: TicketOriginMarket;
    reallocation_refund?: boolean;
}
