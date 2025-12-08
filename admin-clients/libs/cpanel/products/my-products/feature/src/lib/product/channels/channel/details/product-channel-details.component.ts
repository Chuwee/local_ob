import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { ProductEventsService } from '@admin-clients/cpanel-products-my-products-events-data-access';
import {
    NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { RouterOutlet } from '@angular/router';
import { filter, map, tap } from 'rxjs';

@Component({
    selector: 'app-product-channel-details',
    imports: [MatProgressSpinner, FlexLayoutModule, NavTabsMenuComponent, AsyncPipe, RouterOutlet],
    templateUrl: './product-channel-details.component.html',
    styleUrls: ['./product-channel-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductChannelDetailsComponent implements OnDestroy {
    readonly #productsSrv = inject(ProductsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #productEventsSrv = inject(ProductEventsService);

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);
    readonly isInProgress$ = this.#productsSrv.product.channelsList.inProgress$();

    readonly $selectedProductChannel = toSignal(this.#productsSrv.product.channel.get$().pipe(
        filter(Boolean),
        tap(productChannel => this.#productEventsSrv.productEvents.list.load(productChannel.product.id))));

    readonly $productEvents = toSignal(this.#productEventsSrv.productEvents.list.get$().pipe(filter(Boolean)));

    readonly $isNotBoxOfficeChannel = toSignal(this.#productsSrv.product.channel.get$()
        .pipe(map(productChannel => !productChannel || productChannel.channel.type !== ChannelType.boxOffice)));

    ngOnDestroy(): void {
        this.#productsSrv.product.channelsList.clear();
        this.#productsSrv.product.channel.clear();
        this.#productEventsSrv.productEvents.list.clear();
    }
}
