import { SaleRequestGatewayBenefitBinGroup } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { DateTimePickerComponent } from '@admin-clients/shared/common/ui/components';
import { dateIsAfter, dateIsBefore, dateTimeValidator, joinCrossValidations } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, effect, inject, input, model, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatRadioGroup, MatRadioModule } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { VmSaleRequestGatewayBenefit } from '../vm-sale-request-gateway-benefit.model';

@Component({
    selector: 'app-sale-request-payment-methods-benefits-validity',
    imports: [CommonModule, TranslatePipe, MatRadioGroup, MatRadioModule, DateTimePickerComponent, ReactiveFormsModule],
    templateUrl: './sale-request-payment-methods-benefits-validity.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SaleRequestPaymentMethodsBenefitsValidityComponent implements OnInit, OnDestroy {
    readonly #formGroup = inject(FormGroupDirective);
    readonly #fb = inject(FormBuilder);
    readonly #destroy = new Subject<void>();
    readonly $postValidityPeriod = model.required<SaleRequestGatewayBenefitBinGroup['validity_period']>({ alias: 'postValidityPeriod' });
    readonly $postCustomValidPeriod = model.required<SaleRequestGatewayBenefitBinGroup['custom_valid_period']>({ alias: 'postCustomValidPeriod' });

    readonly $vmSaleRequestGatewayBenefit = input.required<VmSaleRequestGatewayBenefit>({ alias: 'vmSaleRequestGatewayBenefit' });

    readonly customValidPeriodCtrl = this.#fb.control<boolean>(false);
    readonly startDateTimeCtrl = this.#fb.control<string | null>(undefined);
    readonly endDateTimeCtrl = this.#fb.control<string | null>(undefined);

    constructor() {
        this.startDateTimeCtrl.setValidators([Validators.required, dateTimeValidator(
            dateIsBefore, 'startDateAfterEndDate', this.endDateTimeCtrl)]);
        this.endDateTimeCtrl.setValidators([Validators.required, dateTimeValidator(
            dateIsAfter, 'endDateBeforeStartDate', this.startDateTimeCtrl)]);
        joinCrossValidations([
            this.startDateTimeCtrl,
            this.endDateTimeCtrl
        ], this.#destroy);

        effect(() => {
            const vm = this.$vmSaleRequestGatewayBenefit();

            if (vm.beingModified) {
                this.customValidPeriodCtrl.enable({ emitEvent: false });
                if (vm.beingModified.create) {
                    this.startDateTimeCtrl.disable({ emitEvent: false });
                    this.endDateTimeCtrl.disable({ emitEvent: false });
                    this.$postCustomValidPeriod.set(false);
                } else if (vm.beingModified.edit) {
                    if (this.customValidPeriodCtrl.value) {
                        this.startDateTimeCtrl.enable({ emitEvent: false });
                        this.endDateTimeCtrl.enable({ emitEvent: false });
                    } else {
                        this.startDateTimeCtrl.disable({ emitEvent: false });
                        this.endDateTimeCtrl.disable({ emitEvent: false });
                    }
                }
            } else {
                const [binGroup] = vm.bin_groups;
                const validityPeriod = binGroup?.validity_period;
                const validityCustom = !!binGroup.validity_period?.start_date;

                this.customValidPeriodCtrl.reset(validityCustom, { emitEvent: false });
                this.customValidPeriodCtrl.disable({ emitEvent: false });
                this.startDateTimeCtrl.reset(validityPeriod?.start_date, { emitEvent: false });
                this.startDateTimeCtrl.disable({ emitEvent: false });
                this.endDateTimeCtrl.reset(validityPeriod?.end_date, { emitEvent: false });
                this.endDateTimeCtrl.disable({ emitEvent: false });

                this.$postCustomValidPeriod.set(validityCustom);
                if (validityPeriod) {
                    this.$postValidityPeriod.set({
                        start_date: validityPeriod.start_date,
                        end_date: validityPeriod.end_date
                    });
                } else {
                    this.$postValidityPeriod.set(null);
                }
            }

        });

        this.customValidPeriodCtrl.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(value => {
                this.$postCustomValidPeriod.set(value);
                if (value) {
                    this.startDateTimeCtrl.enable({ emitEvent: false });
                    this.endDateTimeCtrl.enable({ emitEvent: false });
                } else {
                    this.startDateTimeCtrl.disable({ emitEvent: false });
                    this.endDateTimeCtrl.disable({ emitEvent: false });
                    this.$postValidityPeriod.set(null);
                }
            });

        this.startDateTimeCtrl.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(startDate => {
                this.$postValidityPeriod.update(period => ({
                    ...period,
                    start_date: startDate
                }));
            });

        this.endDateTimeCtrl.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(endDate => {
                this.$postValidityPeriod.update(period => ({
                    ...period,
                    end_date: endDate
                }));
            });
    }

    ngOnInit(): void {
        this.#formGroup.control.addControl('customValidPeriod', this.customValidPeriodCtrl, { emitEvent: false });
        this.#formGroup.control.addControl('startDateTime', this.startDateTimeCtrl, { emitEvent: false });
        this.#formGroup.control.addControl('endDateTime', this.endDateTimeCtrl, { emitEvent: false });
    }

    ngOnDestroy(): void {
        this.#destroy.next();
        this.#destroy.complete();
    }
}
