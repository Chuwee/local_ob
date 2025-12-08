import {
    ChannelSurcharge, ChannelSurchargeType
} from '@admin-clients/cpanel/channels/data-access';
import { cleanRangesBeforeSave, RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { RangeElement } from '@admin-clients/shared-utility-models';
import {
    ChangeDetectionStrategy, Component, DestroyRef, inject, input, model, OnInit
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroupDirective } from '@angular/forms';
import { filter } from 'rxjs/operators';
import { ChannelOperativeService } from '../../../channel-operative.service';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RangeTableComponent
    ],
    selector: 'app-channel-surcharges-promotion-ranges',
    templateUrl: './channel-surcharges-promotion-ranges.component.html'
})
export class ChannelSurchargesPromotionRangesComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #formGroup = inject(FormGroupDirective);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);

    readonly promotionForm = inject(FormBuilder).nonNullable.group({
        ranges: [{ value: null as RangeElement[], disabled: true }]
    });

    readonly $hidePromotions = model.required<boolean>({ alias: 'hidePromotions' });

    readonly $enabledRangesCtrl = input.required<FormControl<boolean>>({ alias: 'enabledRangesCtrl' });
    readonly $userCanWrite = input.required<boolean>({ alias: 'userCanWrite' });
    readonly $currencyCode = input.required<string>({ alias: 'currencyCode' });
    readonly $data = input.required<RangeElement[]>({ alias: 'data' });
    readonly $surchargesRequestCtrl = input.required<FormControl<ChannelSurcharge[]>>({ alias: 'surchargesRequestCtrl' });
    readonly $errorCtrl = input.required<FormControl<string>>({ alias: 'errorCtrl' });

    ngOnInit(): void {
        this.#formGroup.control.addControl(`promotion${this.$currencyCode()}`, this.promotionForm);

        this.#channelOperativeSrv.getChannelSurcharges$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(channelSurcharges => {
                const promotionSurcharges = channelSurcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.promotion);
                if (promotionSurcharges) {
                    this.$enabledRangesCtrl().setValue(promotionSurcharges.enabled_ranges, { emitEvent: false });
                }
                this.$hidePromotions.set(!promotionSurcharges?.enabled_ranges);
            });

        this.$surchargesRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(surchargesRequest => {
                if (this.#formGroup.control.invalid) {
                    if (this.promotionForm.valid || this.$errorCtrl().value) return;
                    this.$errorCtrl().setValue(this.$currencyCode());
                } else {
                    const foundIndex = surchargesRequest
                        .findIndex(surcharge => surcharge.type === ChannelSurchargeType.promotion);

                    if (foundIndex > -1) {
                        surchargesRequest[foundIndex].ranges.push(...cleanRangesBeforeSave(this.promotionForm.value.ranges)
                            .map(range => ({
                                ...range,
                                currency_code: this.$currencyCode()
                            }))
                        );
                    } else {
                        const channelSurcharge: ChannelSurcharge = {
                            type: ChannelSurchargeType.promotion,
                            ranges: cleanRangesBeforeSave(this.promotionForm.value.ranges)
                                .map(range => ({
                                    ...range,
                                    currency_code: this.$currencyCode()
                                })),
                            enabled_ranges: this.$enabledRangesCtrl().value
                        };
                        surchargesRequest.push(channelSurcharge);
                    }
                    this.$surchargesRequestCtrl().setValue(surchargesRequest, { emitEvent: false });
                }
            });
    }
}
