import { Insurer, InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, first, mergeMap, of } from 'rxjs';

export const insurerDetailsResolver: ResolveFn<Insurer> = (route: ActivatedRouteSnapshot) => {
    const insurerSrv = inject(InsurersService);
    const router = inject(Router);
    const breadcrumbSrv = inject(BreadcrumbsService);

    const id = route.paramMap.get('insurerId');

    insurerSrv.insurer.load(Number(id));

    return combineLatest([
        insurerSrv.insurer.get$(),
        insurerSrv.insurer.error$()
    ]).pipe(
        first(values => values.some(value => !!value)),
        mergeMap(([insurer, error]) => {
            if (error) {
                router.navigate(['/insurers']);
                return EMPTY;
            } else {
                breadcrumbSrv.addDynamicSegment(route.data?.['breadcrumb'], insurer.name);
                return of(insurer);
            }
        })
    );
};
