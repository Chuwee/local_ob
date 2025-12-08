/* eslint-disable @typescript-eslint/dot-notation */
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { of } from 'rxjs';
import { first, mergeMap, take } from 'rxjs/operators';
import { Voucher, VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';

export const voucherResolver: ResolveFn<Voucher> = (route: ActivatedRouteSnapshot) => {
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const voucherSrv = inject(VouchersService);

    const code: string = route.paramMap.get('code');

    voucherSrv.getVoucherGroup$()
        .pipe(take(1))
        .subscribe(voucherGroup => voucherSrv.loadVoucher(voucherGroup.id, code));

    return voucherSrv.getVoucher$().pipe(
        first(Boolean),
        mergeMap(voucher => {
            if (route.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], code);
            }
            return of(voucher);
        })
    );
};
