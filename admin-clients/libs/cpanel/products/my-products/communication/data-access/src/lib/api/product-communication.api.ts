import { EventTicketTemplate, EventTicketTemplateType, TicketContentFormat } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PassbookImageType, PdfImageType, ProductTicketImageContent } from '../models/product-ticket-content-image.model';
import { ProductTicketTextContent } from '../models/product-ticket-content-text.model';

@Injectable({ providedIn: 'root' })
export class ProductCommunicationApi {
    readonly #PRODUCTS_API = `${inject(APP_BASE_API)}/mgmt-api/v1/products`;
    readonly #CONTENTS_PATH = 'ticket-contents';
    readonly #PDF_PATH = 'PDF';
    readonly #PASSBOOK_PATH = 'PASSBOOK';
    readonly #TEXTS_PATH = 'texts';
    readonly #IMAGES_PATH = 'images';
    readonly #LANGUAGES_PATH = 'languages';

    readonly #http = inject(HttpClient);

    // TICKET CONTENTS
    getProductPdfTextContents(productId: number): Observable<ProductTicketTextContent[]> {
        return this.#http.get<ProductTicketTextContent[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PDF_PATH}/${this.#TEXTS_PATH}`
        );
    }

    postProductPdfTextContents(productId: number, contents: ProductTicketTextContent[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PDF_PATH}/${this.#TEXTS_PATH}`,
            contents
        );
    }

    getProductPdfImageContents(productId: number): Observable<ProductTicketImageContent<PdfImageType>[]> {
        return this.#http.get<ProductTicketImageContent<PdfImageType>[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PDF_PATH}/${this.#IMAGES_PATH}`
        );
    }

    postProductPdfImageContents(productId: number, contents: ProductTicketImageContent<PdfImageType>[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PDF_PATH}/${this.#IMAGES_PATH}`,
            contents
        );
    }

    deleteProductPdfImageContents(productId: number, language: string): Observable<void> {
        return this.#http.delete<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PDF_PATH}/${this.#IMAGES_PATH}` +
            `/${this.#LANGUAGES_PATH}/${language}`
        );
    }

    getProductPassbookTextContents(productId: number): Observable<ProductTicketTextContent[]> {
        return this.#http.get<ProductTicketTextContent[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PASSBOOK_PATH}/${this.#TEXTS_PATH}`
        );
    }

    postProductPassbookTextContents(productId: number, contents: ProductTicketTextContent[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PASSBOOK_PATH}/${this.#TEXTS_PATH}`,
            contents
        );
    }

    getProductPassbookImageContents(productId: number): Observable<ProductTicketImageContent<PassbookImageType>[]> {
        return this.#http.get<ProductTicketImageContent<PassbookImageType>[]>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PASSBOOK_PATH}/${this.#IMAGES_PATH}`
        );
    }

    postProductPassbookImageContents(productId: number, contents: ProductTicketImageContent<PassbookImageType>[]): Observable<void> {
        return this.#http.post<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PASSBOOK_PATH}/${this.#IMAGES_PATH}`,
            contents
        );
    }

    deleteProductPassbookImageContents(productId: number, language: string): Observable<void> {
        return this.#http.delete<void>(
            `${this.#PRODUCTS_API}/${productId}/${this.#CONTENTS_PATH}/${this.#PASSBOOK_PATH}/${this.#IMAGES_PATH}` +
            `/${this.#LANGUAGES_PATH}/${language}`
        );
    }

    //PRODUCT TEMPLATES
    getProductTemplates(productId: number): Observable<EventTicketTemplate[]> {
        return this.#http.get<EventTicketTemplate[]>(`${this.#PRODUCTS_API}/${productId}/ticket-templates`);
    }

    putProductTemplate(
        productId: number, id: number, type: EventTicketTemplateType, format: TicketContentFormat
    ): Observable<void> {
        return this.#http.put<void>(`${this.#PRODUCTS_API}/${productId}/ticket-templates/${type}/${format}`, id);
    }

}
