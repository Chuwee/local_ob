import { I18nService } from '@admin-clients/shared/core/data-access';
import { Currency } from '@admin-clients/shared-utility-models';
import { inject, Pipe, PipeTransform } from '@angular/core';
import { map, Observable } from 'rxjs';

@Pipe({
    standalone: true,
    name: 'localCurrencyCodesFullTranslation$'
})
export class LocalCurrencyCodesFullTranslationPipe implements PipeTransform {
    readonly #i18nService = inject(I18nService);

    transform(currencies$: Observable<string[]>): Observable<Currency[]> {
        return currencies$
            .pipe(
                map(currencies => currencies?.map<Currency>(currency => ({
                        code: currency,
                        description: this.#i18nService.getCurrencyFullTranslation(currency)
                    }))
                )
            );
    }
}
