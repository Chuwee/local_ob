import { ProductSaleRequest, ProductsSaleRequestsService } from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, first, mergeMap, of } from 'rxjs';

export const productSaleRequestDetailsResolver: ResolveFn<ProductSaleRequest> = (route: ActivatedRouteSnapshot) => {
    const productsSaleRequestSrv = inject(ProductsSaleRequestsService);
    const router = inject(Router);
    const breadcrumbSrv = inject(BreadcrumbsService);

    const id = Number(route.paramMap.get('saleRequestId'));

    productsSaleRequestSrv.productSaleRequest.clear();
    productsSaleRequestSrv.productSaleRequest.load(id);
    return combineLatest([
        productsSaleRequestSrv.productSaleRequest.get$(),
        productsSaleRequestSrv.productSaleRequest.error$()
    ]).pipe(
        first(values => values.some(value => !!value)),
        mergeMap(([saleRequest, error]) => {
            if (error) {
                router.navigate(['/products-sale-requests']);
                return EMPTY;
            } else {
                breadcrumbSrv.addDynamicSegment(
                    route.data?.['breadcrumb'],
                    `${saleRequest.product.product_name}-${saleRequest.channel.name}`
                );
            }
            return of(saleRequest);
        })
    );
};
