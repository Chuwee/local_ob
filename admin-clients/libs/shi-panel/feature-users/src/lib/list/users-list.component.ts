import {
    ListFilteredComponent, ListFiltersService, SortFilterComponent, PaginatorComponent,
    ContextNotificationComponent, SearchInputComponent, FilterItem, StatusSelectComponent, MessageDialogService,
    DialogSize, EphemeralMessageService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { User, UserPermissions, UserRoles, UserStatus } from '@admin-clients/shi-panel/utility-models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { GetUsersRequest } from '../models/get-users-request.model';
import { UsersService } from '../users.service';
import { NewUserDialogComponent } from './create/new-user-dialog.component';

const PAGE_SIZE = 20;

@Component({
    imports: [
        CommonModule, TranslatePipe, PaginatorComponent, MaterialModule, FlexLayoutModule, ContextNotificationComponent,
        RouterModule, SearchInputComponent, StatusSelectComponent, SharedUtilityDirectivesModule
    ],
    selector: 'app-users-list',
    templateUrl: './users-list.component.html',
    styleUrls: ['./users-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersListComponent extends ListFilteredComponent implements AfterViewInit {
    readonly #usersService = inject(UsersService);
    readonly #authService = inject(AuthenticationService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #route = inject(ActivatedRoute);

    #request: GetUsersRequest;
    #sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) matSort: MatSort;
    @ViewChild(PaginatorComponent) paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) searchInputComponent: SearchInputComponent;

    displayedColumns = [
        'name',
        'surname',
        'username',
        'role',
        'status',
        'actions'
    ];

    readonly initSortCol = 'name';
    readonly initSortDir: SortDirection = 'desc';
    readonly usersPageSize = PAGE_SIZE;
    readonly userStatus = UserStatus;
    readonly userRoles = UserRoles;

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.userWrite]))
    );

    users$ = this.#usersService.usersListProvider.getUsersListData$();
    usersMetadata$ = this.#usersService.usersListProvider.getUsersListMetadata$();
    loadingData$ = this.#usersService.usersListProvider.loading$();
    isOwnerUser$ = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomeRole(loggedUser, [UserRoles.owner])));

    isAdminUser$ = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomeRole(loggedUser, [UserRoles.admin])));

    isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this.matSort);
        this.initListFilteredComponent([
            this.paginatorComponent,
            this.#sortFilterComponent,
            this.searchInputComponent
        ]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {
            limit: this.usersPageSize,
            aggs: true,
            offset: 0
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
        this.loadUsers();
    }

    updateStatus: (id: string, status: UserStatus) => Observable<void> = (id, status) =>
        this.#usersService.userDetailsProvider.updateUser(id, { status });

    openDeleteUserDialog(user: User): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_USER',
            message: 'USER.DELETE_USER_WARNING',
            messageParams: { userName: user.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this.#usersService.usersListProvider.deleteUser(user.id.toString())
                        .subscribe(() => {
                            this.#ephemeralMessageService.showSuccess({
                                msgKey: 'USER.DELETE_USER_SUCCESS',
                                msgParams: { userName: user.name }
                            });
                            this.loadUsers();
                        });
                }
            });
    }

    openNewUserDialog(): void {
        this.#matDialog.open<NewUserDialogComponent, null, number>(
            NewUserDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .subscribe(userId => {
                if (userId) {
                    this.router.navigate([userId, 'general-data'], { relativeTo: this.#route });
                }
            });
    }

    getUserLink(user: User, isAdmin: boolean, isOwner: boolean): string[] {
        let id = null;
        if (isOwner || (isAdmin && (user.role !== UserRoles.owner))) {
            id = user.id;
        }
        return id ? [id, 'general-data'] : null;
    }

    canEditUser(user: User, isAdmin: boolean, isOwner: boolean): boolean {
        if (isOwner || (isAdmin && (user.role !== UserRoles.owner && user.role !== UserRoles.admin))) {
            return true;
        }
        return false;
    }

    private loadUsers(): void {
        this.#usersService.usersListProvider.load(this.#request);
        this.#ref.detectChanges();
    }
}
