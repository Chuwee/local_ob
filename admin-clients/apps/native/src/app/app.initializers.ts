import { EnvironmentProviders, inject, provideAppInitializer } from '@angular/core';
import { map, of, switchMap, Observable } from 'rxjs';
import { AuthService } from './modules/auth/services/auth.service';

function retrieveUserFactory(): Observable<boolean> {
    const authService = inject(AuthService);
    return authService.getTokenFromStorage().pipe(
        switchMap(token => {
            if (token) {
                authService.setToken(token);
                return authService.requestLoggedUser();
            }
            return of(null);
        }),
        map(() => true)
    );
}

export const APP_INITIALIZERS: EnvironmentProviders[] = [
    provideAppInitializer(() => retrieveUserFactory())
];
