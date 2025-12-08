import { AdmonitionComponent } from '@admin-clients/admonition';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EntityUserMfaTypes, EntityUsersService, EntityUserStatus, PutEntityUser
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import {
    CopyTextComponent, DialogSize, EphemeralMessageService, IconManagerService,
    MessageDialogService, newPassword, ObMatDialogConfig, StatusSelectComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, finalize, firstValueFrom, map, Observable, switchMap, take } from 'rxjs';
import { SetPasswordDialogComponent } from '../../../entity-users-shared/set-password-dialog/set-password-dialog.component';
import { MYSELF_USER_DETAILS_TOKEN } from '../../entity-user.token';
import { ActivateMfaDialogComponent } from './activate-mfa-dialog/activate-mfa-dialog.component';
import { DeactivateMfaDialogComponent } from './deactivate-mfa-dialog/deactivate-mfa-dialog.component';

@Component({
    selector: 'ob-register-data-security',
    templateUrl: './register-data-security.component.html',
    styleUrls: ['./register-data-security.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe, FlexLayoutModule, UpperCasePipe,
        MaterialModule, CopyTextComponent, AdmonitionComponent, StatusSelectComponent
    ]
})
export class RegisterDataSecurityComponent {
    readonly #entityUsersService = inject(EntityUsersService);
    readonly #entitiesService = inject(EntitiesService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #myself = inject(MYSELF_USER_DETAILS_TOKEN);
    readonly #iconManagerSrv = inject(IconManagerService);

    readonly #isOperatorManager$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly #isSysAdmin$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]);

    readonly #$loggedUser = toSignal(this.#auth.getLoggedUser$());
    readonly #$userId = computed<number | 'myself'>(() => this.#myself ? 'myself' : this.$user().id);
    readonly #$entityIfOperatorManager = toSignal(this.#isOperatorManager$.pipe(filter(Boolean),
        switchMap(() => this.#entitiesService.getEntity$().pipe(filter(Boolean)))
    ));

    readonly #$showEntityOperatorWarning = computed(() => {
        const entity = this.#$entityIfOperatorManager();
        return !!entity && entity.settings?.types?.includes('OPERATOR') && this.$user().entity.id !== entity.id;
    });

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#entityUsersService.isEntityUserLoading$(),
        this.#entityUsersService.isApiKeyRefreshing$()
    ]));

    readonly $canEditUserStatus = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR]));

    readonly userStatusList = Object.values(EntityUserStatus);

    readonly $canCopyApiKey = computed(() => AuthenticationService.isSomeRoleInUserRoles(
        this.#$loggedUser(), [UserRoles.SYS_MGR, UserRoles.OPR_MGR, UserRoles.SYS_ANS, UserRoles.OPR_ANS]
    ) || !!this.$userApiKey());

    readonly $user = toSignal(this.#entityUsersService.getEntityUser$().pipe(filter(Boolean)));
    readonly $userApiKey = computed(() => this.$user().apikey);

    readonly $hasMfa = computed(() => this.$user().mfa_type !== EntityUserMfaTypes.disabled);

    readonly $isMfaActivationAllowed = computed(() => this.$user() && this.#$loggedUser() && (this.$user().id === this.#$loggedUser().id));

    readonly $isRefreshApiKeyAllowed =
        computed(() =>
            AuthenticationService.isSomeRoleInUserRoles(this.#$loggedUser(), [UserRoles.SYS_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR])
            || this.$isMfaActivationAllowed());

    constructor() {
        this.#iconManagerSrv.addIconDefinition(newPassword);
    }

    updateStatus(_: number, status: EntityUserStatus): Observable<void> {
        const user = this.$user();
        const userId = this.#$userId();
        const userChanges: PutEntityUser = {
            status,
            name: user.name,
            last_name: user.last_name,
            entity_id: user.entity.id
        };

        const update$ = this.#entityUsersService.updateEntityUser(userId, userChanges).pipe(
            map(() => this.#ephemeralSrv.showSuccess({ msgKey: 'USER.UPDATE_SUCCESS', msgParams: { userEmail: user.username } })),
            finalize(() => this.#entityUsersService.loadEntityUser(userId))
        );

        if (this.#$showEntityOperatorWarning()) {
            return this.#msgDialogService.showWarn({
                size: DialogSize.MEDIUM,
                title: 'TITLES.ALERT',
                message: 'USER.ENTITY_OPERATOR_WARNING',
                actionLabel: 'FORMS.ACTIONS.SAVE',
                showCancelButton: true
            }).pipe(filter(Boolean), switchMap(() => update$));
        }

        return update$;
    }

    refreshApiKey(): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.REFRESH_API_KEY',
            message: 'USER.API_KEY.REFRESH_WARNING',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#entityUsersService.refreshEntityUserApiKey(this.#$userId()))
            )
            .subscribe({
                next: () => this.#ephemeralSrv.showSuccess({ msgKey: 'USER.API_KEY.REFRESH_SUCCESS' })
            });
    }

    openMfaDialog(hasMfa: boolean = false): void {
        this.#matDialog.closeAll();
        if (!this.$isMfaActivationAllowed()) return;

        const dialogRef = hasMfa
            ? this.#matDialog.open(DeactivateMfaDialogComponent, new ObMatDialogConfig({ userId: 'myself' }))
            : this.#matDialog.open(ActivateMfaDialogComponent, new ObMatDialogConfig({ userId: 'myself' }));

        dialogRef
            .beforeClosed()
            .pipe(
                take(1),
                filter(Boolean)
            )
            .subscribe(({ success, msgKey }) => {
                if (success) {
                    this.#ephemeralSrv.showSuccess({ msgKey });
                    this.#entityUsersService.loadEntityUser(this.#$userId());
                }
            });
    }

    async openSetPasswordDialog(): Promise<void> {
        const isOperator = await firstValueFrom(this.#isOperatorManager$);
        const isSysAdmin = await firstValueFrom(this.#isSysAdmin$);
        const entityUser = this.$user();

        this.#matDialog.open(SetPasswordDialogComponent, new ObMatDialogConfig({
            isOperator: isOperator || isSysAdmin,
            userId: entityUser.id,
            userEntityId: entityUser.entity.id,
            selectionIsMyUser: isSysAdmin ? false : undefined
        })).beforeClosed().pipe(take(1), filter(Boolean))
            .subscribe(() => this.#ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' }));
    }
}
