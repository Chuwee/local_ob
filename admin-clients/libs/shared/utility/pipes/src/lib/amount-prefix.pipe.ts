import { convertAndValidateNumber } from '@admin-clients/shared/utility/utils';
import { Pipe, PipeTransform } from '@angular/core';

export type CurrencyPrefixType = 'addition' | 'subtraction';

@Pipe({
    name: 'amountPrefix',
    standalone: true
})
export class AmountPrefixPipe implements PipeTransform {
    transform(amountAsString: string, amount: number | string, prefixAs: CurrencyPrefixType): string {
        if (amountAsString == null || amountAsString === '' || amount == null || amount === '') {
            return null;
        }

        let prefix = '';
        if (convertAndValidateNumber(amount) < 0) {
            prefix = prefixAs === 'addition' ? '- ' : '+ ';
        } else {
            prefix = prefixAs === 'addition' ? '+ ' : '- ';
        }

        return prefix + amountAsString;
    }
}
