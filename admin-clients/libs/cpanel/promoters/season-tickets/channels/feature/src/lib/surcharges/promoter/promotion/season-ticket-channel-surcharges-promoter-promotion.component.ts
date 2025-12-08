import { ChannelSurcharge, ChannelSurchargeType } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { cleanRangesBeforeSave, RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        RangeTableComponent,
        TranslatePipe
    ],
    selector: 'app-season-ticket-channel-surcharges-promoter-promotion',
    templateUrl: './season-ticket-channel-surcharges-promoter-promotion.component.html'
})
export class SeasonTicketChannelSurchargesPromoterPromotionComponent implements OnInit {
    private readonly _fb = inject(FormBuilder);
    private readonly _stChannelsSrv = inject(SeasonTicketChannelsService);

    private readonly _onDestroy = new Subject<void>();

    readonly promotionForm = this._fb.nonNullable.group({
        enabledRanges: false,
        promotionRanges: this._fb.nonNullable.group({
            ranges: [{ value: null as RangeElement[], disabled: true }]
        })
    });

    readonly data$ = this._stChannelsSrv.getSeasonTicketChannelSurcharges$()
        .pipe(
            filter(Boolean),
            map(surcharges =>
                surcharges
                    .find(surcharges => surcharges.type === ChannelSurchargeType.promotion)
                    ?.ranges ?? []
            )
        );

    @Input() form: FormGroup;
    @Input() surchargesRequestCtrl: FormControl<ChannelSurcharge[]>;
    @Input() currency: string;

    ngOnInit(): void {
        this.form.addControl(`promotion`, this.promotionForm);

        this._stChannelsSrv.getSeasonTicketChannelSurcharges$()
            .pipe(filter(Boolean), takeUntil(this._onDestroy))
            .subscribe(surcharges => {
                const promotionSurcharges = surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.promotion);
                if (promotionSurcharges) {
                    this.promotionForm.controls.enabledRanges.setValue(promotionSurcharges.enabled_ranges, { emitEvent: false });
                }
            });

        this.surchargesRequestCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(surchargesRequest => {
                if (this.form.invalid) return;

                const surcharge: ChannelSurcharge = {
                    type: ChannelSurchargeType.promotion,
                    ranges: cleanRangesBeforeSave(this.promotionForm.value.promotionRanges.ranges),
                    enabled_ranges: this.promotionForm.value.enabledRanges
                };

                surchargesRequest.push(surcharge);
                this.surchargesRequestCtrl.setValue(surchargesRequest, { emitEvent: false });
            });
    }
}
