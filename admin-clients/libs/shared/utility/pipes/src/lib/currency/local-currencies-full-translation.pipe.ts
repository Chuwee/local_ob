import { I18nService } from '@admin-clients/shared/core/data-access';
import { Currency } from '@admin-clients/shared-utility-models';
import { inject, Pipe, PipeTransform } from '@angular/core';
import { map, Observable } from 'rxjs';

@Pipe({
    standalone: true,
    name: 'localCurrenciesFullTranslation$'
})
export class LocalCurrenciesFullTranslation$Pipe implements PipeTransform {
    readonly #i18nService = inject(I18nService);

    transform(currencies$: Observable<Currency[]>): Observable<Currency[]> {
        return currencies$
            .pipe(
                map(currencies => currencies?.map<Currency>(currency => ({
                    ...currency,
                    description: this.#i18nService.getCurrencyFullTranslation(currency.code)
                }))
                )
            );
    }
}
