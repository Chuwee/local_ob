import { EventSessionsService, Session, TaxDataType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    ProducerInvoicePrefix, ProducerInvoiceProviderStatus, ProducersService
} from '@admin-clients/cpanel/promoters/producers/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Subject } from 'rxjs';
import { filter, first, map, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-session-tax-data-invoice',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./session-tax-data-invoice.component.scss', '../../session-other-settings.component.scss'],
    templateUrl: './session-tax-data-invoice.component.html',
    imports: [
        ReactiveFormsModule, FlexLayoutModule, CommonModule, MaterialModule,
        TranslatePipe, EllipsifyDirective
    ]
})
export class SessionTaxDataInvoiceComponent implements OnInit, OnDestroy {
    private readonly _translate = inject(TranslateService);
    private readonly _producersService = inject(ProducersService);
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _onDestroy = new Subject<void>();
    @Input() taxDataFormGroup: UntypedFormGroup;

    readonly computedInvoicePrefixes$ = combineLatest([
        this._producersService.invoiceProvider.get$(),
        this._producersService.getInvoicePrefixesData$(),
        this._producersService.getProducer$(),
        this._sessionsService.session.get$().pipe(filter(session => !!session))
    ]).pipe(map(([invoiceProvider, invoicePrefixes, producer, session]) => {
        let result: ProducerInvoicePrefix[];
        if (!invoicePrefixes?.length) {
            result = [];
        } else if (
            producer
            && !producer.use_simplified_invoice
            && producer.id !== session.settings?.taxes?.data?.invoice_prefix_id
        ) {
            result = [];
        } else if (invoiceProvider?.status !== ProducerInvoiceProviderStatus.completed) {
            const nullProducerInvoicePrefix: ProducerInvoicePrefix = {
                producer: {
                    id: null,
                    name: null
                },
                default: false,
                prefix: this._translate.instant('FORMS.SELECT.NONE'),
                suffix: null,
                id: null
            };
            result = [nullProducerInvoicePrefix].concat(invoicePrefixes);
        } else {
            result = invoicePrefixes;
        }
        return result;
    }));

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this.loadChangeHandler();
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
                this.setFormRequired(session.settings?.taxes?.data?.producer_id);
                this.enableOrDisableForm(session);
            });
    }

    private setFormRequired(taxProducerId: number): void {
        if (!Number.isInteger(taxProducerId)) {
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

    private enableOrDisableForm(session: Session): void {
        if (
            session.settings?.taxes?.data?.type === TaxDataType.event
            || !Number.isInteger(session.settings?.taxes?.data?.producer_id)
        ) {
            this.taxDataFormGroup.get('taxInvoicePrefixId').disable({ emitEvent: false });
            return;
        }

        combineLatest([
            this._producersService.getProducer$(),
            this._producersService.getInvoicePrefixesData$()
        ]).pipe(first(values => values.every(value => !!value)))
            .subscribe(([producer, invoicePrefixes]) => {
                if (invoicePrefixes.length === 0 || !producer.use_simplified_invoice) {
                    this.taxDataFormGroup.get('taxInvoicePrefixId').disable({ emitEvent: false });
                } else {
                    this.taxDataFormGroup.get('taxInvoicePrefixId').enable({ emitEvent: false });
                }
            });
    }
}
