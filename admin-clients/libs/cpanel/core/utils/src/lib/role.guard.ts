import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { first, map } from 'rxjs/operators';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot, _: RouterStateSnapshot) => {
    const router = inject(Router);
    const auth = inject(AuthenticationService);
    const roles = route.data['roles'] as UserRoles[];
    if (!roles) {
        throw Error('You must specify an array of roles inside route data in order to use RoleGuard');
    }
    return auth.getLoggedUser$()
        .pipe(
            first(user => !!user),
            map(user => AuthenticationService.isSomeRoleInUserRoles(user, roles)),
            map(isPermitted => isPermitted ? true : router.parseUrl('/'))
        );

};
