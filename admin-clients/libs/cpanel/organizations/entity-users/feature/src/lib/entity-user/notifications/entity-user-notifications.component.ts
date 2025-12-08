import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    EntityUserNotification, EntityUserStatus,
    EntityUsersService, PutEntityUser, EntityUserNotificationTypes,
    EntityUser, channelMgrNotifications, eventMgrNotifications
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { ContextNotificationComponent, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, throwError } from 'rxjs';
import { filter, map, shareReplay, tap } from 'rxjs/operators';
import { MYSELF_USER_DETAILS_TOKEN } from '../entity-user.token';

@Component({
    selector: 'ob-entity-user-notifications',
    templateUrl: './entity-user-notifications.component.html',
    styleUrls: ['./entity-user-notifications.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, MaterialModule, ReactiveFormsModule, CommonModule,
        TranslatePipe, ContextNotificationComponent
    ]
})
export class EntityUserNotificationsComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _myself = inject(MYSELF_USER_DETAILS_TOKEN);
    private _userId: number | 'myself' = this._myself ? 'myself' : null as number;
    readonly $user = toSignal(this._entityUsersService.getEntityUser$().pipe(filter(Boolean)));
    readonly #$loggedUser = toSignal(this._auth.getLoggedUser$());
    readonly $editLoggedUser = computed(() => this.$user() && this.#$loggedUser() && (this.$user().id === this.#$loggedUser().id));
    form: UntypedFormGroup;
    isOperatorUser$: Observable<boolean>;
    canEditUserStatus$: Observable<boolean>;
    isInProgress$: Observable<boolean>;
    userNotifications$: Observable<EntityUserNotification[]>;
    channelMgrNotifications$: Observable<EntityUserNotification[]>;
    eventMgrNotifications$: Observable<EntityUserNotification[]>;
    userStatusList = Object.values(EntityUserStatus);

    constructor(
        private _fb: UntypedFormBuilder,
        private _entityUsersService: EntityUsersService,
        private _auth: AuthenticationService,
        private _ephemeralSrv: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        this.initForm();
        this._entityUsersService.getEntityUser$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this._destroyRef)
            )
            .subscribe(user => {
                if (this._userId !== 'myself') {
                    this._userId = user.id;
                }
                this._entityUsersService.userNotifications.load(this._userId);
                this.updateFormValues(user);
            });

        this.initComponentModels();
    }

    ngOnDestroy(): void {
        this._entityUsersService.userNotifications.clear();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const fv = this.form.value;
            const notificationsGroupValues = fv.notificationsGroup;
            const notificationsGroupKeys = Object.keys(fv.notificationsGroup);

            const userStatusChanges: Partial<PutEntityUser> = {
                status: fv.userStatus
            };
            obs$.push(this._entityUsersService.updateEntityUser(this._userId, userStatusChanges));

            const request: EntityUserNotification[] = notificationsGroupKeys.map(key => ({
                type: key as EntityUserNotificationTypes,
                enable: notificationsGroupValues[key]
            }));
            obs$.push(this._entityUsersService.userNotifications.update(this._userId, request));

            return forkJoin(obs$).pipe(tap(() => this._ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    private initForm(): void {
        this.form = this._fb.group({
            userStatus: [null, Validators.required]
        });
    }

    private updateFormValues(user: EntityUser): void {
        this.form.patchValue({
            userStatus: user.status
        });
        this.form.markAsPristine();
    }

    private initComponentModels(): void {
        this.isOperatorUser$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
        this.canEditUserStatus$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR]);

        this.userNotifications$ = this._entityUsersService.userNotifications.get$()
            .pipe(
                filter(notifications => !!notifications),
                tap(notifications => {
                    const notificationControls = {};
                    notifications.forEach(notification => notificationControls[notification.type] = notification.enable);
                    this.form.setControl('notificationsGroup', this._fb.group(notificationControls));
                }),
                shareReplay(1)
            );
        this.channelMgrNotifications$ = this._entityUsersService.userNotifications.get$()
            .pipe(map(notifications => notifications.filter(notification => channelMgrNotifications.includes(notification.type))));

        this.eventMgrNotifications$ = this._entityUsersService.userNotifications.get$()
            .pipe(map(notifications => notifications.filter(notification => eventMgrNotifications.includes(notification.type))));

        this.isInProgress$ = booleanOrMerge([
            this._entityUsersService.isEntityUserLoading$(),
            this._entityUsersService.isEntityUserSaving$(),
            this._entityUsersService.userNotifications.inProgress$()
        ]);
    }

    private reloadModels(): void {
        this._entityUsersService.loadEntityUser(this._userId);
        this.form.markAsPristine();
    }

}
