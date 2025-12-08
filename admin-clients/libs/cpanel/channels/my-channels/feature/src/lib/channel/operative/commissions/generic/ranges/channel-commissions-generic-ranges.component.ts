import { ChannelCommission, ChannelCommissionType } from '@admin-clients/cpanel/channels/data-access';
import {
    cleanRangesBeforeSave, RangeTableComponent
} from '@admin-clients/shared/common/ui/components';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroupDirective } from '@angular/forms';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RangeTableComponent
    ],
    selector: 'app-channel-commissions-generic-ranges',
    templateUrl: './channel-commissions-generic-ranges.component.html'
})
export class ChannelCommissionsGenericRangesComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #formGroup = inject(FormGroupDirective);

    readonly genericForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly $userCanWrite = input.required<boolean>({ alias: 'userCanWrite' });
    readonly $currencyCode = input.required<string>({ alias: 'currencyCode' });
    readonly $data = input.required<RangeElement[]>({ alias: 'data' });
    readonly $commissionsRequestCtrl = input.required<FormControl<ChannelCommission[]>>({ alias: 'commissionsRequestCtrl' });
    readonly $errorCtrl = input.required<FormControl<string>>({ alias: 'errorCtrl' });

    ngOnInit(): void {
        this.#formGroup.control.addControl(`generic${this.$currencyCode()}`, this.genericForm);

        this.$commissionsRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(comissionsRequest => {
                if (this.#formGroup.control.invalid) {
                    if (this.genericForm.valid || this.$errorCtrl().value) return;
                    this.$errorCtrl().setValue(this.$currencyCode());
                } else {
                    const foundIndex = comissionsRequest
                        .findIndex(comission => comission.type === ChannelCommissionType.generic);
                    if (foundIndex > -1) {
                        comissionsRequest[foundIndex].ranges.push(...cleanRangesBeforeSave(this.genericForm.value.ranges)
                            .map(range => ({
                                ...range,
                                currency_code: this.$currencyCode()
                            }))
                        );
                    } else {
                        const commission: ChannelCommission = {
                            type: ChannelCommissionType.generic,
                            ranges: cleanRangesBeforeSave(this.genericForm.value.ranges)
                                .map(range => ({
                                    ...range,
                                    currency_code: this.$currencyCode()
                                }))
                        };
                        comissionsRequest.push(commission);
                    }
                    this.$commissionsRequestCtrl().setValue(comissionsRequest, { emitEvent: false });
                }
            });
    }
}
