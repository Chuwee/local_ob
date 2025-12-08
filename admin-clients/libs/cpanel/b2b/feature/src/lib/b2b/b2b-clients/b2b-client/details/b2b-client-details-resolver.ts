import { B2bClient, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, map, mergeMap } from 'rxjs/operators';

export const b2bClientDetailsResolver: ResolveFn<B2bClient> = (route: ActivatedRouteSnapshot) => {
    const b2bSrv = inject(B2bService);
    const authSrv = inject(AuthenticationService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const b2bClientId = parseInt(route.paramMap.get('b2bClientId'));
    const entityId = parseInt(route.queryParamMap.get('entityId'));

    authSrv.getLoggedUser$()
        .pipe(
            first(user => !!user),
            map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]))
        )
        .subscribe(isOperator => {
            if (isOperator && entityId) {
                b2bSrv.loadB2bClient(b2bClientId, entityId);
            } else {
                b2bSrv.loadB2bClient(b2bClientId);
            }
        });

    return combineLatest([
        b2bSrv.getB2bClient$(),
        b2bSrv.getB2bClientError$()
    ]).pipe(
        first(([b2bClient, error]) => !!b2bClient || !!error),
        mergeMap(([b2bClient, error]) => {
            if (error) {
                router.navigate(['/b2b-clients']);
                return EMPTY;
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], b2bClient.name);
            }
            return of(b2bClient);
        })
    );
};
