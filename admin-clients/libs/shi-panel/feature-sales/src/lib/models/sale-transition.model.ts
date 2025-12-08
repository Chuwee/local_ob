import { TimelineElementStatus } from '@admin-clients/shared/common/ui/components';

export interface Transition {
    trace_id: string;
    action: TransitionAction;
    status: TimelineElementStatus;
    date: string;
    description?: string;
}

export enum TransitionAction {
    shiSale = 'SHI_SALE',
    supplierHoldRequest = 'SUPPLIER_HOLD_REQUEST',
    shiSaleConfirm = 'SHI_SALE_CONFIRM',
    shiFulfill = 'SHI_FULFILL',
    supplierSaleRequest = 'SUPPLIER_SALE_REQUEST',
    supplierSale = 'SUPPLIER_SALE',
    supplierFulfill = 'SUPPLIER_FULFILL',
    supplierSaleUpdateRequest = 'SUPPLIER_SALE_UPDATE_REQUEST',
    shiSaleUpdate = 'SHI_SALE_UPDATE'
}
