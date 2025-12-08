import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'hasCommonElements', standalone: true })
export class HasCommonElementsPipe implements PipeTransform {
    transform(array1: string[], array2: string[]): boolean {
        return array1?.some(item => array2?.includes(item));
    }
}
