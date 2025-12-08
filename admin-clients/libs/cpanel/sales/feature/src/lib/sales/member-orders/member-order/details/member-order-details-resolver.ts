import { MemberOrdersService, MemberOrderDetail } from '@admin-clients/cpanel-sales-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { EMPTY, combineLatest, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const memberOrderDetailsResolver: ResolveFn<MemberOrderDetail> = (route: ActivatedRouteSnapshot) => {
    const memberOrdersSrv = inject(MemberOrdersService);
    const router = inject(Router);
    const breadcrumbsService = inject(BreadcrumbsService);
    const translate = inject(TranslateService);

    const code = route.paramMap.get('code');
    memberOrdersSrv.loadMemberOrderDetail(code);

    return combineLatest([
        memberOrdersSrv.getMemberOrderDetail$(),
        memberOrdersSrv.getMemberOrderDetailError$(),
        memberOrdersSrv.isMemberOrderDetailLoading$()
    ]).pipe(
        first(([order, error, loading]) => !loading && order !== null || error !== null),
        mergeMap(([order, error]) => {
            if (error) {
                router.navigate(['/member-orders']);
                return EMPTY;
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbsService.addDynamicSegment(route.data['breadcrumb'],
                    translate.instant('TITLES.MEMBER_ORDER', { code: order.code }));
            }

            return of(order);
        })
    );
};
