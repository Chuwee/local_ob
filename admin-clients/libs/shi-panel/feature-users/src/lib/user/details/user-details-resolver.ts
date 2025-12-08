import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { User } from '@admin-clients/shi-panel/utility-models';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';
import { UsersService } from '../../users.service';

export const userDetailsResolver: ResolveFn<User> = (route: ActivatedRouteSnapshot) => {
    const usersSrv = inject(UsersService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const auth = inject(AuthenticationService);

    const id = route.paramMap.get('userId');
    if (!id) {
        let myId;
        auth.getLoggedUser$().subscribe(user => myId = user?.id);
        usersSrv.userDetailsProvider.loadUser(myId);
    } else {
        usersSrv.userDetailsProvider.loadUser(id);
    }
    return combineLatest([
        usersSrv.userDetailsProvider.getUser$(),
        usersSrv.userDetailsProvider.error$()
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
