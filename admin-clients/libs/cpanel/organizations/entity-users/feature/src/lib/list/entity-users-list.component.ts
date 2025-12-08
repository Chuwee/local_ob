import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    EntityUser, EntityUserMfaTypes, EntityUsersService, EntityUserStatus, GetEntityUsersRequest
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, DialogSize, EphemeralMessageService, FilterItem, IconManagerService,
    ListFilteredComponent, ListFiltersService, MessageDialogService, newPassword, ObMatDialogConfig, PaginatorComponent, PopoverComponent,
    PopoverFilterDirective, renewPassword, SearchInputComponent, SortFilterComponent, StatusSelectComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, first, map, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { NewUserDialogComponent } from '../create/new-user-dialog.component';
import { SetPasswordDialogComponent } from '../entity-users-shared/set-password-dialog/set-password-dialog.component';
import { EntityUsersFilterComponent } from './filter/entity-users-filter.component';

@Component({
    selector: 'ob-entity-users-list',
    templateUrl: './entity-users-list.component.html',
    styleUrls: ['./entity-users-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, SearchInputComponent, CommonModule, FlexLayoutModule,
        PopoverComponent, PaginatorComponent, ChipsComponent, StatusSelectComponent,
        ContextNotificationComponent, RouterModule, EntityUsersFilterComponent,
        ChipsFilterDirective, PopoverFilterDirective, EllipsifyDirective
    ]
})
export class EntityUsersListComponent extends ListFilteredComponent implements AfterViewInit {
    private _entityUsersSrv = inject(EntityUsersService);
    private _auth = inject(AuthenticationService);
    private _breakpointObserver = inject(BreakpointObserver);
    private _ref = inject(ChangeDetectorRef);
    private _matDialog = inject(MatDialog);
    private _msgDialogSrv = inject(MessageDialogService);
    private _ephemeralSrv = inject(EphemeralMessageService);
    private _ephemeralMessageService = inject(EphemeralMessageService);
    private _iconManagerSrv = inject(IconManagerService);

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(EntityUsersFilterComponent) private readonly _filterComponent: EntityUsersFilterComponent;
    private _request: GetEntityUsersRequest;
    private _sortFilterComponent: SortFilterComponent;
    readonly $loggedUser = toSignal(this._auth.getLoggedUser$());

    readonly usersMetadata$ = this._entityUsersSrv.getUsersListMetadata$();
    readonly usersLoading$ = this._entityUsersSrv.isUsersListLoading$();
    readonly users$ = this._entityUsersSrv.getUsersListData$().pipe(filter(Boolean));
    readonly userCanWrite$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.SYS_MGR]);
    readonly userStatusList = EntityUserStatus;
    readonly isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();
    readonly isUserOperatorMgr$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly isSysAdmin$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]);
    readonly canSelectOperator$ = this._auth.hasLoggedUserSomeEntityType$(['SUPER_OPERATOR']);

    readonly displayedColumns$ = combineLatest([this.canSelectEntity$, this.canSelectOperator$])
        .pipe(
            first(),
            map(([canSelectEntity, canSelectOperator]) => {
                if (canSelectEntity && canSelectOperator) {
                    return ['operator', 'entity', 'name', 'last_name', 'email', 'job_title', 'status', 'mfa_type', 'actions'];
                } else if (canSelectEntity && !canSelectOperator) {
                    return ['entity', 'name', 'last_name', 'email', 'job_title', 'status', 'mfa_type', 'actions'];
                } else {
                    return ['name', 'last_name', 'email', 'job_title', 'status', 'mfa_type', 'actions'];
                }
            })
        );

    usersPageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    mfaTypes: typeof EntityUserMfaTypes = EntityUserMfaTypes;

    constructor(
        private _router: Router,
        private _route: ActivatedRoute
    ) {
        super();
        this._iconManagerSrv.addIconDefinition(newPassword, renewPassword);
    }

    trackByFn = (_, item: EntityUser): number => item.id;

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    updateStatus: (id: number, status: EntityUserStatus) => Observable<void> = (id, status) =>
        this._entityUsersSrv.updateEntityUser(id, { status });

    loadData(filters: FilterItem[]): void {
        this._request = new GetEntityUsersRequest();
        this._auth.getLoggedUser$().pipe(take(1)).subscribe(user => {
            if (user.roles.some(role => role.code === UserRoles.ENT_MGR) &&
                (user.roles.every(role => role.code !== UserRoles.OPR_MGR && role.code !== UserRoles.OPR_ANS) &&
                    user.entity.settings.types.every(type => type !== 'ENTITY_ADMIN'))
            ) {
                this._request.entityId = user.entity.id;
            }
        });
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
                    case 'OPERATOR':
                        this._request.operatorId = values[0].value;
                        break;
                    case 'STATUS':
                        this._request.status = values.map(val => val.value);
                        break;
                }
            }
        });
        this.loadUsers();
    }

    openNewUserDialog(): void {
        this._matDialog.open<NewUserDialogComponent, null, number>(
            NewUserDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .subscribe(userId => {
                if (userId) {
                    this._router.navigate([userId, 'register-data'], { relativeTo: this._route });
                }
            });
    }

    openDeleteUserDialog(user: EntityUser): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_ENTITY_USER',
            message: 'USER.DELETE_ENTITY_USER_WARNING',
            messageParams: { eventName: user.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this._entityUsersSrv.deleteEntityUser(user.id.toString())
                        .subscribe(() => {
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'EVENTS.DELETE_ENTITY_USER_SUCCESS',
                                msgParams: { userName: user.name }
                            });
                            this.loadUsers();
                        });
                }
            });
    }

    openRegeneratePasswordDialog(user: EntityUser): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.REGENERATE_PASSWORD',
            message: 'USER.REGENERATE_PASSWORD_INFO',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._auth.forgotPassword(user.email))
            )
            .subscribe(() => this._ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' }));
    }

    openSetPasswordDialog(user: EntityUser): void {
        this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.SYS_MGR])
            .pipe(
                take(1),
                withLatestFrom(this._auth.getLoggedUser$().pipe(filter(loggedUser => !!loggedUser))),
                switchMap(([isOperator, loggedUser]) => {
                    const selectionIsMyUser = user.id === loggedUser.id;
                    return this._matDialog.open(SetPasswordDialogComponent, new ObMatDialogConfig({
                        userId: user.id,
                        isOperator,
                        selectionIsMyUser,
                        userEntityId: user?.entity?.id
                    })).beforeClosed();
                }),
                filter(done => done)
            )
            .subscribe(() => this._ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' }));
    }

    private loadUsers(): void {
        this._entityUsersSrv.loadEntityUsersList(this._request);
        this._ref.detectChanges();
    }
}
