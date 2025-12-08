import { buildHttpParams } from '@OneboxTM/utils-http';
import { User } from '@admin-clients/shi-panel/utility-models';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthResponse } from './model/auth-response.model';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationApi {
    private readonly SHI_MGMT_API = '/api/shi-mgmt-api/v1/users';
    private readonly _http = inject(HttpClient);

    requestCurrentUser(userId: string): Observable<User> {
        return this._http.get<User>(`${this.SHI_MGMT_API}/` + userId);
    }

    login({ username, password }: { username: string; password: string }): Observable<AuthResponse> {
        const requestHeaders = {
            headers: new HttpHeaders()
                .set('Content-Type', 'application/json')
        };
        const body = JSON.stringify({ username, password });

        return this._http.post<AuthResponse>(`${this.SHI_MGMT_API}/login`, body.toString(), requestHeaders);
    }

    // send me an email
    postforgotPassword(username: string): Observable<{ username: string }> {
        return this._http.post<{ username: string }>(`${this.SHI_MGMT_API}/recover-password`, { username });
    }

    // set new password
    postRecoverPassword(request: { new_password: string; token: string }): Observable<void> {
        return this._http.put<void>(`${this.SHI_MGMT_API}/password`, { password: request.new_password, token: request.token });
    }

    // verify token to acces new password form
    getTokenVerification(token: string): Observable<void> {
        const params = buildHttpParams({ token });
        return this._http.get<void>(`${this.SHI_MGMT_API}/recover-password`, { params });
    }
}
