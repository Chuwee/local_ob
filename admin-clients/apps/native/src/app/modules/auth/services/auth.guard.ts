import { inject } from '@angular/core';
import { CanActivateFn, CanMatchFn, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { AuthService } from './auth.service';

export const authCanActivateGuard: CanActivateFn = () => checkAuthentication$();
export const authCanMatchGuard: CanMatchFn = () => checkAuthentication$();

function checkAuthentication$(): Observable<boolean> {
    const router = inject(Router);
    const auth = inject(AuthService);
    return auth.getTokenFromStorage()
        .pipe(
            first(),
            map(token => {
                const authed = !!token;
                if (!authed) {
                    router.navigate(['/login']);
                    return false;
                }

                return true;
            })
        );
}

