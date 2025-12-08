import { TaxDataType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-session-tax-data-type',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['../../session-other-settings.component.scss'],
    templateUrl: './session-tax-data-type.component.html',
    imports: [
        ReactiveFormsModule, MaterialModule, TranslatePipe, FlexLayoutModule
    ]
})
export class SessionTaxDataTypeComponent implements OnInit, OnDestroy {
    private readonly _producersService = inject(ProducersService);
    private readonly _onDestroy = new Subject<void>();
    readonly taxDataType = TaxDataType;
    @Input() taxDataFormGroup: UntypedFormGroup;

    ngOnInit(): void {
        this.formChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private formChangeHandler(): void {
        (this.taxDataFormGroup.get('taxDataType').valueChanges as Observable<TaxDataType>)
            .pipe(takeUntil(this._onDestroy))
            .subscribe(type => {
                this.clearData(type);
                this.updateForm(type);
                this.enableOrDisableForm(type);
            });
    }

    private clearData(type: TaxDataType): void {
        if (type === TaxDataType.event) {
            this._producersService.clearProducer();
            this._producersService.clearInvoicePrefixes();
            this._producersService.invoiceProvider.clear();
        }
    }

    private updateForm(type: TaxDataType): void {
        if (type === TaxDataType.event) {
            this.taxDataFormGroup.get('taxProducerId').reset(null, { emitEvent: false });
        }
    }

    private enableOrDisableForm(type: TaxDataType): void {
        if (type === TaxDataType.event) {
            this.taxDataFormGroup.get('taxProducerId').disable({ emitEvent: false });
        } else {
            this.taxDataFormGroup.get('taxProducerId').enable({ emitEvent: false });
        }
    }
}
