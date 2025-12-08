import { Sale } from './sale.model';

export interface TicketSales {
    promotion: Sale;
    discount: Sale;
    automatic: Sale;
}
