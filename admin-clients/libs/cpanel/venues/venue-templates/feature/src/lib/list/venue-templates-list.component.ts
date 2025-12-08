import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    CloneVenueTemplateDialogComponent, NewVenueTemplateDialogComponent, NewVenueTemplateDialogData, NewVenueTemplateDialogMode
} from '@admin-clients/cpanel-common-venue-templates-feature';
import {
    ChipsComponent,
    ChipsFilterDirective,
    ContextNotificationComponent,
    EphemeralMessageService, FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService, ObMatDialogConfig,
    PaginatorComponent, PopoverComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    GetVenueTemplatesRequest, VenueTemplate, VenueTemplateScope, VenueTemplatesService, VenueTemplateType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgIf } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ViewChild, ViewContainerRef
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { first, map, shareReplay, tap } from 'rxjs/operators';
import { VenueTemplatesListFilterComponent } from './filter/venue-templates-list-filter.component';

@Component({
    selector: 'app-venue-templates-list',
    templateUrl: './venue-templates-list.component.html',
    styleUrls: ['./venue-templates-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, FlexLayoutModule, PaginatorComponent, ContextNotificationComponent, SearchInputComponent,
        MaterialModule, PopoverComponent, PopoverFilterDirective, VenueTemplatesListFilterComponent, RouterLink, AsyncPipe, NgIf,
        ChipsComponent, ChipsFilterDirective
    ]
})
export class VenueTemplatesListComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    private _request: GetVenueTemplatesRequest;
    private _sortFilterComponent: SortFilterComponent;
    private _onDestroy = new Subject<void>();
    private _viewContainerRef = inject(ViewContainerRef);

    @ViewChild(MatSort)
    private _matSort: MatSort;

    @ViewChild(PaginatorComponent)
    private _paginatorComponent: PaginatorComponent;

    @ViewChild(SearchInputComponent)
    private _searchInputComponent: SearchInputComponent;

    @ViewChild(VenueTemplatesListFilterComponent)
    private _filterComponent: VenueTemplatesListFilterComponent;

    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    venueTemplatesPageSize = 20;
    venueTemplatesMetadata$: Observable<Metadata>;
    venueTemplates$: Observable<VenueTemplate[]>;
    loadingData$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();
    readonly userCanWrite$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.REC_MGR])
        .pipe(shareReplay(1));

    readonly displayedColumns$ = this.canSelectEntity$.pipe(
        first(),
        map(canSelectEntity => {
            if (canSelectEntity) {
                return ['name', 'entity_name', 'venue_name', 'venue_city', 'graphic', 'actions'];
            } else {
                return ['name', 'venue_name', 'venue_city', 'graphic', 'actions'];
            }
        })
    );

    constructor(
        private _venueTemplatesService: VenueTemplatesService,
        private _auth: AuthenticationService,
        private _ref: ChangeDetectorRef,
        private _msgDialogSrv: MessageDialogService,
        private _breakpointObserver: BreakpointObserver,
        private _matDialog: MatDialog,
        private _router: Router,
        private _route: ActivatedRoute,
        private _ephemeralMessage: EphemeralMessageService
    ) {
        super();
    }

    trackByFn = (_: number, item: VenueTemplate): number => item.id;

    ngOnInit(): void {
        this.venueTemplatesMetadata$ = this._venueTemplatesService.getVenueTemplatesListMetadata$();
        this.loadingData$ = this._venueTemplatesService.isVenueTemplatesListLoading$();
        this.venueTemplates$ = this._venueTemplatesService.getVenueTemplatesListData$();
    }

    override ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
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
        this._request = {
            limit: this.venueTemplatesPageSize,
            offset: 0,
            scope: VenueTemplateScope.archetype
        };
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
                        this._request.filter = values[0].value;
                        break;
                    case 'ENTITY':
                        this._request.entityId = values[0].value;
                        break;
                    case 'VENUE':
                        this._request.venueId = values[0].value;
                        break;
                    case 'VENUE_ENTITY':
                        this._request.venueEntityId = values[0].value;
                        break;
                    case 'CITY':
                        this._request.city = values[0].value;
                        break;
                    case 'GRAPHIC':
                        this._request.graphic = values[0].value;
                        break;
                }
            }
        });
        this.loadTemplates();
    }

    openDeleteTemplateDialog(template: VenueTemplate): void {
        this._msgDialogSrv.showDeleteConfirmation({
            confirmation: {
                title: 'TITLES.DELETE_PROMOTER_VENUE_TEMPLATE',
                message: 'EVENTS.CONFIGS.DELETE_PROMOTER_TEMPLATE_WARNING',
                messageParams: { templateName: template.name }
            },
            delete$: this._venueTemplatesService.deleteVenueTemplate(template.id.toString()).pipe(
                tap(() => this.loadTemplates())
            ),
            success: {
                msgKey: 'EVENTS.CONFIGS.DELETE_PROMOTER_TEMPLATE_SUCCESS',
                msgParams: { templateName: template.name }
            }
        });
    }

    openNewVenueTemplateDialog(): void {
        this._matDialog.open<NewVenueTemplateDialogComponent, NewVenueTemplateDialogData, number>(
            NewVenueTemplateDialogComponent,
            new ObMatDialogConfig({ mode: NewVenueTemplateDialogMode.venueTemplate }, this._viewContainerRef)
        )
            .beforeClosed()
            .subscribe(id => {
                if (id) {
                    this._ephemeralMessage.showCreateSuccess();
                    this._router.navigate([id], { relativeTo: this._route });
                }
            });
    }

    openCloneTemplateDialog(template: VenueTemplate): void {
        this._matDialog.open<CloneVenueTemplateDialogComponent,
            { fromVenueTemplate: VenueTemplate },
            { entityId: number; templateId: number }>(
                CloneVenueTemplateDialogComponent, new ObMatDialogConfig({ fromVenueTemplate: template })
            )
            .beforeClosed()
            .subscribe(response => {
                if (response) {
                    this._ephemeralMessage.showCreateSuccess();
                    this._router.navigate([response.templateId], { relativeTo: this._route });
                }
            });
    }

    canBeCloned(venueTemplate: VenueTemplate): boolean {
        return venueTemplate.type !== VenueTemplateType.avet;
    }

    private loadTemplates(): void {
        this._venueTemplatesService.loadVenueTemplatesList(this._request);
        this._ref.detectChanges();
    }
}
