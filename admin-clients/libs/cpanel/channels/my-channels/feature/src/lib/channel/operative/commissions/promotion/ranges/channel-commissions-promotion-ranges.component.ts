import { ChannelCommission, ChannelCommissionType } from '@admin-clients/cpanel/channels/data-access';
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
    selector: 'app-channel-commissions-promotion-ranges',
    templateUrl: './channel-commissions-promotion-ranges.component.html'
})
export class ChannelCommissionsPromotionRangesComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #formGroup = inject(FormGroupDirective);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);

    readonly promotionForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly $hidePromotions = model.required<boolean>({ alias: 'hidePromotions' });

    readonly $enabledRangesCtrl = input.required<FormControl<boolean>>({ alias: 'enabledRangesCtrl' });
    readonly $userCanWrite = input.required<boolean>({ alias: 'userCanWrite' });
    readonly $currencyCode = input.required<string>({ alias: 'currencyCode' });
    readonly $data = input.required<RangeElement[]>({ alias: 'data' });
    readonly $commissionsRequestCtrl = input.required<FormControl<ChannelCommission[]>>({ alias: 'commissionsRequestCtrl' });
    readonly $errorCtrl = input.required<FormControl<string>>({ alias: 'errorCtrl' });

    ngOnInit(): void {
        this.#formGroup.control.addControl(`promotion${this.$currencyCode()}`, this.promotionForm);

        this.#channelOperativeSrv.getChannelCommissions$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(commissions => {
                const promotionComissions = commissions
                    .find(comission => comission.type === ChannelCommissionType.promotion);
                if (promotionComissions) {
                    this.$enabledRangesCtrl().setValue(promotionComissions.enabled_ranges, { emitEvent: false });
                }
                this.$hidePromotions.set(!promotionComissions?.enabled_ranges);
            });

        this.$commissionsRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(comissionsRequest => {
                if (this.#formGroup.control.invalid) {
                    if (this.promotionForm.valid || this.$errorCtrl().value) return;
                    this.$errorCtrl().setValue(this.$currencyCode());
                } else {
                    const foundIndex = comissionsRequest
                        .findIndex(comission => comission.type === ChannelCommissionType.promotion);

                    if (foundIndex > -1) {
                        comissionsRequest[foundIndex].ranges.push(...cleanRangesBeforeSave(this.promotionForm.value.ranges)
                            .map(range => ({
                                ...range,
                                currency_code: this.$currencyCode()
                            }))
                        );
                    } else {
                        const commission: ChannelCommission = {
                            type: ChannelCommissionType.promotion,
                            ranges: cleanRangesBeforeSave(this.promotionForm.value.ranges)
                                .map(range => ({
                                    ...range,
                                    currency_code: this.$currencyCode()
                                })),
                            enabled_ranges: this.$enabledRangesCtrl().value
                        };
                        comissionsRequest.push(commission);
                    }
                    this.$commissionsRequestCtrl().setValue(comissionsRequest, { emitEvent: false });
                }
            });
    }
}
