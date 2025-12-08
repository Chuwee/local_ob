import { RangeElement } from '@admin-clients/shared-utility-models';
import { Pipe, PipeTransform } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Pipe({
    standalone: true,
    name: 'rangeCurrencyInput'
})
export class RangeCurrencyInputPipe implements PipeTransform {
    transform(rangesMap$: Observable<Map<string, RangeElement[]>>, currency: string): Observable<RangeElement[]> {
        return rangesMap$
            .pipe(
                filter(Boolean),
                map(rangesMap => rangesMap.get(currency) ?? [])
            );
    }
}
