import {
    ChannelSurcharge,
    ChannelSurchargeType
} from '@admin-clients/cpanel/channels/data-access';
import { EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
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
    selector: 'app-event-channel-surcharges-promoter-generic',
    templateUrl: './event-channel-surcharges-promoter-generic.component.html'
})
export class EventChannelSurchargesPromoterGenericComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    readonly genericForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly data$ = inject(EventChannelsService).getEventChannelSurcharges$()
        .pipe(
            filter(Boolean),
            map(surcharges =>
                surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.generic)
                    ?.ranges ?? []
            )
        );

    @Input() form: FormGroup;
    @Input() enabledRangesCtrl: FormControl<boolean>;
    @Input() surchargesRequestCtrl: FormControl<ChannelSurcharge[]>;
    @Input() currency: string;

    ngOnInit(): void {
        this.form.addControl(`generic`, this.genericForm);

        this.surchargesRequestCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(surchargesRequest => {
                if (this.form.invalid) return;

                const surcharge: ChannelSurcharge = {
                    type: ChannelSurchargeType.generic,
                    ranges: cleanRangesBeforeSave(this.genericForm.value.ranges),
                    enabled_ranges: this.enabledRangesCtrl.value
                };

                surchargesRequest.push(surcharge);
                this.surchargesRequestCtrl.setValue(surchargesRequest, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
