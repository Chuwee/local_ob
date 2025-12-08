import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    EventSessionsService, GetSessionsRequest, SessionsFilterFields, SessionStatus,
    SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SessionsSelectorFilterComponent } from '@admin-clients/cpanel/shared/feature/sessions-selector-filter';
import {
    ProductEventSessionSelectionType, ProductEventsService, PutProductEventSessions
} from '@admin-clients/cpanel-products-my-products-events-data-access';
import {
    EphemeralMessageService, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { atLeastOneRequiredInArray, booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxChange, MatCheckboxModule } from '@angular/material/checkbox';
import { MatError } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import {
    BehaviorSubject, Observable, combineLatest, filter, first, map, shareReplay, switchMap, take, tap, throwError
} from 'rxjs';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-product-event-details-sessions',
    imports: [
        AsyncPipe, SearchablePaginatedSelectionModule, TranslatePipe, DateTimePipe,
        ReactiveFormsModule, FormContainerComponent, MatRadioModule,
        NgTemplateOutlet, MatTooltipModule, MatIconModule, MatCheckboxModule, MatError, MatProgressSpinnerModule,
        MatButtonModule, SessionsSelectorFilterComponent
    ],
    templateUrl: './product-event-details-sessions.component.html',
    styleUrls: ['./product-event-details-sessions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventDetailsSessionsComponent implements OnInit, OnDestroy {
    readonly #productsSrv = inject(ProductsService);
    readonly #productEventsSrv = inject(ProductEventsService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #sessionsSelectedOnly = new BehaviorSubject(false);
    readonly #onDestroy = inject(DestroyRef);

    #sessionsFilter: GetSessionsRequest = {
        limit: PAGE_SIZE,
        offset: 0,
        sort: 'start_date:asc',
        status: [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled],
        type: SessionType.session,
        fields: [
            SessionsFilterFields.name, SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingStatus,
            SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType
        ]
    };

    readonly form = this.#fb.group({
        sessions: this.#fb.control([], [atLeastOneRequiredInArray()]),
        sessionsSelectAll: [true, [Validators.required]],
        selected: this.#fb.control([])
    });

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);
    readonly pageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;
    readonly sessions$ = this.#productEventsSrv.productEvents.sessions.get$().pipe(
        filter(Boolean),
        tap(sessions => {
            this.form.patchValue({
                sessions: sessions.sessions?.map(session => ({
                    id: session.id,
                    name: session.name,
                    start: session.dates.start
                })) || []
            });
        })
    );

    readonly sessionsSelectedOnly$ = this.#sessionsSelectedOnly.asObservable();
    readonly selectedSessions$ = this.form.get('selected').valueChanges
        .pipe(
            filter(Boolean),
            map(selected => selected?.sort((a, b) => a.id - b.id)), // sort by id
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

    readonly isInProgress$ = booleanOrMerge([
        this.#eventSessionsSrv.sessionList.inProgress$(),
        this.#productEventsSrv.productEvents.sessions.loading$(),
        this.#eventSessionsSrv.isAllSessionsLoading$(),
        this.#productsSrv.product.inProgress$()
    ]);

    readonly allEventSessions$ = combineLatest([
        this.#eventSessionsSrv.sessionList.getData$().pipe(filter(Boolean)),
        this.#eventSessionsSrv.sessionList.getMetadata$(),
        this.sessionsSelectedOnly$
    ]).pipe(map(([allSessions, metadata, selectedOnly]) => {
        const response = {
            data: allSessions,
            metadata
        };
        if (selectedOnly) {
            response.data = this.form.get('sessions').value
                .sort((a, b) => a.name.localeCompare(b.name))
                .slice(this.#sessionsFilter.offset, this.#sessionsFilter.offset + this.pageSize);
            response.metadata = new Metadata({
                total: this.form.value.sessions.length,
                offset: this.#sessionsFilter.offset,
                limit: this.pageSize
            });
        }
        this.form.controls.sessions.markAsUntouched();
        this.form.controls.sessions.markAsPristine();
        return response;
    }));

    readonly allEventSessionsMetadata$ = this.allEventSessions$.pipe(map(resp => resp?.metadata), shareReplay(1));
    readonly allEventSessionsData$ = this.allEventSessions$.pipe(map(resp => this.mapSessionsToForm(resp)));

    ngOnInit(): void {
        this.#productEventsSrv.productEvents.sessions.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(sessions => {
            this.form.controls.sessionsSelectAll.patchValue(
                sessions.type === ProductEventSessionSelectionType.all
            );
            this.form.markAsPristine();
            this.form.markAsUntouched();
            this.#sessionsSelectedOnly.next(false);
        });
        this.canWrite$.pipe(
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(canWrite => !canWrite && this.form.disable());
    }

    ngOnDestroy(): void {
        this.#productEventsSrv.productEvents.sessions.clear();
        this.#eventSessionsSrv.sessionList.clear();
    }

    changeSessionsSelectedOnly(): void {
        if (!this.#sessionsSelectedOnly.value) {
            this.#sessionsFilter.offset = 0;
        }
        this.#sessionsSelectedOnly.next(!this.#sessionsSelectedOnly.value);
    }

    reloadSessionsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#sessionsFilter = { ...this.#sessionsFilter, limit, offset, q };
        this.loadSessionsList();
    }

    filterChangeHandler(filters: Partial<GetSessionsRequest>): void {
        this.#sessionsFilter = {
            ...this.#sessionsFilter,
            ...filters
        };
        this.loadSessionsList();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void> {
        if (this.form.valid || !!this.form.controls.sessionsSelectAll.value) {
            const sessions: PutProductEventSessions = {
                type: this.form.value.sessionsSelectAll ?
                    ProductEventSessionSelectionType.all
                    : ProductEventSessionSelectionType.restricted,
                sessions: this.form.value.sessionsSelectAll ? [] : this.form.value.sessions.map(session => session.id)
            };
            return this.#productsSrv.product.get$().pipe(
                take(1),
                switchMap(product => this.#productEventsSrv.productEvents.sessions.update(product.product_id, this.eventIdPath, sessions)),
                tap(() => this.#ephemeralMessageSrv.showSaveSuccess())
            );
        } else {
            this.form.markAllAsTouched();
            //SetValue in order to rerender child components with form fields in order to show input errors.
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid product event detail form');
        }
    }

    selectAll(change?: MatCheckboxChange): void {
        this.#eventSessionsSrv.loadAllSessions(this.eventIdPath, {
            ...this.#sessionsFilter,
            limit: undefined,
            fields: [
                SessionsFilterFields.name, SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingStatus,
                SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType
            ]
        });
        this.#eventSessionsSrv.getAllSessions$().pipe(
            first(allSessions => !!allSessions?.data),
            map(allSessions => this.mapSessionsToForm(allSessions))
        ).subscribe(allSessions => {
            if (change?.checked) {
                this.form.get('sessions').patchValue(allSessions);
            } else {
                this.form.get('sessions').patchValue([]);
            }
            this.form.markAsTouched();
            this.form.markAsDirty();
        });
    }

    get eventIdPath(): number | undefined {
        const allRouteParams = Object.assign({}, ...this.#activatedRoute.snapshot.pathFromRoot.map(path => path.params));
        return parseInt(allRouteParams.eventId);
    }

    private loadSessionsList(): void {
        this.#eventSessionsSrv.sessionList.load(this.eventIdPath, this.#sessionsFilter);
    }

    private mapSessionsToForm(sessions): IdName & { start: string }[] {
        return sessions?.data?.map(session => ({
            id: session.id,
            name: session.name,
            start: session.start_date || session.start,
            smartbooking: session.settings?.smart_booking?.type === 'SMART_BOOKING'
        }));
    }

    private reloadModels(): void {
        this.#productsSrv.product.get$().pipe(
            take(1),
            filter(Boolean)
        ).subscribe(product => {
            this.#productEventsSrv.productEvents.sessions.load(product.product_id, this.eventIdPath);
        });
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

}
