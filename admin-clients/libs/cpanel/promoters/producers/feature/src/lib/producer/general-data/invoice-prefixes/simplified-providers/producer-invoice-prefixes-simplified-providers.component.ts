import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    ProducerInvoiceProviderStatus, ProducerInvoiceProviderOptions, ProducersService
} from '@admin-clients/cpanel/promoters/producers/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, takeUntil, tap } from 'rxjs/operators';
import { ProducerInvoicePrefixesListComponent } from '../list/producer-invoice-prefixes-list.component';
import { ProducerInvoicePrefixesComponent } from '../producer-invoice-prefixes.component';

@Component({
    selector: 'app-producer-invoice-prefixes-simplified-providers',
    templateUrl: './producer-invoice-prefixes-simplified-providers.component.html',
    styleUrls: ['./producer-invoice-prefixes-simplified-providers.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FlexLayoutModule, MaterialModule,
        TranslatePipe, AsyncPipe, FormControlErrorsComponent,
        ProducerInvoicePrefixesListComponent
    ]
})
export class ProducerInvoicePrefixesSimplifiedProvidersComponent implements OnInit, OnDestroy {
    private readonly _auth = inject(AuthenticationService);
    private readonly _producerSrv = inject(ProducersService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _dialog = inject(MessageDialogService);
    private readonly _ephemeralMessage = inject(EphemeralMessageService);
    private readonly _fb = inject(FormBuilder);

    private readonly _onDestroy = new Subject<void>();
    private readonly _producer$ = this._producerSrv.getProducer$();
    private readonly _isExternalNotificationAllowed$ = combineLatest([
        this._entitiesSrv.getEntity$().pipe(map(entity => entity?.invoice_data?.allow_external_notification ?? false)),
        this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR])
    ]).pipe(
        map(([allowExternalNotification, hasSomeUserRoles]) => allowExternalNotification && hasSomeUserRoles),
        distinctUntilChanged()
    );

    readonly form = this._fb.group({
        invoiceProviderOption: [{ value: null as ProducerInvoiceProviderOptions, disabled: true }, Validators.required]
    });

    readonly isListInProgressBS = new BehaviorSubject<boolean>(false);
    readonly invoiceProviderStatus = ProducerInvoiceProviderStatus;
    readonly invoiceProviderStatus$ = this._producerSrv.invoiceProvider.get$()
        .pipe(
            filter(Boolean),
            tap(val => this.form.patchValue({ invoiceProviderOption: val.provider })),
            map(val => val.status),
            distinctUntilChanged()
        );

    readonly isInvoiceProviderRequestDone$ = this.invoiceProviderStatus$
        .pipe(
            map(status => status === ProducerInvoiceProviderStatus.requested
                || status === ProducerInvoiceProviderStatus.completed),
            distinctUntilChanged()
        );

    readonly isRequestInvoiceProviderDisabled$ = booleanOrMerge([
        this._isExternalNotificationAllowed$.pipe(map(value => !value)),
        this._producer$.pipe(map(producer => !producer.use_simplified_invoice)),
        this.isListInProgressBS.asObservable()
    ]);

    readonly invoicePrefixesFormGroup = inject(ProducerInvoicePrefixesComponent).invoicePrefixesFormGroup;
    readonly invoiceProviderOptions$ = this._producerSrv.invoiceProviderOptions.get$();

    ngOnInit(): void {
        this._producer$
            .pipe(first())
            .subscribe(producer => {
                this._producerSrv.invoiceProvider.load(producer.id);
                this._producerSrv.invoiceProviderOptions.load(producer.id);
            });
        this.loadChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._producerSrv.invoiceProvider.clear();
    }

    changeSimplifiedInvoices(active: boolean): void {
        this._producer$
            .pipe(first())
            .subscribe(producer => {
                if (!active) {
                    this._dialog.showWarn({
                        size: DialogSize.SMALL,
                        title: 'PRODUCER.INVOICES.DISABLE_WARNING_TITLE',
                        message: 'PRODUCER.INVOICES.DISABLE_WARNING_MESSAGE',
                        showCancelButton: false
                    })
                        .subscribe(() => {
                            this.requestSimplifiedInvoice(active, producer);
                        });
                } else {
                    this.requestSimplifiedInvoice(active, producer);
                }
            });
    }

    requestInvoiceProvider(): void {
        this._producer$
            .pipe(first())
            .subscribe(producer => {
                this._dialog.showWarn({
                    size: DialogSize.SMALL,
                    title: 'PRODUCER.INVOICES.PROVIDERS.REQUEST.WARNING_TITLE',
                    message: 'PRODUCER.INVOICES.PROVIDERS.REQUEST.WARNING_MESSAGE'
                })
                    .subscribe(isAccepted => {
                        if (isAccepted && this.form.valid) {
                            const formValue = this.form.value;
                            this._producerSrv.invoiceProvider.save(producer.id, formValue.invoiceProviderOption)
                                .subscribe(() => this._ephemeralMessage.showSaveSuccess());
                        }
                    });
            });
    }

    private loadChangeHandler(): void {
        this._producerSrv.getProducer$()
            .pipe(filter(producer => !!producer), takeUntil(this._onDestroy))
            .subscribe(producer => {
                // Update
                this.invoicePrefixesFormGroup.reset({
                    simplifiedInvoice: producer.use_simplified_invoice
                }, { emitEvent: false });

                if (producer.use_simplified_invoice) {
                    this.form.get('invoiceProviderOption').enable({ emitEvent: false });
                } else {
                    this.form.get('invoiceProviderOption').disable({ emitEvent: false });
                }
            });

        combineLatest([
            this.invoiceProviderStatus$,
            this._producerSrv.getInvoicePrefixesData$()
                .pipe(filter(val => !!val), map(prefixes => prefixes.some(prefix => prefix.default)))
        ]).pipe(takeUntil(this._onDestroy))
            .subscribe(([invoiceProviderStatus, isSomePrefixDefault]) => {
                // Enable
                if (
                    invoiceProviderStatus === ProducerInvoiceProviderStatus.requested
                    || invoiceProviderStatus === ProducerInvoiceProviderStatus.completed
                    || !isSomePrefixDefault
                ) {
                    this.invoicePrefixesFormGroup.get('simplifiedInvoice').disable({ emitEvent: false });
                    this.form.get('invoiceProviderOption').disable({ emitEvent: false });
                } else {
                    this.invoicePrefixesFormGroup.get('simplifiedInvoice').enable({ emitEvent: false });
                }
            });

        this.invoicePrefixesFormGroup.get('simplifiedInvoice').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(active => {
                if (active) {
                    this.form.get('invoiceProviderOption').enable({ emitEvent: false });
                } else {
                    this.form.get('invoiceProviderOption').disable({ emitEvent: false });
                }
            });
    }

    private requestSimplifiedInvoice(active: boolean, producer): void {
        this._producerSrv.saveProducerDetails({ use_simplified_invoice: active, id: producer.id })
            .subscribe(() => {
                this._producerSrv.loadProducer(producer.id);
                this._ephemeralMessage.showSaveSuccess();
            });
    }
}
