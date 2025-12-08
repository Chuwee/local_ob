import { StateProperty } from '@OneboxTM/utils-state';
import { EventTicketTemplate } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { Injectable } from '@angular/core';
import { PassbookImageType, PdfImageType, ProductTicketImageContent } from '../models/product-ticket-content-image.model';
import { ProductTicketTextContent } from '../models/product-ticket-content-text.model';

@Injectable({ providedIn: 'root' })
export class ProductCommunicationState {
    readonly pdfTextContents = new StateProperty<ProductTicketTextContent[]>();
    readonly pdfImageContents = new StateProperty<ProductTicketImageContent<PdfImageType>[]>();
    readonly passbookTextContents = new StateProperty<ProductTicketTextContent[]>();
    readonly passbookImageContents = new StateProperty<ProductTicketImageContent<PassbookImageType>[]>();
    readonly productTemplates = new StateProperty<EventTicketTemplate[]>();
}
