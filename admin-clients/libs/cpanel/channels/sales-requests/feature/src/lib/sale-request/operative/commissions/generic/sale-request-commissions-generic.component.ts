import { ChannelCommission, ChannelCommissionType } from '@admin-clients/cpanel/channels/data-access';
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
    selector: 'app-sale-request-commissions-generic',
    templateUrl: './sale-request-commissions-generic.component.html'
})
export class SaleRequestCommissionsGenericComponent implements OnInit, OnDestroy {
    private readonly _salesRequestsSrv = inject(SalesRequestsService);

    private readonly _onDestroy = new Subject<void>();

    readonly genericForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly data$ = this._salesRequestsSrv.getSaleRequestCommissions$()
        .pipe(
            filter(Boolean),
            map(commissions =>
                commissions
                    .find(commission => commission.type === ChannelCommissionType.generic)
                    ?.ranges ?? []
            )
        );

    @Input() form: FormGroup;
    @Input() commissionsRequestCtrl: FormControl<ChannelCommission[]>;
    @Input() currency: string;

    ngOnInit(): void {
        this.form.addControl(`generic`, this.genericForm);

        this.commissionsRequestCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(comissionsRequest => {
                if (this.form.invalid) return;

                const commission: ChannelCommission = {
                    type: ChannelCommissionType.generic,
                    ranges: cleanRangesBeforeSave(this.genericForm.value.ranges)
                };
                comissionsRequest.push(commission);
                this.commissionsRequestCtrl.setValue(comissionsRequest, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
