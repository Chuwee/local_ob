import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { grantPermissionsProviders, GrantPermissionsService } from '@admin-clients/shi-panel/data-access-grant-access';
import { User, UserPermissions, UserRoles, UserStatus } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, Observable, of, throwError } from 'rxjs';
import { filter, first, skip, switchMap, take } from 'rxjs/operators';
import { AvailableUserPermissions } from '../../models/available-user-permissions.model';
import { PutUser } from '../../models/put-user.model';
import { PermissionsDescriptions, PermissionsExtraInformations } from '../../models/user-default-permissions.enum';
import { UsersService } from '../../users.service';

@Component({
    imports: [
        FormContainerComponent, ReactiveFormsModule, MatSelectModule, CommonModule, TranslatePipe,
        MatDividerModule, MatButtonToggleModule, MatProgressSpinnerModule, MatRadioModule, MatFormFieldModule,
        MatCheckboxModule, FlexLayoutModule
    ],
    selector: 'app-permissions',
    templateUrl: './permissions.component.html',
    styleUrls: ['./permissions.component.scss'],
    providers: [grantPermissionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserPermissionsComponent implements OnInit, WritingComponent {
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #usersSrv = inject(UsersService);
    readonly #auth = inject(AuthenticationService);
    readonly #grantAccessSrv = inject(GrantPermissionsService);
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    #user: User;

    get #permissionsGroup(): UntypedFormGroup {
        return this.form.get('permissionsGroup') as UntypedFormGroup;
    }

    readonly form = this.#fb.group({
        userStatus: [null as UserStatus],
        permissionsGroup: this.#fb.group({})
    });

    selectedPermission = this.#fb.control(null as { id: AvailableUserPermissions; name: string });

    readonly isInProgress$: Observable<boolean> = booleanOrMerge([
        this.#usersSrv.userDetailsProvider.loading$(),
        this.#usersSrv.permissions.isPermissionsSaving$(),
        this.#grantAccessSrv.availablePermissions.loading$()
    ]);

    readonly permissionDefaultDescriptions = PermissionsDescriptions;
    readonly permissionExtraInformations = PermissionsExtraInformations;
    readonly userStatusList = Object.values(UserStatus);
    readonly userRoles = UserRoles;
    readonly userPermissions = UserPermissions;

    readonly permissionsByRole$ = this.#grantAccessSrv.availablePermissions.getRoleAvailablePermissions$().pipe(filter(Boolean));

    readonly availableUserPermissions = Object.values(AvailableUserPermissions)
        .map(type => ({ id: type, name: `USER.PERMISSIONS_OPTS.${type.toUpperCase()}` }));

    canEditUser: boolean;

    ngOnInit(): void {
        this.#usersSrv.userDetailsProvider.getUser$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(user => {
                this.#user = user;
                this.updateFormValues(user);
            });
        this.#grantAccessSrv.availablePermissions.load(this.#user.role);
        this.initComponentModels();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const permissionsControlsArr = Object.keys(this.#permissionsGroup.controls);
            const permissionsGroup = this.form.controls.permissionsGroup as FormGroup;

            const permissionsRequest = { permissions: [] };
            permissionsControlsArr
                .filter(permission => permissionsGroup.controls[permission].value)
                .map(permission => {
                    permissionsRequest.permissions.push(permission as AvailableUserPermissions);
                });

            const userChanges: PutUser = {
                status: this.form.controls.userStatus.value
            };

            if (this.#permissionsGroup.dirty) {
                obs$.push(this.#usersSrv.permissions.savePermissions(this.#user.id, permissionsRequest));
            }

            if (this.form.controls.userStatus.dirty) {
                obs$.push(this.#usersSrv.userDetailsProvider.updateUser(this.#user.id.toString(), userChanges));
            }

            if (obs$.length) {
                return forkJoin(obs$).pipe(
                    switchMap(() => {
                        this.#ephemeralSrv.showSaveSuccess();
                        this.#usersSrv.userDetailsProvider.loadUser(this.#user.id.toString());
                        return this.#usersSrv.userDetailsProvider.getUser$().pipe(skip(1), first(Boolean));
                    }));
            } else {
                this.form.markAsPristine();
                return of(null);
            }
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    private updateFormValues(user: User): void {
        this.form.patchValue({
            userStatus: user.status
        });
        this.form.markAsPristine();
    }

    private initComponentModels(): void {
        const permissionControls = {};

        combineLatest([
            this.#usersSrv.userDetailsProvider.getUser$(),
            this.#auth.getLoggedUser$()
        ]).pipe(
            filter(data => data.every(Boolean)),
            take(1)
        ).subscribe(([user, loggedUser]) => {
            const userPermissions = this.getAssignedPermissionsArr(user.permissions);

            // When entering the section, select the first value on the available per list
            this.selectedPermission.patchValue(this.availableUserPermissions[0]);

            const hasEditPermissions = loggedUser.permissions.some(permission => permission === UserPermissions.userWrite);
            this.canEditUser = hasEditPermissions && ((loggedUser.role === UserRoles.owner) ||
                ((loggedUser.role === UserRoles.admin) && (user.role !== UserRoles.owner && user.role !== UserRoles.admin)));

            //Build the structure of controls
            this.availableUserPermissions.forEach(permission => {
                //Controls to pass to permissionsGroup
                permissionControls[permission.id] = userPermissions.includes(permission.id);
            });

            this.form.setControl('permissionsGroup', this.#fb.group(permissionControls));

            this.initFormHandlers();
        });

    }

    private initFormHandlers(): void {
        // Disable permissions when user is not allowed
        if (!this.canEditUser) {
            this.availableUserPermissions.forEach(permission => {
                const permissionFormControl = this.#permissionsGroup.get(permission.id);
                if (permissionFormControl) {
                    permissionFormControl.disable();
                }
            });
        }

        //Control disabled options depending on user role
        this.permissionsByRole$.pipe(
            take(1)
        ).subscribe(permissions => {
            const notAvailablePermissions = this.availableUserPermissions.filter(perm => !permissions.find(p => p === perm.id));
            notAvailablePermissions?.forEach(notPermitted => {
                this.#permissionsGroup.get(notPermitted.id).disable();
            });
        });
    }

    private reloadModels(): void {
        this.#usersSrv.userDetailsProvider.loadUser(this.#user.id.toString());
        this.form.markAsPristine();
    }

    private getAssignedPermissionsArr(permissions: UserPermissions[]): AvailableUserPermissions[] {
        const assignedPermissions = [];
        permissions.forEach(permission => assignedPermissions.push(permission));
        return assignedPermissions;
    }
}
