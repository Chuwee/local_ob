import {
    ChannelSurcharge,
    ChannelSurchargeType
} from '@admin-clients/cpanel/channels/data-access';
import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { cleanRangesBeforeSave, RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        RangeTableComponent,
        TranslatePipe
    ],
    selector: 'app-sale-request-surcharges-generic',
    templateUrl: './sale-request-surcharges-generic.component.html'
})
export class SaleRequestSurchargesGenericComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    readonly genericForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly data$ = inject(SalesRequestsService).getSaleRequestSurcharges$()
        .pipe(
            filter(Boolean),
            map(surcharges =>
                surcharges
                    .find(surcharges => surcharges.type === ChannelSurchargeType.generic)
                    ?.ranges ?? []
            )
        );

    @Input() form: FormGroup;
    @Input() surchargesRequestCtrl: FormControl<ChannelSurcharge[]>;
    @Input() currency: string;

    ngOnInit(): void {
        this.form.addControl(`generic`, this.genericForm);

        this.surchargesRequestCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(channelSurchargesRequest => {
                if (this.form.invalid) return;

                const channelSurcharge: ChannelSurcharge = {
                    type: ChannelSurchargeType.generic,
                    ranges: cleanRangesBeforeSave(this.genericForm.value.ranges)
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
