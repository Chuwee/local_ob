import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    standalone: true,
    name: 'sessionZoneDynamicPriceTranslation'
})
export class SessionZoneDynamicPriceTranslationPipe implements PipeTransform {
    transform(translations: {language: string; value: string}[], language: string): string {
        return translations?.find(translation => translation.language === language)?.value ?? '-';
    }
}
