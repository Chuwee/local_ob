import { I18nService } from '@admin-clients/shared/core/data-access';
import { getCurrencySymbol } from '@angular/common';
import { Pipe, PipeTransform, inject } from '@angular/core';

@Pipe({
    name: 'localCurrencySymbol',
    standalone: true
})
export class LocalCurrencySymbolPipe implements PipeTransform {
    private readonly _i18nSrv = inject(I18nService);

    transform(code: string): string {
        const locale = this._i18nSrv.getLocale();
        return getCurrencySymbol(code, 'narrow', locale);
    }
}
