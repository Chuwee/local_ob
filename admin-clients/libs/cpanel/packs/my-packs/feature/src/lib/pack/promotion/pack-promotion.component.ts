import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs';

@Component({
    selector: 'app-pack-promotion',
    templateUrl: './pack-promotion.component.html',
    imports: [
        AsyncPipe, TranslatePipe, RouterModule, MatProgressSpinnerModule, MatIconModule, FormContainerComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackPromoComponent {
    readonly #packsSrv = inject(PacksService);

    readonly loading$ = this.#packsSrv.pack.loading$();
    readonly eventId$ = this.#packsSrv.packItems.get$().pipe(
        filter(Boolean),
        map(items => {
            const mainItem = items.find(item => item.main);
            if (mainItem.type === 'SESSION') return mainItem.session_data.event.id;
            else return mainItem.item_id;
        }));

}
