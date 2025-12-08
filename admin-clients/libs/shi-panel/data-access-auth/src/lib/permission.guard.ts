import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { inject } from '@angular/core';
import {
    ActivatedRouteSnapshot,
    CanActivateFn, CanMatchFn, Route,
    Router,
    RouterStateSnapshot,
    UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { AuthenticationService } from './authentication.service';

export const permissionCanActivateGuard: CanActivateFn = (route: ActivatedRouteSnapshot, _: RouterStateSnapshot) => {
    const { permissions }: { permissions?: UserPermissions[] } = route.data;
    return checkPermissions$(permissions);
};
export const permissionCanMatchGuard: CanMatchFn = (route: Route) => {
    const { permissions }: { permissions?: UserPermissions[] } = route.data;
    return checkPermissions$(permissions);
};

/**
 * Determine if the user has permission and redirects in negative case.
 */
function checkPermissions$(permissions: UserPermissions[]): Observable<boolean | UrlTree> {
    const router = inject(Router);
    const auth = inject(AuthenticationService);

    if (!permissions.length) {
        throw Error('You must specify an array of permissions inside route data in order to use PermissionGuard');
    }
    return auth.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => AuthenticationService.doesUserHaveSomePermission(user, permissions)),
            map(isPermitted => isPermitted ? true : router.parseUrl('/my-user'))
        );
}
