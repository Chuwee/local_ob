/* eslint-disable @typescript-eslint/dot-notation */
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { first, map } from 'rxjs/operators';
import { VoucherGroup, VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';

export const voucherGroupDetailsResolver: ResolveFn<VoucherGroup> = (route: ActivatedRouteSnapshot) => {
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const voucherSrv = inject(VouchersService);

    const id = Number(route.paramMap.get('voucherGroupId'));
    voucherSrv.loadVoucherGroup(id);

    return voucherSrv.getVoucherGroup$().pipe(
        first(Boolean),
        map(voucherGroup => {
            if (route.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], voucherGroup.name);
            }
            return voucherGroup;
        })
    );
};
