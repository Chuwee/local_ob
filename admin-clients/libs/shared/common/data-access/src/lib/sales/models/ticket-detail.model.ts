import { ActionsHistory } from './actions-history.model';
import { BuyerData } from './buyer-data.model';
import { ClientType } from './client-type.enum';
import { OperativeGrant } from './operative-grant.model';
import { OrderType } from './order-type.enum';
import { Price, PricePartialTaxes } from './price.model';
import { SaleItemProduct } from './product.model';
import { TicketDetailState } from './ticket-detail-state.enum';
import { TicketDetailType } from './ticket-detail-type.enum';
import { TicketOriginMarket } from './ticket-origin-market.type';
import { ReleaseData, RenewalDetails, TransferData } from './ticket-seat-management-data.model';
import { Ticket } from './ticket.model';

export interface TicketDetail {
    id: number;
    type: TicketDetailType;
    state: TicketDetailState;
    ticket: Ticket;
    product: SaleItemProduct;
    price: Price & { taxes?: PricePartialTaxes };
    attendant: Map<string, string>;
    action: OperativeGrant;
    actions_history: ActionsHistory[];
    secondary_market?: {
        original_order?: string;
        season_ticket_seat_id?: number;
        purchase_order?: string;
    };
    order: {
        code: string;
        type: OrderType;
        date: string;
        booking_expires?: string;
        related_original_reallocation_code?: string;
    };
    related_reallocation_code?: string;
    channel: {
        name: string;
        entity: {
            name: string;
        };
    };
    subitems?: {
        id?: number;
        session?: {
            id?: number;
            name?: string;
            date?: string;
        };
        event?: {
            id?: number;
            name?: string;
        };
        price?: any;
        state?: string;
        next_order?: {
            type?: string;
        };
        secondary_market?: {
            uuid?: string;
            purchase_order?: string;
            purchase_date?: string;
        };
    }[];
    client_type: ClientType;
    buyer_data: BuyerData;
    transfer_data: TransferData;
    release_data: ReleaseData;
    renewal_details?: RenewalDetails;
    origin_market?: TicketOriginMarket;
    next_order?: {
        code: string;
        type: OrderType;
        date: string;
    };
    previous_order?: {
        code: string;
        type: OrderType;
        date: string;
    };
    pack?: {
        id: number;
        code: string;
        main_item: boolean;
        informative_prices: {
            insurance: number;
            pack_item: number;
        };
    };
}
