import { EntityUser, EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const entityUserDetailsResolver: ResolveFn<EntityUser> = (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const entityUsersSrv = inject(EntityUsersService);

    const id = route.paramMap.get('userId');
    if (!id) {
        entityUsersSrv.loadEntityUser('myself');
    } else {
        entityUsersSrv.loadEntityUser(Number(id));
    }

    return combineLatest([
        entityUsersSrv.getEntityUser$(),
        entityUsersSrv.getEntityUserError$()
    ])
        .pipe(
            first(([user, error]) => user !== null || error !== null),
            mergeMap(([user, error]) => {
                if (error) {
                    router.navigate(['/users']);
                    return EMPTY;
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], user.username);
                }

                return of(user);
            })
        );
};
