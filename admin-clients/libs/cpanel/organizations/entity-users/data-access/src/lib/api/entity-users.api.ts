import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EntityUserNotification } from '../models/entity-user-notification.model';
import { EntityUserPermissions } from '../models/entity-user-permissions.enum';
import type { UserRateLimit } from '../models/entity-user-rate-limit.model';
import { ResourceServer } from '../models/entity-user-resource-server.model';
import { EntityUserRole } from '../models/entity-user-role.model';
import { EntityUserRoles } from '../models/entity-user-roles.enum';
import { EntityUser } from '../models/entity-user.model';
import { GetEntityUsersRequest } from '../models/get-entity-users-request';
import { GetEntityUsersResponse } from '../models/get-entity-users-response.model';
import { PostEntityUser } from '../models/post-entity-user.model';
import { PostMfaActivation, PostMfaActivationResponse } from '../models/post-mfa-activation.model';
import { PutEntityUser } from '../models/put-entity-user.model';
import { RefreshApiKeyResponse } from '../models/refresh-apikey-response.model';
import { PostEntityUserPasswordRequest, PostMyUserPasswordRequest } from '../models/set-entity-user-password.model';

@Injectable({
    providedIn: 'root'
})
export class EntityUsersApi {
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly USERS_API = `${this.BASE_API}/mgmt-api/v1/users`;
    private readonly RESOURCES_SEGMENT = 'resource-servers';
    private readonly _http = inject(HttpClient);

    getEntityUsers(request: GetEntityUsersRequest): Observable<GetEntityUsersResponse> {
        const params = buildHttpParams({
            limit: request.limit,
            offset: request.offset,
            sort: request.sort,
            q: request.q,
            entity_id: request.entityId,
            operator_id: request.operatorId,
            status: request.status?.length ? request.status.join() : undefined,
            roles: request.roles,
            permissions: request.permissions
        });
        return this._http.get<GetEntityUsersResponse>(this.USERS_API, { params });
    }

    getEntityUser(userId: number | string): Observable<EntityUser> {
        return this._http.get<EntityUser>(`${this.USERS_API}/${userId}`);
    }

    postEntityUser(user: PostEntityUser): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(this.USERS_API, user);
    }

    deleteEntityUser(userId: string): Observable<void> {
        return this._http.delete<void>(`${this.USERS_API}/${userId}`);
    }

    putEntityUser(userId: number | 'myself', user: Partial<PutEntityUser>): Observable<void> {
        return this._http.put<void>(`${this.USERS_API}/${userId}`, user);
    }

    refreshEntityUserApiKey(userId: number | 'myself'): Observable<RefreshApiKeyResponse> {
        return this._http.post<RefreshApiKeyResponse>(`${this.USERS_API}/${userId}/apikey/refresh`, null);
    }

    postEntityUserPassword(userId: number, request: PostEntityUserPasswordRequest): Observable<void> {
        return this._http.post<void>(`${this.USERS_API}/${userId}/password`, request);
    }

    postMyUserPassword(payload: PostMyUserPasswordRequest): Observable<void> {
        return this._http.post<void>(`${this.USERS_API}/myself/password`, payload);
    }

    // Roles and permissions
    getUserRoles(userId: number): Observable<EntityUserRole[]> {
        return this._http.get<EntityUserRole[]>(`${this.USERS_API}/${userId}/roles`);
    }

    getAvailableUserRoles(userId: number): Observable<EntityUserRole[]> {
        return this._http.get<EntityUserRole[]>(`${this.USERS_API}/${userId}/roles/available`);
    }

    // post all user roles
    postUserRoles(userId: number, request: EntityUserRole[]): Observable<void> {
        return this._http.post<void>(`${this.USERS_API}/${userId}/roles`, request);
    }

    // put/delete a role AND its permissions
    putRoleAndPermissions(userId: number, request: Partial<EntityUserRole>): Observable<void> {
        return this._http.put<void>(`${this.USERS_API}/${userId}/roles`, request);
    }

    deleteRoleAndPermissions(userId: number, roleCode: EntityUserRoles): Observable<void> {
        return this._http.delete<void>(`${this.USERS_API}/${userId}/roles/${roleCode}`);
    }

    // add a single permission to a role
    postRolePermission(userId: number, roleCode: EntityUserRoles, permissionCode: EntityUserPermissions): Observable<void> {
        return this._http.post<void>(`${this.USERS_API}/${userId}/roles/${roleCode}/permissions/${permissionCode}`, {});
    }

    // delete a single permission from a role
    deleteRolePermission(userId: number, roleCode: EntityUserRoles, permissionCode: EntityUserPermissions): Observable<void> {
        return this._http.delete<void>(`${this.USERS_API}/${userId}/roles/${roleCode}/permissions/${permissionCode}`, {});
    }

    //Notifications
    getUserNotifications(userId: number | 'myself'): Observable<EntityUserNotification[]> {
        return this._http.get<EntityUserNotification[]>(`${this.USERS_API}/${userId}/notifications`);
    }

    putUserNotifications(userId: number | 'myself', request: EntityUserNotification[]): Observable<void> {
        return this._http.put<void>(`${this.USERS_API}/${userId}/notifications`, request);
    }

    // Api resources management
    getAvailableResourceServers(userId: number | 'myself'): Observable<ResourceServer[]> {
        return this._http.get<ResourceServer[]>(`${this.USERS_API}/${userId}/${this.RESOURCES_SEGMENT}`);
    }

    postUserResourcesServers(userId: number | 'myself', resources: string[]): Observable<void> {
        return this._http.post<void>(`${this.USERS_API}/${userId}/${this.RESOURCES_SEGMENT}`, { resources });
    }

    // Activate or deactivate MFA
    postMfaSendEmail(userId: number | 'myself', payload: PostMfaActivation): Observable<PostMfaActivationResponse> {
        return this._http.post<PostMfaActivationResponse>(`${this.USERS_API}/${userId}/mfa-activation-send`, payload);
    }

    postMfaActivation(userId: number | 'myself', payload: PostMfaActivation): Observable<PostMfaActivationResponse> {
        return this._http.post<PostMfaActivationResponse>(`${this.USERS_API}/${userId}/mfa-activation-confirm`, payload);
    }

    getRateLimit(userId: number | 'myself'): Observable<UserRateLimit> {
        return this._http.get<UserRateLimit>(`${this.USERS_API}/${userId}/rate-limit`);
    }

    postRateLimit(userId: number | 'myself', payload: UserRateLimit): Observable<void> {
        return this._http.post<void>(`${this.USERS_API}/${userId}/rate-limit`, payload);
    }
}
