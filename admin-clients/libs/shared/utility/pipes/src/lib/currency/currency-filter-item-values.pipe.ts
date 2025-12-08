import { Currency } from '@admin-clients/shared-utility-models';
import { Pipe, PipeTransform } from '@angular/core';

type CurrencyFilter = Omit<Currency, 'id'> & { id: string; name: string };
@Pipe({
    standalone: true,
    name: 'currencyFilterItemValues'
})
export class CurrencyFilterItemValuesPipe implements PipeTransform {
    transform(currencies: Currency[]): CurrencyFilter[] {
        return currencies?.map(currency => ({ ...currency, id: currency?.code, name: currency?.code }));
    }
}
