/* eslint-disable @typescript-eslint/dot-notation */
import { channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { SaleRequest, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { Router, ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { EMPTY, combineLatest, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const saleRequestDetailsResolver: ResolveFn<SaleRequest> = (route: ActivatedRouteSnapshot) => {
    const salesRequestSrv = inject(SalesRequestsService);
    const router = inject(Router);
    const breadcrumbSrv = inject(BreadcrumbsService);
    const id = Number(route.paramMap.get('saleRequestId'));

    salesRequestSrv.clearSaleRequest();
    salesRequestSrv.loadSaleRequest(id);
    return combineLatest([
        salesRequestSrv.getSaleRequest$(),
        salesRequestSrv.getSaleRequestError$()
    ])
        .pipe(
            first(([saleRequest, error]) => saleRequest !== null || error !== null),
            mergeMap(([saleRequest, error]) => {
                if (error) {
                    router.navigate(['/sales-requests']);
                    return EMPTY;
                }
                if (!resolveGuard(route, saleRequest)) {
                    navigateBack(route, router);
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbSrv
                        .addDynamicSegment(route.data['breadcrumb'], `${saleRequest.event.name}-${saleRequest.channel.name}`);
                }

                return of(saleRequest);
            })
        );
};

// guard to control if the route is available for the current channel type
function resolveGuard(route: ActivatedRouteSnapshot, saleRequest: SaleRequest): boolean {
    const child = route.firstChild;
    if (child) {
        const subpath = child.firstChild?.routeConfig.path;
        switch (child.routeConfig.path) {
            case 'configuration':
                if (subpath && subpath === 'payment-methods' &&
                    !channelWebTypes.includes(saleRequest.channel.type)) {
                    return false;
                }
                break;
            default:
                return true;
        }
    }
    return true;
}

// navigates to saleRequest default route
function navigateBack(route: ActivatedRouteSnapshot, router: Router): void {
    let url = '';
    let parent = route.parent;

    while (parent) {
        if (parent.url[0]) {
            url = `${parent.url[0].path}/${url}`;
        }
        parent = parent.parent || null;
    }
    router.navigate([url]);
}
