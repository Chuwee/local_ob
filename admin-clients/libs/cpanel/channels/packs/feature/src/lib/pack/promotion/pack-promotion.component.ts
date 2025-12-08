import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter } from 'rxjs';

@Component({
    selector: 'app-pack-promotion',
    templateUrl: './pack-promotion.component.html',
    imports: [
        AsyncPipe, TranslatePipe, RouterModule, MatProgressSpinnerModule, MatIconModule, FormContainerComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackPromoComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #router = inject(Router);

    readonly loading$ = this.#packsSrv.pack.loading$();
    channelId: number;
    promoId: number;

    ngOnInit(): void {
        combineLatest([
            this.#packsSrv.pack.get$(),
            this.#channelsSrv.getChannel$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([pack, channel]) => {
            this.channelId = channel.id;
            this.promoId = pack.promotion.promotion_id;
            if (pack.type === 'AUTOMATIC') {
                this.#router.navigate(['/channels', this.channelId, 'packs', pack.id, 'elements']);
            }
        });
    }
}
