import { ProducerDetails, ProducerInvoicePrefix, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { EntitiesBaseService, Entity } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { NgTemplateOutlet, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject, input, effect } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatList, MatListItem } from '@angular/material/list';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, BehaviorSubject, filter, first, map, startWith, Subject, takeUntil } from 'rxjs';

export type InvoiceInfoLiteralKeys = {
    invoiceDescription: string;
    organizatorData: string;
    producerData: string;
    nif: string;
    socialReason: string;
    address: string;
    invoiceSequenceDescription: string;
    invoiceSequenceLabel: string;
};

@Component({
    selector: 'app-invoice-info',
    templateUrl: './invoice-info.component.html',
    styleUrls: ['./invoice-info.component.scss'],
    imports: [
        ReactiveFormsModule, TranslatePipe, NgTemplateOutlet, MatFormField, MatRadioGroup, MatRadioButton, MatLabel, MatList, MatListItem,
        MatSelect, MatOption, MatTooltip, EllipsifyDirective, MaterialModule, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class InvoiceInfoComponent implements OnInit, OnDestroy {
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #producersService = inject(ProducersService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);

    readonly invoiceDataForm = this.#fb.nonNullable.group({
        useProducerFiscalDataCtrl: [false, [Validators.required]],
        simplifiedInvoicePrefixCtrl: { value: null as number, disabled: true }
    });

    readonly $putCtrl = input<FormControl>(null, { alias: 'putCtrl' });
    readonly $statusCtrl = input<FormControl<string>>(null, { alias: 'statusCtrl' });
    readonly $form = input<FormGroup>(null, { alias: 'form' });
    readonly $status = input<string>(null, { alias: 'status' });
    readonly $useProducerFiscalData = input<boolean>(false, { alias: 'useProducerFiscalData' });
    readonly $producerId = input<number>(null, { alias: 'producerId' });
    readonly $entityId = input<number>(null, { alias: 'entityId' });
    readonly $simplifiedInvoice = input<ProducerInvoicePrefix | null>(null, { alias: 'simplifiedInvoice' });
    readonly $programmingStatus = input<string>(null, { alias: 'programmingStatus' });
    readonly $literalKeys = input.required<InvoiceInfoLiteralKeys>({ alias: 'literalKeys' });
    readonly $showInvoiceSequences = toSignal(
        this.#producersService.producer.get$().pipe(map(producer => producer?.use_simplified_invoice), startWith(false))
    );

    readonly #effectCleanup$ = new Subject<void>();
    readonly invoiceSequences$ = new BehaviorSubject<Pick<ProducerInvoicePrefix, 'id' | 'prefix'>[]>(null);
    readonly invoiceData$ = new BehaviorSubject<ProducerDetails | Entity | null>(null);

    readonly $isMultiProducer = toSignal(this.#entitiesService.getEntityTypes$()
        .pipe(
            first(Boolean),
            map(types => types.some(type => type === 'MULTI_PRODUCER'))
        ));

    constructor() {
        effect(() => {
            this.resetInternalState();
        });
    }

    ngOnInit(): void {
        this.$form().addControl('invoice', this.invoiceDataForm, { emitEvent: false });
        this.#producersService.producer.load(this.$producerId());
        this.#entitiesService.loadEntityTypes(this.$entityId());
        this.#setupSubscriptions();
    }

    ngOnDestroy(): void {
        this.#producersService.producer.clear();
        this.#producersService.invoicePrefixes.clear();
        this.#entitiesService.entityTypes.clear();
        this.#effectCleanup$.complete();
    }

    resetInternalState(): void {
        this.#effectCleanup$.next();

        const useProducerFiscalData = this.$useProducerFiscalData();
        const simplifiedInvoice = this.$simplifiedInvoice();
        const shouldUseProducerData = simplifiedInvoice ? useProducerFiscalData : false;

        this.invoiceDataForm.reset({
            useProducerFiscalDataCtrl: shouldUseProducerData,
            simplifiedInvoicePrefixCtrl: simplifiedInvoice?.id
        }, { emitEvent: false });

        this.invoiceSequences$.next(null);
        this.invoiceData$.next(null);

        this.#loadInvoiceData();
        if (this.$showInvoiceSequences()) {
            this.#updateInvoiceSequences();
        }
        this.#resetSimplifiedInvoiceCtrl(shouldUseProducerData);
    }

    #loadInvoiceData(): void {
        if (this.$useProducerFiscalData()) {
            this.#producersService.producer.get$()
                .pipe(filter(Boolean), first(), takeUntilDestroyed(this.#destroyRef), takeUntil(this.#effectCleanup$))
                .subscribe(producer => this.invoiceData$.next(producer));
        } else {
            this.#entitiesService.getEntity$()
                .pipe(filter(Boolean), first(), takeUntilDestroyed(this.#destroyRef), takeUntil(this.#effectCleanup$))
                .subscribe(entity => this.invoiceData$.next(entity));
        }
    }

    #setupSubscriptions(): void {
        this.invoiceDataForm.controls.useProducerFiscalDataCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(useProducerFiscal => {
                this.#loadInvoiceData();
                if (this.$showInvoiceSequences()) {
                    this.#updateInvoiceSequences();
                }

                this.#resetSimplifiedInvoiceCtrl(useProducerFiscal);
            });

        this.$statusCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                if (this.$showInvoiceSequences()) {
                    this.#updateInvoiceSequences();
                }
            });

        combineLatest([
            this.invoiceDataForm.controls.useProducerFiscalDataCtrl.valueChanges.pipe(
                startWith(this.invoiceDataForm.controls.useProducerFiscalDataCtrl.value)
            ),
            this.#producersService.producer.get$(),
            this.#entitiesService.getEntity$()
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([useProducer, producer, entity]) => {
                const data = useProducer ? producer : entity;
                this.invoiceData$.next(data);
            });

        this.$putCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putValues => {
                if (this.$form().invalid) return;

                const { useProducerFiscalDataCtrl, simplifiedInvoicePrefixCtrl } = this.invoiceDataForm.controls;

                if (useProducerFiscalDataCtrl.dirty || (simplifiedInvoicePrefixCtrl.dirty && simplifiedInvoicePrefixCtrl.enabled)) {
                    putValues.settings = {
                        ...putValues.settings,
                        use_producer_fiscal_data: useProducerFiscalDataCtrl.value
                    };

                    if (simplifiedInvoicePrefixCtrl.dirty && simplifiedInvoicePrefixCtrl.enabled) {
                        putValues.settings.simplified_invoice_prefix = simplifiedInvoicePrefixCtrl.value;
                    }

                    this.$putCtrl().setValue(putValues, { emitEvent: false });
                }
            });
    }

    #updateInvoiceSequences(): void {
        this.#producersService.producer.get$()
            .pipe(filter(Boolean), first(), takeUntilDestroyed(this.#destroyRef), takeUntil(this.#effectCleanup$))
            .subscribe(producer => {
                const useProducerFiscal = this.invoiceDataForm.controls.useProducerFiscalDataCtrl.value;
                const shouldEnable = producer.use_simplified_invoice && this.$statusCtrl().value === this.$programmingStatus() &&
                    useProducerFiscal;

                if (shouldEnable) {
                    this.invoiceDataForm.controls.simplifiedInvoicePrefixCtrl.enable({ emitEvent: false });
                    this.#producersService.invoicePrefixes.loadIfNull(producer.id);
                    this.#producersService.invoicePrefixes.getData$()
                        .pipe(filter(Boolean), first(), takeUntilDestroyed(this.#destroyRef), takeUntil(this.#effectCleanup$))
                        .subscribe(prefixes => this.invoiceSequences$.next(prefixes));
                } else {
                    this.invoiceDataForm.controls.simplifiedInvoicePrefixCtrl.disable({ emitEvent: false });
                    this.#setSimplifiedInvoiceValue();
                }
            });
    }

    #setSimplifiedInvoiceValue(): void {
        const useProducerFiscal = this.invoiceDataForm.controls.useProducerFiscalDataCtrl.value;

        if (this.$simplifiedInvoice() && useProducerFiscal) {
            this.invoiceDataForm.controls.simplifiedInvoicePrefixCtrl.setValue(
                this.$simplifiedInvoice().id,
                { emitEvent: false }
            );
            this.invoiceSequences$.next([this.$simplifiedInvoice()]);
        } else {
            this.invoiceSequences$.next(null);
        }
    }

    #resetSimplifiedInvoiceCtrl(useProducerFiscal: boolean): void {
        const simplifiedInvoiceCtrl = this.invoiceDataForm.controls.simplifiedInvoicePrefixCtrl;
        if (useProducerFiscal) {
            simplifiedInvoiceCtrl.setValidators([Validators.required]);
        } else {
            simplifiedInvoiceCtrl.clearValidators();
        }
        simplifiedInvoiceCtrl.updateValueAndValidity();
    }
}
