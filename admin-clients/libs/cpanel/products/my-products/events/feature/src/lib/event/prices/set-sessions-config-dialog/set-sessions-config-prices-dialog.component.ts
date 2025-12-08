import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { CurrencyInputComponent, DialogSize } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map } from 'rxjs';

@Component({
    selector: 'app-set-sessions-prices-config-dialog',
    imports: [
        TranslatePipe, FlexLayoutModule, FlexModule, ReactiveFormsModule, FormControlErrorsComponent,
        DateTimePipe, CurrencyInputComponent, AsyncPipe, LocalCurrencyPipe, ReactiveFormsModule, MatDivider,
        MatIcon, MatRadioModule, MatFormField, MatDialogModule, MatButtonModule
    ],
    templateUrl: './set-sessions-config-prices-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SetSessionsPricesConfigDialogComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #authSrv = inject(AuthenticationService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #dialogRef = inject(MatDialogRef<SetSessionsPricesConfigDialogComponent>);
    readonly data = inject(MAT_DIALOG_DATA);
    readonly form = this.#fb.group({
        use_custom_price: [false as boolean, [Validators.required]],
        price: [{ value: null as number, disabled: true }, [Validators.required]]
    });

    readonly currency$ = this.#authSrv.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => user.currency)
        );

    readonly dateTimeFormats = DateTimeFormats;

    ngOnInit(): void {
        this.#setFormValue();
        this.form.controls.use_custom_price.valueChanges.pipe(
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(customPrice => {
            if (!customPrice) {
                this.form.controls.price.disable();
            } else if (customPrice) {
                this.form.controls.price.enable();
            }
        });
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(): void {
        this.#dialogRef.close();
    }

    setSessionsConfig(): void {
        const resp = {
            variants: [{
                id: this.data.session.variant_id,
                price: this.form.controls.price.value,
                use_custom_price: this.form.controls.use_custom_price.value
            }]
        };
        this.#dialogRef.close(resp);
    }

    #setFormValue(): void {
        this.form.controls.use_custom_price.setValue(this.data.session.use_custom_price);
        this.form.controls.price.setValue(this.data.session.price);

        if (this.data.session.use_custom_price) {
            this.form.controls.price.enable();
        }
    }
}
