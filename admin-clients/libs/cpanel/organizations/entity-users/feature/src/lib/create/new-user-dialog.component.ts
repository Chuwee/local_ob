import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService, entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EntityUsersService, PostEntityUser, UserFieldsRestrictions
} from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { Operator, OperatorsService, operatorsProviders } from '@admin-clients/cpanel-configurations-operators-data-access';
import { EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import { DialogSize, MessageDialogService, ObDialog, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { EMPTY, Subject, combineLatest, of } from 'rxjs';
import { distinctUntilChanged, filter, first, map, switchMap, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'ob-new-user-dialog',
    templateUrl: './new-user-dialog.component.html',
    styleUrls: ['./new-user-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [entitiesProviders, operatorsProviders],
    imports: [
        MaterialModule, ReactiveFormsModule, SelectSearchComponent,
        FormControlErrorsComponent, TranslatePipe, CommonModule, FlexLayoutModule
    ]
})
export class NewUserDialogComponent extends ObDialog<NewUserDialogComponent, null, number> implements OnInit, OnDestroy {

    private readonly _auth = inject(AuthenticationService);
    private readonly _entitiesService = inject(EntitiesService);
    private readonly _operatorsSrv = inject(OperatorsService);
    private readonly _entityUsersSrv = inject(EntityUsersService);
    private readonly _msgDialogService = inject(MessageDialogService);
    private readonly _fb = inject(FormBuilder);
    private _onDestroy: Subject<void> = new Subject();
    private _showEntityOperatorWarning = false;

    readonly entities$ = this._entitiesService.entityList.getData$();
    readonly operators$ = this._operatorsSrv.operators.getData$();
    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();
    readonly canSelectOperator$ = this._auth.hasLoggedUserSomeEntityType$(['SUPER_OPERATOR'])
        .pipe(tap(canSelectOperator => {
            if (canSelectOperator) this.newUserForm.get('operator').addValidators(Validators.required);
        }));

    readonly newUserForm = this._fb.group({
        operator: null as Operator,
        entity: [null as Entity, Validators.required],
        name: [null as string, [Validators.required, Validators.maxLength(UserFieldsRestrictions.userNameLength)]],
        surname: [null as string, [Validators.required, Validators.maxLength(UserFieldsRestrictions.userSurnameLength)]],
        email: [null as string, [Validators.required, Validators.maxLength(UserFieldsRestrictions.userEmailLength), Validators.email]],
        position: [null as string, [Validators.maxLength(UserFieldsRestrictions.userPositionLength)]]
    });

    readonly isInProgress$ = booleanOrMerge([
        this._entityUsersSrv.isEntityUserSaving$(),
        this._entitiesService.entityList.inProgress$(),
        this._entitiesService.isEntityLoading$(),
        this._operatorsSrv.operators.loading$()
    ]);

    constructor(
    ) {
        super(DialogSize.MEDIUM);
        this.dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        combineLatest([
            this._auth.getLoggedUser$().pipe(first(Boolean)),
            this.canSelectEntity$,
            this.canSelectOperator$
        ]).pipe(
            first(),
            switchMap(([user, canSelectEntity, canSelectOperator]) => {
                if (canSelectOperator) {
                    this._operatorsSrv.operators.load({
                        limit: 999,
                        sort: 'name:asc'
                    });
                    this.newUserForm.get('entity').disable();
                    return of([]);
                } else if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        include_entity_admin: true
                    });
                    return this._entitiesService.entityList.getData$().pipe(filter(entities => entities?.length > 0));
                } else {
                    this._entitiesService.loadEntity(user.entity.id);
                    return this._entitiesService.getEntity$().pipe(filter(Boolean), map(entity => [entity]));
                }
            }),
            takeUntil(this._onDestroy)
        )
            .subscribe(entities => {
                if (entities.length === 1) {
                    this.newUserForm.patchValue({ entity: entities[0] });
                }
            });

        this.newUserForm.get('operator').valueChanges
            .pipe(
                distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(operator => {
                if (operator) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        include_entity_admin: true,
                        operator_id: operator.id
                    });
                    this.newUserForm.get('entity').enable();
                } else {
                    this.newUserForm.get('entity').patchValue(null);
                    this.newUserForm.get('entity').disable();
                }
            });

        //When logged user is operator or superoperator, must load the selected entity for checking if it is of type operator
        this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.SYS_MGR])
            .pipe(
                take(1),
                filter(Boolean),
                switchMap(() => this.newUserForm.get('entity').valueChanges),
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(entity => this._entitiesService.loadEntity(entity.id));

        this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.SYS_MGR])
            .pipe(
                take(1),
                filter(Boolean),
                switchMap(() => this._entitiesService.getEntity$().pipe(filter(Boolean))),
                takeUntil(this._onDestroy)
            )
            .subscribe(entity => this._showEntityOperatorWarning = entity.settings?.types?.includes('OPERATOR'));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._entitiesService.clearEntity();
    }

    createUser(): void {
        if (this.newUserForm.valid) {
            this.canSelectOperator$
                .pipe(
                    first(),
                    switchMap(canSelectOperator => {
                        const user: PostEntityUser = {
                            username: this.newUserForm.value.email,
                            entity_id: this.newUserForm.value.entity.id,
                            name: this.newUserForm.value.name,
                            last_name: this.newUserForm.value.surname,
                            job_title: this.newUserForm.value.position
                        };

                        if (canSelectOperator) {
                            user.operator_id = this.newUserForm.value.operator.id;
                        }

                        if (this._showEntityOperatorWarning) {
                            return this._msgDialogService.showWarn({
                                size: DialogSize.MEDIUM,
                                title: 'TITLES.ALERT',
                                message: 'USER.ENTITY_OPERATOR_WARNING',
                                actionLabel: 'FORMS.ACTIONS.CREATE',
                                showCancelButton: true
                            })
                                .pipe(switchMap(accepted => accepted ? this._entityUsersSrv.createEntityUser(user) : EMPTY));
                        } else {
                            return this._entityUsersSrv.createEntityUser(user);
                        }
                    }))
                .subscribe(userId => this.close(userId));
            // TODO: handle EMAIL_CONFLICT from server as form error
        } else {
            this.newUserForm.markAllAsTouched();
        }
    }

    close(userId: number = null): void {
        this.dialogRef.close(userId);
    }
}
