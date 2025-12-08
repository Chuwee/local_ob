import { ChannelsPipesModule, ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketChannel } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { SeasonTicketChannelSurchargesPromoterEnableComponent } from './enable/season-ticket-channel-surcharges-promoter-enable.component';
import {
    SeasonTicketChannelSurchargesPromoterGenericComponent
} from './generic/season-ticket-channel-surcharges-promoter-generic.component';
import {
    SeasonTicketChannelSurchargesPromoterInvitationComponent
} from './invitation/season-ticket-channel-surcharges-promoter-invitation.component';
import {
    SeasonTicketChannelSurchargesPromoterPromotionComponent
} from './promotion/season-ticket-channel-surcharges-promoter-promotion.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        ChannelsPipesModule,
        TranslatePipe,
        SeasonTicketChannelSurchargesPromoterEnableComponent,
        SeasonTicketChannelSurchargesPromoterGenericComponent,
        SeasonTicketChannelSurchargesPromoterPromotionComponent,
        SeasonTicketChannelSurchargesPromoterInvitationComponent
    ],
    selector: 'app-season-ticket-channel-surcharges-promoter',
    templateUrl: './season-ticket-channel-surcharges-promoter.component.html'
})
export class SeasonTicketChannelSurchargesPromoterComponent {
    readonly enableGenericRangesCtrl = inject(FormBuilder).nonNullable.control(false);

    @Input() form: FormGroup;
    @Input() surchargesRequestCtrl: FormControl<ChannelSurcharge[]>;
    @Input() currency: string;
    @Input() channel: SeasonTicketChannel['channel'];
}
