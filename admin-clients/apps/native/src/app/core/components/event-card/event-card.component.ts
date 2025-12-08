/* eslint-disable @typescript-eslint/naming-convention */
import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'event-card',
    imports: [CommonModule, IonicModule, TranslatePipe, DateTimePipe],
    templateUrl: './event-card.component.html',
    styleUrls: ['./event-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventCardComponent {
    @Input() readonly event: Event;
    @Output() readonly tap = new EventEmitter<Event>();
    readonly dateTimeFormats = DateTimeFormats;

    onTap(): void {
        this.tap.emit(this.event);
    }

    get getCssClass(): string {
        const cssClass = {
            PLANNED: 'planned',
            IN_PROGRAMMING: 'in-programming',
            READY: 'ready',
            NOT_ACCOMPLISHED: 'not-accomplished',
            CANCELLED: 'cancelled',
            FINISHED: 'finished',
            default: ''
        };

        return cssClass[this.event.status] || cssClass.default;
    }

    get listOfLocations(): string {
        if (this.event.venue_templates) {
            return this.event.venue_templates
                .map(venueTemplate => venueTemplate.venue.name)
                .join(', ');
        }
        return '';
    }
}
