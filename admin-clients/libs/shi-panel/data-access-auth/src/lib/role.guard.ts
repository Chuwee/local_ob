import { UserRoles } from '@admin-clients/shi-panel/utility-models';
import { inject } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivateFn,
    CanMatchFn, Route,
    Router,
    RouterStateSnapshot,
    UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { AuthenticationService } from './authentication.service';

export const roleCanActivateGuard: CanActivateFn = (route: ActivatedRouteSnapshot, _: RouterStateSnapshot) => {
    const { roles }: { roles?: UserRoles[] } = route.data;
    return checkRoles$(roles);
};

export const roleCanMatchGuard: CanMatchFn = (route: Route) => {
    const { roles }: { roles?: UserRoles[] } = route.data;
    return checkRoles$(roles);
};

/**
 * Determine if the user has role permission and redirects in negative case.
 */
function checkRoles$(roles: UserRoles[]): Observable<boolean | UrlTree> {
    const router = inject(Router);
    const auth = inject(AuthenticationService);

    if (!roles) {
        throw Error('You must specify an array of roles inside route data in order to use RoleGuard');
    }
    return auth.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => AuthenticationService.doesUserHaveSomeRole(user, roles)),
            map(isPermitted => isPermitted ? true : router.parseUrl('/my-user'))
        );
}

