import { Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const operatorDetailsResolver: ResolveFn<Operator> = (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const operatorsSrv = inject(OperatorsService);

    const id = Number(route.paramMap.get('operatorId'));
    operatorsSrv.operator.clear();
    operatorsSrv.operator.load(id);

    return combineLatest([
        operatorsSrv.operator.get$(),
        operatorsSrv.operator.error$()
    ])
        .pipe(
            first(([operator, error]) => operator !== null || error !== null),
            mergeMap(([operator, error]) => {
                if (error) {
                    router.navigate(['/operators']);
                    return EMPTY;
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], operator.name);
                }

                return of(operator);
            })
        );

};
