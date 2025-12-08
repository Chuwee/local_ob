import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    Customer, CustomerFriends, CustomerFriendsStatusAction, customerFriendsStatusAction, CustomersService
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    ContextNotificationComponent, DialogSize, EmptyStateTinyComponent, EphemeralMessageService, MessageDialogService,
    MessageType, ObMatDialogConfig, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, LowerCasePipe, NgTemplateOutlet } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, computed, DestroyRef, EventEmitter, inject, OnDestroy, signal, ViewContainerRef
} from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormControl } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, shareReplay, take, throwError } from 'rxjs';
import { catchError, filter, first, map, startWith } from 'rxjs/operators';
import { AddFriendFamilyDialogComponent } from './add/add-friend-family-dialog.component';

const PAGE_SIZE = 20;

@Component({
    selector: 'app-customer-friends-family',
    templateUrl: './customer-friends-family.component.html',
    styleUrls: ['./customer-friends-family.component.scss'],
    imports: [
        FormContainerComponent, AsyncPipe, SearchablePaginatedSelectionModule, TranslatePipe, MatIconButton,
        MatTooltip, MatIcon, MatMenuTrigger, MatMenu, MatMenuItem, MatCheckbox, MatColumnDef, MatHeaderCell, MatCellDef,
        EllipsifyDirective, MatCell, MatHeaderCellDef, LowerCasePipe, EmptyStateTinyComponent, NgTemplateOutlet,
        ContextNotificationComponent, MatProgressSpinner, MatButton
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerFriendsFamilyComponent implements OnDestroy {
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #customersSrv = inject(CustomersService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #viewCrf = inject(ViewContainerRef);
    readonly #router = inject(Router);

    filters: PageableFilter = { limit: PAGE_SIZE };

    readonly #$customer = toSignal(this.#customersSrv.customer.get$().pipe(first(Boolean)));
    readonly $selectedFriends = signal<CustomerFriends[]>([]);

    readonly $customerManagers = toSignal(this.#customersSrv.customerFriendOfList.getManagersLinksList$());
    readonly $isCustomerManaged = computed(() => this.#$customer()?.is_managed);
    readonly $entityCustomerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$().pipe(first(Boolean)));
    readonly $entityFriends = toSignal(this.#entitiesSrv.entityFriends.get$().pipe(filter(Boolean)));

    readonly canLoggedUserWrite$ = inject(AuthenticationService).getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => AuthenticationService.isSomeRoleInUserRoles(
                user, [UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR])
            ));

    readonly $displayedColumns = computed(() => this.$entityCustomerTypes() && this.$entityCustomerTypes().length > 0
        ? ['active', 'name', 'relation', 'customer_types', 'member_id', 'status', 'actions']
        : ['active', 'name', 'relation', 'member_id', 'status', 'actions']);

    readonly isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly isLoading$ = booleanOrMerge([
        this.#customersSrv.customer.loading$(),
        this.#customersSrv.customerFriendsList.loading$(),
        this.#customersSrv.customerFriendOfList.loading$(),
        this.#customersSrv.customerFriend.loading$(),
        this.#entitiesSrv.entityCustomerTypes.inProgress$()
    ]);

    readonly customerFriendsStatusAction = customerFriendsStatusAction;

    readonly pageSize = PAGE_SIZE;
    readonly showSelectedOnlyClick = new EventEmitter<boolean>();
    readonly allSelectedClick = new EventEmitter<boolean>();
    selected = new FormControl<CustomerFriends[]>([]);

    readonly selectedOnly$ = toSignal(this.showSelectedOnlyClick.pipe(
        startWith(false),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    ));

    readonly allSelected$: Observable<boolean> = this.allSelectedClick.pipe(
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly totalCustomerFriends$ = this.#customersSrv.customerFriendsList.getMetaData$()
        .pipe(map(metadata => metadata?.total || 0));

    readonly $totalCustomerFriendsData = toSignal(this.#customersSrv.customerFriendsList.getData$().pipe(filter(Boolean)));
    readonly $customerFriendsData = computed(() => this.selectedOnly$() ? this.$selectedFriends() : this.$totalCustomerFriendsData());
    readonly data$ = toObservable(this.$customerFriendsData);

    readonly $totalCustomerFriendsMetadata = toSignal(this.#customersSrv.customerFriendsList.getMetaData$().pipe(filter(Boolean)));
    readonly $customerFriendsMetadata = computed(() => this.selectedOnly$() ?
        this.$selectedFriends().map(() => new Metadata({ total: this.$selectedFriends()?.length, limit: 999, offset: 0 })) :
        this.$totalCustomerFriendsMetadata());

    readonly metadata$ = toObservable(this.$customerFriendsMetadata);

    constructor() {
        if (this.$isCustomerManaged()) {
            this.#customersSrv.customerFriendOfList.load(this.#$customer().id);
        }
        this.#entitiesSrv.entityCustomerTypes.load(this.#$customer().entity.id);
        this.#entitiesSrv.entityFriends.load(this.#$customer().entity.id);

        this.selected.valueChanges.pipe(
            startWith(this.selected.value),
            map(selected => {
                if (!selected || selected.length === 0) {
                    this.showSelectedOnlyClick.next(false);
                }
                return selected;
            }),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        ).subscribe(selectedFriends => {
            this.$selectedFriends.set(selectedFriends);
        }
        );

        this.allSelected$.pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(allSelected => {
            this.selected.setValue(allSelected ? this.$customerFriendsData() : []);
            this.selected.markAsDirty();
        });

    }

    ngOnDestroy(): void {
        this.#customersSrv.customerFriendsList.clear();
        this.#customersSrv.customerFriendOfList.clear();
        this.#entitiesSrv.entityFriends.clear();
    }

    get selectedFriends(): number {
        return this.selected?.value?.length || 0;
    }

    clickShowSelected(): void {
        this.showSelectedOnlyClick.emit(!this.selectedOnly$());
    }

    openAddFriendAndFamilyDialog(): void {
        const customer = this.#$customer();
        this.#matDialog.open(
            AddFriendFamilyDialogComponent,
            new ObMatDialogConfig({
                customerId: customer.id,
                customerEntityId: customer.entity?.id
                // TODO: uncomment when implemented in customers project
                //friendsRelationType: this.$entityFriends()?.friends_relation_mode || 'BIDIRECTIONAL'
            }, this.#viewCrf)
        ).beforeClosed().subscribe();
    }

    loadCustomerFriendsList(filters: Partial<PageableFilter>): void {
        this.filters = { ...this.filters, ...filters };
        const customer = this.#$customer();
        this.#customersSrv.customerFriendsList.load(customer.id, this.filters);
        this.#customersSrv.customerFriendsList.getData$().pipe(
            take(1)
        ).subscribe(() => this.showSelectedOnlyClick.emit(this.selectedOnly$()));
    }

    deleteCustomerFriend(friend: CustomerFriends): void {
        const customer = this.#$customer();
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'CUSTOMER.DELETE_CUSTOMER_' + friend.relation + '_WARNING.TITLE',
            message: 'CUSTOMER.DELETE_CUSTOMER_' + friend.relation + '_WARNING.INFO',
            messageParams: { customer: friend.name },
            actionLabel: 'FORMS.ACTIONS.UNLINK',
            showCancelButton: true
        }).subscribe(isAccepted => {
            if (isAccepted) {
                this.#customersSrv.customerFriend.delete(customer.id, friend.id)
                    .pipe(catchError(err => {
                        this.#customersSrv.customerFriendsList.load(customer.id);
                        return throwError(err);
                    }))
                    .subscribe(() =>
                        this.#customerFriendActionSuccess(customer, 'CUSTOMER.DELETE_CUSTOMER_FRIEND_SUCCESS',
                            { customer: `${friend.name} ${friend.surname}` }
                        ));
            }
        });
    }

    changeStatusCustomerFriend(friend: CustomerFriends, action: CustomerFriendsStatusAction): void {
        const customer = this.#$customer();
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'CUSTOMER.' + action + '_CUSTOMER_FRIEND_WARNING.TITLE',
            message: 'CUSTOMER.' + action + '_CUSTOMER_FRIEND_WARNING.INFO',
            actionLabel: 'FORMS.ACTIONS.' + action,
            showCancelButton: true
        }).subscribe(isAccepted => {
            if (isAccepted) {
                this.#customersSrv.customerFriend.changeStatus(friend.id, customer.entity.id.toString(), action)
                    .pipe(catchError(err => {
                        this.#customersSrv.customerFriendsList.load(customer.id);
                        return throwError(err);
                    }))
                    .subscribe(() =>
                        this.#customerFriendActionSuccess(customer, 'CUSTOMER.' + action + '_CUSTOMER_FRIEND_SUCCESS',
                            { customer: `${friend.name} ${friend.surname}` }
                        ));
            }
        });
    }

    deleteMultipleCustomerFriends(): void {
        const customer = this.#$customer();
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'CUSTOMER.DELETE_CUSTOMER_MULTIPLE_FRIENDS_WARNING.TITLE',
            message: 'CUSTOMER.DELETE_CUSTOMER_MULTIPLE_FRIENDS_WARNING.INFO',
            actionLabel: 'FORMS.ACTIONS.UNLINK',
            showCancelButton: true
        }).subscribe(isAccepted => {
            if (isAccepted) {
                const friends = this.selected.value.map(friend => friend.id);
                this.#customersSrv.customerFriendsList.delete(customer.id, friends)
                    .pipe(catchError(err => {
                        this.#customersSrv.customerFriendsList.load(customer.id);
                        return throwError(err);
                    }))
                    .subscribe(() =>
                        this.#customerFriendActionSuccess(customer, 'CUSTOMER.DELETE_CUSTOMER_MULTIPLE_FRIENDS_SUCCESS', {})
                    );
            }
        });
    }

    changeStatusMultipleCustomerFriend(action: CustomerFriendsStatusAction): void {
        const customer = this.#$customer();
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'CUSTOMER.' + action + '_CUSTOMER_MULTIPLE_FRIENDS_WARNING.TITLE',
            message: 'CUSTOMER.' + action + '_CUSTOMER_MULTIPLE_FRIENDS_WARNING.INFO',
            actionLabel: 'FORMS.ACTIONS.' + action,
            showCancelButton: true
        }).subscribe(isAccepted => {
            if (isAccepted) {
                const friends = this.selected.value.map(friend => friend.id);
                this.#customersSrv.customerFriendsList.changeStatus(friends, action)
                    .pipe(catchError(err => {
                        this.#customersSrv.customerFriendsList.load(customer.id);
                        return throwError(err);
                    }))
                    .subscribe(() =>
                        this.#customerFriendActionSuccess(customer, 'CUSTOMER.' + action + '_CUSTOMER_MULTIPLE_FRIENDS_SUCCESS', {})
                    );
            }
        });
    }

    goToCustomer(friendId: string): void {
        this.#router.navigate(['/customers', friendId], { queryParams: { entityId: this.#$customer().entity.id } });
    }

    handleManagerClick(event: MouseEvent): void {
        const target = event.target as HTMLElement;
        if (target.tagName.toLowerCase() === 'a') {
            event.preventDefault();
            this.goToCustomer(target.classList[1]);
        }
    }

    #customerFriendActionSuccess(customer: Customer, msgKey: string, msgParams: unknown): void {
        this.#ephemeralMessageSrv.show({ type: MessageType.success, msgKey, msgParams });
        this.#customersSrv.customerFriendsList.load(customer.id);
    }
}
