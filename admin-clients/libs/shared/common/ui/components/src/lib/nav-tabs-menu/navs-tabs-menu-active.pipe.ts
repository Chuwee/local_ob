import { Pipe, PipeTransform } from '@angular/core';
import { NavTabMenuElement } from './models/nav-tabs-menu.models';

@Pipe({
    name: 'navsTabsMenuActive',
    standalone: true
})
export class NavsTabsMenuActivePipe implements PipeTransform {
    transform(element: NavTabMenuElement, rlaActive: boolean): boolean {
        if ('active' in element) {
            return element.active;
        } else {
            return rlaActive;
        }
    }
}
