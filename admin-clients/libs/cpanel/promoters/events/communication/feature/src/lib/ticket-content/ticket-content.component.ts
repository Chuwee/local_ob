import { Component, ChangeDetectionStrategy } from '@angular/core';

@Component({
    selector: 'app-ticket-content',
    templateUrl: './ticket-content.component.html',
    styleUrls: ['./ticket-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketContentComponent { }
