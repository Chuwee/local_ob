import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'variantText',
    standalone: true
})
export class VariantTextPipe implements PipeTransform {
    transform(attr1: string, attr2: string): string {
        if (attr1) {
            if (attr2) {
                return `${attr1} / ${attr2}`;
            }
            return `${attr1}`;
        }
        return 'ORDER.PRODUCTS_DATA.VARIANT_UNIQUE';
    }
}
