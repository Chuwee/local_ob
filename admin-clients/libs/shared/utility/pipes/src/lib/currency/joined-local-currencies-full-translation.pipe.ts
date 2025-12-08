import { I18nService } from '@admin-clients/shared/core/data-access';
import { inject, Pipe, PipeTransform } from '@angular/core';

@Pipe({
    standalone: true,
    name: 'joinedLocalCurrenciesFullTranslation'
})
export class JoinedLocalCurrenciesFullTranslationPipe implements PipeTransform {
    readonly #i18nService = inject(I18nService);

    transform(currencies: string[]): string {
        return currencies?.map(currency => this.#i18nService.getCurrencyFullTranslation(currency)).join(', ');
    }
}
