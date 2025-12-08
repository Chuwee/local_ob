import { ProducerInvoiceProviderStatus, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Subject } from 'rxjs';
import { distinctUntilChanged, filter, map, takeUntil } from 'rxjs/operators';
import { ProducerInvoicePrefixesListComponent } from '../list/producer-invoice-prefixes-list.component';
import { ProducerInvoicePrefixesComponent } from '../producer-invoice-prefixes.component';

@Component({
    selector: 'app-producer-invoice-prefixes-simplified',
    templateUrl: './producer-invoice-prefixes-simplified.component.html',
    styleUrls: ['./producer-invoice-prefixes-simplified.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, ReactiveFormsModule, MatSlideToggleModule,
        MatTooltipModule, MatDividerModule, ProducerInvoicePrefixesListComponent, TranslatePipe
    ]
})
export class ProducerInvoicePrefixesSimplifiedComponent implements OnInit, OnDestroy {
    private readonly _producerSrv = inject(ProducersService);
    private readonly _dialog = inject(MessageDialogService);

    private readonly _onDestroy = new Subject<void>();

    readonly invoicePrefixesFormGroup = inject(ProducerInvoicePrefixesComponent).invoicePrefixesFormGroup;
    readonly invoiceProviderStatus$ = this._producerSrv.invoiceProvider.get$()
        .pipe(
            filter(Boolean),
            map(val => val.status),
            distinctUntilChanged()
        );

    ngOnInit(): void {
        this.loadChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    changeSimplifiedInvoices(active: boolean): void {
        if (!active) {
            this._dialog.showWarn({
                title: 'PRODUCER.INVOICES.DISABLE_WARNING_TITLE',
                message: 'PRODUCER.INVOICES.DISABLE_WARNING_MESSAGE',
                showCancelButton: false
            });
        }
    }

    private loadChangeHandler(): void {
        this._producerSrv.getProducer$()
            .pipe(filter(producer => !!producer), takeUntil(this._onDestroy))
            .subscribe(producer => {
                // Update
                this.invoicePrefixesFormGroup.reset({
                    simplifiedInvoice: producer.use_simplified_invoice
                }, { emitEvent: false });
            });

        // Add the same enable/disable logic as in the toggle of producer-invoice-prefixes-simplified-providers
        // to prevent it from always returning a backend error
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
                } else {
                    this.invoicePrefixesFormGroup.get('simplifiedInvoice').enable({ emitEvent: false });
                }
            });
    }
}
