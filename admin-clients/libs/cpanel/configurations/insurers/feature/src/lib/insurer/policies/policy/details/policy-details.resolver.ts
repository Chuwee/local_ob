import { InsurersService, Policy } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const insurerPolicyDetailsResolver: ResolveFn<Policy> = (route: ActivatedRouteSnapshot) => {
    const insurerSrv = inject(InsurersService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const allRouteParams = Object.assign({}, ...route.pathFromRoot.map(path => path.params));
    const insurerId = allRouteParams.insurerId;
    const policyId = allRouteParams.policyId;

    insurerSrv.policy.clear();
    insurerSrv.policy.load(insurerId, policyId);
    insurerSrv.policyRanges.load(insurerId, policyId);
    insurerSrv.policyTermsConditions.load(insurerId, policyId);

    return combineLatest([
        insurerSrv.policy.get$(),
        insurerSrv.policy.error$()
    ]).pipe(
        first(([policy, policyError]) => !!(policy || policyError)),
        mergeMap(([policy, policyError]) => {
            if (policyError) {
                router.navigate(['/insurers', insurerId, 'policies']);
                return EMPTY;
            } else {
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], policy.name);
                return of(policy);
            }
        })
    );
};
