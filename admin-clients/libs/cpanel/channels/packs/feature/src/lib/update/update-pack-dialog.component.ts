import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { first, throwError } from 'rxjs';

@Component({
    selector: 'app-update-pack-dialog',
    templateUrl: './update-pack-dialog.component.html',
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, FlexLayoutModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UpdatePackDialogComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #dialogRef = inject(MatDialogRef<UpdatePackDialogComponent>);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);

    readonly data = inject<{ channelId: number; packId?: number }>(MAT_DIALOG_DATA);

    readonly form = this.#fb.group({
        name: [null as string, [Validators.required, Validators.maxLength(50)]]
    });

    readonly isLoading$ = this.#packsSrv.pack.loading$();

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.#packsSrv.pack.get$()
            .pipe(first(pack => pack?.id === this.data.packId))
            .subscribe(pack =>
                this.form.patchValue({ name: pack.name })
            );
    }

    updateSessionPack(): void {
        if (this.form.valid) {
            this.#packsSrv.pack.update(this.data.channelId, this.data.packId, { name: this.form.controls['name'].getRawValue() })
                .subscribe(() => {
                    this.#ephemeralMessageService.showSuccess({ msgKey: 'CHANNELS.UPDATE_PACK_SUCCESS' });
                    this.close(this.data.packId);
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            throwError(() => 'invalid form');
        }
    }

    close(packId: number = null): void {
        this.#dialogRef.close(packId);
    }
}
