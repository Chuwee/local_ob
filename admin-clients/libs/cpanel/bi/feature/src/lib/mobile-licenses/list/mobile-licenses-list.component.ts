
import { Metadata } from '@OneboxTM/utils-state';
import {
    GetEntityUsersRequest, EntityUser, EntityUsersService,
    EntityUserRoles, EntityUserPermissions
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { EntitiesBaseService, aggDataMobileLicenses } from '@admin-clients/shared/common/data-access';
import {
    ListFilteredComponent, ListFiltersService, SortFilterComponent, DialogSize, EphemeralMessageService,
    MessageDialogService, ObMatDialogConfig, PaginatorComponent, SearchInputComponent, FilterItem,
    AggregatedDataComponent,
    ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AggregatedData } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgIf } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, Observable, Subject, switchMap } from 'rxjs';
import { AssignLicenseDialogComponent } from '../../assign-bi-permissions-dialog/assign-license-dialog.component';
import { AssignLicenseDialogData } from '../../assign-bi-permissions-dialog/assign-license-dialog.model';

const PAGE_SIZE = 20;

@Component({
    selector: 'app-mobile-licenses-list',
    templateUrl: './mobile-licenses-list.component.html',
    styleUrls: ['./mobile-licenses-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        AggregatedDataComponent,
        ContextNotificationComponent,
        PaginatorComponent,
        AsyncPipe, NgIf,
        SearchInputComponent,
        TranslatePipe,
        FlexLayoutModule
    ]
})
export class MobileLicensesComponent extends ListFilteredComponent implements OnInit, AfterViewInit, OnDestroy {

    private _sortFilterComponent: SortFilterComponent;
    private _request: GetEntityUsersRequest;

    readonly #onDestroy = new Subject<void>();
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #usersSrv = inject(EntityUsersService);
    readonly #matDialog = inject(MatDialog);

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;

    readonly pageSize = PAGE_SIZE;

    reqInProgress$: Observable<boolean>;
    users$: Observable<EntityUser[]>;
    usersMetadata$: Observable<Metadata>;
    mobileLicensesAggData$: Observable<AggregatedData>;
    aggDataMobileLicenses = aggDataMobileLicenses;
    columns = ['name', 'email', 'operator', 'entity', 'actions'];

    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';

    isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    ngOnInit(): void {
        this.#entitiesSrv.entitiesUsersLimits.load();
        this.model();
    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent
        ]);
    }

    override ngOnDestroy(): void {
        this.#usersSrv.clearUsersList();
        this.#entitiesSrv.entitiesUsersLimits.clear();
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    loadData(filters: FilterItem[]): void {
        this._request = {
            limit: this.pageSize,
            offset: 0,
            roles: [EntityUserRoles.BI_USR],
            permissions: [EntityUserPermissions.mobile]
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
                        this._request.q = values[0].value;
                        break;
                }
            }
        });
        this.#usersSrv.loadEntityUsersList(this._request);
    }

    addMobileLicenseToUser(): void {
        this.#matDialog.open<AssignLicenseDialogComponent, AssignLicenseDialogData, boolean>(
            AssignLicenseDialogComponent, new ObMatDialogConfig({
                permission: EntityUserPermissions.mobile,
                title: 'BI_REPORTS.MOBILE_USERS.ASSIGN_LICENSE'
            })
        )
            .beforeClosed()
            .subscribe(added => {
                if (added) {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'BI_REPORTS.MOBILE_USERS.ASSIGN_SUCCESS' });
                    this.#usersSrv.loadEntityUsersList(this._request);
                    this.#entitiesSrv.entitiesUsersLimits.load();
                }
            });
    }

    openDeleteMobileUserLicenseDialog(user: EntityUser): void {
        this.#usersSrv.loadUserRoles(user.id);
        this.#usersSrv.getUserRoles$()
            .pipe(
                first(roles => !!roles),
                switchMap(roles =>
                    this.#msgDialogService.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.DELETE_MOBILE_LICENSE',
                        message: 'BI_REPORTS.MOBILE_USERS.DELETE_WARNING',
                        messageParams: { email: user.email },
                        actionLabel: 'FORMS.ACTIONS.UNASSIGN',
                        showCancelButton: true
                    })
                        .pipe(map(accepted => ({ accepted, roles })))
                ),
                filter(({ accepted }) => !!accepted),
                switchMap(({ roles }) => {
                    const permissions = roles.map(role => role.permissions);
                    if (permissions.some(permission => permission?.includes(EntityUserPermissions.basic))
                        || permissions.some(permission => permission?.includes(EntityUserPermissions.advanced))
                        || permissions.some(permission => permission?.includes(EntityUserPermissions.impersonation))) {
                        return this.#usersSrv.rolesAndPermissions.deleteRolePermission(
                            user.id, EntityUserRoles.BI_USR, EntityUserPermissions.mobile);
                    } else {
                        return this.#usersSrv.rolesAndPermissions.deleteRoleAndPermissions(user.id, EntityUserRoles.BI_USR);
                    }
                })
            )
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'BI_REPORTS.MOBILE_USERS.UNASSIGN_SUCCESS',
                    msgParams: { email: user.email }
                });
                this.#usersSrv.loadEntityUsersList(this._request);
                this.#entitiesSrv.entitiesUsersLimits.load();
            });
    }

    private model(): void {
        this.reqInProgress$ = booleanOrMerge([
            this.#usersSrv.isUsersListLoading$(),
            this.#usersSrv.rolesAndPermissions.isRoleAndPermissionsSaving$(),
            this.#entitiesSrv.entitiesUsersLimits.inProgress$()
        ]);

        this.users$ = this.#usersSrv.getUsersListData$()
            .pipe(filter(list => !!list));

        this.usersMetadata$ = this.#usersSrv.getUsersListMetadata$()
            .pipe(filter(md => !!md));

        this.mobileLicensesAggData$ = this.#entitiesSrv.entitiesUsersLimits.getMobileMetadata$();
    }

}
