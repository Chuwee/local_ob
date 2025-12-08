import { Metadata } from '@OneboxTM/utils-state';
import { PromotionSessionsScope } from '@admin-clients/cpanel/promoters/data-access';
import { EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import {
    eventSessionsProviders, EventSessionsService, GetSessionsRequest, SessionsFilterFields, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SessionsSelectorFilterComponent } from '@admin-clients/cpanel/shared/feature/sessions-selector-filter';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { FormControlHandler, differenceWith, unionWith } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, Input, OnInit
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import {
    distinctUntilChanged, filter, map, scan, shareReplay, startWith, switchMap, take, withLatestFrom
} from 'rxjs/operators';
import { EventPromotionSessionsListElement as SessionElement } from './event-promotion-sessions-list-element.model';

const PAGE_SIZE = 10;

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        SearchablePaginatedSelectionModule,
        CommonModule,
        DateTimePipe,
        SessionsSelectorFilterComponent
    ],
    selector: 'app-event-promotion-sessions',
    templateUrl: './event-promotion-sessions.component.html',
    styleUrls: ['./event-promotion-sessions.component.scss'],
    providers: [
        eventSessionsProviders
    ]
})
export class EventPromotionSessionsComponent implements OnInit {
    private readonly _eventSessionsService = inject(EventSessionsService);
    private readonly _eventPromotionsService = inject(EventPromotionsService);
    private readonly _destroyRef = inject(DestroyRef);

    private _isSelectAllChecked: boolean;
    private _filters: GetSessionsRequest = {
        limit: PAGE_SIZE,
        sort: `${SessionsFilterFields.type}:desc,${SessionsFilterFields.startDate}:desc`,
        fields: [
            SessionsFilterFields.name, SessionsFilterFields.type, SessionsFilterFields.startDate,
            SessionsFilterFields.settingsSmartbookingStatus, SessionsFilterFields.settingsSmartbookingRelatedSession,
            SessionsFilterFields.venueTplType
        ]
    };

    readonly scope = PromotionSessionsScope;
    readonly dateTimeFormats = DateTimeFormats;
    readonly pageSize = PAGE_SIZE;
    readonly showSelectedOnlyClick = new EventEmitter<void>();

    metadata$: Observable<Metadata>;
    hasSelectableSessions$: Observable<boolean>;
    readonly loading$ = this._eventSessionsService.sessionList.inProgress$();
    readonly selectedOnly$ = this.showSelectedOnlyClick.pipe(
        scan((isSelectedOnlyMode: boolean) => !isSelectedOnlyMode, false),
        startWith(false),
        takeUntilDestroyed(),
        shareReplay(1)
    );

    selectedSessions$: Observable<SessionElement[]>;
    totalSessions$: Observable<number>;
    sessionsList$: Observable<{
        sessionPacks: SessionElement[];
        sessions: SessionElement[];
        unknown: SessionElement[];
    }>;

    @Input() sessionsForm: UntypedFormGroup;
    @Input() eventId: number;
    @Input() promotionId: number;

    ngOnInit(): void {
        this._eventPromotionsService.promotionSessions.load(this.eventId, this.promotionId);

        this.selectedSessions$ = this.sessionsForm.get('selected').valueChanges
            .pipe(
                filter((promoSessions: SessionElement[]) => !!promoSessions),
                map(selected => selected?.sort((a, b) => a.id - b.id)), // sort by id
                takeUntilDestroyed(this._destroyRef),
                shareReplay(1)
            );

        this.selectedSessions$.subscribe();

        // all selectable sessions
        const allSessions$ = this._eventSessionsService.sessionList.get$()
            .pipe(
                filter(Boolean),
                map((sessionsList => sessionsList.data.map(session => ({
                    id: session.id,
                    name: session.name,
                    dates: { start: session.start_date },
                    type: session.type,
                    isSmartBooking: session.settings?.smart_booking?.type === 'SMART_BOOKING'
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
                this.selectedSessions$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
                this._eventSessionsService.sessionList.get$()
                    .pipe(map(sl => sl?.metadata))
            ),
            takeUntilDestroyed(this._destroyRef),
            shareReplay(1)
        );

        this._eventPromotionsService.promotionSessions.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(promSessions => {
                this.sessionsForm.patchValue({
                    type: promSessions.type || null,
                    selected: promSessions.sessions || []
                });
                this.sessionsForm.markAsPristine();
                this.sessionsForm.markAsUntouched();
            });

        this.sessionsForm.get('type').valueChanges
            .pipe(distinctUntilChanged(), takeUntilDestroyed(this._destroyRef))
            .subscribe((scope: PromotionSessionsScope) => {
                scope === PromotionSessionsScope.restricted ?
                    this.sessionsForm.get('selected').enable() : this.sessionsForm.get('selected').disable();
            });

        this.totalSessions$ = this._eventSessionsService.sessionList.get$()
            .pipe(map(sl => sl?.metadata?.total || 0));

        combineLatest([
            this._eventPromotionsService.promotionSessions.get$().pipe(filter(Boolean)),
            this.sessionsForm.valueChanges // only used as a trigger
        ])
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promSessions]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.sessionsForm.get('type'),
                    promSessions.type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.sessionsForm.get('selected'),
                    promSessions.sessions || []
                );
            });

        this._eventSessionsService.getAllSessionsData$()
            .pipe(
                filter(sessions => !!sessions),
                map(sessions => sessions?.map(session => ({
                    id: session.id,
                    name: session.name,
                    dates: { start: session.start_date },
                    type: session.type
                })))
            )
            .subscribe(sessions => {
                if (this._isSelectAllChecked) {
                    this.sessionsForm.get('selected').patchValue(unionWith(this.sessionsForm.get('selected').value, sessions));
                } else {
                    this.sessionsForm.get('selected').patchValue(differenceWith(this.sessionsForm.get('selected').value, sessions));
                }
                this.sessionsForm.markAsTouched();
                this.sessionsForm.markAsDirty();
            });
    }

    get selectedSessions(): number {
        return this.sessionsForm.get('selected')?.value?.length || 0;
    }

    /**
     * selects all filtered sessions
     */
    selectAll(change?: MatCheckboxChange): void {
        this._isSelectAllChecked = change?.checked;
        this._eventSessionsService.loadAllSessions(this.eventId, {
            ...this._filters,
            limit: undefined
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
        this._eventSessionsService.sessionList.load(this.eventId, this._filters);

        // change to non-selected only view if table content loaded
        this._eventSessionsService.sessionList.get$().pipe(
            withLatestFrom(this.selectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnly]) => {
            if (isSelectedOnly) {
                this.showSelectedOnlyClick.emit();
            }
        });
    };
}
