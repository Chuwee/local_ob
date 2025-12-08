import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API, APP_BASE_API_OAUTH } from '@admin-clients/shared/core/data-access';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthRequestPayload } from '../model/auth-request-payload.model';
import { AuthResponse } from '../model/auth-response.model';
import { MstrUrls } from '../reports-config.model';
import { User } from '../user.model';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationApi {
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly BASE_API_OAUTH = inject(APP_BASE_API_OAUTH);
    private readonly USERS_API = `${this.BASE_API}/mgmt-api/v1/users`;

    constructor(private _http: HttpClient) { }

    requestCurrentUser(): Observable<User> {
        return this._http.get<User>(`${this.USERS_API}/myself`);
    }

    login({ username, password, operator, mfa }: AuthRequestPayload): Observable<AuthResponse> {
        const requestHeaders = { headers: new HttpHeaders().set('Content-Type', 'application/json') };
        const body: AuthRequestPayload = {
            username,
            password
        };
        if (operator) {
            body.operator = operator;
        }
        if (mfa?.type) {
            body.mfa = mfa;
        }

        return this._http.post<AuthResponse>(`${this.BASE_API_OAUTH}/authentication`, body, requestHeaders);
    }

    // send me an email
    postforgotPassword(email: string): Observable<{ email: string }> {
        return this._http.post<{ email: string }>(`${this.USERS_API}/forgot-password`, { email });
    }

    // set new password
    postRecoverPassword(request: { new_password: string; token: string }): Observable<void> {
        return this._http.post<void>(`${this.USERS_API}/forgot-password/recover`, request);
    }

    // verify token to acces new password form
    getTokenVerification(token: string): Observable<void> {
        const params = buildHttpParams({ token });
        return this._http.get<void>(`${this.USERS_API}/forgot-password`, { params });
    }

    getMstrUrls(id?: number): Observable<MstrUrls> {
        const params = buildHttpParams({ impersonated_user_id: id });
        return this._http.get<MstrUrls>(`${this.USERS_API}/myself/mstrUrls`, { params });
    }
}
