import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-set-sessions-config-dialog',
    imports: [TranslatePipe, MaterialModule, FlexLayoutModule, FlexModule, ReactiveFormsModule, FormControlErrorsComponent, DateTimePipe],
    templateUrl: './set-sessions-config-dialog.component.html',
    styleUrls: ['./set-sessions-config-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SetSessionsConfigDialogComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #onDestroy = inject(DestroyRef);
    readonly #dialogRef = inject(MatDialogRef<SetSessionsConfigDialogComponent>);
    readonly data = inject(MAT_DIALOG_DATA);
    readonly form = this.#fb.group({
        use_custom_stock: this.#fb.control(false as boolean, Validators.required),
        stock: this.#fb.control({ value: null as number, disabled: true }, [Validators.required])
    });

    readonly dateTimeFormats = DateTimeFormats;

    ngOnInit(): void {
        this.#setFormValue();
        this.form.controls.use_custom_stock.valueChanges.pipe(takeUntilDestroyed(this.#onDestroy)).subscribe(customStock => {
            if (!customStock) {
                this.form.controls.stock.disable();
            } else if (customStock) {
                this.form.controls.stock.enable();
            }
        });
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(): void {
        this.#dialogRef.close();
    }

    setSessionsConfig(): void {
        this.#dialogRef.close(this.form.value);
    }

    #setFormValue(): void {
        this.form.controls.use_custom_stock.setValue(this.data.session.use_custom_stock);
        this.form.controls.stock.setValue(this.data.session.stock);

        if (this.data.session.use_custom_stock) {
            this.form.controls.stock.enable();
        }
    }
}
