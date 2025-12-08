import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    Event, EventListElement, EventsService, EventStatus, GetEventsRequest
} from '@admin-clients/cpanel/promoters/events/data-access';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, DialogSize, EphemeralMessageService,
    FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService, ObMatDialogConfig,
    PaginatorComponent, PopoverComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { LocalCurrencyPartialTranslationPipe, LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgIf } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { switchMap } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';
import { NewEventDialogComponent } from '../create/new-event-dialog.component';
import { EventsListFilterComponent } from './filter/events-list-filter.component';

@Component({
    selector: 'app-events-list',
    templateUrl: './events-list.component.html',
    styleUrls: ['./events-list.component.scss'],
    providers: [ListFiltersService],
    imports: [
        MaterialModule, TranslatePipe, FlexLayoutModule, LocalCurrencyPartialTranslationPipe,
        PopoverFilterDirective, ChipsFilterDirective, PopoverComponent, ChipsComponent,
        RouterLink, LocalDateTimePipe, AsyncPipe, ContextNotificationComponent,
        EventsListFilterComponent, SearchInputComponent, PaginatorComponent, NgIf
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventsListComponent extends ListFilteredComponent implements AfterViewInit {
    readonly #eventsSrv = inject(EventsService);
    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsg = inject(EphemeralMessageService);

    #request: GetEventsRequest;
    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(EventsListFilterComponent) private readonly _filterComponent: EventsListFilterComponent;
    #sortFilterComponent: SortFilterComponent;

    readonly dateTimeFormats = DateTimeFormats;
    readonly eventsPageSize = 20;
    readonly initSortCol = 'name';
    readonly initSortDir = 'asc';
    readonly eventsMetadata$ = this.#eventsSrv.eventsList.getMetadata$();
    readonly eventsLoading$ = this.#eventsSrv.eventsList.loading$();
    readonly events$ = this.#eventsSrv.eventsList.getData$()
        .pipe(
            filter(Boolean),
            map(events => events.map(event => new EventListElement(event)))
        );

    readonly isHandsetOrTablet$ = isHandsetOrTablet$();
    readonly eventCreationCapability$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.EVN_MGR]);
    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();
    readonly displayedColumns$ = this.#auth.getLoggedUser$()
        .pipe(
            first(),
            map(AuthenticationService.operatorCurrencyCodes),
            switchMap(currencies => this.canSelectEntity$
                .pipe(
                    map(canSelectEntity => {
                        const displayedColumns = ['name', 'producer', 'city', 'venue', 'start_date', 'status', 'type', 'actions'];
                        if (canSelectEntity) {
                            displayedColumns.splice(1, 0, 'entity');
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
            urlParameters['status'] = [EventStatus.planned, EventStatus.ready, EventStatus.inProgramming].join(',');
            this._router.navigate(['.'], { relativeTo: this._route, queryParams: urlParameters, replaceUrl: true });
        }
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent]);
    }

    trackByFn = (_, item: Event): number => item.id;

    openNewEventDialog(): void {
        this.#matDialog.open(NewEventDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(eventId => {
                if (eventId) {
                    this._router.navigate([eventId, 'general-data'], { relativeTo: this._route });
                }
            });
    }

    openDeleteEventDialog(event: Event): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_EVENT',
            message: 'EVENTS.DELETE_EVENT_WARNING',
            messageParams: { eventName: event.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this.#eventsSrv.event.delete(event.id.toString())
                        .subscribe(() => {
                            this.#ephemeralMsg.showSuccess({ msgKey: 'EVENTS.DELETE_EVENT_SUCCESS', msgParams: { eventName: event.name } });
                            this.loadEvents();
                        });
                }
            });
    }

    loadData(filters: FilterItem[]): void {
        this.#request = new GetEventsRequest();
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
                    case 'COUNTRY':
                        this.#request.country = values[0].value;
                        break;
                    case 'CITY':
                        this.#request.city = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values.map(val => val.value);
                        break;
                    case 'TYPE':
                        this.#request.type = values[0].value;
                        break;
                    case 'START_DATE':
                        this.#request.startDate = values[0].value;
                        break;
                    case 'END_DATE':
                        this.#request.endDate = values[0].value;
                        break;
                    case 'SHOW_ARCHIVED':
                        this.#request.includeArchived = values[0].value;
                        break;
                    case 'CURRENCY':
                        this.#request.currency = values[0].value;
                        break;
                }
            }
        });

        this.loadEvents();
    }

    private loadEvents(): void {
        this.#eventsSrv.eventsList.load(this.#request);
        this.#ref.detectChanges();
    }
}
