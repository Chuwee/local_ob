/* eslint-disable @typescript-eslint/dot-notation */
import { CustomersService, Customer } from '@admin-clients/cpanel-viewers-customers-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const customerDetailsResolver: ResolveFn<Customer> = (route: ActivatedRouteSnapshot) => {
    const customersSrv = inject(CustomersService);
    const router = inject(Router);
    const breadcrumbSrv = inject(BreadcrumbsService);

    const id = decodeURIComponent(route.paramMap.get('customerId'));
    const entityId = route.queryParamMap.get('entityId');
    customersSrv.customer.load(id, entityId);
    return combineLatest([
        customersSrv.customer.loading$(),
        customersSrv.customer.get$(),
        customersSrv.customer.error$()
    ]).pipe(
        first(([loading, customer, error]) => !loading && customer !== null || error !== null),
        mergeMap(([_, customer, error]) => {
            if (error) {
                router.navigate(['/customers']);
                return EMPTY;
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbSrv.addDynamicSegment(route.data['breadcrumb'], customer.name);
            }
            return of(customer);
        })
    );
};
