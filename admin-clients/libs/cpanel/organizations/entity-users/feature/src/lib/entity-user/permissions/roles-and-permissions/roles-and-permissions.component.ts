import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    EntityUserRoles, EntityUserPermissions, EntityUser, EntityUserRole, RolesDefaultPermissions,
    ModulesAvailable, BiUserTypes, EntityUserStatus, EntityUsersService, taqUserPermissionsDependencies,
    entityMgrRoleDependencies, entityMgrPermissionsDependencies, operatorMgrPermissionsDependencies
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { GetProducersRequest, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import {
    MessageDialogService, EphemeralMessageService, DialogSize, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, FormGroup, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, concat, Observable, of, Subject, throwError } from 'rxjs';
import { bufferCount, debounceTime, filter, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs/operators';
import { IsBiUserPipe } from '../../pipes/bi-user-role.pipe';
import { IsProducerUserPipe } from '../../pipes/producer-user-role.pipe';

@Component({
    selector: 'ob-roles-and-permissions',
    templateUrl: './roles-and-permissions.component.html',
    styleUrls: ['./roles-and-permissions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, MaterialModule, ReactiveFormsModule, TranslatePipe,
        CommonModule, IsBiUserPipe, IsProducerUserPipe, FlexLayoutModule, SelectServerSearchComponent
    ]
})
export class RolesAndPermissionsComponent implements OnInit, OnDestroy, WritingComponent {
    #msgDialogService = inject(MessageDialogService);
    #ephemeralSrv = inject(EphemeralMessageService);
    #usersSrv = inject(EntityUsersService);
    #producersSrv = inject(ProducersService);
    #auth = inject(AuthenticationService);
    #fb = inject(UntypedFormBuilder);

    #onDestroy = new Subject<void>();
    #userId: number;
    #entityId: number;
    #userRolesDisplayOrder = Object.values(EntityUserRoles);
    #loadedUserPermissions: EntityUserPermissions[];
    #loadedUserRoles: EntityUserRoles[];

    get #rolesGroup(): UntypedFormGroup {
        return this.form.get('rolesGroup') as UntypedFormGroup;
    }

    get #permissionsGroup(): UntypedFormGroup {
        return this.form.get('permissionsGroup') as UntypedFormGroup;
    }

    form: UntypedFormGroup;
    selectedRole: FormControl<{ code: EntityUserRoles; permissions?: EntityUserPermissions[] }>;
    isOperatorUser$: Observable<boolean>;
    canEditUserStatus$: Observable<boolean>;
    entityUser$: Observable<EntityUser>;
    isInProgress$: Observable<boolean>;
    availableUserRoles$: Observable<EntityUserRole[]>;
    rolesDefaultPermissions = RolesDefaultPermissions;
    modulesAvailable = ModulesAvailable;
    biTypes = Object.values(BiUserTypes);
    userStatusList = Object.values(EntityUserStatus);
    isBiInheritUser$: Observable<boolean>;

    readonly entityUserRoles = EntityUserRoles;
    readonly producerRole = EntityUserRoles.PRD_ANS;
    readonly feverReportingRole = EntityUserRoles.FV_REPORTING;
    readonly entityUserPermissions = EntityUserPermissions;
    readonly producers$ = this.#producersSrv.producersList.getData$();
    readonly moreProducersAvailable$ = this.#producersSrv.producersList.getMetadata$()
        .pipe(map(metadata => !!metadata && metadata.offset + metadata.limit < metadata.total));

    readonly isSysAdmin$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]);

    ngOnInit(): void {
        this.initForm();

        this.#usersSrv.getEntityUser$()
            .pipe(
                filter(user => !!user),
                takeUntil(this.#onDestroy)
            )
            .subscribe(user => {
                this.#entityId = user.entity.id;
                this.#userId = user.id;
                this.updateFormValues(user);
                this.#usersSrv.loadUserRoles(user.id);
                this.#usersSrv.loadAvailableUserRoles(user.id);
            });

        this.initComponentModels();
    }

    ngOnDestroy(): void {
        this.#usersSrv.clearUserRoles();
        this.#producersSrv.producersList.clear();
        this.#usersSrv.clearAvailableUserRoles();
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    loadProducers(q?: string, nextPage?: boolean): void {
        const request: GetProducersRequest = {
            q,
            entityId: this.#entityId,
            limit: 100
        };
        if (!nextPage) {
            this.#producersSrv.producersList.load(request);
        } else {
            this.#producersSrv.producersList.loadMore(request);
        }
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            let obs$: Observable<void>[] = [];
            const rolesControlsArr = Object.keys(this.#rolesGroup.controls).filter(role => role !== EntityUserRoles.BI_USR);
            const rolesGroup = this.form.controls['rolesGroup'] as FormGroup;
            const permissionsValue = this.#permissionsGroup.getRawValue();

            const rolesRequest = rolesControlsArr
                .filter(role => rolesGroup.controls[role].value)
                .map(role => {
                    const roleReq: EntityUserRole = {
                        code: role as EntityUserRoles
                    };
                    if (role === EntityUserRoles.CNL_TAQ) {
                        roleReq.permissions = taqUserPermissionsDependencies.filter(permission => permissionsValue[permission]);
                    } else if (role === EntityUserRoles.CNL_INT) {
                        roleReq.permissions = [];
                        if (permissionsValue[EntityUserPermissions.secondMarket]) {
                            roleReq.permissions.push(EntityUserPermissions.secondMarket);
                        }
                    } else if (role === EntityUserRoles.OPR_MGR && permissionsValue[EntityUserPermissions.automaticSales]) {
                        roleReq.permissions = [EntityUserPermissions.automaticSales];
                    } else if (role === EntityUserRoles.PRD_ANS && this.form.controls['producer'].value) {
                        roleReq.additional_properties = { producer_ids: [this.form.controls['producer'].value.id] };
                    }
                    return roleReq;
                });
            if (this.#rolesGroup.dirty || this.#permissionsGroup.dirty || this.form.controls['producer'].dirty) {
                obs$.push(this.#usersSrv.rolesAndPermissions.saveRoles(this.#userId, rolesRequest));
            }

            // BI role handling
            const biRoleCtrl = this.#rolesGroup.get(this.entityUserRoles.BI_USR);
            const prdAnsCtrl = this.#rolesGroup.get(this.entityUserRoles.PRD_ANS);
            const biPermissionsCtrl = this.#permissionsGroup.get('biPermissionControl');

            if (biRoleCtrl?.dirty) {
                if (biRoleCtrl?.value) {
                    if (!this.#loadedUserRoles.includes(EntityUserRoles.BI_USR)) {
                        obs$.push(this.#usersSrv.rolesAndPermissions.saveRoleAndPermissions(this.#userId, {
                            code: EntityUserRoles.BI_USR,
                            permissions: [biPermissionsCtrl.value]
                        }));
                    } else {
                        biRoleCtrl.markAsPristine();
                    }
                } else {
                    if (this.#loadedUserRoles.includes(EntityUserRoles.BI_USR)) {
                        if (this.#loadedUserPermissions.some(permission => permission === EntityUserPermissions.mobile)) {
                            obs$ = [
                                this.#msgDialogService.showWarn({
                                    size: DialogSize.SMALL,
                                    title: 'TITLES.DELETE_BI_USR_WITH_MOBILE',
                                    message: 'BI_REPORTS.DELETE_BI_USR_MOBILE_WARN',
                                    actionLabel: 'FORMS.ACTIONS.DELETE',
                                    showCancelButton: true
                                })
                                    .pipe(
                                        switchMap(accepted => {
                                            if (accepted) {
                                                return this.#usersSrv.rolesAndPermissions.deleteRoleAndPermissions(
                                                    this.#userId, EntityUserRoles.BI_USR);
                                            } else if (obs$.length > 1) {
                                                return of(null);
                                            } else {
                                                return of();
                                            }
                                        })
                                    ), ...obs$];
                        } else {
                            obs$ = [
                                this.#usersSrv.rolesAndPermissions.deleteRoleAndPermissions(this.#userId, EntityUserRoles.BI_USR), ...obs$];
                        }
                    } else {
                        biRoleCtrl.markAsPristine();
                    }
                }
            } else if (biPermissionsCtrl?.dirty) {
                const permissionToDelete = biPermissionsCtrl.value === EntityUserPermissions.basic
                    ? EntityUserPermissions.advanced
                    : EntityUserPermissions.basic;

                const saveObs$ = this.#usersSrv.rolesAndPermissions.saveRolePermission(
                    this.#userId, EntityUserRoles.BI_USR, biPermissionsCtrl.value);
                const deleteObs$ = this.#usersSrv.rolesAndPermissions.deleteRolePermission(
                    this.#userId, EntityUserRoles.BI_USR, permissionToDelete);

                const doSave = !this.#loadedUserPermissions.includes(biPermissionsCtrl.value);
                const doDelete = this.#loadedUserPermissions.includes(permissionToDelete);

                if (doSave && doDelete) {
                    if (prdAnsCtrl?.dirty && !prdAnsCtrl?.value && this.#loadedUserRoles.includes(EntityUserRoles.PRD_ANS)) {
                        obs$.push(concat(deleteObs$, saveObs$));
                    } else {
                        obs$ = [deleteObs$, saveObs$, ...obs$];
                    }
                } else if (doSave) {
                    if (prdAnsCtrl?.dirty && !prdAnsCtrl?.value && this.#loadedUserRoles.includes(EntityUserRoles.PRD_ANS)) {
                        obs$.push(saveObs$);
                    } else {
                        obs$ = [saveObs$, ...obs$];
                    }
                } else {
                    if (prdAnsCtrl?.dirty && !prdAnsCtrl?.value && this.#loadedUserRoles.includes(EntityUserRoles.PRD_ANS)) {
                        obs$.push(deleteObs$);
                    } else {
                        obs$ = [deleteObs$, ...obs$];
                    }
                }
            }

            if (obs$.length) {
                return concat(...obs$).pipe(
                    bufferCount(obs$.length),
                    tap(() => this.#ephemeralSrv.showSaveSuccess()));
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

    private initForm(): void {
        this.form = this.#fb.group({
            userStatus: [null, Validators.required],
            producer: [{ value: null, disabled: true }, Validators.required]
        });
        this.selectedRole = this.#fb.control(null);
    }

    private updateFormValues(user: EntityUser): void {
        this.form.patchValue({
            userStatus: user.status
        });
        this.form.markAsPristine();
    }

    private initComponentModels(): void {
        this.isOperatorUser$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
        this.canEditUserStatus$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR]);
        this.entityUser$ = this.#usersSrv.getEntityUser$().pipe(filter(user => !!user));
        this.isBiInheritUser$ = this.#usersSrv.getUserRoles$()
            .pipe(
                filter(roles => !!roles),
                map(roles => roles.some(role => role.permissions?.includes(EntityUserPermissions.biInherit)))
            );
        this.#usersSrv.getUserRoles$().pipe(
            filter(roles => !!roles),
            map(roles => roles.filter(role => role.code === this.producerRole)?.at(0)?.additional_properties?.producer_ids?.at(0)),
            filter(producerId => !!producerId),
            switchMap(producerId => this.#producersSrv.getProducersNames$([producerId])),
            takeUntil(this.#onDestroy)
        ).subscribe(producer => {
            this.form.controls['producer'].reset(producer?.at(0));
        });
        this.availableUserRoles$ = combineLatest([
            this.#usersSrv.getAvailableUserRoles$().pipe(filter(availableRoles => !!availableRoles)),
            this.#usersSrv.getUserRoles$().pipe(filter(userRoles => !!userRoles))
        ]).pipe(
            debounceTime(500),
            map(([availableRoles, userRoles]) => [this.sortAvailableUserRoles(availableRoles), userRoles]),
            tap(([availableRoles, userRoles]) => {
                this.#loadedUserPermissions = userRoles.flatMap(role => role.permissions);
                this.#loadedUserRoles = userRoles.flatMap(role => role.code);
                const userRolesCodes = userRoles.map(userRole => userRole.code);
                const userPermissions = this.getAssignedPermissionsArr(userRoles);
                const roleControls = {};
                const permissionControls: Record<string, unknown> = {};
                //When enter the section, select the first value on the available roles list
                this.selectedRole.patchValue(availableRoles[0]);
                //Build the structure of controls
                availableRoles.forEach(role => {
                    //Controls to pass to rolesGroup
                    roleControls[role.code] = userRolesCodes.includes(role.code);
                    //Controls to pass to permissionsGroup
                    if (role.code !== EntityUserRoles.BI_USR) {
                        role.permissions?.forEach(permission => {
                            permissionControls[permission] = userPermissions.includes(permission);
                        });
                    } else {
                        const biKey = 'biPermissionControl';
                        permissionControls[biKey] = null;
                        if (userPermissions.includes(EntityUserPermissions.basic)) {
                            permissionControls[biKey] = EntityUserPermissions.basic;
                        } else if (userPermissions.includes(EntityUserPermissions.advanced)) {
                            permissionControls[biKey] = EntityUserPermissions.advanced;
                        }
                    }
                });
                this.form.setControl('rolesGroup', this.#fb.group(roleControls));
                this.form.setControl('permissionsGroup', this.#fb.group(permissionControls));

                // Bi permissions are only required if no permission is previously set (mobile could be externally set)
                if (!userRoles.find(role => role.code === EntityUserRoles.BI_USR)?.permissions?.length) {
                    this.form.get('permissionsGroup.biPermissionControl').setValidators(Validators.required);
                }

                if (userRolesCodes.includes(EntityUserRoles.BI_USR) && userPermissions.includes(EntityUserPermissions.biInherit)) {
                    this.#rolesGroup.get(EntityUserRoles.BI_USR).disable();
                    this.#permissionsGroup.get('biPermissionControl').disable();
                }

                this.initFormHandlers();
            }),
            map(([availableRoles]) => availableRoles),
            shareReplay(1)
        );

        this.isInProgress$ = booleanOrMerge([
            this.#usersSrv.isEntityUserLoading$(),
            this.#usersSrv.isUserRolesLoading$(),
            this.#usersSrv.isAvailableUserRolesLoading$(),
            this.#usersSrv.rolesAndPermissions.isRoleAndPermissionsSaving$()
        ]);
    }

    private initFormHandlers(): void {
        if (this.#rolesGroup.get(this.entityUserRoles.ENT_MGR)) {
            //Check initial value of ENT_MGR role and disable dependant roles if ENT_MGR is setted (true)
            this.checkEntityMgrRoleInitialValue(this.#rolesGroup.get(this.entityUserRoles.ENT_MGR).value);

            //If ENT_MGR value changes, dependant roles/permissions must change accordingly and appear disabled if setted (true)
            this.#rolesGroup.get(this.entityUserRoles.ENT_MGR).valueChanges
                .pipe(takeUntil(this.#onDestroy))
                .subscribe(isChecked => {
                    entityMgrRoleDependencies.forEach(role => {
                        const roleFormControl = this.#rolesGroup.get(role);
                        if (roleFormControl) {
                            roleFormControl.patchValue(isChecked);
                            if (isChecked) {
                                roleFormControl.disable();
                            } else {
                                roleFormControl.enable();
                                roleFormControl.markAsDirty();
                            }
                        }
                    });
                    entityMgrPermissionsDependencies.forEach(permission => {
                        const permissionFormControl = this.#permissionsGroup.get(permission);
                        if (permissionFormControl && isChecked) {
                            permissionFormControl.disable();
                        }
                    });
                });
        }

        if (this.#rolesGroup.get(this.entityUserRoles.OPR_MGR)) {
            //If initial OPR_MGR value is unsetted (false), disable permissions
            if (!this.#rolesGroup.get(this.entityUserRoles.OPR_MGR).value) {
                operatorMgrPermissionsDependencies.forEach(permission => this.#permissionsGroup.get(permission)?.disable());
            }

            //If OPR_MGR value changes, its permissions also change
            this.#rolesGroup.get(this.entityUserRoles.OPR_MGR).valueChanges
                .pipe(takeUntil(this.#onDestroy))
                .subscribe(roleIsChecked => {
                    if (roleIsChecked) {
                        operatorMgrPermissionsDependencies.forEach(permission => this.#permissionsGroup.get(permission)?.enable());
                    } else {
                        operatorMgrPermissionsDependencies.forEach(permission => this.#permissionsGroup.get(permission)?.disable());
                    }
                });
        }

        if (this.#rolesGroup.get(this.entityUserRoles.CNL_TAQ)) {
            //If initial CNL_TAQ value is unsetted (false), disable permissions
            if (!this.#rolesGroup.get(this.entityUserRoles.CNL_TAQ).value) {
                taqUserPermissionsDependencies.forEach(permission => this.#permissionsGroup.get(permission).disable());
            }

            //If CNL_TAQ value changes, its permissions also change
            this.#rolesGroup.get(this.entityUserRoles.CNL_TAQ).valueChanges
                .pipe(takeUntil(this.#onDestroy))
                .subscribe(roleIsChecked => {
                    taqUserPermissionsDependencies.forEach(permission => {
                        const permissionFormControl = this.#permissionsGroup.get(permission);
                        if (permissionFormControl) {
                            if (roleIsChecked) {
                                permissionFormControl.enable();
                            } else if (!roleIsChecked) {
                                permissionFormControl.disable();
                            }
                            if (permissionFormControl.value !== roleIsChecked) {
                                permissionFormControl.patchValue(roleIsChecked);
                            }
                        }
                    });
                });
        }

        if (this.#rolesGroup.get(this.entityUserRoles.PRD_ANS)) {
            if (this.#rolesGroup.get(this.entityUserRoles.PRD_ANS).value) {
                this.form.controls['producer'].enable();
                Object.values(this.#rolesGroup.controls).map(control => {
                    if (control !== this.#rolesGroup.get(this.entityUserRoles.PRD_ANS)
                        && control !== this.#rolesGroup.get(this.entityUserRoles.BI_USR)) {
                        control.disable();
                        control.patchValue(false, { emitEvent: false });
                    } else {
                        control.enable({ emitEvent: false });
                    }
                }
                );
                this.#permissionsGroup.get('biPermissionControl').disable();
                this.#permissionsGroup.get('biPermissionControl').patchValue(BiUserTypes.basic);
            }

            this.#rolesGroup.get(this.entityUserRoles.PRD_ANS).valueChanges
                .pipe(takeUntil(this.#onDestroy))
                .subscribe(roleIsChecked => {
                    if (roleIsChecked) {
                        this.form.controls['producer'].enable();
                        Object.values(this.#rolesGroup.controls).map(control => {
                            if (control !== this.#rolesGroup.get(this.entityUserRoles.PRD_ANS)
                                && control !== this.#rolesGroup.get(this.entityUserRoles.BI_USR)) {
                                control.disable();
                                control.patchValue(false, { emitEvent: false });
                            }
                        }
                        );
                        this.#permissionsGroup.get('biPermissionControl').disable();
                        if (this.#permissionsGroup.get('biPermissionControl').value === BiUserTypes.advanced) {
                            this.#permissionsGroup.get('biPermissionControl').patchValue(BiUserTypes.basic);
                            this.#permissionsGroup.get('biPermissionControl').markAsDirty();
                        }
                    } else {
                        this.form.controls['producer'].disable();
                        Object.values(this.#rolesGroup.controls).map(control => {
                            if (control !== this.#rolesGroup.get(this.entityUserRoles.PRD_ANS)
                                && control !== this.#rolesGroup.get(this.entityUserRoles.BI_USR)) {
                                control.enable();
                            }
                        });
                        if (this.#rolesGroup.get(this.entityUserRoles.BI_USR).enabled
                            && this.#rolesGroup.get(this.entityUserRoles.BI_USR).value === true) {
                            this.#permissionsGroup.get('biPermissionControl').enable();
                        }
                    }
                });
        }

        if (this.#rolesGroup.get(this.entityUserRoles.CNL_INT)) {
            //If initial CNL_INT value is unsetted (false), disable permission
            if (!this.#rolesGroup.get(this.entityUserRoles.CNL_INT).value) {
                this.#permissionsGroup.get(this.entityUserPermissions.secondMarket).disable();
            }

            //If CNL_INT value changes, its permission also change
            this.#rolesGroup.get(this.entityUserRoles.CNL_INT).valueChanges
                .pipe(takeUntil(this.#onDestroy))
                .subscribe(roleIsChecked => {
                    const permissionFormControl = this.#permissionsGroup.get(this.entityUserPermissions.secondMarket);
                    if (permissionFormControl) {
                        if (roleIsChecked) {
                            permissionFormControl.enable();
                        } else if (!roleIsChecked) {
                            permissionFormControl.disable();
                        }
                        if (permissionFormControl.value !== roleIsChecked) {
                            permissionFormControl.patchValue(roleIsChecked);
                        }
                    }
                });
        }

        if (this.#rolesGroup.get(this.entityUserRoles.BI_USR)) {
            //If initial BI_USR value is unsetted (false), disable permission
            if (!this.#rolesGroup.get(this.entityUserRoles.BI_USR).value) {
                this.#permissionsGroup.get('biPermissionControl').disable();
            }

            //If BI_USR value changes, its permission also change
            this.#rolesGroup.get(this.entityUserRoles.BI_USR).valueChanges
                .pipe(takeUntil(this.#onDestroy))
                .subscribe(roleIsChecked => {
                    const permissionFormControl = this.#permissionsGroup.get('biPermissionControl');
                    if (permissionFormControl) {
                        if (roleIsChecked && !this.#rolesGroup.get(this.entityUserRoles.PRD_ANS)?.value) {
                            permissionFormControl.enable();
                        } else if (!roleIsChecked || this.#rolesGroup.get(this.entityUserRoles.BI_USR).value) {
                            permissionFormControl.disable();
                        }
                    }
                });
        }
    }

    private reloadModels(): void {
        this.#usersSrv.loadEntityUser(this.#userId);
        this.form.markAsPristine();
    }

    private sortAvailableUserRoles(availableRoles: EntityUserRole[]): EntityUserRole[] {
        const sortOrder = this.#userRolesDisplayOrder;
        return availableRoles.sort((a, b) => sortOrder.indexOf(a.code) - sortOrder.indexOf(b.code));
    }

    private getAssignedPermissionsArr(roles: EntityUserRole[]): EntityUserPermissions[] {
        const permissions = [];
        roles.forEach(role => {
            if (role.permissions) {
                role.permissions.forEach(permission => permissions.push(permission));
            }
        });
        return permissions;
    }

    private checkEntityMgrRoleInitialValue(isChecked: boolean): void {
        entityMgrRoleDependencies.forEach(role => {
            const roleFormControl = this.#rolesGroup.get(role);
            if (roleFormControl && this.#rolesGroup.get(role).value) {
                if (isChecked) {
                    roleFormControl.disable();
                }
            }
        });
        entityMgrPermissionsDependencies.forEach(permission => {
            const permissionFormControl = this.#permissionsGroup.get(permission);
            if (permissionFormControl) {
                if (isChecked) {
                    permissionFormControl.disable();
                }
            }
        });
    }
}
