import { Metadata } from '@OneboxTM/utils-state';
import { SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SessionsSelectorFilterComponent } from '@admin-clients/cpanel/shared/feature/sessions-selector-filter';
import { ChannelPromotionSessionScope } from '@admin-clients/cpanel-channels-promotions-data-access';
import {
    GetSaleRequestSessionsRequest, SalesRequestsStatus, SalesRequestsService, provideSalesRequestService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { SearchablePaginatedSelectionModule, SelectorListComponent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, differenceWith, unionWith } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgIf, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, Input, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule, MatCheckboxChange } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import {
    debounceTime, distinctUntilChanged, filter, first, map, scan, shareReplay, startWith, switchMap, take, withLatestFrom
} from 'rxjs/operators';
import {
    SessionsEventSelectionListElement as EventElement, EventSessionSelectorListElement as SessionElement
} from './event-session-selector.model';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-event-session-selector',
    templateUrl: './event-session-selector.component.html',
    styleUrls: ['./event-session-selector.component.scss'],
    providers: [
        provideSalesRequestService()
    ],
    imports: [
        AsyncPipe, UpperCasePipe, DateTimePipe, NgIf, TranslatePipe, SearchablePaginatedSelectionModule, ReactiveFormsModule,
        MatTooltipModule, MatCheckboxModule, MatIconModule, SelectorListComponent, SessionsSelectorFilterComponent,
        MatFormFieldModule, MatInputModule, FlexLayoutModule, MatButtonModule, MatProgressSpinnerModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventSessionSelectorComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #salesRequestsService = inject(SalesRequestsService);
    #filters: GetSaleRequestSessionsRequest = {
        limit: PAGE_SIZE,
        sort: 'type:desc,start_date:asc'
    };

    readonly scope = ChannelPromotionSessionScope;
    readonly dateTimeFormats = DateTimeFormats;
    readonly showSelectedOnlyClick = new EventEmitter<void>();
    readonly loading$ = booleanOrMerge([
        this.#salesRequestsService.isSalesRequestsListLoading$(),
        this.#salesRequestsService.isSaleRequestSessionsLoading$()
    ]);

    readonly totalSessions$ = this.#salesRequestsService.getSaleRequestSessionsMetadata$().pipe(map(metadata => metadata?.total));
    readonly allEvents$ = this.#salesRequestsService.getSalesRequestsListData$().pipe(
        filter(Boolean),
        map(list => list.map(elem => ({
            id: elem.event.id,
            saleReqId: elem.id,
            name: elem.event.name
        })))
    );

    readonly allSessions$ = this.#salesRequestsService.getSaleRequestSessions$().pipe(
        filter(Boolean),
        map((sessionsList => sessionsList.map(session => ({
            id: session.id,
            name: session.name,
            dates: session.date,
            catalog_sale_request_id: this.selectedEventId,
            event_id: this.selectedEventId,
            type: session.type
        }))))
    );

    readonly selectedOnly$ = this.showSelectedOnlyClick.pipe(
        scan((isSelectedOnlyMode: boolean) => !isSelectedOnlyMode, false),
        startWith(false),
        takeUntilDestroyed(this.#onDestroy),
        shareReplay(1)
    );

    eventSelectForm = new UntypedFormControl();
    events$: Observable<EventElement[]>;
    metadata$: Observable<Metadata>;
    selectedSessions$: Observable<SessionElement[]>;
    isSelectionDisabled: boolean;
    sessionsList$: Observable<{
        sessionPacks: SessionElement[];
        sessions: SessionElement[];
        unknown: SessionElement[];
    }>;

    @Input() parentEvents$?: Observable<EventElement[]>;
    @Input() channelId: number;
    @Input() entityId: number;
    @Input() form: UntypedFormGroup;
    @Input() pageSize?: number = PAGE_SIZE;
    @Input() selectAllEnabled? = true;

    ngOnInit(): void {
        this.loadEvents();

        this.eventSelectForm.valueChanges.pipe(
            debounceTime(200),
            filter(Boolean),
            map(value => value?.[0]),
            distinctUntilChanged((x, y) => x?.id === y?.id),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(() => {
            this.#filters = { ...this.#filters, offset: 0 };
            this.loadSessions();
        });

        this.events$ = this.parentEvents$ ? this.parentEvents$ : this.allEvents$;

        this.events$.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(events => {
            if (events?.length && !this.eventSelectForm.value) {
                this.eventSelectForm.setValue([events[0]]);
            }
        });

        this.selectedSessions$ = combineLatest([
            this.form.get('selected').valueChanges,
            this.eventSelectForm.valueChanges
        ]).pipe(
            filter(([sessions]: [SessionElement[], EventElement]) => !!sessions),
            map(([selected]) => selected.filter(session => // filter sessions not in this sale req
            (this.saleRequestId ?
                (session.event_id === this.selectedEventId) :
                (session.catalog_sale_request_id === this.selectedEventId))
            )),
            map(selected => selected?.sort((a, b) => a.id - b.id)), // sort by id
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

        this.selectedSessions$.subscribe();

        // all selectable events extracted from sales requests
        this.sessionsList$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ? this.selectedSessions$ : this.allSessions$),
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
            }))
        );

        this.metadata$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ?
                this.selectedSessions$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
                this.#salesRequestsService.getSaleRequestSessionsMetadata$()
            ),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );
    }

    /**
     * selects all filtered sessions
     */
    selectAll(change?: MatCheckboxChange): void {
        this.#salesRequestsService.loadAllSaleRequestSessions({
            saleRequestId: this.saleRequestId ? this.saleRequestId.toString() : this.selectedEventId.toString(),
            ...this.#filters
        });

        this.#salesRequestsService.getAllSaleRequestSessions$()
            .pipe(
                first(Boolean),
                map(sessions => sessions?.map(session => ({
                    id: session.id,
                    name: session.name,
                    dates: { start: session.date },
                    catalog_sale_request_id: this.selectedEventId,
                    event_id: this.selectedEventId
                })))
            ).subscribe(sessions => {
                if (change?.checked) {
                    this.form.get('selected').patchValue(unionWith(this.form.get('selected').value, sessions));
                } else {
                    this.form.get('selected').patchValue(differenceWith(this.form.get('selected').value, sessions));
                }
                this.form.markAsTouched();
                this.form.markAsDirty();
            });
    }

    getSelectedSessionsInEvent(event?: EventElement): number {
        let selectedSessions;
        if (this.saleRequestId) {
            selectedSessions = this.form.get('selected').value?.filter((session: SessionElement) =>
                session.event_id === (event?.id || this.selectedEventId));
        } else {
            selectedSessions = this.form.get('selected').value?.filter((session: SessionElement) =>
                session.catalog_sale_request_id === (event?.id || this.selectedEventId));
        }

        return selectedSessions.length || 0;
    }

    filterChangeHandler(filters: Partial<GetSaleRequestSessionsRequest>): void {
        this.#filters = {
            ...this.#filters,
            ...filters
        };
        this.loadSessions();
    }

    loadSessions = (): void => {
        if (!this.selectedEventId && !this.saleRequestId) {
            return;
        }
        // cancel prev requests so it keeps consistency
        this.#salesRequestsService.cancelSaleRequestSessions();
        this.#salesRequestsService.loadSaleRequestSessions({
            ...this.#filters,
            saleRequestId: this.saleRequestId ? this.saleRequestId.toString() : this.selectedEventId.toString()
        });

        // change to non selected only view if table content loaded
        this.#salesRequestsService.getSaleRequestSessions$().pipe(
            withLatestFrom(this.selectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnly]) => {
            if (isSelectedOnly) {
                this.showSelectedOnlyClick.emit();
            }
        });
    };

    loadEvents = (filter?: string): void => {
        this.#salesRequestsService.loadSalesRequestsList({
            q: filter,
            sort: 'event.name:asc',
            eventEntity: this.entityId,
            channel: this.channelId,
            status: [SalesRequestsStatus.accepted],
            include_third_party_entity_events: true,
            fields: ['event.id', 'event.name', 'event.start_date']
        });
    };

    get selectedEventId(): number {
        return this.eventSelectForm.value?.[0]?.id;
    }

    get saleRequestId(): number {
        return this.eventSelectForm.value?.[0]?.saleReqId;
    }
}
