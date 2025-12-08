import { ChannelCommissionType } from '@admin-clients/cpanel/channels/data-access';
import {
    EventChannelsService
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { RangeTableComponent } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        RangeTableComponent,
        TranslatePipe
    ],
    selector: 'app-event-channel-commissions-generic',
    templateUrl: './event-channel-commissions-generic.component.html'
})
export class EventChannelCommissionsGenericComponent {
    readonly data$ = inject(EventChannelsService).getEventChannelCommissions$()
        .pipe(
            filter(Boolean),
            map(commissions =>
                commissions
                    .find(commission => commission.type === ChannelCommissionType.generic)
                    ?.ranges ?? []
            )
        );

    @Input() currency: string;
}
