import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject, viewChild, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, map, tap } from 'rxjs';
import { ProductChannelsListComponent } from '../list/product-channels-list.component';

@Component({
    selector: 'app-product-channels-container',
    imports: [
        NgClass, MaterialModule, TranslatePipe, FlexModule, FlexLayoutModule, RouterModule,
        ProductChannelsListComponent, EmptyStateComponent, AsyncPipe
    ],
    templateUrl: './product-channels-container.component.html',
    styleUrls: ['./product-channels-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductChannelsContainerComponent implements OnDestroy, OnInit {
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #productsSrv = inject(ProductsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);

    readonly productChannels$ = this.#productsSrv.product.channelsList.get$();

    readonly isLoading$ = this.#productsSrv.product.channelsList.inProgress$();

    readonly sidebarWidth$: Observable<string> = this.#breakpointObserver
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(
            map(result => result.matches ? '240px' : '280px')
        );

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);

    listComponent = viewChild(ProductChannelsListComponent);

    ngOnInit(): void {
        this.#productsSrv.product.get$().pipe(
            takeUntilDestroyed(this.#destroyRef),
            tap(product => {
                this.#productsSrv.product.channelsList.load(product.product_id);
            })
        ).subscribe();
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.channelsList.clear();
    }

    addChannels(): void {
        this.listComponent().openAddChannelsDialog();
    }

}
