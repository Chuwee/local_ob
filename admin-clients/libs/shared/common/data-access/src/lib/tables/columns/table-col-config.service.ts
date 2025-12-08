import { Injectable } from '@angular/core';

// colselectionDialogService is used to set and get the order of the columns of a table on localStorage
export type PreserveConfig = 'BUYERS' | 'TICKETS' | 'PRODUCTS' | 'ORDERS' | 'MEMBER_ORDERS' | 'VOUCHER_ORDERS' | 'PAYOUTS'
    | 'EXP_TICKETS' | 'EXP_TICKETS_ACTIONS' | 'EXP_ORDERS' | 'EXP_MEMBER_ORDERS' | 'EXP_VOUCHER_ORDERS'
    | 'EXP_CUSTOMERS' | 'EXP_BUYERS' | 'EXP_COLLECTIVES' | 'EXP_COLLECTIVES_USER' | 'EXP_COLLECTIVES_USER_PASS'
    | 'EXP_VOUCHER' | 'EXP_CLIENT_ECONOMIC_MANAGMENT' | 'EXP_SESSION_INTERNAL_BARCODES' | 'EXP_SESSION_EXTERNAL_BARCODES'
    | 'EXP_NOTIFICATION_DETAILS' | 'EXP_NOTIFICATIONS_SUMMARY' | 'EXP_SESSION_CAPACITY' | 'EXP_SEASON_TICKET_RENEWAL'
    | 'EXP_SUBSCRIPTION_CLIENTS' | 'EXP_B2B_CLIENTS' | 'EXP_B2B_SEATS' | 'EXP_PRODUCTS' | 'EXP_PAYOUTS' | 'EXP_SHI_SALES'
    | 'EXP_SHI_LISTINGS' | 'EXP_SHI_MAPPINGS' | 'EXP_SHI_MATCHINGS' | 'EXP_SHI_ERROR_DASHBOARD' | 'HIDDEN_AGGS_ORDERS';

@Injectable({ providedIn: 'root' })
export class TableColConfigService {

    setColumns(type: PreserveConfig, columns: string[]): void {
        localStorage.setItem(type, JSON.stringify(columns));
    }

    getColumns(type: PreserveConfig): string[] {
        const array = localStorage.getItem(type);
        if (!array) return null;
        return JSON.parse(array);
    }
}
