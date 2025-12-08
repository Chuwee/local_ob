import {
    ChannelSurcharge, ChannelSurchargeType
} from '@admin-clients/cpanel/channels/data-access';
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
    selector: 'app-channel-surcharges-invitation-ranges',
    templateUrl: './channel-surcharges-invitation-ranges.component.html'
})
export class ChannelSurchargesInvitationRangesComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #formGroup = inject(FormGroupDirective);

    readonly invitationForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly $userCanWrite = input.required<boolean>({ alias: 'userCanWrite' });
    readonly $currencyCode = input.required<string>({ alias: 'currencyCode' });
    readonly $data = input.required<RangeElement[]>({ alias: 'data' });
    readonly $surchargesRequestCtrl = input.required<FormControl<ChannelSurcharge[]>>({ alias: 'surchargesRequestCtrl' });
    readonly $errorCtrl = input.required<FormControl<string>>({ alias: 'errorCtrl' });

    ngOnInit(): void {
        this.#formGroup.control.addControl(`invitation${this.$currencyCode()}`, this.invitationForm);

        this.$surchargesRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(surchargesRequest => {
                if (this.#formGroup.control.invalid) {
                    if (this.invitationForm.valid || this.$errorCtrl().value) return;
                    this.$errorCtrl().setValue(this.$currencyCode());
                } else {
                    const foundIndex = surchargesRequest
                        .findIndex(surcharge => surcharge.type === ChannelSurchargeType.invitation);
                    if (foundIndex > -1) {
                        surchargesRequest[foundIndex].ranges.push(...cleanRangesBeforeSave(this.invitationForm.value.ranges)
                            .map(range => ({
                                ...range,
                                currency_code: this.$currencyCode()
                            }))
                        );
                    } else {
                        const surcharge: ChannelSurcharge = {
                            type: ChannelSurchargeType.invitation,
                            ranges: cleanRangesBeforeSave(this.invitationForm.value.ranges)
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
