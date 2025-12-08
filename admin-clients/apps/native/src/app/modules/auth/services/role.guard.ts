import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { first, map } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { UserRoles } from './user-roles.model';
// TODO look to unify with cpanel-client
export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot, _: RouterStateSnapshot) => {
    const router = inject(Router);
    const auth = inject(AuthService);
    const roles = route.data['roles'] as UserRoles[];
    if (!roles) {
        throw Error('You must specify an array of roles inside route data in order to use RoleGuard');
    }

    return auth.getLoggedUser$()
        .pipe(
            first(user => !!user),
            map(user => AuthService.isSomeRoleInUserRoles(user, roles)),
            map(isPermitted => isPermitted ? true : router.parseUrl('/'))
        );

};
