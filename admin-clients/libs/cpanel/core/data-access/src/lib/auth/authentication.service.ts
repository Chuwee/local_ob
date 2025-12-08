import { StateManager } from '@OneboxTM/utils-state';
import { CustomManagementType, EntityType } from '@admin-clients/shared/common/data-access';
import { AuthService, I18nService } from '@admin-clients/shared/core/data-access';
import { TrackingService } from '@admin-clients/shared/data-access/trackers';
import { Currency } from '@admin-clients/shared-utility-models';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import sha1 from 'crypto-js/sha1';
import { Observable, of, throwError } from 'rxjs';
import { catchError, filter, finalize, map, mapTo, switchMap, tap } from 'rxjs/operators';
import { AuthenticationApi } from './api/authentication.api';
import { CustomResources } from './custom-resources.model';
import { AuthRequestPayload } from './model/auth-request-payload.model';
import { AuthResponseState } from './model/auth-response.model';
import { MstrUrls } from './reports-config.model';
import { AuthenticationState } from './state/authentication.state';
import { UserRoles } from './user-roles.model';
import { User } from './user.model';

const TOKEN_NAME = 'token';

/**
 * Used to log in and log out the user
 *
 * It posts the credentials (username and password) to the backend and check for the response if it has JWT token.
 * if the response from the backend has jwt token, then the authentication was successful, and saves it in to the localstorage
 * */
@Injectable({ providedIn: 'root' })
export class AuthenticationService implements AuthService {

    private readonly _authApi = inject(AuthenticationApi);
    private readonly _authState = inject(AuthenticationState);
    private readonly _i18n = inject(I18nService);
    private readonly _trackingService = inject(TrackingService);
    private readonly _router = inject(Router);

    readonly impersonation = Object.freeze({

        set: (userId: string) => this._authState.impersonation.setValue(userId),
        get$: () => this._authState.impersonation.getValue$(),
        clear: () => this._authState.impersonation.setValue(null)
    });

    readonly mstrUrls = Object.freeze({
        load: (userId: number): void => StateManager.load(
            this._authState.mstrUrls,
            this._authApi.getMstrUrls(userId)
        ),
        getMstrUrls$: (userId: number): Observable<MstrUrls> =>
            this._authApi.getMstrUrls(userId)
                .pipe(
                    catchError(() => of(null)),
                    map(urls => urls)
                ),
        get$: () => this._authState.mstrUrls.getValue$(),
        error$: () => this._authState.mstrUrls.getError$(),
        loading$: () => this._authState.mstrUrls.isInProgress$(),
        clear: () => this._authState.mstrUrls.setValue(null)
    });

    static isSomeRoleInUserRoles(user: User, matchingRoles: UserRoles[]): boolean {
        if (user?.roles) {
            const userRoles = user.roles.map(userRole => userRole.code);
            return matchingRoles.find(role => userRoles.indexOf(role) > -1) !== undefined;
        }
        return false;
    }

    static isSomeEntityTypeInUserEntityTypes(user: User, entityTypes: EntityType[]): boolean {
        if (user?.entity.settings.types) {
            return entityTypes.find(entityType => user.entity.settings.types.indexOf(entityType) > -1) !== undefined;
        }
        return false;
    }

    //TODO: Eradicate this please
    static isInternalUser(user: User): boolean {
        return user && (user.email.includes('@oneboxtds.com') || user.email.includes('oneboxtm.com'));
    }

    static matchSomeRoleAndEntityTypeInUser(
        user: User, matchingRoles: UserRoles[], matchingEntityTypes: EntityType[], matchingCustomManagements: CustomManagementType[]
    ): boolean {
        if (user !== null) {
            const userRoles = user.roles.map(userRole => userRole.code);
            const isUserMatch = matchingRoles.find(role => userRoles.indexOf(role) > -1) !== undefined;
            const isEntityTypeMatch = !!matchingEntityTypes?.find(type => user.entity.settings?.types.indexOf(type) > -1);
            const userCustomManagements = user.entity.settings?.external_integration?.custom_managements;
            const isCustomManagementsMatch = !!matchingCustomManagements
                ?.find(type => !!userCustomManagements?.find(cm => cm.enabled && cm.type === type));
            let result = isUserMatch;
            if (matchingEntityTypes) {
                result = result && isEntityTypeMatch;
            }
            if (matchingCustomManagements) {
                result = result && isCustomManagementsMatch;
            }
            return result;
        }
        return false;
    }

