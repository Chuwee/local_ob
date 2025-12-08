import { NgClass, NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BadgeComponent } from '../badge/badge.component';
import { NavTabMenuElement } from './models/nav-tabs-menu.models';
import { NavsTabsMenuActivePipe } from './navs-tabs-menu-active.pipe';
import { NavsTabsMenuShowPipe } from './navs-tabs-menu-show.pipe';

type NavTabsMenuType = 'horizontalSubtab' | 'tabMenu' | 'navigation';

const typesClass: Record<NavTabsMenuType, string> = {
    horizontalSubtab: 'ob-horizontal-subtab',
    tabMenu: 'ob-tab-menu',
    navigation: 'ob-tab-component ob-navigation-tabs'
} as const;

@Component({
    imports: [
        TranslatePipe, MatTabsModule, MatTooltipModule, NgClass, NgTemplateOutlet,
        RouterLink, RouterLinkActive, NavsTabsMenuShowPipe, NavsTabsMenuActivePipe, BadgeComponent
    ],
    selector: 'app-nav-tabs-menu',
    templateUrl: './nav-tabs-menu.component.html',
    styleUrls: ['./nav-tabs-menu.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavTabsMenuComponent {
    typeClass: string;

    @Input() elements: NavTabMenuElement[] = [];
    @Input() disabled = false;
    @Input() set type(type: NavTabsMenuType) {
        this.typeClass = typesClass[type];
    }
}
