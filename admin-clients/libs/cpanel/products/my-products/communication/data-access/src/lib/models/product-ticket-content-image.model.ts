import { ValidatorFn } from '@angular/forms';

export const productTicketPdfContentImageType = 'BODY';
export type PdfImageType = typeof productTicketPdfContentImageType[number];

export const productTicketPassbookContentImageType = 'THUMBNAIL';
export type PassbookImageType = typeof productTicketPassbookContentImageType[number];

export type ProductTicketImageContent<T> = {
    type: T;
    language: string;
    image_url: string;
};

export interface ProductTicketImageFields<T> {
    type: T;
    validators?: ValidatorFn[];
}
