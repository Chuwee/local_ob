import { ValidatorFn } from '@angular/forms';

export type ProductTicketTextContent = {
    type: ProductTicketContentTextType;
    language: string;
    value: string;
};

export const productTicketContentTextType = { name: 'NAME', delivery: 'DELIVERY_DETAIL' } as const;
export type ProductTicketContentTextType = (typeof productTicketContentTextType)[keyof typeof productTicketContentTextType];

export const productTicketTextRestrictions = {
    nameLength: 50,
    deliveryLength: 500
};

export interface ProductTicketTextFields {
    type: ProductTicketContentTextType;
    validators?: ValidatorFn[];
}
