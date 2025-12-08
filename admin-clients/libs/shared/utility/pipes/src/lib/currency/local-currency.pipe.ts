import { AUTHENTICATION_SERVICE, I18nService } from '@admin-clients/shared/core/data-access';
import { convertAndValidateNumber } from '@admin-clients/shared/utility/utils';
import { formatCurrency, getCurrencySymbol } from '@angular/common';
import { inject, Injectable, Pipe, PipeTransform } from '@angular/core';
import { first } from 'rxjs/operators';

@Pipe({
    name: 'localCurrency',
    pure: true,
    standalone: true
})
@Injectable({ providedIn: 'root' })
export class LocalCurrencyPipe implements PipeTransform {
    private readonly _i18nSrv = inject(I18nService);
    private readonly _authSrv = inject(AUTHENTICATION_SERVICE);
    private _currency: string = null;

    transform(amount: string | number, currency?: string, currencyFormat?: 'wide' | 'narrow'): string {
        if (amount == null || amount === '' || amount !== amount) {
            return null;
        }
        // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
        if (!currency && !this._currency) {
            this._authSrv.getLoggedUser$().pipe(
                first(user => !!user)
            ).subscribe(user => this._currency = user?.currency);
        }
        if (!currency) {
            currency = this._currency;
        }
        currencyFormat = currencyFormat ?? 'wide';
        const locale = this._i18nSrv.getLocale();
        const localeFormat = this._i18nSrv.getLocaleFormat();
        const currencySymbol = getCurrencySymbol(currency, currencyFormat, locale);
        const amountAsNumber = convertAndValidateNumber(amount);

        return formatCurrency(amountAsNumber, locale, currencySymbol, currency, localeFormat);
    }

}
