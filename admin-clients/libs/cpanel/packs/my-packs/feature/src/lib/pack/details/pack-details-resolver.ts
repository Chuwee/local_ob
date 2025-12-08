import { Pack, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, first, mergeMap, of } from 'rxjs';

export const packDetailsResolver: ResolveFn<Pack> = (route: ActivatedRouteSnapshot) => {
    const packsSrv = inject(PacksService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const id = route.paramMap.get('packId');
    packsSrv.pack.load(Number(id));
    packsSrv.packItems.load(Number(id));

    return combineLatest([
        packsSrv.pack.get$(),
        packsSrv.pack.getError$()
    ]).pipe(
        first(([pack, error]) => pack !== null || error !== null),
        mergeMap(([pack, error]) => {
            if (error) {
                router.navigate(['/pack']);
                return EMPTY;
            }
            if (route.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], pack.name);
            }
            return of(pack);
        })
    );

    return of(null);
};
