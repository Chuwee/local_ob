import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
    MatDialogModule, MatDialogRef,
    MAT_DIALOG_DATA
} from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-new-thanks-module-dialog',
    templateUrl: './new-thanks-module-dialog.component.html',
    imports: [
        TranslatePipe, MatIconModule, MatDialogModule, MatButtonModule, FlexLayoutModule, MatRadioModule, ReactiveFormsModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewThanksConfigModuleComponent {
    readonly #dialogRef = inject(MatDialogRef<NewThanksConfigModuleComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #data = inject<{ blocks }>(MAT_DIALOG_DATA);

    readonly form = this.#fb.group({
        type: [null, Validators.required]
    });

    readonly blocks = this.#data.blocks;

    readonly canCreateMain = this.blocks.filter(block => block.type === 'MAIN' && !block.enabled).length !== 0;
    readonly canCreateSmartbooking = this.blocks.filter(block => block.type === 'SMARTBOOKING' && !block.enabled).length !== 0;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    setType(): void {
        this.#dialogRef.close(this.form.controls.type.value);
    }

    close(): void {
        this.#dialogRef.close(null);
    }
}
