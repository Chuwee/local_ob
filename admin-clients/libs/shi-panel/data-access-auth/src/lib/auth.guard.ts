import { Location } from '@angular/common';
import { inject } from '@angular/core';
import {
    Router,
    ActivatedRouteSnapshot,
    RouterStateSnapshot,
    Route,
    CanActivateFn, CanMatchFn
} from '@angular/router';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { AuthenticationService } from './authentication.service';

/*
The auth guard is used to prevent unauthenticated users from accessing restricted routes.
The auth guard will return:
true: If the user is logged in and is authenticated to access the route
false: If the user is logged out, thus not authenticated to access the route

Here the route access condition is to be logged in (it works on the presence of a valid JWT token)
There can be other conditions too, like role based authentication
 */

export const authCanActivateGuard: CanActivateFn = (router: ActivatedRouteSnapshot, state: RouterStateSnapshot) => checkAuthentication$();
export const authCanMatchGuard: CanMatchFn = (route: Route) => checkAuthentication$();

/**
 * Determine if the user is logged and redirects in negative case.
 */
function checkAuthentication$(): Observable<boolean> {
const auth = inject(AuthenticationService);
const location = inject(Location);
const router = inject(Router);
return auth.getToken$()
    .pipe(
        first(),
        map(token => {
            const authed = !!token;
            if (!authed) {
                // not logged in so redirect to login page with the return url
                router.navigate(['/login'], { queryParams: { returnUrl: location.path() } });
                return false;
            }
            return true;
        }),
        first()
    );
}
