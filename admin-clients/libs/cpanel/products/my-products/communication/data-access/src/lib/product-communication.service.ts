import { StateManager } from '@OneboxTM/utils-state';
import { EventTicketTemplateType, TicketContentFormat } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { Injectable, inject } from '@angular/core';
import { ProductCommunicationApi } from './api/product-communication.api';
import { PassbookImageType, PdfImageType, ProductTicketImageContent } from './models/product-ticket-content-image.model';
import { ProductTicketTextContent } from './models/product-ticket-content-text.model';
import { ProductCommunicationState } from './state/product-communication.state';

@Injectable({ providedIn: 'root' })
export class ProductCommunicationService {
    readonly #api = inject(ProductCommunicationApi);
    readonly #state = inject(ProductCommunicationState);

    readonly ticketsContents = Object.freeze({
        pdf: Object.freeze({
            texts: Object.freeze({
                load: (productId: number) => StateManager.load(
                    this.#state.pdfTextContents,
                    this.#api.getProductPdfTextContents(productId)
                ),
                get$: () => this.#state.pdfTextContents.getValue$(),
                update: (productId: number, contents: ProductTicketTextContent[]) => StateManager.inProgress(
                    this.#state.pdfTextContents,
                    this.#api.postProductPdfTextContents(productId, contents)
                ),
                loading$: () => this.#state.pdfTextContents.isInProgress$(),
                clear: () => this.#state.pdfTextContents.setValue(null)
            }),
            images: Object.freeze({
                load: (productId: number) => StateManager.load(
                    this.#state.pdfImageContents,
                    this.#api.getProductPdfImageContents(productId)
                ),
                get$: () => this.#state.pdfImageContents.getValue$(),
                post: (productId: number, contents: ProductTicketImageContent<PdfImageType>[]) => StateManager.inProgress(
                    this.#state.pdfImageContents,
                    this.#api.postProductPdfImageContents(productId, contents)
                ),
                delete: (productId: number, lang: string) =>
                    StateManager.inProgress(
                        this.#state.pdfImageContents,
                        this.#api.deleteProductPdfImageContents(productId, lang)
                    ),
                loading$: () => this.#state.pdfImageContents.isInProgress$(),
                clear: () => this.#state.pdfImageContents.setValue(null)
            })
        }),
        passbook: Object.freeze({
            texts: Object.freeze({
                load: (productId: number) => StateManager.load(
                    this.#state.passbookTextContents,
                    this.#api.getProductPassbookTextContents(productId)
                ),
                get$: () => this.#state.passbookTextContents.getValue$(),
                update: (productId: number, contents: ProductTicketTextContent[]) => StateManager.inProgress(
                    this.#state.passbookTextContents,
                    this.#api.postProductPassbookTextContents(productId, contents)
                ),
                loading$: () => this.#state.passbookTextContents.isInProgress$(),
                clear: () => this.#state.passbookTextContents.setValue(null)
            }),
            images: Object.freeze({
                load: (productId: number) => StateManager.load(
                    this.#state.passbookImageContents,
                    this.#api.getProductPassbookImageContents(productId)
                ),
                get$: () => this.#state.passbookImageContents.getValue$(),
                post: (productId: number, contents: ProductTicketImageContent<PassbookImageType>[]) => StateManager.inProgress(
                    this.#state.passbookImageContents,
                    this.#api.postProductPassbookImageContents(productId, contents)
                ),
                delete: (productId: number, lang: string) => StateManager.inProgress(
                    this.#state.passbookImageContents,
                    this.#api.deleteProductPassbookImageContents(productId, lang)
                ),
                loading$: () => this.#state.passbookImageContents.isInProgress$(),
                clear: () => this.#state.passbookImageContents.setValue(null)
            })
        })
    });

    readonly productTemplates = Object.freeze({
        load: (productId: number) => StateManager.load(
            this.#state.productTemplates,
            this.#api.getProductTemplates(productId)
        ),
        get$: () => this.#state.productTemplates.getValue$(),
        update: (
            productId: number, templateId: number, type: EventTicketTemplateType, format: TicketContentFormat
        ) => StateManager.inProgress(
            this.#state.productTemplates,
            this.#api.putProductTemplate(productId, templateId, type, format)
        ),
        loading$: () => this.#state.productTemplates.isInProgress$(),
        clear: () => this.#state.productTemplates.setValue(null)
    });
}
