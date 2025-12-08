import { ChannelCommission, ChannelCommissionType } from '@admin-clients/cpanel/channels/data-access';
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
        FlexLayoutModule,
        MaterialModule,
        RangeTableComponent,
        TranslatePipe,
        ReactiveFormsModule
    ],
    selector: 'app-sale-request-commissions-promotion',
    templateUrl: './sale-request-commissions-promotion.component.html'
})
export class SaleRequestCommissionsPromotionComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _salesRequestsSrv = inject(SalesRequestsService);

    private readonly _onDestroy = new Subject<void>();

    readonly promotionForm = this._fb.nonNullable.group({
        enabledRanges: false,
        promotionRanges: this._fb.nonNullable.group({
            ranges: [{ value: null as RangeElement[], disabled: true }]
        })
    });

    readonly data$ = this._salesRequestsSrv.getSaleRequestCommissions$()
        .pipe(
            filter(Boolean),
            map(commissions =>
                commissions
                    .find(commission => commission.type === ChannelCommissionType.promotion)
                    ?.ranges ?? []
            )
        );

    @Input() form: FormGroup;
    @Input() commissionsRequestCtrl: FormControl<ChannelCommission[]>;
    @Input() currency: string;

    ngOnInit(): void {
        this.form.addControl(`promotion`, this.promotionForm);

        this._salesRequestsSrv.getSaleRequestCommissions$()
            .pipe(filter(Boolean), takeUntil(this._onDestroy))
            .subscribe(commissions => {
                const promotionComissions = commissions
                    .find(comission => comission.type === ChannelCommissionType.promotion);
                if (promotionComissions) {
                    this.promotionForm.controls.enabledRanges.setValue(promotionComissions.enabled_ranges, { emitEvent: false });
                }
            });

        this.commissionsRequestCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(channelComissionsRequest => {
                if (this.form.invalid) return;

                const channelCommission: ChannelCommission = {
                    type: ChannelCommissionType.promotion,
                    ranges: cleanRangesBeforeSave(this.promotionForm.value.promotionRanges.ranges),
                    enabled_ranges: this.promotionForm.value.enabledRanges
                };
                channelComissionsRequest.push(channelCommission);
                this.commissionsRequestCtrl.setValue(channelComissionsRequest, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
