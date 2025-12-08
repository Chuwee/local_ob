
import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BadgeComponent } from '../../badge/badge.component';
import { events } from '../../custom-icons/custom-icons';
import { IconManagerService } from '../../custom-icons/icon-manager.service';
import { SectionList } from '../section-list.models';

@Component({
    selector: 'app-expanded-menu',
    imports: [
        MatExpansionPanel, MatAccordion, BadgeComponent,
        MatIcon, MatExpansionPanelHeader, MatExpansionPanelTitle,
        RouterLink, RouterLinkActive, TranslatePipe
    ],
    templateUrl: './expanded-menu.component.html',
    styleUrls: ['./expanded-menu.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExpandedMenuComponent {
    private _iconManagerSrv = inject(IconManagerService);
    @Input() sectionList: SectionList;
    @Input() logo: string;

    constructor() {
        this._iconManagerSrv.addIconDefinition(events);
    }
}
