import {
    EventChannel, getReleaseStatusIndicator, getSaleStatusIndicator
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

//TODO: Are we using this component somewhere?
@Component({
    selector: 'channel-event-card',
    imports: [CommonModule, IonicModule, TranslatePipe],
    templateUrl: './channel-event-card.component.html',
    styleUrls: ['./channel-event-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelEventCardComponent {
    getReleaseStatusIndicator = getReleaseStatusIndicator;
    getSaleStatusIndicator = getSaleStatusIndicator;

    @Output() readonly tap = new EventEmitter<EventChannel>();
    @Input() readonly channel: EventChannel;
    //TODO: Are we using this for anything?
    @Input() readonly eventId: number;

    readonly dateTimeFormats = DateTimeFormats;

    goToEventChannelDetail(): void {
        this.tap.emit(this.channel);
    }
}
