import { BiService, BiUser, BiUsersRequest } from '@admin-clients/cpanel/bi/data-access';
import { EntityFilterButtonComponent, EntityFilterModule } from '@admin-clients/cpanel/organizations/entities/feature';
import { EntitiesFilterFields, GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EmptyStateComponent, EphemeralMessageService, FilterItem, IconManagerService, ListFilteredComponent, ListFiltersService,
    MessageDialogService, ObMatDialogConfig, PaginatorComponent, renewPassword, SearchInputComponent,
    SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, switchMap } from 'rxjs';
import { BiUserCreateDialogComponent, BiUserCreateDialogData } from '../create/dialog/bi-user-create-dialog.component';

const PAGE_SIZE = 10;
@Component({
    selector: 'app-bi-users-list',
    templateUrl: './bi-users-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ListFiltersService],
    imports: [
        TranslatePipe, MatButton, MatDivider, MatIcon, MatSort, MatTooltipModule, EntityFilterModule, EmptyStateComponent,
        MatProgressSpinner, MatIconButton, MatSortHeader, SearchInputComponent, PaginatorComponent, MatTableModule
    ]
})
export class BiUsersListComponent extends ListFilteredComponent implements AfterViewInit {
    readonly #biSrv = inject(BiService);
    readonly #matDialog = inject(MatDialog);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #iconManagerSrv = inject(IconManagerService);

    private readonly _matSortComponent = viewChild(MatSort);
    private readonly _paginatorComponent = viewChild(PaginatorComponent);
    private readonly _searchInputComponent = viewChild(SearchInputComponent);
    private readonly _entityFilterButton = viewChild(EntityFilterButtonComponent);

    #request: BiUsersRequest;

    readonly $data = toSignal(this.#biSrv.usersList.getData$());
    readonly $metadata = toSignal(this.#biSrv.usersList.getMetadata$());
    readonly $loading = toSignal(this.#biSrv.usersList.loading$());
    readonly displayedColumns = ['name', 'last_name', 'email', 'actions'];
    readonly pageSize = PAGE_SIZE;
    readonly initSortCol = 'name';
    readonly initSortDir = 'asc';
    readonly entitiesRequest = {
        limit: 999,
        sort: `${this.initSortCol}:${this.initSortDir}`,
        fields: [EntitiesFilterFields.name]
    } as GetEntitiesRequest;

    readonly $entityIdSelected = toSignal(this.listFiltersService.onFilterValuesApplied$().pipe(
        map(filterItems => filterItems?.find(filterItem => filterItem.key === 'ENTITY')?.values?.[0]?.value)
    ));

    readonly $isHandsetOrTablet = toSignal(this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches)));

    constructor() {
        super();
        this.#iconManagerSrv.addIconDefinition(renewPassword);
    }

    ngAfterViewInit(): void {
        this.initListFilteredComponent([
            new SortFilterComponent(this._matSortComponent()),
            this._paginatorComponent(),
            this._searchInputComponent(),
            this._entityFilterButton()
        ]);
    }

    override loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: PAGE_SIZE,
            offset: 0,
            entity_id: this.$entityIdSelected()
        };

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
                        this.#request.entity_id = values[0].value;
                        break;
                }
            }
        });

        if (this.#request.entity_id) {
            this.#biSrv.usersList.load(this.#request);
        }
    }

    goToDetails(user: BiUser): void {
        this.#router.navigate([user.id], { relativeTo: this.#route });
    }

    openNewUserDialog(): void {
        this.#matDialog
            .open<BiUserCreateDialogComponent, BiUserCreateDialogData, boolean>(
                BiUserCreateDialogComponent, new ObMatDialogConfig({ entityId: this.$entityIdSelected() })
            )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(() => this.#biSrv.usersList.load(this.#request));
    }

    openDeleteDialog(user: BiUser): void {
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'BI.USERS_LIST.ACTIONS.DELETE_TITLE',
                message: 'BI.USERS_LIST.ACTIONS.DELETE_MESSAGE',
                messageParams: { email: user.email },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#biSrv.usersList.delete(user.id))
            )
            .subscribe(() => {
                this.#biSrv.usersList.load(this.#request);
                this.#ephemeralMsgSrv.showDeleteSuccess();
            });
    }

    openRegeneratePasswordDialog(user: BiUser): void {
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.REGENERATE_PASSWORD',
                message: 'B2B_CLIENTS.USERS_MANAGEMENT.REGENERATE_PASSWORD_WARNING',
                actionLabel: 'FORMS.ACTIONS.OK',
                showCancelButton: true
            })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#biSrv.usersList.regeneratePassword(user.id))
            )
            .subscribe(() => this.#ephemeralMsgSrv.showSuccess({ msgKey: 'B2B_CLIENTS.USERS_MANAGEMENT.REGENERATE_PASSWORD_SUCCESS' }));
    }
}
