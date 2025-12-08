import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalCurrencySymbolPipe } from '@admin-clients/shared/utility/pipes';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, filter, map } from 'rxjs';
import { CurrenciesApi } from '../../currencies.api';
import { CurrenciesService } from '../../currencies.service';
import { Currency } from '../../models/currency.model';
import { PutCurrency } from '../../models/put-currency.model';
import { CurrenciesState } from '../../state/currencies.state';

@Component({
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule,
        FormControlErrorsComponent, FlexLayoutModule,
        LocalCurrencySymbolPipe, EllipsifyDirective,
        MatDialogModule, MatTooltipModule, MatButtonModule,
        MatFormFieldModule, MatIcon, MatProgressSpinner, MatInput
    ],
    selector: 'app-modify-currency-dialog',
    styleUrls: ['./modify-currency-dialog.component.scss'],
    templateUrl: './modify-currency-dialog.component.html',
    providers: [CurrenciesService, CurrenciesState, CurrenciesApi],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModifyCurrencyDialogComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _currenciesSrv = inject(CurrenciesService);
    private readonly _authService = inject(AuthenticationService);
    private readonly _msgDialogSrv = inject(MessageDialogService);

    private _onDestroy = new Subject();

    form = this._fb.group({
        rate: [null as number, [Validators.required, Validators.min(0.01)]]
    });

    isInProgress$ = this._currenciesSrv.list.loading$();
    canModify$: Observable<boolean> = this._authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.exchangeRateWrite])));

    currency: Currency;

    constructor(
        private _dialogRef: MatDialogRef<ModifyCurrencyDialogComponent, boolean>,
        @Inject(MAT_DIALOG_DATA) private _data: { currency: Currency }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.currency = _data.currency;
    }

    ngOnInit(): void {
        this.form.controls['rate'].patchValue(this.currency.rate);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    updateCurrency(): void {
        if (this.form.valid) {
            const exchangeRate: PutCurrency = {
                supplier: this.currency.supplier,
                source: this.currency.source,
                target: this.currency.target,
                rate: this.form.controls['rate'].getRawValue()
            };

            this._msgDialogSrv.showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.MODIFY_EXCHANGE_RATE_WARN',
                message: 'CURRENCIES.MODIFY_CURRENCY.MESSAGE_WARNING',
                actionLabel: 'FORMS.ACTIONS.UPDATE',
                showCancelButton: true
            })
                .pipe(
                    filter(Boolean)
                ).subscribe(accepted => {
                    accepted ? this._currenciesSrv.list.updateCurrency(exchangeRate).subscribe(() => this.close(true)) : this.close(false);
                });

        } else {
            this.form.markAllAsTouched();
        }
    }

    close(edited = false): void {
        this._dialogRef.close(edited);
    }
}
