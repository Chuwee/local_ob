import { VoucherOrdersService, VoucherOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { EMPTY, combineLatest, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const voucherOrderDetailsResolver: ResolveFn<VoucherOrderDetail> = (route: ActivatedRouteSnapshot) => {
    const voucherOrdersSrv = inject(VoucherOrdersService);
    const router = inject(Router);
    const breadcrumbsService = inject(BreadcrumbsService);
    const translate = inject(TranslateService);

    const code = route.paramMap.get('voucherOrderCode');
    voucherOrdersSrv.loadVoucherOrderDetail(code);

    return combineLatest([
        voucherOrdersSrv.getVoucherOrderDetail$(),
        voucherOrdersSrv.getVoucherOrderDetailError$(),
        voucherOrdersSrv.isVoucherOrderDetailLoading$()
    ]).pipe(
        first(([voucherOrder, error, loading]) => !loading && voucherOrder !== null || error !== null),
        mergeMap(([voucherOrderDetail, error]) => {
            if (error) {
                router.navigate(['/voucher-orders']);
                return EMPTY;
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbsService.addDynamicSegment(route.data['breadcrumb'],
                    translate.instant('TITLES.VOUCHER_ORDER', { voucherOrderCode: voucherOrderDetail.code }));
            }

            return of(voucherOrderDetail);
        })
    );
};
