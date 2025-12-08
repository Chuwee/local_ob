import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntityUserPermissions } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { inject } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivateFn, CanMatchFn, Route,
    RouterStateSnapshot,
    UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';

export const entityUserPermissionCanActivateGuard: CanActivateFn = (route: ActivatedRouteSnapshot, _: RouterStateSnapshot) => {
    const { permissions }: { permissions?: EntityUserPermissions[] } = route.data;
    return checkPermissions$(permissions);
};
export const entityUserPermissionCanMatchGuard: CanMatchFn = (route: Route) => {
    const { permissions }: { permissions?: EntityUserPermissions[] } = route.data;
    return checkPermissions$(permissions);
};

/**
 * Determine if the user has permission and redirects in negative case.
 */
function checkPermissions$(permissions: EntityUserPermissions[]): Observable<boolean | UrlTree> {
    const auth = inject(AuthenticationService);

    if (!permissions.length) {
        throw Error('You must specify an array of permissions inside route data in order to use PermissionGuard');
    }
    return auth.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => !!user.roles.filter(role => role.permissions
                ?.some(permission => permissions.includes(permission as EntityUserPermissions))).length)
        );
}
