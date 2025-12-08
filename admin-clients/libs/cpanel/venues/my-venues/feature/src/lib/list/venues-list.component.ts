import { Metadata } from '@OneboxTM/utils-state';
import { UserRoles, AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { GetVenuesRequest, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, ListFilteredComponent, ListFiltersService,
    SortFilterComponent, PaginatorComponent, SearchInputComponent, FilterItem
} from '@admin-clients/shared/common/ui/components';
import { Venue } from '@admin-clients/shared/data-access/models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { EMPTY, Observable } from 'rxjs';
import { filter, first, map, switchMap, take } from 'rxjs/operators';
import { NewVenueDialogComponent } from '../create/new-venue-dialog.component';
import { VenuesListFilterComponent } from './filter/venues-list-filter.component';

@Component({
    selector: 'app-venues-list',
    templateUrl: './venues-list.component.html',
    styleUrls: ['./venues-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VenuesListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    private _breakpointObserver = inject(BreakpointObserver);
    private _auth = inject(AuthenticationService);
    private _request: GetVenuesRequest;
    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(VenuesListFilterComponent) private _filterComponent: VenuesListFilterComponent;
    private _sortFilterComponent: SortFilterComponent;
    venuesPageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    venuesMetadata$: Observable<Metadata>;
    venuesLoading$: Observable<boolean>;
    venues$: Observable<Venue[]>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    userCanWrite$: Observable<boolean>;

    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();

    readonly displayedColumns$ = this.canSelectEntity$
        .pipe(
            first(),
            map(canSelectEntity => {
                if (canSelectEntity) {
                    return ['entity', 'name', 'country', 'city', 'capacity', 'actions'];
                } else {
                    return ['name', 'country', 'city', 'capacity', 'actions'];
                }
            })
        );

    constructor(
        private _venuesSrv: VenuesService,
        private _ref: ChangeDetectorRef,
        private _matDialog: MatDialog,
        private _msgDialogSrv: MessageDialogService,
        private _ephemeralService: EphemeralMessageService,
        private _router: Router,
        private _route: ActivatedRoute
    ) {
        super();
    }

    trackByFn = (_: number, item: Venue): number => item.id;

    ngOnInit(): void {
        this.venuesMetadata$ = this._venuesSrv.venuesList.getMetadata$();
        this.venuesLoading$ = this._venuesSrv.venuesList.isLoading$();
        this.venues$ = this._venuesSrv.venuesList.getData$();
        this.userCanWrite$ = this._auth.getLoggedUser$()
            .pipe(take(1), map(user => AuthenticationService.isSomeRoleInUserRoles(
                user, [UserRoles.OPR_MGR, UserRoles.REC_MGR, UserRoles.ENT_MGR]
            )));
    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent]);
    }

    loadData(filters: FilterItem[]): void {
        this._request = {};
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this._request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this._request.limit = values[0].value.limit;
                        this._request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this._request.q = values[0].value;
                        break;
                    case 'ENTITY':
                        this._request.entityId = values[0].value;
                        break;
                    case 'CITY':
                        this._request.city = values[0].value;
                        break;
                    case 'COUNTRY':
                        this._request.countryCode = values[0].value;
                        break;
                }
            }
        });

        this.loadVenues();
    }

    openNewVenueDialog(): void {
        this._matDialog.open<NewVenueDialogComponent, null, number>(
            NewVenueDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .subscribe(venueId => {
                if (venueId) {
                    this._router.navigate([venueId, 'general-data'], { relativeTo: this._route });
                }
            });
    }

    openDeleteVenueDialog(venue: Venue): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_VENUE',
            message: 'VENUES.DELETE_VENUE_WARNING',
            messageParams: { venueName: venue.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(accepted => !!accepted),
                switchMap(accepted => {
                    if (accepted) {
                        return this._venuesSrv.deleteVenue(venue.id);
                    } else {
                        return EMPTY;
                    }
                })
            )
            .subscribe(() => {
                this._ephemeralService.showSuccess({
                    msgKey: 'VENUES.DELETE_VENUE_SUCCESS',
                    msgParams: { venueName: venue.name }
                });
                this.loadVenues();
            });
    }

    private loadVenues(): void {
        this._venuesSrv.venuesList.load(this._request);
        this._ref.detectChanges();
    }
}
