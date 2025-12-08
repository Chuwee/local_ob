import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    defaultTicketTemplateLiterals, GetTicketTemplatesRequest, TicketTemplate, TicketTemplatesService
} from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, DialogSize, EphemeralMessageService, FilterItem,
    ListFilteredComponent, ListFiltersService, MessageDialogService, ObMatDialogConfig, PaginatorComponent, PopoverComponent,
    PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DefaultIconComponent } from '@admin-clients/shared-common-ui-default-icon';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgIf } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, take, tap } from 'rxjs/operators';
import { CloneTicketTemplateDialogComponent } from '../clone/clone-ticket-template-dialog.component';
import { NewTicketTemplateDialogComponent } from '../create/new-ticket-template-dialog.component';
import { TicketTemplatesListFilterComponent } from './filter/ticket-templates-list-filter.component';

@Component({
    selector: 'app-ticket-templates-list',
    templateUrl: './ticket-templates-list.component.html',
    styleUrls: ['./ticket-templates-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, NgIf, MaterialModule, PopoverComponent, PopoverFilterDirective,
        TicketTemplatesListFilterComponent, SearchInputComponent, PaginatorComponent,
        ChipsComponent, ChipsFilterDirective, DefaultIconComponent, RouterLink, ContextNotificationComponent,
        AsyncPipe, TranslatePipe
    ]
})
export class TicketTemplatesListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    private _request: GetTicketTemplatesRequest;
    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(TicketTemplatesListFilterComponent) private _filterComponent: TicketTemplatesListFilterComponent;
    private _sortFilterComponent: SortFilterComponent;
    ticketTemplatesPageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    ticketTemplatesMetadata$: Observable<Metadata>;
    ticketTemplatesLoading$: Observable<boolean>;
    ticketTemplates$: Observable<TicketTemplate[]>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    ticketTemplateCreationCapability$: Observable<boolean>;

    updateDefault: (ticketTemplateId: number, isDefault: boolean) => Observable<boolean>;

    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();
    readonly displayedColumns$ = this.canSelectEntity$
        .pipe(
            map(canSelectEntity => {
                if (canSelectEntity) {
                    return ['default', 'name', 'entity', 'format', 'design', 'printer', 'paper_type', 'actions'];
                } else {
                    return ['default', 'name', 'format', 'design', 'printer', 'paper_type', 'actions'];
                }
            })
        );

    readonly defaultTicketTemplateLiterals = defaultTicketTemplateLiterals;

    constructor(
        private _ticketTemplatesSrv: TicketTemplatesService,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog,
        private _msgDialogService: MessageDialogService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _breakpointObserver: BreakpointObserver,
        private _ref: ChangeDetectorRef,
        private _router: Router,
        private _route: ActivatedRoute
    ) {
        super();
    }

    trackByFn = (_, item: TicketTemplate): number => item.id;

    ngOnInit(): void {
        this.ticketTemplatesMetadata$ = this._ticketTemplatesSrv.getTicketTemplatesListMetadata$();
        this.ticketTemplatesLoading$ = this._ticketTemplatesSrv.isTicketTemplatesLoading$();
        this.ticketTemplates$ = this._ticketTemplatesSrv.getTicketTemplates$()
            .pipe(
                filter(ticketTemplates => !!ticketTemplates)
            );
        this.ticketTemplateCreationCapability$ = this._auth.getLoggedUser$()
            .pipe(take(1), map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR, UserRoles.EVN_MGR])));

        this.updateDefault = (ticketTemplateId, isDefault) =>
            this._ticketTemplatesSrv.updateTicketTemplateDefault(ticketTemplateId, isDefault)
                .pipe(tap(() => this.loadTicketTemplates()));
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
        this._request = { offset: 0, limit: 0 };
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
                        this._request.entity_id = values[0].value;
                        break;
                    case 'FORMAT':
                        this._request.format = values[0].value;
                        break;
                    case 'DESIGN':
                        this._request.design_id = values[0].value;
                        break;
                    case 'PRINTER':
                        this._request.printer = values[0].value;
                        break;
                    case 'PAPER_TYPE':
                        this._request.paper_type = values[0].value;
                        break;
                }
            }
        });

        this.loadTicketTemplates();
    }

    openNewTicketTemplateDialog(): void {
        this._matDialog.open(NewTicketTemplateDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(ticketTemplateId => {
                if (ticketTemplateId) {
                    this._router.navigate([ticketTemplateId, 'general-data'], { relativeTo: this._route });
                }
            });
    }

    openDeleteTicketTemplateDialog(ticketTemplate: TicketTemplate): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_TICKET_TEMPLATE',
            message: 'TICKET_TEMPLATE.DELETE_WARNING_MESSAGE',
            messageParams: { name: ticketTemplate.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(filter(Boolean))
            .subscribe(() => {
                this._ticketTemplatesSrv.deleteTicketTemplate(ticketTemplate.id.toString())
                    .subscribe(() => {
                        this._ephemeralMessageService.showSuccess({
                            msgKey: 'TICKET_TEMPLATE.DELETE_SUCCESS_MESSAGE',
                            msgParams: { name: ticketTemplate.name }
                        });
                        this.loadTicketTemplates();
                    });
            });
    }

    openCloneTicketTemplate(ticketTemplate: TicketTemplate): void {
        this._matDialog.open(CloneTicketTemplateDialogComponent, new ObMatDialogConfig({ ticketTemplate }))
            .beforeClosed()
            .pipe(filter(id => !!id))
            .subscribe(id => {
                this._router.navigate([id, 'general-data'], { relativeTo: this._route });
            });
    }

    private loadTicketTemplates(): void {
        this._ticketTemplatesSrv.loadTicketTemplates(this._request);
        this._ref.detectChanges();
    }
}
