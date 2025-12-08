import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EntityUsersService, EntityUserRoles, EntityUser,
    EntityUserPermissions, GetEntityUsersRequest
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AsyncPipe, NgFor } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, filter, first, Observable, Subject, switchMap, takeUntil } from 'rxjs';
import { AssignLicenseDialogData } from './assign-license-dialog.model';

@Component({
    selector: 'app-assign-license-dialog',
    templateUrl: './assign-license-dialog.component.html',
    styleUrls: ['./assign-license-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, NgFor, FormControlErrorsComponent, TranslatePipe, AsyncPipe, FlexModule,
        ReactiveFormsModule, SelectSearchComponent
    ]
})
export class AssignLicenseDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    form: UntypedFormGroup;
    operators$: Observable<Operator[]>;
    entities$: Observable<Entity[]>;
    users$ = new BehaviorSubject<EntityUser[]>([]);
    title: string;

    constructor(
        private _operatorsSrv: OperatorsService,
        private _entitiesSrv: EntitiesBaseService,
        private _usersSrv: EntityUsersService,
        private _fb: UntypedFormBuilder,
        private _dialogRef: MatDialogRef<AssignLicenseDialogComponent, boolean>,
        @Inject(MAT_DIALOG_DATA) public data: AssignLicenseDialogData
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this._operatorsSrv.operators.load({ offset: 0, limit: 999 });
        this.initForm();
        this.model();
        this.initFormHandlers();
    }

    ngOnDestroy(): void {
        this._operatorsSrv.operators.clear();
        this._entitiesSrv.entityList.clear();
        this.users$.next(null);
        this.users$.complete();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(added = false): void {
        this._dialogRef.close(added);
    }

    addUser(): void {
        if (this.form.valid) {
            const user = this.form.value.user;
            this._usersSrv.getUserRoles$()
                .pipe(
                    first(roles => !!roles),
                    switchMap(roles => {
                        if (roles.map(role => role.code).includes(EntityUserRoles.BI_USR)) {
                            return this._usersSrv.rolesAndPermissions.saveRolePermission(
                                user.id, EntityUserRoles.BI_USR, this.data.permission);
                        } else {
                            return this._usersSrv.rolesAndPermissions.saveRoleAndPermissions(user.id, {
                                code: EntityUserRoles.BI_USR,
                                permissions: [this.data.permission]
                            });
                        }
                    })
                )
                .subscribe(() => this.close(true));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    private initForm(): void {
        this.form = this._fb.group({
            operator: [null, Validators.required],
            entity: [{ value: null, disabled: true }, Validators.required],
            user: [{ value: null, disabled: true }, Validators.required]
        });
    }

    private model(): void {
        this.operators$ = this._operatorsSrv.operators.getData$()
            .pipe(filter(list => !!list));
        this.entities$ = this._entitiesSrv.entityList.getData$()
            .pipe(filter(list => !!list));
    }

    private initFormHandlers(): void {
        this.form.get('operator').valueChanges
            .pipe(
                takeUntil(this._onDestroy)
            )
            .subscribe(operatorId => {
                if (operatorId) {
                    this._entitiesSrv.entityList.load({
                        offset: 0,
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        operator_id: operatorId,
                        include_entity_admin: true
                    });
                    this.form.get('entity').enable();
                } else {
                    this.form.get('entity').disable();
                }
            });

        this.form.get('entity').valueChanges
            .pipe(
                takeUntil(this._onDestroy)
            )
            .subscribe(entityId => {
                if (entityId) {
                    const usersReq: GetEntityUsersRequest = { entityId, offset: 0, limit: 999, sort: 'email:asc' };
                    if (this.data.permission === EntityUserPermissions.impersonation) {
                        usersReq.roles = [EntityUserRoles.BI_USR];
                    }
                    this._usersSrv.getUsersList(usersReq)
                        .pipe(first(users => !!users))
                        .subscribe(users => {
                            let nextUsers = users;
                            if (this.data.onlyInternalUsers) {
                                nextUsers = nextUsers.filter(user => EntityUsersService.isInternalUser(user));
                            }
                            if (this.data.alreadyAssigned) {
                                nextUsers = nextUsers.filter(user => !this.data.alreadyAssigned.includes(user.id));
                            }
                            this.users$.next(nextUsers);
                        });
                    this.form.get('user').enable();
                } else {
                    this.form.get('user').disable();
                }
            });

        this.form.get('user').valueChanges
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(user => {
                this._usersSrv.loadUserRoles(user.id);
            });
    }
}
