import { AutomaticSaleConfig } from './automatic-sale-config.model';
import { AutomaticSale } from './automatic-sale.model';

export interface AutomaticSalesPost {
    sales: AutomaticSale[];
    config: AutomaticSaleConfig;
}
