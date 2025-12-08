import { EventSessionsService, EventSessionsState, Session, SessionsFilterFields, SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-linked-sessions',
    templateUrl: './linked-sessions.component.html',
    styleUrls: ['./linked-sessions.component.scss'],
    providers: [
        EventSessionsState,
        EventSessionsService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, LocalDateTimePipe, CommonModule
    ]
})
export class LinkedSessionsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    @Input() currentSession$: Observable<Session>;
    linkedSessions$: Observable<Session[]>;
    displayedColumns = ['name', 'date'];
    dateTimeFormats = DateTimeFormats;

    constructor(
        private _sessionsSrv: EventSessionsService,
        private _router: Router,
        private _route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        this.currentSession$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(session =>
                this._sessionsSrv.loadAllSessions(
                    session.event.id,
                    {
                        sort: `${SessionsFilterFields.startDate}:asc`,
                        type: SessionType.session,
                        filterByIds: session.session_ids
                    }
                )
            );
        this.linkedSessions$ = this._sessionsSrv.getAllSessionsData$().pipe(filter(sessions => !!sessions));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    openSessionInNewWindow(sessionId: number): void {
        const baseUrl = window.location.origin.replace(this._router.url, '');
        const newRelativeUrl = this._router.serializeUrl(
            this._router.createUrlTree([`../../../sessions/${sessionId}/planning`], { relativeTo: this._route })
        );

        window.open(baseUrl + newRelativeUrl, '_blank');
    }

}
