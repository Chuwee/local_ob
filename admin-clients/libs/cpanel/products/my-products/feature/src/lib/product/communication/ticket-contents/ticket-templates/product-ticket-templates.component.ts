import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    EventTicketTemplate, EventTicketTemplateFields, EventTicketTemplateType, TicketContentFormat
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { ProductCommunicationService } from '@admin-clients/cpanel-products-communication-data-access';
import {
    GetTicketTemplatesRequest, TicketTemplate, TicketTemplateDesignTypes, TicketTemplateFormat, TicketTemplatesService
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { IconManagerService, starIcon, ticketPdfIcon } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-product-ticket-templates',
    templateUrl: './product-ticket-templates.component.html',
    styleUrls: ['./product-ticket-templates.component.scss'],
    imports: [
        ReactiveFormsModule, MatFormFieldModule, MatSelectModule, MatTooltipModule, MatIconModule, TranslatePipe,
        EllipsifyDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductTicketTemplatesComponent implements OnInit, AfterViewInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #productService = inject(ProductsService);
    readonly #productCommunicationService = inject(ProductCommunicationService);
    readonly #ticketTemplatesService = inject(TicketTemplatesService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #iconManagerSrv = inject(IconManagerService);

    #templateFields: EventTicketTemplateFields[];
    #productId: number;
    #templates$: Observable<void>;
    #entityId: number;

    templatesForm: FormGroup;
    templatesPdf: TicketTemplate[] = [];

    @Input() form: FormGroup;
    @Input() language$: Observable<string>;

    constructor() {
        this.#iconManagerSrv.addIconDefinition(ticketPdfIcon, starIcon);
    }

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();
        this.loadContents();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    cancel(): void {
        this.templatesForm.markAsPristine();
        this.#productCommunicationService.productTemplates.load(this.#productId);
        this.#ticketTemplatesService.loadTicketTemplates(
            { entity_id: this.#entityId, limit: 999, sort: 'name:asc', design_type: TicketTemplateDesignTypes.product } as GetTicketTemplatesRequest);
    }

    save(
        getTemplateFields: (contentForm: FormGroup, templateFields: EventTicketTemplateFields[]) => EventTicketTemplate[]
    ): Observable<void | void[]>[] {
        const templatesToSave = getTemplateFields(this.templatesForm, this.#templateFields);
        if (templatesToSave.length > 0) {
            return [
                this.#productCommunicationService.productTemplates.update(this.#productId, templatesToSave[0].id, EventTicketTemplateType.single, TicketContentFormat.pdf)
                    .pipe(
                        tap(() => this.cancel()) // reloads data from backend
                    )
            ];
        }

        return [];
    }

    private prepareFields(): void {
        this.#templateFields = [
            {
                format: TicketContentFormat.pdf,
                formField: 'singlePdfTemplate',
                type: EventTicketTemplateType.single
            }
        ];
    }

    private initForms(): void {
        const templateFields = {};

        this.#templateFields.forEach(templateField => {
            templateFields[templateField.formField] = [{ value: null, disabled: true }];
        });

        this.templatesForm = this.#fb.group(templateFields);
        this.form.addControl('templatesForm', this.templatesForm);
    }

    private loadContents(): void {
        this.#productService.product.get$()
            .pipe(
                take(1),
                tap(product => {
                    this.#productId = product.product_id;
                    this.#entityId = product.entity.id;
                    this.#productCommunicationService.productTemplates.load(product.product_id);
                    this.#ticketTemplatesService.loadTicketTemplates({
                        entity_id: this.#entityId,
                        limit: 999,
                        sort: 'name:asc',
                        design_type: TicketTemplateDesignTypes.product
                    } as GetTicketTemplatesRequest);
                })
            ).subscribe();

        this.#templates$ = combineLatest([
            this.#productCommunicationService.productTemplates.get$(),
            this.#ticketTemplatesService.getTicketTemplates$(),
            this.language$
        ]).pipe(
            takeUntilDestroyed(this.#onDestroy),
            filter(([templates, templatesList]) => !!templates && !!templatesList),
            tap(([templates, templatesList, _]) => {
                this.templatesPdf = templatesList.filter(template => template.design.format === TicketTemplateFormat.pdf);
                if (this.templatesPdf.length) {
                    this.templatesForm.get('singlePdfTemplate').enable();
                }
                this.#templateFields.forEach(templateField => {
                    const field = this.templatesForm.get(templateField.formField);
                    field.reset();
                    for (const template of templates) {
                        if (template.format === templateField.format && template.type === templateField.type) {
                            field.setValue(+template.id);
                        }
                    }
                });
            }),
            map(() => null)
        );
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this.#templates$,
            this.form.valueChanges
        ]).pipe(
            filter(([language]) => !!language),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe();
    }
}
