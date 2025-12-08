import { ChannelsPipesModule, ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import { EventChannel } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import {
    EventChannelSurchargesPromoterEnableComponent
} from './enable/event-channel-surcharges-promoter-enable.component';
import {
    EventChannelSurchargesPromoterGenericComponent
} from './generic/event-channel-surcharges-promoter-generic.component';
import {
    EventChannelSurchargesPromoterInvitationComponent
} from './invitation/event-channel-surcharges-promoter-invitation.component';
import {
    EventChannelSurchargesPromoterPromotionComponent
} from './promotion/event-channel-surcharges-promoter-promotion.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        EventChannelSurchargesPromoterEnableComponent,
        EventChannelSurchargesPromoterGenericComponent,
        EventChannelSurchargesPromoterPromotionComponent,
        EventChannelSurchargesPromoterInvitationComponent,
        ChannelsPipesModule,
        TranslatePipe
    ],
    selector: 'app-event-channel-surcharges-promoter',
    templateUrl: './event-channel-surcharges-promoter.component.html'
})
export class EventChannelSurchargesPromoterComponent {
    readonly enableGenericRangesCtrl = inject(FormBuilder).nonNullable.control(false);

    @Input() form: FormGroup;
    @Input() surchargesRequestCtrl: FormControl<ChannelSurcharge[]>;
    @Input() currency: string;
    @Input() channel: EventChannel['channel'];
}
