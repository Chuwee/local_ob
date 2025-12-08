import { convertAndValidateNumber } from '@admin-clients/shared/utility/utils';
import { formatNumber } from '@angular/common';
import { inject, Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Pipe({
    name: 'localNumber',
    pure: true,
    standalone: true
})
export class LocalNumberPipe implements PipeTransform {
    private readonly _translateSrv = inject(TranslateService);

    transform(value: any, format = '.0-2'): string {
        if (value === null || value === undefined) { return ''; }
        const number = convertAndValidateNumber(value);
        return formatNumber(number, this._translateSrv.getCurrentLang(), format);
    }
}
