import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AsyncPipe, NgClass, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDrawer } from '@angular/material/sidenav';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { PacksListComponent } from '../list/packs-list.component';

@Component({
    selector: 'app-packs-container',
    templateUrl: './packs-container.component.html',
    styleUrls: ['./packs-container.component.scss'],
    imports: [
        MaterialModule, EmptyStateComponent, NgIf, NgClass, FlexLayoutModule, AsyncPipe, TranslatePipe, PacksListComponent, RouterModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PacksContainerComponent {
    readonly #packsSrv = inject(PacksService);

    readonly isLoading$ = this.#packsSrv.packList.loading$();
    @ViewChild(PacksListComponent) listComponent: PacksListComponent;

    sidebarToggle(drawer: MatDrawer): void {
        drawer.toggle();
    }
}
