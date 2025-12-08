import { Metadata } from '@OneboxTM/utils-state';
import { SeasonTicketReleaseSeatListRequest, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { ReleasedList, ReleasedListFilter } from '@admin-clients/cpanel/promoters/season-tickets/locality-management/data-access';
import {
    SeasonTicketSession, SeasonTicketSessionsService,
    seasonTicketSessionsProviders
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { ReleaseDataSessionStatus } from '@admin-clients/shared/common/data-access';
import {
    EmptyStateComponent,
    EmptyStateTinyComponent, EphemeralMessageService, ExportDialogComponent,
    ListFiltersService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ExportFormat, ExportResponse } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, first, map, switchMap } from 'rxjs';
import { ReleasedListFilterComponent } from './filter/released-list-filter.component';
import { exportDataReleasedList } from './released-list-export-data';

@Component({
    imports: [
        TranslatePipe, MaterialModule, NgClass, EllipsifyDirective, LocalCurrencyPipe, DateTimePipe, ReleasedListFilterComponent,
        FormContainerComponent, EmptyStateTinyComponent, EmptyStateComponent, RouterLink, AsyncPipe
    ],
    providers: [ListFiltersService, seasonTicketSessionsProviders],
    selector: 'app-release-seats-released-list',
    templateUrl: './release-seats-released-list.component.html',
    styleUrls: ['./release-seats-released-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReleaseSeatsReleasedListComponent implements OnDestroy {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #sessionsListSrv = inject(SeasonTicketSessionsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #dialog = inject(MatDialog);
    readonly #fb = inject(FormBuilder);
    readonly #router = inject(Router);

    readonly columns = ['user_data', 'session', 'ticket_data', 'release_date', 'status', 'sold_price', 'total_gained', 'actions'];
    readonly releasedListMetadata$: Observable<Metadata> = this.#seasonTicketSrv.seasonTicketReleaseSeat.list.getMetadata$();
    readonly originalReleasedList$: Observable<ReleasedList[]> = this.#seasonTicketSrv.seasonTicketReleaseSeat.list.getData$();
    readonly releasedList$ = this.originalReleasedList$;
    readonly sessionList$: Observable<SeasonTicketSession[]> = this.#sessionsListSrv.sessions.getData$();
    readonly seasonTicket$ = this.#seasonTicketSrv.seasonTicket.get$();
    readonly loadingData$ = booleanOrMerge([
        this.#seasonTicketSrv.seasonTicketReleaseSeat.list.loading$(),
        this.#sessionsListSrv.sessions.loading$()
    ]);

    readonly form = this.#fb.group({
        session_id: [null as string, Validators.required],
        release_status: [{ value: [ReleaseDataSessionStatus.sold, ReleaseDataSessionStatus.released], disabled: true }]
    });

    readonly $seasonTicketId = toSignal(this.#seasonTicketSrv.seasonTicket.get$()
        .pipe(
            first(Boolean),
            takeUntilDestroyed(this.#onDestroy),
            map(seasonTicket => seasonTicket.id)
        ));

    #filters: ReleasedListFilter = {
        session_id: null,
        release_status: []
    };

    request: SeasonTicketReleaseSeatListRequest = {
        limit: 6,
        offset: 0,
        session_id: null,
        release_status: [ReleaseDataSessionStatus.sold, ReleaseDataSessionStatus.released]
    };

    ngOnDestroy(): void {
        this.#seasonTicketSrv.seasonTicketReleaseSeat.list.clear();
    }

    loadData(pageEvent: PageEvent): void {
        this.request.offset = pageEvent ? pageEvent.pageIndex * pageEvent.pageSize : 0;
        this.#seasonTicketSrv.seasonTicketReleaseSeat.list.load(this.$seasonTicketId(), this.request);
    }

    requestChangedHandler(request: SeasonTicketReleaseSeatListRequest): void {
        this.request = request;
        this.#seasonTicketSrv.seasonTicketReleaseSeat.list.load(this.$seasonTicketId(), this.request);
    }

    viewAssociatedSale(orderCode: string): void {
        const baseUrl = window.location.origin.replace(this.#router.url, '');
        const urlTree = this.#router.createUrlTree(['/transactions', orderCode]);
        window.open(baseUrl + this.#router.serializeUrl(urlTree), '_blank');
    }

    exportReleasedList(): void {
        this.#dialog.open(ExportDialogComponent, new ObMatDialogConfig({
            exportData: exportDataReleasedList,
            exportFormat: ExportFormat.csv
        }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(exportList => {
                    const filteredFilters = Object.entries(this.#filters)
                        .filter(([, value]) => value !== null && (!Array.isArray(value) || value.length > 0))
                        .reduce((acc, [key, value]) => {
                            acc[key] = value;
                            return acc;
                        }, {});

                    exportList = {
                        ...filteredFilters,
                        ...exportList
                    };

                    return this.#seasonTicketSrv.seasonTicketReleaseSeat.list.export(this.$seasonTicketId(), exportList);
                }),
                filter((result: ExportResponse) => !!result.export_id)
            )
            .subscribe(() => {
                this.#ephemeralMessageSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
            });

    }
}
