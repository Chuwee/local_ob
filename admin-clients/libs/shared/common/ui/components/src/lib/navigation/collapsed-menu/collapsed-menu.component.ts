
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { BadgeComponent } from '../../badge/badge.component';
import { events } from '../../custom-icons/custom-icons';
import { IconManagerService } from '../../custom-icons/icon-manager.service';
import { SectionList } from '../section-list.models';

@Component({
    selector: 'app-collapsed-menu',
    imports: [
        MatIcon, MatIconButton, MatTooltip, MatDivider, BadgeComponent,
        RouterModule, TranslatePipe, SatPopoverModule, EllipsifyDirective
    ],
    templateUrl: './collapsed-menu.component.html',
    styleUrls: ['./collapsed-menu.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CollapsedMenuComponent {
    private _iconManagerSrv = inject(IconManagerService);

    @Input() sectionList: SectionList;
    @Input() logo: string;

    constructor() {
        this._iconManagerSrv.addIconDefinition(events);
    }
}
