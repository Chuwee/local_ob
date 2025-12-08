import { AuthenticationApi, AuthenticationState, AuthResponseState, User } from '@admin-clients/cpanel/core/data-access';
import { AuthService as Shared_AuthService, I18nService } from '@admin-clients/shared/core/data-access';
import { BasicUser } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthRequestPayload } from 'libs/cpanel/core/data-access/src/lib/auth/model/auth-request-payload.model';
import { catchError, finalize, map, Observable, of, switchMap, tap, throwError } from 'rxjs';
import { DeviceStorage } from '../../../core/services/deviceStorage';
import { TrackingService } from '../../../core/services/tracking.service';
import { UserRoles } from './user-roles.model';

@Injectable({
    providedIn: 'root'
})
export class AuthService implements Shared_AuthService {
    readonly #deviceStorage = inject(DeviceStorage);
    readonly #authApi = inject(AuthenticationApi);
    readonly #authState = inject(AuthenticationState);
    readonly #router = inject(Router);
    readonly #i18n = inject(I18nService);
    readonly #tracking = inject(TrackingService);
    requestLoggedUser(): Observable<BasicUser> {
        this.#authState.loggedUser.setInProgress(true);
        return this.#authApi.requestCurrentUser().pipe(
            tap(user => {
                this.#authState.loggedUser.setValue(user);
                if (user.timezone) {
                    this.#i18n.setTimezone(user.timezone);
                }
                if (user.language) {
                    this.#i18n.setLocale(user.language).subscribe();
                }
                this.#deviceStorage.setItem('user_data', JSON.stringify(user));
            }),
            catchError((error: HttpErrorResponse) => {
                // hemos detectado que el error response cuando expira el token es 0 y de texto unknown error, por eso hemos añadido este condicional
                if (error.status === 404 || error.status === 0 || error.status === 403 || error.status === 401) {
                    this.#router.navigate(['login']);
                }
                this.#tracking.exception({ description: 'Login error', error: error.message });
                throw error;
            }),
            finalize(() => this.#authState.loggedUser.setInProgress(false))
        );
    }

    getLoggedUser$(): Observable<User> {
        return this.#authState.loggedUser.getValue$();
    }

    isLoggedUserLoading$(): Observable<boolean> {
        return this.#authState.loggedUser.isInProgress$();
    }

    getLoggedUserError$(): Observable<HttpErrorResponse> {
        throw new Error('Method not implemented.');
    }

    getToken$(): Observable<string> {
        return this.#authState.token.getValue$();
    }

    getTokenFromStorage(): Observable<string> {
        return this.#deviceStorage.getItem('user_token') as Observable<string>;
    }

    setToken(token: string): void {
        this.#authState.token.setValue(token);
    }

    isTokenLoading$(): Observable<boolean> {
        return this.#authState.token.isInProgress$();
    }

    getLoginError$(): Observable<HttpErrorResponse> {
        return this.#authState.token.getError$();
    }

    clearLoginError$(): void {
        return this.#authState.token.setError(null);
    }

    login(data: AuthRequestPayload): Observable<BasicUser> {
        this.#authState.token.setError(null);
        this.#authState.token.setInProgress(true);

        return this.#authApi.login(data).pipe(
            switchMap(response =>
                (response.state === AuthResponseState.success && response.properties?.access_token) ?
                    of(response) : throwError(() => new HttpErrorResponse({ error: response }))),
            tap(({ properties }) => {
                const token: string = properties.access_token;
                this.#authState.token.setValue(token);
                this.#deviceStorage.setItem('user_token', token);
            }),
            switchMap(() => this.requestLoggedUser()),
            finalize(() => this.#authState.token.setInProgress(false)),
            catchError((response: HttpErrorResponse) => {
                this.#authState.token.setError(response);
                this.#tracking.exception({ description: 'Login error', response });
                return throwError(() => response);
            })
        );
    }

    async logout(): Promise<void> {
        this.#tracking.event('logout');
        this.#authState.loggedUser.setValue(null);
        this.#authState.token.setValue(null);
        this.#deviceStorage.clearStorage();
        sessionStorage.clear();
    }

    forgotPassword(username: string): Observable<{ email: string }> {
        this.#authState.forgotPwd.setInProgress(true);
        return this.#authApi.postforgotPassword(username).pipe(
            map(response => ({ email: response.email })),
            finalize(() => this.#authState.forgotPwd.setInProgress(false))
        );
    }

    isForgotPwdLoading$(): Observable<boolean> {
        return this.#authState.forgotPwd.isInProgress$();
    }

    isNewPwdLoading$(): Observable<boolean> {
        throw new Error('Method not implemented.');
    }

    verifyToken(token: string): Observable<void> {
        throw new Error('Method not implemented.');
    }

    setNewPassword(request: { new_password: string; token: string }): Observable<void> {
        throw new Error('Method not implemented.');
    }

    static isSomeRoleInUserRoles(user: User, matchingRoles: UserRoles[]): boolean {
        if (user?.roles) {
            const userRoles = user.roles.map(userRole => userRole.code);
            return matchingRoles.find(role => userRoles.indexOf(role) > -1) !== undefined;
        }
        return false;
    }

    static operatorCurrencyCodes(user: User): string[] {
        return user?.operator?.currencies?.selected.map(currency => currency.code);
    }
}
