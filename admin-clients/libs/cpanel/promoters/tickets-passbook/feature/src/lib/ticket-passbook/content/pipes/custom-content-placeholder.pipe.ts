import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'customPlaceholderValue',
    pure: true,
    standalone: false
})
export class CustomContentPlaceholderPipe implements PipeTransform {

    transform(value: string): string {
        if (value !== null) {
            return value.replace(/{|}/gm, '');
        }
        return '';
    }
}
