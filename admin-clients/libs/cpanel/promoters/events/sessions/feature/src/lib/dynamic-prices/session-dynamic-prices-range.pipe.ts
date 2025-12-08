import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { getCurrencySymbol } from '@angular/common';
import { inject, Pipe, PipeTransform } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs/operators';

@Pipe({
    standalone: true,
    name: 'sessionDynamicPricesRangePipe'
})
export class SessionDynamicPricesRangePipe implements PipeTransform {
    readonly #i18nSrv = inject(I18nService);
    readonly #localeCurrencyPipe = inject(LocalCurrencyPipe);
    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly #userCurrency = toSignal(inject(AuthenticationService).getLoggedUser$().pipe(map(user => user.currency)));

    transform(values: { price: number }[], currency: string): string {
        if (!values || values.length === 0) {
            return '-';
        } else if (values.length === 1) {
            return this.#localeCurrencyPipe.transform(values[0].price, currency, 'narrow');
        } else {
            const prices = values?.map(value => value.price);
            const min = Math.min(...prices);
            const max = Math.max(...prices);
            if (min === max) {
                return this.#localeCurrencyPipe.transform(min, currency, 'narrow');
            } else {
                return `${min} - ${max} ${getCurrencySymbol(currency ?? this.#userCurrency(), 'narrow', this.#i18nSrv.getLocale())}`;
            }
        }
    }
}
