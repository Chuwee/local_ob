import { Metadata } from '@OneboxTM/utils-state';
import { NotificationSessionsScope } from '@admin-clients/cpanel/notifications/data-access';
import {
    eventSessionsProviders, EventSessionsService, GetSessionsRequest, SessionsFilterFields, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { differenceWith, unionWith } from '@admin-clients/shared/utility/utils';
import { NgIf, AsyncPipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit } from '@angular/core';
import { ExtendedModule } from '@angular/flex-layout/extended';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { TranslatePipe } from '@ngx-translate/core';
import {
    distinctUntilChanged, filter, first, map, Observable, scan, shareReplay, startWith, Subject, switchMap, take, takeUntil, withLatestFrom
} from 'rxjs';
import { NotificationSessionsListElement as SessionElement } from './notification-sessions-list-element.model';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-notification-sessions',
    templateUrl: './notification-sessions.component.html',
    styleUrls: ['./notification-sessions.component.scss'],
    providers: [
        eventSessionsProviders
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, FormsModule, ReactiveFormsModule, MaterialModule,
        ExtendedModule, SearchablePaginatedSelectionModule, NgIf, TranslatePipe, AsyncPipe, UpperCasePipe, DateTimePipe
    ]
})
export class NotificationSessionsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _filters: GetSessionsRequest = {
        limit: PAGE_SIZE,
        sort: 'type:desc,start_date:asc',
        fields: [SessionsFilterFields.name, SessionsFilterFields.type, SessionsFilterFields.startDate]
    };

    private _eventId: number;

    readonly scope = NotificationSessionsScope;
    readonly dateTimeFormats = DateTimeFormats;
    readonly pageSize = PAGE_SIZE;
    readonly showSelectedOnlyClick = new EventEmitter<void>();

    metadata$: Observable<Metadata>;
    hasSelectableSessions$: Observable<boolean>;
    loading$: Observable<boolean>;
    selectedOnly$: Observable<boolean>;
    selectedSessions$: Observable<SessionElement[]>;
    totalSessions$: Observable<number>;
    sessionsList$: Observable<{
        sessionPacks: SessionElement[];
        sessions: SessionElement[];
        unknown: SessionElement[];
    }>;

    @Input() sessionsForm: UntypedFormGroup;
    @Input() entityId: number;
    @Input() eventId$: Observable<number>;

    constructor(
        private _eventSessionsService: EventSessionsService
    ) { }

    ngOnInit(): void {
        this.eventId$.pipe(filter(Boolean)).subscribe(id => {
            this._eventId = id;
            this._filters.offset = 0; //Reset pagination on event change
            this._eventSessionsService.sessionList.load(id, this._filters);
        });

        this.selectedOnly$ = this.showSelectedOnlyClick.pipe(
            scan((isSelectedOnlyMode: boolean) => !isSelectedOnlyMode, false),
            startWith(false),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.selectedSessions$ = this.sessionsForm.get('selected').valueChanges
            .pipe(
                filter((notificationSessions: SessionElement[]) => !!notificationSessions),
                map(selected => selected?.sort((a, b) => a.id - b.id)), // sort by id
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this.selectedSessions$.subscribe();

        // all selectable sessions
        const allSessions$ = this._eventSessionsService.sessionList.get$()
            .pipe(
                map(sl => sl?.data),
                filter(sessionsList => !!sessionsList),
                map((sessionsList => sessionsList.map(session => ({
                    id: session.id,
                    name: session.name,
                    dates: { start: session.start_date },
                    type: session.type
                })))),
                shareReplay(1)
            );

        this.hasSelectableSessions$ = allSessions$
            .pipe(
                map(sessions => !!sessions?.length),
                shareReplay(1)
            );

        this.sessionsList$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ? this.selectedSessions$ : allSessions$),
            map(sessions => sessions.reduce((sessionsGroups, session) => {
                if (session.type === SessionType.session) {
                    sessionsGroups.sessions.push(session);
                } else if (session.type) {
                    sessionsGroups.sessionPacks.push(session);
                } else {
                    sessionsGroups.unknown.push(session);
                }
                return sessionsGroups;
            }, {
                sessionPacks: [],
                sessions: [],
                unknown: []
            })),
            shareReplay(1)
        );

        this.metadata$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ?
                this.selectedSessions$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 })))
                : this._eventSessionsService.sessionList.get$().pipe(map(sl => sl?.metadata))
            ),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.loading$ = this._eventSessionsService.sessionList.inProgress$();

        this.sessionsForm.get('type').valueChanges
            .pipe(distinctUntilChanged(), takeUntil(this._onDestroy))
            .subscribe((scope: NotificationSessionsScope) => {
                scope === NotificationSessionsScope.restricted ?
                    this.sessionsForm.get('selected').enable() : this.sessionsForm.get('selected').disable();
            });

        this.totalSessions$ = this._eventSessionsService.sessionList.get$()
            .pipe(map(sl => sl?.metadata?.total));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    get selectedSessions(): number {
        return this.sessionsForm.get('selected')?.value?.length || 0;
    }

    /**
     * selects all filtered sessions
     */
    selectAll(change?: MatCheckboxChange): void {
        this._eventSessionsService.loadAllSessions(this._eventId, {
            ...this._filters,
            limit: undefined
        });
        this._eventSessionsService.getAllSessionsData$()
            .pipe(
                first(sessions => !!sessions),
                map(sessions => sessions?.map(session => ({
                    id: session.id,
                    name: session.name,
                    dates: { start: session.start_date },
                    type: session.type
                })))
            )
            .subscribe(sessions => {
                if (change?.checked) {
                    this.sessionsForm.get('selected').patchValue(unionWith(this.sessionsForm.get('selected').value, sessions));
                } else {
                    this.sessionsForm.get('selected').patchValue(differenceWith(this.sessionsForm.get('selected').value, sessions));
                }
                this.sessionsForm.markAsTouched();
                this.sessionsForm.markAsDirty();
            });

    }

    filterChangeHandler(filters: Partial<GetSessionsRequest>): void {
        this._filters = {
            ...this._filters,
            ...filters
        };
        this.loadSessions();
    }

    loadSessions = (): void => {
        if (this._eventId) {
            this._eventSessionsService.sessionList.load(this._eventId, this._filters);
        }

        // change to non selected only view if table content loaded
        this._eventSessionsService.sessionList.get$().pipe(
            map(sl => sl?.data),
            withLatestFrom(this.selectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnly]) => {
            if (isSelectedOnly) {
                this.showSelectedOnlyClick.emit();
            }
        });
    };
}
