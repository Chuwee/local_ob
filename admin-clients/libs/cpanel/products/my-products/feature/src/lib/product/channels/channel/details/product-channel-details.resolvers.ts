import { ProductChannel, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const productChannelDetailsResolver: ResolveFn<ProductChannel> = (route: ActivatedRouteSnapshot) => {
    const productSrv = inject(ProductsService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const allRouteParams = Object.assign({}, ...route.pathFromRoot.map(path => path.params));
    const productId = allRouteParams.productId;
    const channelId = allRouteParams.channelId;

    productSrv.product.channel.clear();
    productSrv.product.channel.load(productId, channelId);

    return combineLatest([
        productSrv.product.channel.get$(),
        productSrv.product.channel.error$()
    ]).pipe(
        first(([channel, channelError]) => !!(channel || channelError)),
        mergeMap(([channel, channelError]) => {
            if (channelError) {
                router.navigate(['/products', productId, 'channels']);
                return EMPTY;
            } else {
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], channel.channel.name);
                return of(channel);
            }
        })
    );
};
