import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-pack-details',
    templateUrl: './pack-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [FlexModule, GoBackComponent, NavTabsMenuComponent, RouterOutlet, AsyncPipe, MatTooltip, TranslatePipe]
})
export class PackDetailsComponent implements OnDestroy {
    readonly #packsService = inject(PacksService);
    readonly pack$ = this.#packsService.pack.get$();

    ngOnDestroy(): void {
        this.#packsService.pack.clear();
    }
}
