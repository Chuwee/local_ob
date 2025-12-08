import { Pipe, PipeTransform, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Chip } from '../chips.component';

@Pipe({
    standalone: true,
    name: 'chipLabel'
})
export class ChipLabelPipe implements PipeTransform {
    private readonly _translate = inject(TranslateService);

    transform(chip: Chip): string {
        return `${chip.label}${chip.valueText ? ': ' + this._translate.instant(chip.valueText) : ''}`;
    }
}
