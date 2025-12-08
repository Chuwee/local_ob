import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { first, of } from 'rxjs';

export const productPromotionDetailsResolver: ResolveFn<number> = route => {
    const productSrv = inject(ProductsService);
    const breadcrumbsSvc = inject(BreadcrumbsService);
    const promoId = Number(route.paramMap.get('promotionId'));

    breadcrumbsSvc.addDynamicSegment(route.data['breadcrumb'], 'LOADING');

    productSrv.product.get$().pipe(first()).subscribe(product => {
        productSrv.product.promotion.load(product.product_id, promoId);
    });

    productSrv.product.promotion.get$()
        .pipe(first(promotion => promotion?.id === promoId))
        .subscribe(promotion =>
            breadcrumbsSvc.addDynamicSegment(route.data['breadcrumb'], promotion.name)
        );

    return of(promoId);
};
