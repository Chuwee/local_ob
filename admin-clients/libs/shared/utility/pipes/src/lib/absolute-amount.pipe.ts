import { convertAndValidateNumber } from '@admin-clients/shared/utility/utils';
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'absoluteAmount',
    standalone: true
})
export class AbsoluteAmountPipe implements PipeTransform {
    transform(amount: string | number): number {
        if (amount == null || amount === '') {
            return null;
        }

        return Math.abs(convertAndValidateNumber(amount));
    }
}
