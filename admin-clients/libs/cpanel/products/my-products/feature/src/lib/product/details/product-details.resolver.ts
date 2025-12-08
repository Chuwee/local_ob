/* eslint-disable @typescript-eslint/dot-notation */
import { Product, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const productDetailsResolver: ResolveFn<Product> = (route: ActivatedRouteSnapshot) => {
    const productSrv = inject(ProductsService);
    const entitiesSrv = inject(EntitiesBaseService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const id = route.paramMap.get('productId');

    productSrv.product.load(Number(id));

    return combineLatest([
        productSrv.product.get$(),
        productSrv.product.error$()
    ]).pipe(
        first(values => values.some(value => !!value)),
        mergeMap(([product, error]) => {
            if (error) {
                router.navigate(['/products']);
                return EMPTY;
            } else {
                entitiesSrv.clearEntity();
                entitiesSrv.loadEntity(product.entity.id);
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], product.name);
                return of(product);
            }
        }));
};
