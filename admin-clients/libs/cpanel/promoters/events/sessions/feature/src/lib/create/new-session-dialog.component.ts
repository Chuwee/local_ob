import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, EventSessionsState, Session, SessionsFilterFields, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BehaviorSubject, Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { SessionCreationType } from './models/session-creation-type.enum';

@Component({
    selector: 'app-new-session-dialog',
    templateUrl: './new-session-dialog.component.html',
    styleUrls: ['./new-session-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        EventSessionsService, EventSessionsState
    ],
    standalone: false
})
export class NewSessionDialogComponent implements OnInit {
    readonly #creationType = new BehaviorSubject<SessionCreationType>(null);
    readonly #eventsService = inject(EventsService);
    readonly #sessionsService = inject(EventSessionsService);
    readonly #dialogRef = inject(MatDialogRef<NewSessionDialogComponent>);

    eventId: number;
    loading$: Observable<boolean>;
    sessionCreationTypes = SessionCreationType;
    creationType$ = this.#creationType.asObservable();
    lastSession$: Observable<Session>;

    constructor(
        @Inject(MAT_DIALOG_DATA) private _data: { eventId: number }
    ) {
        this.#dialogRef.addPanelClass(DialogSize.SMALL);
        this.#dialogRef.disableClose = false;

        this.eventId = this._data.eventId;
    }

    ngOnInit(): void {
        this.loading$ = booleanOrMerge([
            this.#eventsService.event.inProgress$(),
            this.#sessionsService.sessionList.inProgress$()
        ]);
        this.#eventsService.event.load(this.eventId.toString());
        this.#eventsService.event.get$()
            .pipe(first(event => event !== null))
            .subscribe(event => {
                if (event.type === EventType.avet) {
                    this.setCreationType(SessionCreationType.single);
                } else if (event.additional_config.inventory_provider === ExternalInventoryProviders.sga) {
                    this.setCreationType(SessionCreationType.single);
                }
            });
        this.#sessionsService.sessionList.load(this.eventId, {
            sort: `${SessionsFilterFields.startDate}:desc`,
            offset: 0,
            limit: 1,
            type: SessionType.session
        });
        this.lastSession$ = this.#sessionsService.sessionList.get$()
            .pipe(
                first(Boolean),
                map(sessions => sessions.data[0])
            );
    }

    close(sessionIds: number[] = null): void {
        this.#dialogRef.close(sessionIds);
    }

    setCreationType(creationType: SessionCreationType): void {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.#dialogRef.removePanelClass(DialogSize.SMALL);
        this.#dialogRef.disableClose = true;

        this.#creationType.next(creationType);
    }
}
