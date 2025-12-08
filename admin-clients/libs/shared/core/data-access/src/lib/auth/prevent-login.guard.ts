import { Location } from '@angular/common';
import { inject } from '@angular/core';
import { CanActivateFn, DefaultUrlSerializer, Router, UrlTree } from '@angular/router';
import { catchError, first, map, Observable, of, switchMap, tap, timeout } from 'rxjs';
import { AUTHENTICATION_SERVICE } from './authentication.token';

export const preventLoginGuard: CanActivateFn = () => preventLogin$();

function preventLogin$(): Observable<boolean> {
    const auth = inject(AUTHENTICATION_SERVICE);
    const router = inject(Router);
    const location = inject(Location);
    const RETURN_URL = 'returnUrl=';

    return auth.isLoggedUserLoading$()
        .pipe(
            timeout(5000),
            first(loading => !loading),
            switchMap(() =>
                auth.getLoggedUser$()
                    .pipe(
                        first(),
                        tap(user => {
                            if (user) {
                                const url = decodeURIComponent(location.path().split(RETURN_URL).at(1) || '/');
                                const { queryParams }: UrlTree = new DefaultUrlSerializer().parse(url);
                                router.navigate([url.split('?').at(0)], { queryParams });
                            }
                        }),
                        map(user => !user)
                    )
            ),
            catchError(() => of(true))
        );
}
