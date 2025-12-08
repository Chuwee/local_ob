import { Location } from '@angular/common';
import { inject } from '@angular/core';
import { Router, CanActivateFn, CanMatchFn } from '@angular/router';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { AuthenticationService } from './authentication.service';

/**
 * The auth guard is used to prevent unauthenticated users from accessing restricted routes.
 *
 * The auth guard will return:
 *
 * - `true`: _If the user is logged in and is authenticated to access the route_
 * - `false`: _If the user is logged out, thus not authenticated to access the route_
 *
 * Here the route access condition is to be logged in (it works on the presence of a valid JWT token).
 */
export const authCanActivateGuard: CanActivateFn = () => checkAuthentication$();
export const authCanMatchGuard: CanMatchFn = () => checkAuthentication$();

function checkAuthentication$(): Observable<boolean> {
    const router = inject(Router);
    const auth = inject(AuthenticationService);
    const location = inject(Location);
    return auth.getToken$()
        .pipe(
            first(),
            map(token => {
                const authed = !!token;
                if (!authed) {
                    // not logged in so redirect to login page with the return url
                    router.navigate(['/login'], {
                        queryParams: { returnUrl: location.path() }
                    });
                    return false;
                }

                return true;
            }),
            first()
        );
}
