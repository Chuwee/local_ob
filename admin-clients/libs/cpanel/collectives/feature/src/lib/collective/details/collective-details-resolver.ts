/* eslint-disable @typescript-eslint/dot-notation */
import { Collective, CollectivesService } from '@admin-clients/cpanel/collectives/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const collectiveDetailsResolver: ResolveFn<Collective> = (route: ActivatedRouteSnapshot) => {
    const breadcrumbsServ = inject(BreadcrumbsService);
    const collectiveSrv = inject(CollectivesService);

    const id = Number(route.paramMap.get('collectiveId'));

    collectiveSrv.loadCollective(id);

    return collectiveSrv.getCollective$().pipe(
        first(Boolean),
        mergeMap(collective => {
            if (route.data?.['breadcrumb']) {
                breadcrumbsServ.addDynamicSegment(route.data?.['breadcrumb'], collective.name);
            }
            return of(collective);
        })
    );
};
