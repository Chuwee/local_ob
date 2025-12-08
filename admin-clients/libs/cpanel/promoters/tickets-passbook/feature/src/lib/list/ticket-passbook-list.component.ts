import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    defaultPassbookLiterals, GetTicketPassbookRequest, TicketPassbook, TicketPassbookType, TicketsPassbookService
} from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, MessageDialogService, ObMatDialogConfig, ListFilteredComponent,
    ListFiltersService, SortFilterComponent, PaginatorComponent, SearchInputComponent, FilterItem, EphemeralMessageService
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { NewTicketPassbookDialogComponent } from '../create/new-ticket-passbook-dialog.component';
import { TicketPassbookListFilterComponent } from './list-filter/ticket-passbook-list-filter.component';

@Component({
    selector: 'app-ticket-passbook-list',
    templateUrl: './ticket-passbook-list.component.html',
    styleUrls: ['./ticket-passbook-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TicketPassbookListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    readonly #ticketsPassbookSrv = inject(TicketsPassbookService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #auth = inject(AuthenticationService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);

    #request: GetTicketPassbookRequest;
    #sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(TicketPassbookListFilterComponent) private _filterComponent: TicketPassbookListFilterComponent;

    dateTimeFormats = DateTimeFormats;
    ticketPassbookMetadata$: Observable<Metadata>;
    ticketPassbookLoading$: Observable<boolean>;
    ticketPassbook$: Observable<TicketPassbook[]>;
    ticketPassbookPageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    entities$: Observable<Entity[]>;

    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();
    readonly isAvetEntityDigitalSeasonTicket$ = this.#auth.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => user.entity.settings?.allow_avet_integration && user.entity.settings?.allow_digital_season_ticket)
        );

    readonly displayedColumns$ = combineLatest([this.canSelectEntity$, this.isAvetEntityDigitalSeasonTicket$])
        .pipe(
            map(([canSelectEntity, isAvetEntityDigitalSeasonTicket]) => {
                if (canSelectEntity) {
                    return ['default', 'name', 'entity', 'create_date', 'type', 'actions'];
                } else if (isAvetEntityDigitalSeasonTicket) {
                    return ['default', 'name', 'create_date', 'type', 'actions'];
                } else {
                    return ['default', 'name', 'create_date', 'actions'];
                }
            })
        );

    readonly passbookTypes = TicketPassbookType;

    updateDefault: (ticketPassBookId: { code: string; entityId: number }, isDefault: boolean) => Observable<boolean>;
    readonly defaultPassbookLiterals = defaultPassbookLiterals;

    constructor(
    ) {
        super();
    }

    trackByFn = (_, item: TicketPassbook): string => `${item.code}_${item.entity_id}`;

    ngOnInit(): void {
        this.ticketPassbookMetadata$ = this.#ticketsPassbookSrv.getTicketPassbookListMetadata$();
        this.ticketPassbookLoading$ = this.#ticketsPassbookSrv.isTicketsPassbookListInProgress$();
        this.ticketPassbook$ = this.#ticketsPassbookSrv.getTicketPassbookListData$()
            .pipe(
                filter(ticketsPassbook => !!ticketsPassbook)
            );

        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this.#entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this.#entitiesService.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );
        this.updateDefault = (ticketPassBookId, isDefault) =>
            this.#ticketsPassbookSrv.updateTicketPassbookDefault(ticketPassBookId, isDefault)
                .pipe(tap(() => this.loadTicketsPassbook()));
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: 20,
            offset: 0
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        // eslint-disable-next-line no-case-declarations
                        const [filterName, direction] = values[0].value.split(':');
                        this.#request.sort = `${filterName.toUpperCase()}:${direction}`;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'ENTITY':
                        this.#request.entity_id = values[0].value;
                        break;
                    case 'START_DATE':
                        this.#request.create_start_date = values[0].value;
                        break;
                    case 'END_DATE':
                        this.#request.create_end_date = values[0].value;
                        break;
                    case 'TYPE':
                        this.#request.type = values[0].value;
                        break;
                }
            }
        });

        this.loadTicketsPassbook();
    }

    getEntityName(entityId: number): Observable<string> {
        return this.entities$
            .pipe(
                first(entities => !!entities),
                map(entities => entities.find(entity => entity.id === entityId)?.name));
    }

    openNewTicketPassbookDialog(): void {
        this.#matDialog.open(NewTicketPassbookDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(data => {
                if (data) {
                    this.#router.navigate([data.ticketPassbookId], {
                        relativeTo: this.#route,
                        queryParams: { entity_id: data.entityId }
                    });
                }
            });
    }

    openDeleteTicketPassbookDialog(ticketPassbook: TicketPassbook): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_TICKET_PASSBOOK',
            message: 'TICKET_PASSBOOK.DELETE_TICKET_PASSBOOK_WARNING',
            messageParams: { ticketPassbookName: ticketPassbook.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this.#ticketsPassbookSrv.deleteTicketPassbook(
                        ticketPassbook.code.toString(),
                        ticketPassbook.entity_id.toString())
                        .subscribe(() => {
                            this.#ephemeralMessageService.showSuccess({
                                msgKey: 'TICKET_PASSBOOK.DELETE_TICKET_PASSBOOK_SUCCESS',
                                msgParams: { ticketPassbookName: ticketPassbook.name }
                            });
                            this.loadTicketsPassbook();
                        });
                }
            });
    }

    private loadTicketsPassbook(): void {
        combineLatest([this.canSelectEntity$, this.isAvetEntityDigitalSeasonTicket$])
            .subscribe(([canSelectEntity, isAvetEntityDigitalSeasonTicket]) => {
                if (canSelectEntity || isAvetEntityDigitalSeasonTicket) {
                    this.#ticketsPassbookSrv.loadTicketPassbookList(this.#request);
                } else {
                    this.#request.type = TicketPassbookType.order;
                    this.#ticketsPassbookSrv.loadTicketPassbookList(this.#request);
                }
                this.#ref.detectChanges();
            });
    }

}
