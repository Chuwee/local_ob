import { I18nService } from '@admin-clients/shared/core/data-access';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { inject, Pipe, PipeTransform } from '@angular/core';
import { map, Observable } from 'rxjs';

type CurrencyFilter =  FilterOption & { description: string };
@Pipe({
    standalone: true,
    name: 'currencyFilterItemsFullTranslation$'
})
export class CurrencyFilterItemsFullTranslationPipe implements PipeTransform {
    readonly #i18nService = inject(I18nService);

    transform(currencies$: Observable<FilterOption[]>): Observable<CurrencyFilter[]> {
        return currencies$
            .pipe(
                map(currencies => currencies?.map(currency => ({
                    ...currency,
                    description: this.#i18nService.getCurrencyFullTranslation(currency.id)
                })))
            );
    }
}
