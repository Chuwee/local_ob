
import { buildHttpParams } from '@OneboxTM/utils-http';
import { User } from '@admin-clients/shi-panel/utility-models';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetUsersRequest } from './models/get-users-request.model';
import { GetUsersResponse } from './models/get-users-response.model';
import { PostUser } from './models/post-user.model';
import { PutPermissions } from './models/put-permissions.model';
import { PutUser } from './models/put-user.model';

@Injectable()
export class UsersApi {
    private readonly BASE_USERS_URL = '/api/shi-mgmt-api/v1/users';
    private readonly _http = inject(HttpClient);

    getUsers(request: GetUsersRequest): Observable<GetUsersResponse> {
        const params = buildHttpParams(request);

        return this._http.get<GetUsersResponse>(this.BASE_USERS_URL, { params });
    }

    getUser(userId: string): Observable<User> {
        return this._http.get<User>(`${this.BASE_USERS_URL}/${userId}`);
    }

    postUser(user: PostUser): Observable<{ id: string }> {
        return this._http.post<{ id: string }>(this.BASE_USERS_URL, user);
    }

    putUser(userId: string, user: PutUser): Observable<void> {
        return this._http.put<void>(`${this.BASE_USERS_URL}/${userId}`, user);
    }

    deleteUser(userId: string): Observable<void> {
        return this._http.delete<void>(`${this.BASE_USERS_URL}/${userId}`);
    }

    putUserPermissions(userId: number, request: PutPermissions): Observable<void> {
        return this._http.put<void>(`${this.BASE_USERS_URL}/${userId}/permissions`, request);
    }
}
