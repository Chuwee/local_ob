import { Pipe, PipeTransform } from '@angular/core';
import { NavTabMenuElement } from './models/nav-tabs-menu.models';

@Pipe({
    name: 'navsTabsMenuShow',
    standalone: true
})
export class NavsTabsMenuShowPipe implements PipeTransform {
    transform(element: NavTabMenuElement): boolean {
        if ('show' in element) {
            return element.show;
        } else if ('hidden' in element) {
            return !element.hidden;
        } else {
            return true;
        }
    }
}
