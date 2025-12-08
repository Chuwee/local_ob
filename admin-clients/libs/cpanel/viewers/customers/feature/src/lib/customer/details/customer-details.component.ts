import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { Customer, CustomersService, CustomerStatus } from '@admin-clients/cpanel-viewers-customers-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService,
    GoBackComponent,
    MessageDialogConfig,
    MessageDialogService,
    NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { filter, first, map, mapTo, shareReplay, switchMap, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-customer-details',
    imports: [
        MatButtonModule, MatIconModule, AsyncPipe, GoBackComponent, MatTooltipModule, FlexLayoutModule,
        NavTabsMenuComponent, RouterOutlet, EllipsifyDirective
    ],
    templateUrl: './customer-details.component.html',
    styleUrls: ['./customer-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerDetailsComponent implements OnInit, OnDestroy {
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #auth = inject(AuthenticationService);
    readonly #translateSrv = inject(TranslateService);
    readonly #msgDialog = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #customersSrv = inject(CustomersService);
    readonly #entitySrv = inject(EntitiesBaseService);
    readonly #destroyRef = inject(DestroyRef);

    #lastEntityId: number;
    customer$: Observable<Customer>;
    canLoggedUserWrite$: Observable<boolean>;
    isLoadingOrSaving$: Observable<boolean>;

    readonly $isLoyaltyPointsEnabled = toSignal(this.#entitySrv.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => entity.settings.allow_loyalty_points)
        ));

    readonly $isFriendsEnabled = toSignal(this.#entitySrv.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => entity.settings.allow_friends)
        ));

    ngOnInit(): void {
        this.model();
        this.setEntityIdQueryParam();
    }

    ngOnDestroy(): void {
        this.#customersSrv.customer.clear();
    }

    lockButtonHandler(customerStatus: CustomerStatus): void {
        if (customerStatus === CustomerStatus.locked) {
            this.openUnLockCustomerDialog();
        } else {
            this.openLockCustomerDialog();
        }
    }

    getLockButtonText(customerStatus: CustomerStatus): string {
        if (customerStatus === CustomerStatus.locked) {
            return this.#translateSrv.instant('CUSTOMER.UNLOCK_CUSTOMER_BTN');
        } else {
            return this.#translateSrv.instant('CUSTOMER.LOCK_CUSTOMER_BTN');
        }
    }

    private setEntityIdQueryParam(): void {
        this.#route.queryParamMap
            .pipe(
                switchMap(queryParamMap => this.customer$
                    .pipe(
                        take(1),
                        tap(customer => {
                            if (!queryParamMap.has('entityId')) {
                                this.#router.navigate(
                                    [],
                                    {
                                        queryParams: { entityId: customer.entity?.id },
                                        relativeTo: this.#route,
                                        queryParamsHandling: 'merge',
                                        skipLocationChange: true
                                    }
                                );
                            }
                            if (customer.entity?.id && this.#lastEntityId !== Number(customer.entity.id)) {
                                this.#lastEntityId = Number(customer.entity.id);
                                this.#entitySrv.loadEntity(Number(customer.entity.id));
                            }
                        })
                    )
                ),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe();
    }

    private model(): void {
        // Loading
        this.isLoadingOrSaving$ = this.#customersSrv.customer.loading$();

        this.customer$ = this.#customersSrv.customer.get$()
            .pipe(
                filter(customer => customer !== null),
                shareReplay(1)
            );

        // Logged user
        const loggedUser$ = this.#auth.getLoggedUser$()
            .pipe(
                first(user => user !== null)
            );
        const writingRoles = [UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR];
        // check if logged user has write permissions
        this.canLoggedUserWrite$ = loggedUser$
            .pipe(
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, writingRoles)),
                shareReplay(1)
            );
    }

    private openLockCustomerDialog(): void {
        this.canCustomerFormChangesBeDismissed()
            .pipe(
                switchMap(() => this.customer$),
                take(1),
                switchMap(customer => this.canCustomerBeChanged({
                    size: DialogSize.MEDIUM,
                    title: 'TITLES.NOTICE',
                    message: 'CUSTOMER.LOCK_CUSTOMER_WARNING',
                    actionLabel: 'FORMS.ACTIONS.YES',
                    messageParams: { name: customer.name },
                    showCancelButton: true
                }).pipe(
                    switchMap(() => this.#customersSrv.customerLockUnLock.lock(customer.id, customer.entity?.id?.toString())
                        .pipe(
                            tap(() =>
                                this.customerChangeSuccess(
                                    customer,
                                    'CUSTOMER.LOCK_CUSTOMER_SUCCESS',
                                    { name: customer.name })
                            )
                        )))
                )
            ).subscribe();
    }

    private openUnLockCustomerDialog(): void {
        this.canCustomerFormChangesBeDismissed()
            .pipe(
                switchMap(() => this.customer$),
                take(1),
                switchMap(customer => this.canCustomerBeChanged({
                    size: DialogSize.SMALL,
                    title: 'TITLES.NOTICE',
                    message: 'CUSTOMER.UNLOCK_CUSTOMER_WARNING',
                    messageParams: { name: customer.name },
                    actionLabel: 'FORMS.ACTIONS.YES',
                    showCancelButton: true
                }).pipe(
                    switchMap(() => this.#customersSrv.customerLockUnLock.unlock(customer.id, customer.entity?.id?.toString())
                        .pipe(
                            tap(() =>
                                this.customerChangeSuccess(
                                    customer,
                                    'CUSTOMER.UNLOCK_CUSTOMER_SUCCESS',
                                    { name: customer.name })
                            )
                        )))
                )
            ).subscribe();
    }

    private canCustomerFormChangesBeDismissed(): Observable<void> {
        return this.#customersSrv.customerForm.get$()
            .pipe(
                take(1),
                switchMap(form => {
                    if (form?.dirty) {
                        return this.#msgDialog.showWarn({
                            size: DialogSize.MEDIUM,
                            title: 'TITLES.NOTICE',
                            message: 'FORMS.ACTIONS.UNSAVED_CHANGES',
                            actionLabel: 'FORMS.ACTIONS.YES'
                        });
                    } else {
                        return of(true);
                    }
                }),
                take(1),
                filter(Boolean),
                mapTo(null)
            );
    }

    private canCustomerBeChanged(messageDialogConfig: MessageDialogConfig): Observable<void> {
        return this.#msgDialog.showWarn(messageDialogConfig)
            .pipe(
                take(1),
                filter(success => !!success),
                map(() => null)
            );
    }

    private customerChangeSuccess(customer: Customer, msgKey: string, msgParams: unknown): void {
        this.#ephemeralMessageService.showSuccess({ msgKey, msgParams });
        this.#customersSrv.customer.load(customer.id, customer.entity?.id?.toString());
    }
}
