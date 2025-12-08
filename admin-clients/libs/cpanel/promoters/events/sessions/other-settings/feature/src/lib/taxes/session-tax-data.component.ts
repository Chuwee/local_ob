import { EventSessionsService, Session, TaxDataType, Taxes } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Subject, withLatestFrom } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { SessionTaxDataInvoiceComponent } from './invoice/session-tax-data-invoice.component';
import { SessionTaxDataProducerComponent } from './producer/session-tax-data-producer.component';
import { SessionTaxDataTypeComponent } from './type/session-tax-data-type.component';

@Component({
    selector: 'app-session-tax-data',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['../session-other-settings.component.scss'],
    templateUrl: './session-tax-data.component.html',
    imports: [
        SessionTaxDataInvoiceComponent, SessionTaxDataProducerComponent, SessionTaxDataTypeComponent,
        ReactiveFormsModule, FlexLayoutModule
    ]
})
export class SessionTaxDataComponent implements OnInit, OnDestroy {
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _producersService = inject(ProducersService);
    private readonly _onDestroy = new Subject<void>();

    @ViewChild(SessionTaxDataProducerComponent) private readonly _taxDataProducerComponent: SessionTaxDataProducerComponent;
    @ViewChild(SessionTaxDataInvoiceComponent) private readonly _taxDataInvoiceComponent: SessionTaxDataInvoiceComponent;

    readonly taxDataFormGroup = inject(UntypedFormBuilder)
        .group({
            taxDataType: [null, [Validators.required]],
            taxProducerId: [null, [Validators.required]],
            taxInvoicePrefixId: [null]
        });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('taxData')) {
            return;
        }
        value.addControl('taxData', this.taxDataFormGroup, { emitEvent: false });
    }

    readonly markForCheck = (): void => {
        this._taxDataProducerComponent?.markForCheck();
        this._taxDataInvoiceComponent?.markForCheck();
    };

    ngOnInit(): void {
        this.loadChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._producersService.clearProducer();
        this._producersService.clearInvoicePrefixes();
        this._producersService.invoiceProvider.clear();
        const form = this.taxDataFormGroup.parent as UntypedFormGroup;
        form.removeControl('taxData', { emitEvent: false });
    }

    getValue(): Taxes {
        return {
            data: {
                type: this.taxDataFormGroup.value.taxDataType,
                producer_id: this.taxDataFormGroup.value.taxDataType === TaxDataType.producer
                    && this.taxDataFormGroup.value.taxProducerId || null,
                invoice_prefix_id: this.taxDataFormGroup.value.taxInvoicePrefixId
            }
        };
    }

    private loadChangeHandler(): void {
        this._sessionsService.session.get$()
            .pipe(
                filter(session => !!session),
                withLatestFrom(this._entitiesSrv.getEntity$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([session, entity]) => {
                this.updateTaxDataFormFields(session);
                this.clearData();
                this.loadData(
                    session.settings?.taxes?.data?.type,
                    session.settings?.taxes?.data?.producer_id,
                    entity.invoice_data?.allow_external_notification
                );
            });
    }

    private updateTaxDataFormFields(session: Session): void {
        this.taxDataFormGroup.reset({
            taxDataType: session.settings?.taxes?.data?.type,
            taxProducerId: session.settings?.taxes?.data?.producer_id,
            taxInvoicePrefixId: session.settings?.taxes?.data?.invoice_prefix_id
        }, { emitEvent: false });
    }

    private clearData(): void {
        this._producersService.clearProducer();
        this._producersService.clearInvoicePrefixes();
        this._producersService.invoiceProvider.clear();
    }

    private loadData(taxDataType: TaxDataType, taxProducerId: number, allowExternalNotification: boolean): void {
        if (taxDataType === TaxDataType.producer) {
            if (allowExternalNotification) {
                this._producersService.invoiceProvider.load(taxProducerId);
            }
            this._producersService.loadProducer(taxProducerId);
            this._producersService.loadInvoicePrefixes(taxProducerId);
        }
    }
}
