import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap, take } from 'rxjs/operators';

export const entityDetailsResolver: ResolveFn<Entity> = (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);
    const auth = inject(AuthenticationService);
    const entitiesSrv = inject(EntitiesService);
    const breadcrumbsService = inject(BreadcrumbsService);

    const id = route.paramMap.get('entityId');
    if (!id) {
        auth.getLoggedUser$()
            .pipe(take(1))
            .subscribe(user => entitiesSrv.loadEntity(user.entity.id));
    } else {
        entitiesSrv.loadEntity(Number(id));
    }

    return combineLatest([
        entitiesSrv.getEntity$(),
        entitiesSrv.entity.error$()
    ])
        .pipe(
            first(values => values.some(value => !!value)),
            mergeMap(([entity, error]) => {
                if (error) {
                    router.navigate(['/entities']);
                    return EMPTY;
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbsService.addDynamicSegment(route.data['breadcrumb'], entity.name);
                }

                return of(entity);
            })
        );
};
