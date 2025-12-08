import { GroupData } from './group-data.model';
import { TicketAllocation } from './ticket-allocation.model';
import { TicketBarcode } from './ticket-barcode.model';
import { TicketOriginMarket } from './ticket-origin-market.type';
import { TicketPrint } from './ticket-print.model';
import { TicketSales } from './ticket-sales.model';
import { TicketType } from './ticket-type.enum';
import { TicketValidation } from './ticket-validation.model';

export interface Ticket {
    type: TicketType;
    allocation: TicketAllocation;
    rate: {
        id: number;
        name: string;
    };
    sales: TicketSales;
    group_data: GroupData;
    barcode: TicketBarcode;
    prints: TicketPrint[];
    total_prints: number;
    validation_last_date: string;
    validations: TicketValidation[];
    previous_allocation?: TicketAllocation;
    origin_market?: TicketOriginMarket;
    external_properties?: {
        session_smartbooking: boolean;
    };
}
