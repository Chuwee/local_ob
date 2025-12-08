
import { Metadata } from '@OneboxTM/utils-state';
import {
    GetEntityUsersRequest, EntityUser, EntityUsersService, EntityUserRoles, EntityUserPermissions
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, PaginatorComponent,
    SearchInputComponent, SortFilterComponent, ListFiltersService, ListFilteredComponent, FilterItem,
    ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgIf } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, Observable, Subject, switchMap } from 'rxjs';
import { take } from 'rxjs/operators';
import { AssignLicenseDialogComponent } from '../../assign-bi-permissions-dialog/assign-license-dialog.component';
import { AssignLicenseDialogData } from '../../assign-bi-permissions-dialog/assign-license-dialog.model';

const PAGE_SIZE = 20;

@Component({
    selector: 'app-impersonations-list',
    templateUrl: './impersonations-list.component.html',
    styleUrls: ['./impersonations-list.component.scss'],
    providers: [ListFiltersService],
    imports: [
        AsyncPipe, NgIf,
        SearchInputComponent,
        FlexLayoutModule,
        PaginatorComponent,
        ContextNotificationComponent,
        PaginatorComponent,
        TranslatePipe,
        MaterialModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImpersonationsComponent extends ListFilteredComponent implements OnInit, AfterViewInit, OnDestroy {

    #sortFilterComponent: SortFilterComponent;
    #request: GetEntityUsersRequest;

    readonly #onDestroy = new Subject<void>();
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #usersSrv = inject(EntityUsersService);
    readonly #matDialog = inject(MatDialog);

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;

    readonly pageSize = PAGE_SIZE;

    users$: Observable<EntityUser[]>;
    usersMetadata$: Observable<Metadata>;
    reqInProgress$: Observable<boolean>;
    columns = ['name', 'last_name', 'email', 'operator', 'entity', 'actions'];

    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';

    isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    ngOnInit(): void {
        this.model();
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent
        ]);
    }

    override ngOnDestroy(): void {
        this.#usersSrv.clearUsersList();
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: this.pageSize,
            offset: 0,
            roles: [EntityUserRoles.BI_USR],
            permissions: [EntityUserPermissions.impersonation]
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
                }
            }
        });
        this.#usersSrv.loadEntityUsersList(this.#request);
    }

    addImpersonatedLicenseToUser(): void {
        this.users$
            .pipe(
                take(1),
                map(users => users.map(user => user.id)),
                switchMap(userIds =>
                    this.#matDialog.open<AssignLicenseDialogComponent, AssignLicenseDialogData, boolean>(
                        AssignLicenseDialogComponent, new ObMatDialogConfig({
                            permission: EntityUserPermissions.impersonation,
                            title: 'BI_REPORTS.IMPERSONATION.ASSIGN_LICENSE',
                            onlyInternalUsers: true,
                            alreadyAssigned: userIds
                        }))
                        .beforeClosed()
                ))
            .subscribe(added => {
                if (added) {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'BI_REPORTS.IMPERSONATION.ASSIGN_SUCCESS' });
                    this.#usersSrv.loadEntityUsersList(this.#request);
                }
            });
    }

    openDeleteImpersonatedLicenseDialog(user: EntityUser): void {
        this.#usersSrv.loadUserRoles(user.id);
        this.#usersSrv.getUserRoles$()
            .pipe(
                first(roles => !!roles),
                switchMap(roles =>
                    this.#msgDialogService.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.DELETE_IMPERSONATED_LICENSE',
                        message: 'BI_REPORTS.IMPERSONATION.DELETE_WARNING',
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
                        || permissions.some(permission => permission?.includes(EntityUserPermissions.mobile))) {
                        return this.#usersSrv.rolesAndPermissions.deleteRolePermission(
                            user.id, EntityUserRoles.BI_USR, EntityUserPermissions.impersonation);
                    } else {
                        return this.#usersSrv.rolesAndPermissions.deleteRoleAndPermissions(user.id, EntityUserRoles.BI_USR);
                    }
                })
            )
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'BI_REPORTS.IMPERSONATION.UNASSIGN_SUCCESS',
                    msgParams: { email: user.email }
                });
                this.#usersSrv.loadEntityUsersList(this.#request);
            });
    }

    private model(): void {
        this.reqInProgress$ = booleanOrMerge([
            this.#usersSrv.isUsersListLoading$(),
            this.#usersSrv.rolesAndPermissions.isRoleAndPermissionsSaving$()
        ]);

        this.users$ = this.#usersSrv.getUsersListData$()
            .pipe(filter(list => !!list));

        this.usersMetadata$ = this.#usersSrv.getUsersListMetadata$()
            .pipe(filter(md => !!md));
    }

}
