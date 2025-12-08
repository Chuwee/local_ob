import { TicketValidation } from './ticket-validation.model';

export interface SaleItemProduct {
    id: number;
    name: string;
    type: 'SIMPLE';
    variant_id: number;
    attribute1: ProductAttributeValueRelation;
    attribute2: ProductAttributeValueRelation;
    delivery: ProductDelivery;
    validations: TicketValidation[];
    barcode?: string;
}

export interface ProductAttributeValueRelation {
    id: number;
    name: string;
    value: {
        id: number;
        name: string;
    };
}

export interface ProductDelivery {
    point: {
        id: number;
        name: string;
    };
    date: {
        from: string;
        to: string;
    };
    type: 'SESSION_BASED';
}
