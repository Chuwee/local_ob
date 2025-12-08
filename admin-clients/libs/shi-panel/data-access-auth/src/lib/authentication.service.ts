import { I18nService, AuthService } from '@admin-clients/shared/core/data-access';
import { User, UserPermissions, UserRoles } from '@admin-clients/shi-panel/utility-models';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { catchError, filter, finalize, map, mapTo, switchMap, tap } from 'rxjs/operators';
import { AuthenticationApi } from './authentication.api';
import { AuthenticationState } from './state/authentication.state';

// authentication service is used to LOGIN and LOGOUT of the application
// it posts the creds (username and password) to the backend and check for the response if it has JWT token
// if the response from the backend has jwt token, then the authentication was succesful

const TOKEN_NAME = 'shi-panel-token';
const USERID_NAME = 'user-id';

@Injectable({ providedIn: 'root' })
export class AuthenticationService implements AuthService {
    private readonly _authState = inject(AuthenticationState);
    private readonly _authApi = inject(AuthenticationApi);
    private readonly _router = inject(Router);
    private readonly _i18n = inject(I18nService);

    static getTokenFromStorage(): string {
        return localStorage.getItem(TOKEN_NAME);
    }

    static doesUserHaveSomeRole(user: User, matchingRoles: UserRoles[]): boolean {
        if (user?.role) {
            return matchingRoles.includes(user.role);
        }
        return false;
    }

    static doesUserHaveSomePermission(user: User, matchingPermissions: UserPermissions[]): boolean {
        if (user?.permissions.length > 0) {
            return matchingPermissions.some(permission => user.permissions.includes(permission));
        }
        return false;
    }

    static doesUserHaveAllPermissions(user: User, matchingPermissions: UserPermissions[]): boolean {
        if (user?.permissions.length > 0) {
            return matchingPermissions.every(permission => user.permissions.includes(permission));
        }
        return false;
    }

    requestLoggedUser(): Observable<User> {
        const userId = localStorage.getItem(USERID_NAME);
        if (!userId) {
            this.logout();
            return null;
        }
        this._authState.loggedUser.setInProgress(true);
        return this._authApi.requestCurrentUser(userId)
            .pipe(
                tap(user => {
                    this._authState.loggedUser.setValue(user);
                }),
                catchError((error: HttpErrorResponse) => {
                    if (error.status === 404) {
                        this._router.navigate(['login']);
                    }
                    throw error;
                }),
                finalize(() => this._authState.loggedUser.setInProgress(false))
            );
    }

    getLoggedUser$(): Observable<User> {
        return this._authState.loggedUser.getValue$();
    }

    isLoggedUserLoading$(): Observable<boolean> {
        return this._authState.loggedUser.isInProgress$();
    }

    getLoggedUserError$(): Observable<HttpErrorResponse> {
        return this._authState.loggedUser.getError$();
    }

    getToken$(): Observable<string> {
        return this._authState.token.getValue$();
    }

    setToken(token: string): void {
        this._authState.token.setValue(token);
    }

    isTokenLoading$(): Observable<boolean> {
        return this._authState.token.isInProgress$();
    }

    getLoginError$(): Observable<HttpErrorResponse> {
        return this._authState.token.getError$();
    }

    // login
    login(data: { username: string; password: string; operator: string }): Observable<User> {
        this._authState.token.setError(null);
        this._authState.token.setInProgress(true);
        return this._authApi.login(data)
            .pipe(
                tap(response => {
                    // store jwt token in local storage to keep user logged in between page refreshes
                    const token = response.access_token;
                    localStorage.setItem(TOKEN_NAME, token);
                    this._authState.token.setValue(token);

                    const userId = response.user_id;
                    localStorage.setItem(USERID_NAME, userId);
                }),
                switchMap(() => this.requestLoggedUser()),
                switchMap(user => {
                    if (user.timezone) {
                        this._i18n.setTimezone(user.timezone);
                    }
                    if (user.language) {
                        //TODO: Only english for now
                        const lang = 'en_US';
                        return this._i18n.setLocale(lang)
                            .pipe(mapTo(user));
                    }
                    return of(user);
                }),
                finalize(() => this._authState.token.setInProgress(false)),
                catchError((response: HttpErrorResponse) => {
                    this._authState.token.setError(response);
                    return throwError(() => response);
                })
            );
    }

    // logout
    logout(): void {
        // remove user & JWT token from local storage to log user out
        localStorage.removeItem(TOKEN_NAME);
        localStorage.removeItem(USERID_NAME);
        this._authState.token.setValue(null);
        this._authState.loggedUser.setValue(null);
    }

    // forgot password - send me an email
    forgotPassword(username: string): Observable<{ email: string }> {
        this._authState.forgotPwd.setInProgress(true);
        return this._authApi.postforgotPassword(username)
            .pipe(map(response => ({ email: response.username })), finalize(() => this._authState.forgotPwd.setInProgress(false)));
    }

    isForgotPwdLoading$(): Observable<boolean> {
        return this._authState.forgotPwd.isInProgress$();
    }

    // set new password
    setNewPassword(request: { new_password: string; token: string }): Observable<void> {
        this._authState.newPwd.setInProgress(true);
        return this._authApi.postRecoverPassword(request)
            .pipe(finalize(() => this._authState.newPwd.setInProgress(false)));
    }

    isNewPwdLoading$(): Observable<boolean> {
        return this._authState.newPwd.isInProgress$();
    }

    // recovery password token verification
    verifyToken(token: string): Observable<void> {
        this._authState.tokenVerification.setInProgress(true);
        return this._authApi.getTokenVerification(token)
            .pipe(finalize(() => this._authState.tokenVerification.setInProgress(false)));
    }

    clearLoginError$(): void {
        return this._authState.token.setError(null);
    }

    hasLoggedUserSomeRoles$(roles: UserRoles[]): Observable<boolean> {
        return this.getLoggedUser$().pipe(
            filter(Boolean),
            map(user => AuthenticationService.doesUserHaveSomeRole(user, roles))
        );
    }
}
