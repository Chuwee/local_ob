import { OrderDetail, OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { EMPTY, combineLatest, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const orderDetailsResolver: ResolveFn<OrderDetail> = (route: ActivatedRouteSnapshot) => {
    const ordersSrv = inject(OrdersService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const translate = inject(TranslateService);

    const orderCode = route.paramMap.get('orderCode');
    ordersSrv.loadOrderDetail(orderCode);

    return combineLatest([
        ordersSrv.isOrderDetailLoading$(),
        ordersSrv.getOrderDetail$(),
        ordersSrv.getOrderDetailError$()
    ]).pipe(
        first(([loading, order, error]) => !loading && order !== null || error !== null),
        mergeMap(([_, orderDetail, error]) => {
            if (error) {
                router.navigate(['/transactions']);
                return EMPTY;
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'],
                    translate.instant('TITLES.ORDER', { orderCode: orderDetail.code }));
            }

            return of(orderDetail);
        })
    );
};
