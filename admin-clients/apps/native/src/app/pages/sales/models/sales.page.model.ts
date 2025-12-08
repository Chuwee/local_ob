import { GetOrdersWithFieldsRequest, VmOrderWithFields } from '@admin-clients/cpanel-sales-data-access';
import { GetTicketsRequest, OrderItem, OrderItemDetails } from '@admin-clients/shared/common/data-access';

export type SegmentType = 'tickets' | 'transactions';

interface Kpi {
    name: string;
    amount: number | string;
}
export enum KpiTicketNames {
    products = 'products',
    basePrice = 'basePrice',
    discountsAndPromotions = 'discountsAndPromotions',
    recharges = 'recharges',
    total = 'total'
}
export enum KpiTransactionNames {
    transactions = 'transactions',
    products = 'products',
    basePrice = 'basePrice',
    discountsAndPromotions = 'discountsAndPromotions',
    recharges = 'recharges',
    paymentMethodCharges = 'paymentMethodCharges',
    total = 'total'
}
export interface KpiTicket extends Kpi {
    name: keyof typeof KpiTicketNames;
}
export interface KpiTransaction extends Kpi {
    name: keyof typeof KpiTransactionNames;
}

export interface AppliedSearchFilter {
    name: string;
    value: any;
    label: {
        name: string;
        value: string;
    };
}

export interface SalesModel {
    tickets: {
        kpis: Kpi[];
        searchedResults: (OrderItemDetails | VmOrderWithFields)[];
        totalResultsCounter: number;
        appliedSearchFilters: GetTicketsRequest & GetOrdersWithFieldsRequest;
    };
    transactions: {
        kpis: Kpi[];
        searchedResults: (OrderItem | VmOrderWithFields)[];
        totalResultsCounter: number;
        appliedSearchFilters: GetTicketsRequest & GetOrdersWithFieldsRequest;
    };
}
