import { Pipe, PipeTransform } from '@angular/core';
import { MatrixExtract } from '@svgdotjs/svg.js';

@Pipe({
    standalone: true,
    name: 'matrix',
    pure: true
})
export class SvgTransformPipe implements PipeTransform {
    transform(matrix: MatrixExtract): string {
        if (matrix) {
            return `matrix(${matrix.a},${matrix.b},${matrix.c},${matrix.d},${matrix.e},${matrix.f})`;
        } else {
            return '';
        }
    }
}
