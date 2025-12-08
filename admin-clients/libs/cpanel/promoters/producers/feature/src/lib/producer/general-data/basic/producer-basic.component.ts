import {
    ProducerInvoiceProviderStatus, ProducersFilterFields, ProducersService, ProducerStatus, PutProducerDetails,
    ProducerFieldsRestrictions as restrictions
} from '@admin-clients/cpanel/promoters/producers/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-producer-basic',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './producer-basic.component.html',
    imports: [
        ReactiveFormsModule, FlexLayoutModule, MaterialModule, TranslatePipe, AsyncPipe, NgIf
    ]
})
export class ProducerBasicComponent implements OnInit, OnDestroy {
    private readonly _producerSrv = inject(ProducersService);
    private readonly _fb = inject(UntypedFormBuilder);

    private readonly _onDestroy = new Subject<void>();

    readonly basicFormGroup = this._fb
        .group({
            id: null,
            entity: null,
            name: [null, [Validators.required, Validators.maxLength(restrictions.producerNameLength)]],
            social_reason: [null, [
                Validators.required,
                Validators.maxLength(restrictions.producerSocialReasonMaxLength)
            ]],
            tax_id: [null, [
                Validators.required,
                Validators.maxLength(restrictions.producerTaxIdMaxLength),
                Validators.pattern(restrictions.producerTaxIdPattern)
            ]],
            enabled: [{ value: null, disabled: true }],
            default: [{ value: null, disabled: true }]
        });

    readonly producer$ = this._producerSrv.getProducer$()
        .pipe(filter(producer => !!producer));

    readonly producerFieldsRestrictions = restrictions;

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('basic')) {
            return;
        }
        value.addControl('basic', this.basicFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this.loadChangeHandler();
        this.formChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._producerSrv.clearProducersList();
        const parent = this.basicFormGroup.parent as UntypedFormGroup;
        parent.removeControl('basic');
    }

    getResult(result: PutProducerDetails): PutProducerDetails {
        if (!this.basicFormGroup.dirty) {
            return result;
        }

        const { id, name, enabled, tax_id: taxId, social_reason: socialReason } = this.basicFormGroup.getRawValue();
        const value = {
            id,
            name,
            social_reason: socialReason,
            nif: taxId,
            default: this.basicFormGroup.getRawValue().default,
            status: enabled ? ProducerStatus.active : ProducerStatus.inactive
        };
        return {
            ...result,
            ...value
        };
    }

    private loadChangeHandler(): void {
        this._producerSrv.getProducer$()
            .pipe(filter(producer => !!producer), takeUntil(this._onDestroy))
            .subscribe(producer => {
                this._producerSrv.clearProducersList();
                this._producerSrv.loadProducersList(
                    999, 0, 'name:asc', '', [ProducersFilterFields.name], producer.entity.id);
                // Update
                this.basicFormGroup.reset({
                    id: producer.id,
                    entity: producer.entity,
                    name: producer.name,
                    social_reason: producer.social_reason,
                    tax_id: producer.nif,
                    enabled: producer.status === ProducerStatus.active,
                    default: producer.default
                }, { emitEvent: false });
                // Enable
                if (!producer.default) {
                    this.basicFormGroup.get('enabled').enable({ emitEvent: false });
                }

            });

        combineLatest([
            this._producerSrv.getProducersListMetadata$(),
            this._producerSrv.getProducer$()
        ]).pipe(filter(([metadata]) => !!metadata), takeUntil(this._onDestroy))
            .subscribe(([metadata, producer]) => {
                // Enable
                if (metadata.total !== 1 && !producer.default) {
                    this.basicFormGroup.get('default').enable({ emitEvent: false });
                }
            });

        this._producerSrv.invoiceProvider.get$()
            .pipe(filter(val => !!val), takeUntil(this._onDestroy))
            .subscribe(val => {
                // Enable
                if (val.status === ProducerInvoiceProviderStatus.completed) {
                    this.basicFormGroup.get('tax_id').disable({ emitEvent: false });
                } else {
                    this.basicFormGroup.get('tax_id').enable({ emitEvent: false });
                }
            });
    }

    private formChangeHandler(): void {
        (this.basicFormGroup.get('default').valueChanges as Observable<boolean>)
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isDefault => {
                // Update
                if (isDefault) {
                    this.basicFormGroup.get('enabled').reset(true, { emitEvent: false });
                }
                // Enable
                if (isDefault) {
                    this.basicFormGroup.get('enabled').disable({ emitEvent: false });
                } else {
                    this.basicFormGroup.get('enabled').enable({ emitEvent: false });
                }
            });
    }
}
