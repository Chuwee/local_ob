import { CollectivesService, PutCollectiveExternalValidatorProperties } from '@admin-clients/cpanel/collectives/data-access';
import { EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-validator-auth-edit-dialog',
    templateUrl: './collective-entity-validator-auth-edit-dialog.component.html',
    styleUrls: ['./collective-entity-validator-auth-edit-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ValidatorAuthDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _collectiveId: number;
    private _collectiveEntityId: number;
    private _validatorAuthUser: string;
    private _validatorAuthPwd: string;

    form: UntypedFormGroup;

    constructor(
        private _fb: UntypedFormBuilder,
        private _collectivesSrv: CollectivesService,
        private _ephemeralSrv: EphemeralMessageService,
        private _dialogRef: MatDialogRef<ValidatorAuthDialogComponent>,
        @Inject(MAT_DIALOG_DATA) data: {
            collectiveId: number;
            collectiveEntityId: number;
            validatorAuthUser: string;
            validatorAuthPwd: string;
        }
    ) {
        this._collectiveId = data.collectiveId;
        this._collectiveEntityId = data.collectiveEntityId;
        this._validatorAuthUser = data.validatorAuthUser;
        this._validatorAuthPwd = data.validatorAuthPwd;
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            user: [this._validatorAuthUser, Validators.required],
            password: [this._validatorAuthPwd, Validators.required]
        });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(isDone = false): void {
        this._dialogRef.close(isDone);
    }

    editValidatorAuth(): void {
        if (this.form.valid) {
            const { user, password } = this.form.value;
            const validatorProperties: PutCollectiveExternalValidatorProperties = {
                external_validator_properties: { user, password },
                entity_id: this._collectiveEntityId
            };
            this._collectivesSrv.saveCollectiveExternalValidators(this._collectiveId, validatorProperties)
                .subscribe(() => {
                    this._ephemeralSrv.showSaveSuccess();
                    this.close(true);
                });
        } else {
            this.form.markAllAsTouched();
        }
    }
}
