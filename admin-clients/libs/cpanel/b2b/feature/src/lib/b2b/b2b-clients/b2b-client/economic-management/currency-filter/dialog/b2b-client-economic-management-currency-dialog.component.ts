import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { CurrencyFilterItemValuesPipe, LocalCurrencyCodesFullTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { Currency } from '@admin-clients/shared-utility-models';
import { AsyncPipe, NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map } from 'rxjs/operators';

@Component({
    selector: 'app-b2b-client-economic-management-currency-dialog',
    templateUrl: './b2b-client-economic-management-currency-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatIcon, FlexLayoutModule, MatDialogTitle, TranslatePipe, MatIconButton, MatDialogContent, ReactiveFormsModule,
        MatSelect, MatOption, SelectSearchComponent, FormControlErrorsComponent, AsyncPipe, MatDialogActions,
        MatButton, ObFormFieldLabelDirective, MatLabel, MatFormField, CurrencyFilterItemValuesPipe, LocalCurrencyCodesFullTranslationPipe,
        NgForOf
    ]
})
export class B2bClientEconomicManagementCurrencyDialogComponent {
    readonly #auth = inject(AuthenticationService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #i18nSrv = inject(I18nService);

    readonly #dialogRef = inject<MatDialogRef<B2bClientEconomicManagementCurrencyDialogComponent,
        string>>(MatDialogRef);

    readonly #currency = inject<string>(MAT_DIALOG_DATA);
    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(first(), map(user => AuthenticationService.operatorCurrencyCodes(user) ?? [user.currency]));

    readonly currencyCtrl = this.#fb.nonNullable.control(null as Currency, Validators.required);

    compareWith = compareWithIdOrCode;

    constructor(
    ) {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        if (this.#currency) {
            this.currencyCtrl.setValue({
                code: this.#currency,
                description: this.#i18nSrv.getCurrencyFullTranslation(this.#currency)
            });
        }
    }

    selectCurrency(): void {
        if (this.currencyCtrl.valid) {
            this.#dialogRef.close(this.currencyCtrl.value.code);
        } else {
            this.currencyCtrl.markAsTouched();
        }
    }

    close(): void {
        this.#dialogRef.close();
    }
}

