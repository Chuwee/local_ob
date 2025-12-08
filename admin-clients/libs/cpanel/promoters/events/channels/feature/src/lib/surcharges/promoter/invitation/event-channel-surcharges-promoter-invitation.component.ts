import { ChannelSurcharge, ChannelSurchargeType } from '@admin-clients/cpanel/channels/data-access';
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
    selector: 'app-event-channel-surcharges-promoter-invitation',
    templateUrl: './event-channel-surcharges-promoter-invitation.component.html'
})
export class EventChannelSurchargesPromoterInvitationComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    readonly invitationForm = inject(FormBuilder).nonNullable.group({
        ranges: [null as RangeElement[]]
    });

    readonly data$ = inject(EventChannelsService).getEventChannelSurcharges$()
        .pipe(
            filter(Boolean),
            map(surcharges =>
                surcharges
                    .find(surcharges => surcharges.type === ChannelSurchargeType.invitation)
                    ?.ranges ?? []
            )
        );

    @Input() form: FormGroup;
    @Input() surchargesRequestCtrl: FormControl<ChannelSurcharge[]>;
    @Input() currency: string;

    ngOnInit(): void {
        this.form.addControl(`invitation`, this.invitationForm);

        this.surchargesRequestCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(surchargesRequest => {
                if (this.form.invalid) return;

                const surcharge: ChannelSurcharge = {
                    type: ChannelSurchargeType.invitation,
                    ranges: cleanRangesBeforeSave(this.invitationForm.value.ranges)
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
