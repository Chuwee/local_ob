import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, TaxDataType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ProducerInvoiceProviderStatus, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, merge, Observable, Subject } from 'rxjs';
import { filter, first, map, takeUntil, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-session-tax-data-producer',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./session-tax-data-producer.component.scss', '../../session-other-settings.component.scss'],
    templateUrl: './session-tax-data-producer.component.html',
    imports: [
        ReactiveFormsModule, MaterialModule, CommonModule, FlexLayoutModule,
        TranslatePipe, EllipsifyDirective
    ]
})
export class SessionTaxDataProducerComponent implements OnInit, OnDestroy {
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _eventsSrv = inject(EventsService);
    private readonly _producersService = inject(ProducersService);
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _onDestroy = new Subject<void>();

    @Input() taxDataFormGroup: UntypedFormGroup;
    readonly producers$ = this._producersService.getProducersListData$()
        .pipe(filter(producers => !!producers));

    isProducerDataType$: Observable<boolean>;

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this._eventsSrv.event.get$()
            .pipe(first())
            .subscribe(event =>
                this._producersService.loadProducersList(999, 0, 'name:asc', '', null, event.entity.id));

        this.loadChangeHandler();
        this.formChangeHandler();

        this.isProducerDataType$ = merge(
            (this.taxDataFormGroup.get('taxDataType').valueChanges as Observable<TaxDataType>),
            this._sessionsService.session.get$()
                .pipe(
                    filter(session => !!session),
                    map(session => session.settings?.taxes?.data?.type)
                )
        ).pipe(map(taxDataType => taxDataType === TaxDataType.producer));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private loadChangeHandler(): void {
        this._sessionsService.session.get$()
            .pipe(
                filter(session => !!session),
                takeUntil(this._onDestroy)
            )
            .subscribe(session => {
                // Enable
                if (session.settings?.taxes?.data?.type === TaxDataType.event) {
                    this.taxDataFormGroup.get('taxProducerId').disable({ emitEvent: false });
                } else {
                    this.taxDataFormGroup.get('taxProducerId').enable({ emitEvent: false });
                }
            });
    }

    private formChangeHandler(): void {
        (this.taxDataFormGroup.get('taxProducerId').valueChanges as Observable<number>)
            .pipe(
                withLatestFrom(this._entitiesSrv.getEntity$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([taxProducerId, entity]) => {
                const allowExternalNotification = entity.invoice_data?.allow_external_notification;
                this.clearData();
                this.updateForm();
                this.setFormRequired(allowExternalNotification);
                this.enableOrDisableForm();
                this.loadData(taxProducerId, allowExternalNotification);
            });
    }

    private clearData(): void {
        this._producersService.clearProducer();
        this._producersService.clearInvoicePrefixes();
        this._producersService.invoiceProvider.clear();
    }

    private updateForm(): void {
        this.taxDataFormGroup.get('taxInvoicePrefixId').reset(null, { emitEvent: false });
        this._producersService.getInvoicePrefixesData$()
            .pipe(first(value => !!value))
            .subscribe(invoicePrefixes => {
                if (invoicePrefixes.length === 1) {
                    this.taxDataFormGroup.get('taxInvoicePrefixId').reset(
                        invoicePrefixes[0].id,
                        { emitEvent: false }
                    );
                } else if (invoicePrefixes.length) {
                    const indexOfDefault = invoicePrefixes.findIndex(invoicePrefix => invoicePrefix.default);
                    if (indexOfDefault !== -1) {
                        this.taxDataFormGroup.get('taxInvoicePrefixId').reset(
                            invoicePrefixes[indexOfDefault].id, { emitEvent: false });
                    }
                }
            });
    }

    private setFormRequired(allowExternalNotification: boolean): void {
        if (!allowExternalNotification) {
            this.taxDataFormGroup.get('taxInvoicePrefixId').setValidators(null);
            this.taxDataFormGroup.get('taxInvoicePrefixId').updateValueAndValidity({ emitEvent: false });
            return;
        }

        this._producersService.invoiceProvider.get$()
            .pipe(first(value => !!value))
            .subscribe(value => {
                if (value.status === ProducerInvoiceProviderStatus.completed) {
                    this.taxDataFormGroup.get('taxInvoicePrefixId').setValidators(Validators.required);
                } else {
                    this.taxDataFormGroup.get('taxInvoicePrefixId').setValidators(null);
                }
                this.taxDataFormGroup.get('taxInvoicePrefixId').updateValueAndValidity({ emitEvent: false });
            });
    }

    private enableOrDisableForm(): void {
        combineLatest([
            this._producersService.getProducer$(),
            this._producersService.getInvoicePrefixesData$()
        ]).pipe(first(values => values.every(value => !!value)))
            .subscribe(([producer, invoicePrefixes]) => {
                if (invoicePrefixes.length === 0 || !producer?.use_simplified_invoice) {
                    this.taxDataFormGroup.get('taxInvoicePrefixId').disable({ emitEvent: false });
                } else {
                    this.taxDataFormGroup.get('taxInvoicePrefixId').enable({ emitEvent: false });
                }
            });
    }

    private loadData(taxProducerId: number, allowExternalNotification: boolean): void {
        if (allowExternalNotification) {
            this._producersService.invoiceProvider.load(taxProducerId);
        }
        this._producersService.loadProducer(taxProducerId);
        this._producersService.loadInvoicePrefixes(taxProducerId);
    }
}
