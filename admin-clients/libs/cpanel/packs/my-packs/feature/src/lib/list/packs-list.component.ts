import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { GetPacksRequest, Pack, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, DialogSize, EmptyStateComponent, EphemeralMessageService, FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService, ObMatDialogConfig, PaginatorComponent, PopoverComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, TitleCasePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, take } from 'rxjs/operators';
import { CreatePackDialogComponent } from '../create/create-pack-dialog.component';
import { PacksListFilterComponent } from './filter/packs-list-filter.component';

const PAGE_SIZE = 20;
@Component({
    selector: 'app-packs-list',
    templateUrl: './packs-list.component.html',
    styleUrls: ['./packs-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, MaterialModule, PaginatorComponent,
        ChipsComponent, ChipsFilterDirective, RouterLink, ContextNotificationComponent, AsyncPipe, EmptyStateComponent,
        TranslatePipe, TitleCasePipe, PacksListFilterComponent, PopoverFilterDirective, PopoverComponent, SearchInputComponent
    ]
})
export class PacksListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    readonly #packsSrv = inject(PacksService);
    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(PacksListFilterComponent) private _filterComponent: PacksListFilterComponent;
    #sortFilterComponent: SortFilterComponent;
    #request = new GetPacksRequest();

    readonly packsPageSize = PAGE_SIZE;
    readonly initSortCol = 'name';
    readonly initSortDir: SortDirection = 'asc';
    readonly packsMetadata$ = this.#packsSrv.packsList.getMetadata$();
    readonly packsLoading$ = this.#packsSrv.packsList.loading$();
    readonly packs$ = this.#packsSrv.packsList.getData$();

    readonly isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    readonly canCreate$ = this.#auth.getLoggedUser$()
        .pipe(take(1), map(user => AuthenticationService.isSomeRoleInUserRoles(user,
            [UserRoles.OPR_MGR, UserRoles.EVN_MGR]
        )));

    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();

    readonly displayedColumns$ = this.canSelectEntity$
        .pipe(
            map(canSelectEntity => {
                //TODO: Pack type hidden until we have manual packs
                // 'pack_type',
                if (canSelectEntity) {
                    return ['name', 'entity', 'status', 'actions'];
                } else {
                    return ['name', 'status', 'actions'];
                }
            })
        );

    hasAppliedFilters = false;

    trackByFn = (_, item: Pack): number => item.id;

    ngOnInit(): void {
        this.loadPacks();
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
        this.hasAppliedFilters = false;
        this.#request = new GetPacksRequest();
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                this.hasAppliedFilters = true;
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        this.hasAppliedFilters = false;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        this.hasAppliedFilters = false;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values[0].value;
                        break;
                    case 'ENTITY':
                        this.#request.entity_id = values[0].value;
                        break;
                }
            }
        });

        this.loadPacks();
    }

    openNewPackDialog(): void {
        this.#matDialog.open(CreatePackDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(packId => {
                if (packId) {
                    this.#router.navigate([packId, 'general-data'], { relativeTo: this.#route });
                }
            });
    }

    openDeletePackDialog(pack: Pack): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_PACK',
            message: 'PACK.DELETE_WARNING_MESSAGE',
            messageParams: { name: pack.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        }).pipe(filter(Boolean))
            .subscribe(() => {
                this.#packsSrv.pack.delete(pack.id)
                    .subscribe({
                        next: () => {
                            this.#ephemeralMessageService.showSuccess({
                                msgKey: 'PACK.DELETE_SUCCESS_MESSAGE',
                                msgParams: { name: pack.name }
                            });
                            this.loadPacks();
                        },
                        error: (error: HttpErrorResponse) => {
                            if (error.error.code === 'PACK_HAS_SALES') {
                                const errorMessage = [];
                                errorMessage.push(error.error.message);
                                this.#msgDialogService.showAlert({
                                    size: DialogSize.SMALL,
                                    title: 'TITLES.ERROR_DIALOG',
                                    message: 'API_ERRORS.PACK_HAS_SALES',
                                    subMessages: errorMessage
                                });
                            }
                        }
                    });
            });
    }

    //TODO: Clone not ready in backend yet
    // openClonePack(pack: Pack): void {
    //     this.#matDialog.open(ClonePackDialogComponent, new ObMatDialogConfig({ pack }))
    //         .beforeClosed()
    //         .pipe(filter(Boolean))
    //         .subscribe(id => {
    //             this.#router.navigate([id, 'general-data'], { relativeTo: this.#route });
    //         });
    // }

    private loadPacks(): void {
        this.#packsSrv.packsList.load(this.#request);
        this.#ref.detectChanges();
    }
}
