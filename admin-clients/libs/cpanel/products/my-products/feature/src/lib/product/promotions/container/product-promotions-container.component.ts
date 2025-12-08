import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDrawer, MatDrawerContainer, MatDrawerContent } from '@angular/material/sidenav';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs';
import { ProductPromotionsListComponent } from '../list/product-promotions-list.component';

@Component({
    selector: 'app-product-promotions-container',
    templateUrl: './product-promotions-container.component.html',
    styleUrls: ['./product-promotions-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDrawerContainer, MatDrawer, MatDrawerContent, RouterOutlet, EmptyStateComponent,
        MatIconModule, TranslatePipe, FlexLayoutModule, MatProgressSpinnerModule, MatButtonModule,
        NgClass, ProductPromotionsListComponent
    ]
})
export class ProductPromotionsContainerComponent {
    readonly $filterComponent = viewChild(ProductPromotionsListComponent);
    readonly #productsSrv = inject(ProductsService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly $isLoadingPromotion = toSignal(this.#productsSrv.product.promotion.loading$());
    readonly $promotionListMetadata = toSignal(this.#productsSrv.product.promotionList.getMetadata$());
    readonly $sidebarWidth = toSignal(this.#breakpointObserver
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(map(result => result.matches ? '240px' : '290px')));
}
