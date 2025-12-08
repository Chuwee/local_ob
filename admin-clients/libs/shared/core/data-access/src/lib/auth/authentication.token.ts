import { BasicUser } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export interface AuthService {
    requestLoggedUser(): Observable<BasicUser>;

    getLoggedUser$(): Observable<BasicUser>;

    isLoggedUserLoading$(): Observable<boolean>;

    getLoggedUserError$(): Observable<HttpErrorResponse>;

    getToken$(): Observable<string>;

    setToken(token: string): void;

    isTokenLoading$(): Observable<boolean>;

    getLoginError$(): Observable<HttpErrorResponse>;

    clearLoginError$?(): void;

    login(data: { username: string; password: string; operator: string; mfa: { code: string; type: string } }): Observable<BasicUser>;

    logout(): void;

    forgotPassword(email: string): Observable<{ email: string }>;

    isForgotPwdLoading$(): Observable<boolean>;

    setNewPassword(request: { new_password: string; token: string }): Observable<void>;

    isNewPwdLoading$(): Observable<boolean>;

    verifyToken(token: string): Observable<void>;
}

export const AUTHENTICATION_SERVICE = new InjectionToken<AuthService>('AUTHENTICATION_SERVICE');
