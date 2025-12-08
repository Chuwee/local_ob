import { FeverService } from '@admin-clients/cpanel-fever-data-access';
import { DialogSize, ObDialog, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    standalone: true,
    selector: 'app-fever-zone-entity-dialog',
    templateUrl: './fever-zone-entity-dialog.component.html',
    imports: [
        TranslatePipe, MatFormField, MatSelect, MatOption, MatButton, MatDialogActions,
        MatDialogContent, MatDialogTitle, MatLabel, SelectSearchComponent, ReactiveFormsModule, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FeverZoneEntityDialogComponent extends ObDialog<FeverZoneEntityDialogComponent, null, number>
    implements OnDestroy {
    readonly #dialogRef = inject(MatDialogRef);
    readonly #feverSrv = inject(FeverService);
    readonly #fb = inject(FormBuilder);

    readonly entities$ = this.#feverSrv.entites.get$();
    readonly entityIdCtrl = this.#fb.control<number>(null, { validators: [Validators.required] });

    constructor() {
        super(DialogSize.MEDIUM, true);
        this.#feverSrv.entites.load();
    }

    ngOnDestroy(): void {
        this.#feverSrv.entites.clear();
    }

    save(): void {
        if (this.entityIdCtrl.valid) {
            this.#dialogRef.close(this.entityIdCtrl.value);
        }
    }
}
