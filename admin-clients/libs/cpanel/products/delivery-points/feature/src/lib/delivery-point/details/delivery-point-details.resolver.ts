import { DeliveryPoint, ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const deliveryPointDetailsResolver: ResolveFn<DeliveryPoint> = (route: ActivatedRouteSnapshot) => {
    const deliveryPointSrv = inject(ProductsDeliveryPointsService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const id = route.paramMap.get('deliveryPointId');

    deliveryPointSrv.deliveryPoint.load(Number(id));

    return combineLatest([
        deliveryPointSrv.deliveryPoint.get$(),
        deliveryPointSrv.deliveryPoint.error$()
    ]).pipe(
        first(values => values.some(value => !!value)),
        mergeMap(([deliveryPoint, error]) => {
            if (error) {
                router.navigate(['/delivery-points']);
                return EMPTY;
            } else {
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], deliveryPoint.name);
                return of(deliveryPoint);
            }
        }));
};
