import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EventTicketTemplate, EventTicketTemplateFields } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    PassbookImageType, PdfImageType, ProductCommunicationService, productTicketContentTextType,
    ProductTicketImageContent, ProductTicketImageFields, productTicketPassbookContentImageType,
    productTicketPdfContentImageType, ProductTicketTextContent, ProductTicketTextFields
} from '@admin-clients/cpanel-products-communication-data-access';
import { TicketTemplatesService } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import {
    EphemeralMessageService, LanguageBarComponent, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, inject, OnInit,
    viewChild, viewChildren
} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, forkJoin, Observable, of } from 'rxjs';
import { first, map, tap } from 'rxjs/operators';
import { ProductTicketContentPassbookComponent } from './ticket-content-passbook/product-ticket-content-passbook.component';
import { ProductTicketContentPdfComponent } from './ticket-content-pdf/product-ticket-content-pdf.component';
import { ProductTicketTemplatesComponent } from './ticket-templates/product-ticket-templates.component';

@Component({
    selector: 'app-product-ticket-content',
    imports: [
        FormContainerComponent, LanguageBarComponent, MaterialModule, ReactiveFormsModule,
        AsyncPipe, ProductTicketContentPassbookComponent, ProductTicketContentPdfComponent,
        ProductTicketTemplatesComponent
    ],
    templateUrl: './product-ticket-content.component.html',
    styleUrls: ['./product-ticket-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductTicketContentComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #productsService = inject(ProductsService);
    readonly #productCommunicationService = inject(ProductCommunicationService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #ticketTemplatesService = inject(TicketTemplatesService);

    private readonly _matExpansionPanelQueryList = viewChildren(MatExpansionPanel);
    private readonly _pdfContentComponent = viewChild<ProductTicketContentPdfComponent>('pdfContent');
    private readonly _passbookContentComponent = viewChild<ProductTicketContentPassbookComponent>('passbookContent');
    private readonly _templatesComponent = viewChild<ProductTicketTemplatesComponent>('templatesComponent');

    readonly textContentType = productTicketContentTextType;
    readonly pdfImageContentType = productTicketPdfContentImageType;
    readonly passbookImageContentType = productTicketPassbookContentImageType;

    readonly form = this.#fb.group({});

    isLoadingOrSaving$ = booleanOrMerge([
        this.#productsService.product.inProgress$(),
        this.#productsService.product.languages.inProgress$(),
        this.#productCommunicationService.ticketsContents.passbook.images.loading$(),
        this.#productCommunicationService.ticketsContents.passbook.texts.loading$(),
        this.#productCommunicationService.ticketsContents.pdf.images.loading$(),
        this.#productCommunicationService.ticketsContents.pdf.texts.loading$(),
        this.#productCommunicationService.productTemplates.loading$(),
        this.#ticketTemplatesService.isTicketTemplatesLoading$()
    ]);

    readonly #selectedLanguage = new BehaviorSubject<string>(null);
    readonly selectedLanguage$ = this.#selectedLanguage.asObservable();
    readonly languages$ = this.#productsService.product.languages.get$().pipe(
        first(Boolean),
        tap(languages => this.#selectedLanguage.next(languages.find(lang => lang.is_default).code)),
        map(languages => languages.map(lang => lang.code))
    );

    ngOnInit(): void {
        this.#productsService.product.get$().pipe(first(Boolean)).subscribe(product => {
            this.#productsService.product.languages.load(product.product_id);
        });
    }

    canChangeLanguage: () => Observable<boolean> = () =>
        this.validateIfCanChangeLanguage();

    cancel(): void {
        if (this._pdfContentComponent) {
            this._pdfContentComponent().cancel();
        }
        if (this._passbookContentComponent) {
            this._passbookContentComponent().cancel();
        }
        if (this._templatesComponent) {
            this._templatesComponent().cancel();
        }
    }

    getTemplateFields(contentForm: FormGroup, templateFields: EventTicketTemplateFields[]): EventTicketTemplate[] {
        const templatesToSave: EventTicketTemplate[] = [];
        templateFields.forEach(templateField => {
            const field = contentForm.get(templateField.formField);
            if (field.dirty) {
                templatesToSave.push({ id: field.value, type: templateField.type, format: templateField.format });
            }
        });
        return templatesToSave;
    }

    getTextFields(
        contentForm: FormGroup, textFields: ProductTicketTextFields[], language: string
    ): ProductTicketTextContent[] {
        const textsToSave: ProductTicketTextContent[] = [];
        textFields.forEach(textField => {
            const field = contentForm.get(textField.type);
            if (field.dirty) {
                textsToSave.push({ type: textField.type, value: field.value, language });
            }
        });
        return textsToSave;
    }

    getPassbookImageFields(
        contentForm: FormGroup, imageFields: ProductTicketImageFields<PassbookImageType>[], language: string
    ): { [key: string]: ProductTicketImageContent<PassbookImageType>[] } {
        const imagesToSave: ProductTicketImageContent<PassbookImageType>[] = [];
        const imagesToDelete: ProductTicketImageContent<PassbookImageType>[] = [];
        imageFields.forEach(imageField => {
            const field = contentForm.get(imageField.type);
            if (field.dirty) {
                const imageValue = field.value;
                if (imageValue?.data) {
                    imagesToSave.push({ type: imageField.type, image_url: imageValue?.data, language });
                } else {
                    imagesToDelete.push({ type: imageField.type, image_url: null, language });
                }
            }
        });
        return { imagesToSave, imagesToDelete };
    }

    getPdfImageFields(
        contentForm: FormGroup, imageFields: ProductTicketImageFields<PdfImageType>[], language: string
    ): { [key: string]: ProductTicketImageContent<PdfImageType>[] } {
        const imagesToSave: ProductTicketImageContent<PdfImageType>[] = [];
        const imagesToDelete: ProductTicketImageContent<PdfImageType>[] = [];
        imageFields.forEach(imageField => {
            const field = contentForm.get(imageField.type);
            if (field.dirty) {
                const imageValue = field.value;
                if (imageValue?.data) {
                    imagesToSave.push({ type: imageField.type, image_url: imageValue?.data, language });
                } else {
                    imagesToDelete.push({ type: imageField.type, image_url: null, language });
                }
            }
        });
        return { imagesToSave, imagesToDelete };
    }

    save(): void {
        this.save$()?.subscribe();
    }

    save$(): Observable<(void | void[])[]> {
        if (this.form.valid) {
            const obs$: Observable<void | void[]>[] = [];
            if (this._pdfContentComponent) {
                obs$.push(...this._pdfContentComponent().save(this.getTextFields.bind(this), this.getPdfImageFields.bind(this)));
            }
            if (this._passbookContentComponent) {
                obs$.push(...this._passbookContentComponent().save(this.getTextFields.bind(this), this.getPassbookImageFields.bind(this)));
            }
            if (this._templatesComponent) {
                obs$.push(...this._templatesComponent().save(this.getTemplateFields.bind(this)));
            }
            return forkJoin(obs$).pipe(tap(() => this.#ephemeralMessage.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            return of([scrollIntoFirstInvalidFieldOrErrorMsg(document)]);
        }
    }

    changeLanguage(newLanguage: string): void {
        this.#selectedLanguage.next(newLanguage);
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
