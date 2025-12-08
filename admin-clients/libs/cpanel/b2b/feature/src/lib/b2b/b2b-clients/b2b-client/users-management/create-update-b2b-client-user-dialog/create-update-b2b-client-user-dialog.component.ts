import { B2bClientUser, B2bService, B2bState, B2bUserType, PostB2bClientUser, PutB2bClientUser } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { b2bClientFieldsRestrictions } from '../../../models/b2b-client-fields-restrictions';
import { CreateOrUpdateB2bClientUserDialogActions, CreateOrUpdateB2bClientUserDialogData, CreateOrUpdateB2bClientUserDialogReturnData } from '../models/create-update-b2b-client-user-dialog-data.model';

interface CreateUpdateB2bClientUserForm {
    userCreationName: FormControl<string>;
    accessEmail: FormControl<string>;
    externalReference: FormControl<string>;
    credentialReceptionEmail: FormControl<string>;
    type: FormControl<B2bUserType>;
}

@Component({
    selector: 'app-create-update-b2b-client-user-dialog',
    templateUrl: './create-update-b2b-client-user-dialog.component.html',
    styleUrls: ['./create-update-b2b-client-user-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    /*
        Provide service and state for preventing "Error NG0100: Expression has changed after it was checked"
        due to b2b-client-users-management.component and create-up-b2b-client-dialog.component both listenning to
        this._b2bSrv.isB2bClientUserInProgress$() on isInProgress$ observable (for the spinner)
    */
    providers: [
        B2bService, B2bState
    ],
    standalone: false
})
export class CreateUpdateB2bClientUserDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _isOperatorUser: boolean;
    private _entityId: number;
    private _b2bClientId: number;

    form: FormGroup;
    b2bClientUser$: Observable<B2bClientUser>;
    isInProgress$: Observable<boolean>;

    readonly userTypes = B2bUserType;

    readonly dialogActions = CreateOrUpdateB2bClientUserDialogActions;

    constructor(
        private _b2bSrv: B2bService,
        private _auth: AuthenticationService,
        private _route: ActivatedRoute,
        private _dialogRef: MatDialogRef<CreateUpdateB2bClientUserDialogComponent, CreateOrUpdateB2bClientUserDialogReturnData>,
        private _fb: FormBuilder,
        @Inject(MAT_DIALOG_DATA) public data: CreateOrUpdateB2bClientUserDialogData
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.initForm();
        /*
            Load again the b2bClient (due to providing state on this component)
            Can't get the b2bClientId from paramMap
        */
        combineLatest([
            this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]),
            this._route.queryParamMap
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([isOperator, queryParamMap]) => {
                isOperator
                    ? this._b2bSrv.loadB2bClient(this.data.b2bClientId, (parseInt(queryParamMap.get('entityId'))))
                    : this._b2bSrv.loadB2bClient(this.data.b2bClientId);
            });

        combineLatest([
            this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]),
            this._b2bSrv.getB2bClient$().pipe(filter(b2bClient => !!b2bClient))
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([isOperator, b2bClient]) => {
                this._isOperatorUser = isOperator;
                this._entityId = b2bClient.entity?.id;
                this._b2bClientId = b2bClient.id;
                if (this.data.action === this.dialogActions.update) {
                    if (isOperator) {
                        this._b2bSrv.loadB2bClientUser(b2bClient.id, this.data.b2bClientUserId, b2bClient.entity?.id);
                    } else {
                        this._b2bSrv.loadB2bClientUser(b2bClient.id, this.data.b2bClientUserId);
                    }
                }
            });

        if (this.data.action === this.dialogActions.update) {
            this._b2bSrv.getB2bClientUser$()
                .pipe(
                    filter(b2bClientUser => !!b2bClientUser),
                    takeUntil(this._onDestroy)
                )
                .subscribe(b2bClientUser => this.updateFormValues(b2bClientUser));
        }

        this.initComponentModels();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._b2bSrv.clearB2bClient();
        this._b2bSrv.clearB2bClientUser();
    }

    close(dialogReturnData: CreateOrUpdateB2bClientUserDialogReturnData = null): void {
        this._dialogRef.close(dialogReturnData);
    }

    createOrUpdateB2bClientUser(): void {
        if (this.form.valid) {
            const formValue = this.form.value;
            let obs$: Observable<number | void>;

            if (this.data.action === this.dialogActions.add) {
                const createB2bUserRequest: PostB2bClientUser = {
                    name: formValue.userCreationName,
                    email: formValue.credentialReceptionEmail,
                    username: formValue.accessEmail,
                    external_reference: formValue.externalReference,
                    type: formValue.type
                };
                if (this._isOperatorUser) {
                    createB2bUserRequest.entity_id = this._entityId;
                }
                obs$ = this._b2bSrv.createB2bClientUser(this._b2bClientId, createB2bUserRequest);
            } else if (this.data.action === this.dialogActions.update) {
                const updateB2bUserRequest: PutB2bClientUser = {
                    name: formValue.userCreationName,
                    email: formValue.credentialReceptionEmail,
                    external_reference: formValue.externalReference,
                    type: formValue.type
                };
                if (this._isOperatorUser) {
                    updateB2bUserRequest.entity_id = this._entityId;
                }
                obs$ = this._b2bSrv.saveB2bClientUser(this._b2bClientId, this.data.b2bClientUserId, updateB2bUserRequest);
            }

            obs$.subscribe(b2bClientUserId => {
                if (b2bClientUserId) { //POST
                    this.close({ newB2bClientUserId: b2bClientUserId, actionPerformed: true });
                } else { //PUT
                    this.close({ actionPerformed: true });
                }
            });
        } else {
            this.form.markAllAsTouched();
        }
    }

    private initForm(): void {
        this.form = this._fb.group<CreateUpdateB2bClientUserForm>({
            userCreationName: new FormControl(null, [
                Validators.required, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientUserCreationNameMaxLength)
            ]),
            accessEmail: new FormControl({ value: null, disabled: true }, [
                Validators.required, Validators.email, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientAccessEmailMaxLength)
            ]),
            credentialReceptionEmail: new FormControl(null, [
                Validators.required, Validators.email, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientCredentialEmailMaxLength)
            ]),
            externalReference: new FormControl(null as string),
            type: new FormControl({ value: B2bUserType.admin, disabled: false })
        });
        if (this.data.action === this.dialogActions.add) {
            this.form.get('accessEmail').enable();
        }
    }

    private updateFormValues(b2bClientUser: B2bClientUser): void {
        this.form.patchValue({
            userCreationName: b2bClientUser.name,
            credentialReceptionEmail: b2bClientUser.email,
            externalReference: b2bClientUser.external_reference,
            type: b2bClientUser.type
        });
        this.form.markAsPristine();
    }

    private initComponentModels(): void {
        this.b2bClientUser$ = this._b2bSrv.getB2bClientUser$().pipe(filter(b2bClientUser => !!b2bClientUser));
        this.isInProgress$ = booleanOrMerge([
            this._b2bSrv.isB2bClientInProgress$(),
            this._b2bSrv.isB2bClientUserInProgress$()
        ]);
    }

}
