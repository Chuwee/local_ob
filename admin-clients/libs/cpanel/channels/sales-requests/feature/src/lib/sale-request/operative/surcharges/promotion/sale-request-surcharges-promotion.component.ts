import {
    ChannelSurcharge,
    ChannelSurchargeType
} from '@admin-clients/cpanel/channels/data-access';
import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { cleanRangesBeforeSave, RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        RangeTableComponent,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule, FlexLayoutModule
    ],
    selector: 'app-sale-request-surcharges-promotion',
    templateUrl: './sale-request-surcharges-promotion.component.html'
})
export class SaleRequestSurchargesPromotionComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _salesRequestsSrv = inject(SalesRequestsService);

    private readonly _onDestroy = new Subject<void>();

    readonly promotionForm = this._fb.nonNullable.group({
        enabledRanges: false,
        promotionRanges: this._fb.nonNullable.group({
            ranges: [{ value: null as RangeElement[], disabled: true }]
        })
    });

    readonly data$ = this._salesRequestsSrv.getSaleRequestSurcharges$()
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

        this._salesRequestsSrv.getSaleRequestSurcharges$()
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
            .subscribe(channelSurchargesRequest => {
                if (this.form.invalid) return;

                const channelSurcharge: ChannelSurcharge = {
                    type: ChannelSurchargeType.promotion,
                    ranges: cleanRangesBeforeSave(this.promotionForm.value.promotionRanges.ranges),
                    enabled_ranges: this.promotionForm.value.enabledRanges
                };
                channelSurchargesRequest.push(channelSurcharge);
                this.surchargesRequestCtrl.setValue(channelSurchargesRequest, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