    static getTokenFromStorage(): string {
        return localStorage.getItem(TOKEN_NAME);
    }

    static operatorCurrencyCodes(user: User): string[] {
        return user?.operator.currencies?.selected.map(currency => currency.code);
    }

    static operatorCurrencies(user: User): Currency[] {
        return user.operator.currencies?.selected;
    }

    static operatorDefaultCurrency(user: User): string {
        return user.operator.currencies?.default_currency;
    }

    static canBiImpersonate(user: User): boolean {
        return user.reports?.can_impersonate;
    }

    requestLoggedUser(): Observable<User> {
        this._authState.loggedUser.setInProgress(true);
        return this._authApi.requestCurrentUser()
            .pipe(
                tap(user => {
                    const emailSha1 = sha1(user.email).toString();
                    this._trackingService.setUserUA({
                        dimension1: user.entity.id,
                        dimension2: user.entity.short_name,
                        dimension3: emailSha1
                    });
                    this._trackingService.setUserGTM(emailSha1);
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

    getResourcesUrls$(): Observable<CustomResources> {
        return this._authState.loggedUser.getValue$()
            .pipe(
                map(user => {
                    const operatorCustomization = user?.operator.settings?.customization?.enabled
                        ? user.operator.settings?.customization : {};
                    const entityCustomization = user?.entity.settings.customization?.enabled ? user.entity.settings.customization : {};
                    return { ...operatorCustomization, ...entityCustomization };
                }));
    }

    hasLoggedUserSomeRoles$(roles: UserRoles[]): Observable<boolean> {
        return this.getLoggedUser$().pipe(
            filter(user => user !== null),
            map(user => AuthenticationService.isSomeRoleInUserRoles(user, roles))
        );
    }

    hasLoggedUserSomeEntityType$(entityTypes: EntityType[]): Observable<boolean> {
        return this.getLoggedUser$().pipe(
            filter(user => user !== null),
            map(user => AuthenticationService.isSomeEntityTypeInUserEntityTypes(user, entityTypes))
        );
    }

    canReadMultipleEntities$(): Observable<boolean> {
        return this.hasLoggedUserSomeEntityType$(['SUPER_OPERATOR', 'OPERATOR', 'ENTITY_ADMIN']);
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

    clearLoginError$(): void {
        return this._authState.token.setError(null);
    }

    // login
    login(data: AuthRequestPayload): Observable<User> {
        this._authState.token.setError(null);
        this._authState.token.setInProgress(true);
        return this._authApi.login(data)
            .pipe(
                switchMap(response =>
                    (response.state === AuthResponseState.success && response.properties?.access_token) ?
                        of(response) : throwError(() => new HttpErrorResponse({ error: response }))),
                tap(({ properties }) => {
                    // store jwt token in local storage to keep user logged in between page refreshes
                    const token: string = properties?.access_token;
                    localStorage.setItem(TOKEN_NAME, token);
                    this._authState.token.setValue(token);
                }),
                switchMap(() => this.requestLoggedUser()),
                switchMap(user => {
                    if (user.timezone) {
                        this._i18n.setTimezone(user.timezone);
                    }
                    if (user.language) {
                        return this._i18n.setLocale(user.language)
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
        this._authState.token.setValue(null);
        this._authState.loggedUser.setValue(null);
        this._authState.impersonation.setValue(null);
    }

    // forgot password - sends email to user...
    forgotPassword(email: string): Observable<{ email: string }> {
        this._authState.forgotPwd.setInProgress(true);
        return this._authApi.postforgotPassword(email)
            .pipe(finalize(() => this._authState.forgotPwd.setInProgress(false)));
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
}
