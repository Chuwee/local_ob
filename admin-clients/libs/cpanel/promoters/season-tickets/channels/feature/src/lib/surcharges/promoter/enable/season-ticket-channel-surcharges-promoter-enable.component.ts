import { ChannelSurchargeType } from '@admin-clients/cpanel/channels/data-access';
import {
    SeasonTicketChannelsService
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe
    ],
    selector: 'app-season-ticket-channel-surcharges-promoter-enable',
    templateUrl: './season-ticket-channel-surcharges-promoter-enable.component.html'
})
export class SeasonTicketChannelSurchargesPromoterEnableComponent implements OnInit, OnDestroy {
    private readonly _stChannelsSrv = inject(SeasonTicketChannelsService);
    private readonly _onDestroy = new Subject<void>();

    @Input() form: FormGroup;
    @Input() enabledRangesCtrl: FormControl<boolean>;

    ngOnInit(): void {
        this.form.addControl(`enable`, this.enabledRangesCtrl);
        this._stChannelsSrv.getSeasonTicketChannelSurcharges$()
            .pipe(filter(Boolean), takeUntil(this._onDestroy))
            .subscribe(surcharges => {
                const genericSurcharges = surcharges
                    .find(surcharge => surcharge.type === ChannelSurchargeType.generic);
                if (genericSurcharges) {
                    this.enabledRangesCtrl.setValue(genericSurcharges.enabled_ranges, { emitEvent: false });
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
