import { EventsService, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EventTicketContentComponent } from '../ticket-content/event-ticket-content/event-ticket-content.component';

@Component({
    selector: 'app-event-invitation-content',
    templateUrl: './event-invitation-content.component.html',
    styleUrls: ['./event-invitation-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventInvitationContentComponent implements OnInit, OnDestroy {

    @ViewChild(EventTicketContentComponent)
    private _ticketContentComponent: EventTicketContentComponent;

    private _eventId: number;
    private _onDestroy = new Subject<void>();

    ticketType = TicketType.invitation;
    hideTicketContents = false;

    readonly isLoadingOrSaving$ = this._eventsService.event.inProgress$();

    constructor(
        private _eventsService: EventsService
    ) { }

    get form(): UntypedFormGroup {
        return this._ticketContentComponent ? this._ticketContentComponent.form : null;
    }

    ngOnInit(): void {
        this._eventsService.event.get$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(event => {
                this._eventId = event.id;
                this.hideTicketContents = event.settings.invitation_use_ticket_template;
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    saveUseTicketTemplates(useTicketTemplates: boolean): void {
        const event: PutEvent = {
            settings: {
                invitation_use_ticket_template: useTicketTemplates
            }
        };
        this._eventsService.event.update(this._eventId, event).subscribe(() => {
            this._eventsService.event.load(this._eventId.toString());
        });
    }
}
