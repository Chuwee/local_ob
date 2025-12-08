import { SaleRequestPurchaseContentTextType } from './sale-request-purchase-content-text-type.enum';

export interface SaleRequestPurchaseContentText {
    language: string;
    type: SaleRequestPurchaseContentTextType;
    redirect_url?: string;
}

export interface SaleRequestPurchaseContentTextField {
    formField: string;
    type: SaleRequestPurchaseContentTextType;
}
