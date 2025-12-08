import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    GetSeasonTicketsRequest, SeasonTicketListElement, SeasonTicketSearch,
    SeasonTicketStatus, SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, DialogSize, EphemeralMessageService,
    FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService, ObMatDialogConfig,
    PaginatorComponent, PopoverComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPartialTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgClass, NgIf } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { NewSeasonTicketsDialogComponent } from '../create/new-season-tickets-dialog.component';
import { SeasonTicketsListFilterComponent } from './list-filter/season-tickets-list-filter.component';

@Component({
    selector: 'app-season-tickets-list',
    templateUrl: './season-tickets-list.component.html',
    styleUrls: ['./season-tickets-list.component.scss'],
    providers: [
        ListFiltersService
    ],
    imports: [
        NgIf,
        NgClass,
        MaterialModule,
        FlexLayoutModule,
        RouterModule,
        TranslatePipe,
        AsyncPipe,
        LocalCurrencyPartialTranslationPipe,
        ChipsComponent,
        ChipsFilterDirective,
        PaginatorComponent,
        SearchInputComponent,
        PopoverComponent,
        PopoverFilterDirective,
        ContextNotificationComponent,
        SeasonTicketsListFilterComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketsListComponent extends ListFilteredComponent implements AfterViewInit {
    readonly #seasonTicketsSrv = inject(SeasonTicketsService);
    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);

    #request: GetSeasonTicketsRequest;
    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(SeasonTicketsListFilterComponent) private readonly _filterComponent: SeasonTicketsListFilterComponent;
    #sortFilterComponent: SortFilterComponent;

    readonly initSortCol = 'name';
    readonly initSortDir: SortDirection = 'asc';
    readonly seasonTicketsPageSize = 20;

    readonly seasonTicketsMetadata$ = this.#seasonTicketsSrv.seasonTicketList.getMetadata$()
        .pipe(shareReplay(1));

    readonly isLoading$ = booleanOrMerge([
        this.#seasonTicketsSrv.seasonTicket.inProgress$(),
        this.#seasonTicketsSrv.seasonTicketStatus.inProgress$(),
        this.#seasonTicketsSrv.seasonTicketList.loading$()
    ])
        .pipe(shareReplay(1));

    readonly seasonTickets$ = this.#seasonTicketsSrv.seasonTicketList.getData$()
        .pipe(
            filter(seasonTickets => !!seasonTickets),
            map(seasonTickets => seasonTickets.map(seasonTicket => new SeasonTicketListElement(seasonTicket))
            ));

    readonly isHandsetOrTablet$ = isHandsetOrTablet$();

    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();
    readonly displayedColumns$ = this.#auth.getLoggedUser$()
        .pipe(
            first(),
            map(AuthenticationService.operatorCurrencyCodes),
            switchMap(currencies => this.canSelectEntity$
                .pipe(
                    map(canSelectEntity => {
                        const displayedColumns = ['info', 'name', 'producer', 'venue', 'status', 'actions'];
                        if (canSelectEntity) {
                            displayedColumns.splice(2, 0, 'entity');
                        }
                        if (currencies?.length > 1) {
                            displayedColumns.splice(displayedColumns.length - 1, 0, 'currency');
                        }
                        return displayedColumns;
                    })
                ))
        );

    constructor(
        private _router: Router,
        private _route: ActivatedRoute
    ) {
        super();
        // Set default filters
        const urlParameters = Object.assign({}, this._route.snapshot.queryParams);
        if (!urlParameters['status']) {
            urlParameters['status'] = [SeasonTicketStatus.setUp, SeasonTicketStatus.ready, SeasonTicketStatus.pendingPublication].join(',');
            this._router.navigate(['.'], { relativeTo: this._route, queryParams: urlParameters, replaceUrl: true });
        }
    }

    trackByFn = (_, item: SeasonTicketSearch): number => item.id;

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = new GetSeasonTicketsRequest();
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'ENTITY':
                        this.#request.entityId = values[0].value;
                        break;
                    case 'PRODUCER':
                        this.#request.producerId = values[0].value;
                        break;
                    case 'VENUE':
                        this.#request.venueId = values[0].value;
                        break;
                    case 'CURRENCY':
                        this.#request.currency = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                }
            }

        });
        this.loadSeasonTickets();
    }

    openNewSeasonTicketDialog(): void {
        this.#matDialog.open(NewSeasonTicketsDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(seasonTicketId => {
                if (seasonTicketId) {
                    this._router.navigate([seasonTicketId, 'general-data'], { relativeTo: this._route });
                }
            });
    }

    openDeleteSeasonTicketDialog(seasonTicket: SeasonTicketSearch): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_SEASON_TICKET',
            message: 'SEASON_TICKET.DELETE_SEASON_TICKET_WARNING',
            messageParams: { seasonTicketName: seasonTicket.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                switchMap(success => {
                    if (!success) {
                        return of(null);
                    }
                    return this.#seasonTicketsSrv.seasonTicket.delete(seasonTicket.id)
                        .pipe(
                            tap(() => {
                                this.#ephemeralMessageService.showSuccess({
                                    msgKey: 'SEASON_TICKET.DELETE_SEASON_TICKET_SUCCESS',
                                    msgParams: { seasonTicketName: seasonTicket.name }
                                });
                                this.loadSeasonTickets();
                            })
                        );
                })
            ).subscribe();
    }

    private loadSeasonTickets(): void {
        this.#seasonTicketsSrv.seasonTicketList.load(this.#request);
        this.#ref.detectChanges();
    }
}
