import {
    GetSeasonTicketSessionsRequest, SeasonTicketSessionSelected, seasonTicketSessionsProviders,
    SeasonTicketSessionsService, SeasonTicketSessionStatus
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import {
    EmptyStateTinyComponent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import {
    ChangeDetectionStrategy, Component, computed, DestroyRef, effect, inject, input, output, signal
} from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormControl } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { take } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef } from '@angular/material/table';
import { Metadata } from '@OneboxTM/utils-state';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { AsyncPipe } from '@angular/common';

const PAGE_SIZE = 10;
export type ExcludedActionType = 'BLOCK' | 'UNBLOCK';

@Component({
    selector: 'app-excluded-sessions-config',
    imports: [
        SearchablePaginatedSelectionModule, TranslatePipe, MatIconButton, MatTooltip, MatIcon,
        MatCheckbox, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatCell, MatCellDef, AsyncPipe,
        DateTimePipe, MatButton, MatMenu, MatMenuItem, MatMenuTrigger, EmptyStateTinyComponent
    ],
    providers: [seasonTicketSessionsProviders],
    templateUrl: './excluded-sessions-config.component.html',
    styleUrls: ['./excluded-sessions-config.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExcludedSessionsConfigComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #seasonTicketSessionsSrv = inject(SeasonTicketSessionsService);
    readonly #breakpointObserver = inject(BreakpointObserver);

    readonly #$filters = signal({
        limit: PAGE_SIZE,
        sort: 'session_starting_date:asc',
        status: SeasonTicketSessionStatus.assigned,
        startDate: new Date(Date.now()).toISOString()
    } as GetSeasonTicketSessionsRequest);

    readonly #$sessionFormValue = signal<SeasonTicketSessionSelected[]>([]);
    readonly #$sessionsList = toSignal(this.#seasonTicketSessionsSrv.sessions.getData$()
      .pipe(filter(Boolean)));

    readonly #$allSessionsList = toSignal(this.#seasonTicketSessionsSrv.sessions.getAllData$()
      .pipe(filter(Boolean)));

    readonly #$allSessionsSelectedList = computed(() => {
        return this.#$allSessionsList()?.map(session => ({
            id: session.session_id,
            ...session,
            blocked: this.$excludedSessions()?.includes(session.session_id)
        }));
    });

    readonly $seasonTicketId = input.required<number>({ alias: 'seasonTicketId' });
    readonly $excludedSessions = input.required<number[]>({ alias: 'excludedSessionsIds' });
    readonly $emptyTitle = input<string>('SEASON_TICKET.EXCLUDED_SESSIONS.EMPTY_LIST_TITLE', { alias: 'emptyTitle' });
    readonly $emptyDescription = input<string>('SEASON_TICKET.EXCLUDED_SESSIONS.EMPTY_LIST_DESCRIPTION', { alias: 'emptyDescription' });

    readonly saveExcludedSessions = output<{type: ExcludedActionType, excludedSessions: number[]}>({ alias: 'saveExcludedSessions' });

    readonly sessionForm = new FormControl<SeasonTicketSessionSelected[]>([]);
    readonly pageSize = PAGE_SIZE;
    readonly sessionsColumns = ['active', 'session_name', 'session_starting_date', 'event_name', 'status', 'actions'];

    readonly #$selectedSessions = computed(() => {
        const sessions = this.$hasFilters()
            ? this.#$sessionFormValue()?.filter(selectedSession =>
                !!this.#$sessionsList().find(session => session.session_id === selectedSession.session_id))
            : this.#$sessionFormValue();

        return sessions?.sort((a, b) =>
            new Date(a.session_starting_date).getTime() - new Date(b.session_starting_date).getTime()
        );
    });

    readonly $allSelected = signal(false);
    readonly $isSelectedOnlyMode = signal(false);
    readonly $sessionsList = computed(() => {
        const sessions = this.$isSelectedOnlyMode() ? this.#$selectedSessions() : this.#$sessionsList();
        return sessions?.map(session => ({
            id: session.session_id,
            ...session,
            blocked: this.$excludedSessions()?.includes(session.session_id)
        }));
    });

    readonly $filteredSelectedSessions = signal<{
        blocked: SeasonTicketSessionSelected[],
        unblocked: SeasonTicketSessionSelected[]
    }>(null);

    readonly $totalSessionListMetadata = toSignal(this.#seasonTicketSessionsSrv.sessions.getMetadata$().pipe(filter(Boolean)));
    readonly $sessionsListMetadata = computed(() => this.$isSelectedOnlyMode() ?
      new Metadata({ total: this.#$selectedSessions().length, limit: 999, offset: 0 }) : this.$totalSessionListMetadata());

    readonly $isHandsetOrTablet = toSignal(this.#breakpointObserver
      .observe([Breakpoints.Handset, Breakpoints.Tablet])
      .pipe(map(result => result.matches)));

    readonly $hasFilters = computed(() => this.#$filters().q);

    readonly data$ = toObservable(this.$sessionsList);
    readonly metadata$ = toObservable(this.$sessionsListMetadata);
    readonly sessionsLoading$ = this.#seasonTicketSessionsSrv.sessions.loading$();
    readonly dateTimeFormats = DateTimeFormats;

    disableActions = true;

    constructor() {
        effect(() => {
            this.#seasonTicketSessionsSrv.sessions.loadAll(this.$seasonTicketId().toString());
        });

        effect(() => {
            this.sessionForm.setValue(this.$allSelected() ? this.#$allSessionsSelectedList() : []);
            this.sessionForm.markAsDirty();
        });

        effect(() => {
            if (this.$isSelectedOnlyMode() && !this.#$sessionFormValue()?.length) {
                this.$isSelectedOnlyMode.set(false);
            }
        });

        effect(() => {
            const excludedSessions = this.$excludedSessions();
            const updatedSessions = this.sessionForm.value?.map(session => ({
                ...session,
                blocked: excludedSessions?.includes(session.session_id)
            }));
            this.sessionForm.setValue(updatedSessions);
        });

        this.sessionForm.valueChanges.pipe(
          takeUntilDestroyed(this.#onDestroy)
        ).subscribe(selectedSessions => {
            const blocked = selectedSessions?.filter(session => session.blocked);
            const unblocked = selectedSessions?.filter(session => !session.blocked);
            this.#$sessionFormValue.set(selectedSessions);
            this.$filteredSelectedSessions.set({ blocked, unblocked });
            this.disableActions = false;
        });
    }

    filterChangeHandler(filters: Partial<GetSeasonTicketSessionsRequest>): void {
        this.#$filters.set({ ...this.#$filters(), ...filters });
        this.loadSessions();
    }

    loadSessions(): void {
        this.#seasonTicketSessionsSrv.sessions.load(this.$seasonTicketId().toString(), this.#$filters());

        // change to non selected only view if table content loaded
        this.#seasonTicketSessionsSrv.sessions.getData$().pipe(
            take(1)
        ).subscribe(() => this.$isSelectedOnlyMode.set(this.$isSelectedOnlyMode()));
    }

    block(session: SeasonTicketSessionSelected = null): void {
        this.#saveExcludedSessions('BLOCK', this.$filteredSelectedSessions().unblocked, session);
    }

    unblock(session: SeasonTicketSessionSelected = null): void {
        this.#saveExcludedSessions('UNBLOCK', this.$filteredSelectedSessions().blocked, session);
    }

    #saveExcludedSessions(type: ExcludedActionType, filteredSessions: SeasonTicketSessionSelected[], session: SeasonTicketSessionSelected = null): void {
        const currentExcluded = this.$excludedSessions() ?? [];
        const sessionsToAction = session ? [session.id] : filteredSessions.map(session => session.id);

        const excludedSessions = type === 'BLOCK' ? [
            ...currentExcluded,
            ...sessionsToAction.filter(id => !currentExcluded.includes(id))
        ] : currentExcluded.filter(id => !sessionsToAction.includes(id));

        this.saveExcludedSessions.emit({type, excludedSessions});
        this.disableActions = true;
    }
}
