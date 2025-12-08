import { I18nService } from '@admin-clients/shared/core/data-access';
import { inject, Pipe, PipeTransform } from '@angular/core';

@Pipe({
    standalone: true,
    name: 'localCurrencyFullTranslation'
})
export class LocalCurrencyFullTranslationPipe implements PipeTransform {
    readonly #i18nService = inject(I18nService);

    transform(currency: string): string {
        return this.#i18nService.getCurrencyFullTranslation(currency);
    }
}
