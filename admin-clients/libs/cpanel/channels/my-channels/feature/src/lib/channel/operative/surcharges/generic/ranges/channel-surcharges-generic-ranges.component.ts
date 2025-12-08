import { ChannelSurcharge, ChannelSurchargeType } from '@admin-clients/cpanel/channels/data-access';
import { cleanRangesBeforeSave, RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroupDirective } from '@angular/forms';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RangeTableComponent
    ],
    selector: 'app-channel-surcharges-generic-ranges',
    templateUrl: './channel-surcharges-generic-ranges.component.html'
})
export class ChannelSurchargesGenericRangesComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #formGroup = inject(FormGroupDirective);

    readonly genericForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly $userCanWrite = input.required<boolean>({ alias: 'userCanWrite' });
    readonly $currencyCode = input.required<string>({ alias: 'currencyCode' });
    readonly $data = input.required<RangeElement[]>({ alias: 'data' });
    readonly $surchargesRequestCtrl = input.required<FormControl<ChannelSurcharge[]>>({ alias: 'surchargesRequestCtrl' });
    readonly $errorCtrl = input.required<FormControl<string>>({ alias: 'errorCtrl' });

    ngOnInit(): void {
        this.#formGroup.control.addControl(`generic${this.$currencyCode()}`, this.genericForm);

        this.$surchargesRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(surchargesRequest => {
                if (this.#formGroup.control.invalid) {
                    if (this.genericForm.valid || this.$errorCtrl().value) return;
                    this.$errorCtrl().setValue(this.$currencyCode());
                } else {
                    const foundIndex = surchargesRequest
                        .findIndex(surcharge => surcharge.type === ChannelSurchargeType.generic);
                    if (foundIndex > -1) {
                        surchargesRequest[foundIndex].ranges.push(...cleanRangesBeforeSave(this.genericForm.value.ranges)
                            .map(range => ({
                                ...range,
                                currency_code: this.$currencyCode()
                            }))
                        );
                    } else {
                        const surcharge: ChannelSurcharge = {
                            type: ChannelSurchargeType.generic,
                            ranges: cleanRangesBeforeSave(this.genericForm.value.ranges)
                                .map(range => ({
                                    ...range,
                                    currency_code: this.$currencyCode()
                                }))
                        };
                        surchargesRequest.push(surcharge);
                    }
                    this.$surchargesRequestCtrl().setValue(surchargesRequest, { emitEvent: false });
                }
            });
    }
}
