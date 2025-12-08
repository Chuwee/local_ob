import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, withLatestFrom } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';
import { EntityUsersApi } from './api/entity-users.api';
import { EntityUserNotification } from './models/entity-user-notification.model';
import { EntityUserPermissions } from './models/entity-user-permissions.enum';
import type { UserRateLimit } from './models/entity-user-rate-limit.model';
import { EntityUserRole } from './models/entity-user-role.model';
import { EntityUserRoles } from './models/entity-user-roles.enum';
import { EntityUser } from './models/entity-user.model';
import { GetEntityUsersRequest } from './models/get-entity-users-request';
import { PostEntityUser } from './models/post-entity-user.model';
import { PostMfaActivation, PostMfaActivationResponse } from './models/post-mfa-activation.model';
import { PutEntityUser } from './models/put-entity-user.model';
import { PostEntityUserPasswordRequest, PostMyUserPasswordRequest } from './models/set-entity-user-password.model';
import { EntityUsersState } from './state/entity-users.state';

@Injectable({
    providedIn: 'root'
})
export class EntityUsersService {

    // Notifications
    readonly userNotifications = Object.freeze({
        load: (userId: number | 'myself'): void => StateManager.load(
            this._entityUsersState.userNotifications,
            this._entityUsersApi.getUserNotifications(userId)
        ),
        update: (userId: number | 'myself', request: EntityUserNotification[]): Observable<void> =>
            StateManager.inProgress(
                this._entityUsersState.userNotifications,
                this._entityUsersApi.putUserNotifications(userId, request)
            ),
        get$: () => this._entityUsersState.userNotifications.getValue$(),
        error$: () => this._entityUsersState.userNotifications.getError$(),
        inProgress$: () => this._entityUsersState.userNotifications.isInProgress$(),
        clear: () => this._entityUsersState.userNotifications.setValue(null)
    });

    readonly rolesAndPermissions = Object.freeze({
        saveRoles: (userId: number, request: EntityUserRole[]): Observable<void> =>
            StateManager.inProgress(
                this._entityUsersState.rolesAndPermissions,
                this._entityUsersApi.postUserRoles(userId, request)
            ),
        saveRoleAndPermissions: (userId: number, request: Partial<EntityUserRole>): Observable<void> =>
            StateManager.inProgress(
                this._entityUsersState.rolesAndPermissions,
                this._entityUsersApi.putRoleAndPermissions(userId, request)
            ),
        isRoleAndPermissionsSaving$: () => this._entityUsersState.rolesAndPermissions.isInProgress$(),
        deleteRoleAndPermissions: (userId: number, roleCode: EntityUserRoles): Observable<void> =>
            StateManager.inProgress(
                this._entityUsersState.rolesAndPermissions,
                this._entityUsersApi.deleteRoleAndPermissions(userId, roleCode)
            ),
        saveRolePermission: (userId: number, roleCode: EntityUserRoles, permissionCode: EntityUserPermissions): Observable<void> =>
            StateManager.inProgress(
                this._entityUsersState.rolesAndPermissions,
                this._entityUsersApi.postRolePermission(userId, roleCode, permissionCode)
            ),
        deleteRolePermission: (userId: number, roleCode: EntityUserRoles, permissionCode: EntityUserPermissions): Observable<void> =>
            StateManager.inProgress(
                this._entityUsersState.rolesAndPermissions,
                this._entityUsersApi.deleteRolePermission(userId, roleCode, permissionCode)
            )
    });

    readonly userResourceServers = Object.freeze({
        load: (userId: number | 'myself'): void => StateManager.load(
            this._entityUsersState.userResourceServers,
            this._entityUsersApi.getAvailableResourceServers(userId)
        ),
        update: (userId: number | 'myself', resources: string[]): Observable<void> => StateManager.inProgress(
            this._entityUsersState.userResourceServers,
            this._entityUsersApi.postUserResourcesServers(userId, resources)
        ),
        get$: () => this._entityUsersState.userResourceServers.getValue$(),
        inProgress$: () => this._entityUsersState.userResourceServers.isInProgress$(),
        clear: () => this._entityUsersState.userResourceServers.setValue(null)
    });

    readonly userRateLimit = Object.freeze({
        load: (userId: number): void => StateManager.load(
            this._entityUsersState.rateLimit,
            this._entityUsersApi.getRateLimit(userId)
        ),
        get$: () => this._entityUsersState.rateLimit.getValue$(),
        update: (userId: number | 'myself', request: UserRateLimit) => StateManager.inProgress(
            this._entityUsersState.rateLimit,
            this._entityUsersApi.postRateLimit(userId, request)
        ),
        inProgress$: () => this._entityUsersState.rateLimit.isInProgress$(),
        clear: () => this._entityUsersState.rateLimit.setValue(null)
    });

    constructor(
        private _entityUsersApi: EntityUsersApi,
        private _entityUsersState: EntityUsersState
    ) { }

    //TODO: Eradicate this please
    static isInternalUser(user: EntityUser): boolean {
        return user && (user.email.includes('@oneboxtds.com') || user.email.includes('oneboxtm.com'));
    }

    loadEntityUsersList(request: GetEntityUsersRequest): void {
        this._entityUsersState.usersList.setInProgress(true);
        this._entityUsersApi.getEntityUsers(request)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this._entityUsersState.usersList.setInProgress(false))
            )
            .subscribe(usersList =>
                this._entityUsersState.usersList.setValue(usersList)
            );
    }

    getUsersList(request: GetEntityUsersRequest): Observable<EntityUser[]> {
        return this._entityUsersApi.getEntityUsers(request)
            .pipe(
                catchError(() => of(null)),
                map(response => response.data)
            );
    }

    getUsersListData$(): Observable<EntityUser[]> {
        return this._entityUsersState.usersList.getValue$().pipe(getListData());
    }

    getUsersListMetadata$(): Observable<Metadata> {
        return this._entityUsersState.usersList.getValue$().pipe(getMetadata());
    }

    isUsersListLoading$(): Observable<boolean> {
        return this._entityUsersState.usersList.isInProgress$();
    }

    clearUsersList(): void {
        this._entityUsersState.usersList.setValue(null);
    }

    loadEntityUser(userId?: number | 'myself'): void {
        this._entityUsersState.user.setInProgress(true);
        this._entityUsersApi.getEntityUser(userId)
            .pipe(
                finalize(() => this._entityUsersState.user.setInProgress(false))
            )
            .subscribe((user: EntityUser) =>
                this._entityUsersState.user.setValue(user)
            );
    }

    getEntityUser$(): Observable<EntityUser> {
        return this._entityUsersState.user.getValue$();
    }

    getEntityUserError$(): Observable<HttpErrorResponse> {
        return this._entityUsersState.user.getError$();
    }

    clearEntityUser(): void {
        this._entityUsersState.user.setValue(null);
    }

    isEntityUserSaving$(): Observable<boolean> {
        return this._entityUsersState.isEntityUserSaving$();
    }

    createEntityUser(user: PostEntityUser): Observable<number> {
        this._entityUsersState.setEntityUserSaving(true);
        return this._entityUsersApi.postEntityUser(user)
            .pipe(
                map(result => result.id),
                finalize(() => this._entityUsersState.setEntityUserSaving(false))
            );
    }

    isEntityUserLoading$(): Observable<boolean> {
        return this._entityUsersState.user.isInProgress$();
    }

    deleteEntityUser(id: string): Observable<void> {
        return this._entityUsersApi.deleteEntityUser(id);
    }

    updateEntityUser(userId: number | 'myself', user: Partial<PutEntityUser>): Observable<void> {
        this._entityUsersState.setEntityUserSaving(true);
        return this._entityUsersApi.putEntityUser(userId, user)
            .pipe(finalize(() => this._entityUsersState.setEntityUserSaving(false)));
    }

    refreshEntityUserApiKey(userId: number | 'myself'): Observable<void> {
        this._entityUsersState.setRefreshingApiKey(true);

        return this._entityUsersApi.refreshEntityUserApiKey(userId).pipe(
            withLatestFrom(this._entityUsersState.user.getValue$()),
            map(([{ apikey }, user]) => {
                this._entityUsersState.user.setValue({ ...user, apikey });
            }),
            finalize(() => this._entityUsersState.setRefreshingApiKey(false))
        );
    }

    isApiKeyRefreshing$(): Observable<boolean> {
        return this._entityUsersState.isRefreshingApiKey$();
    }

    saveEntityUserPassword(userId: number, request: PostEntityUserPasswordRequest): Observable<void> {
        this._entityUsersState.setSavingUserPassword(true);
        return this._entityUsersApi.postEntityUserPassword(userId, request)
            .pipe(finalize(() => this._entityUsersState.setSavingUserPassword(false)));
    }

    saveMyUserPassword(request: PostMyUserPasswordRequest): Observable<void> {
        this._entityUsersState.setSavingUserPassword(true);
        return this._entityUsersApi.postMyUserPassword(request)
            .pipe(finalize(() => this._entityUsersState.setSavingUserPassword(false)));
    }

    isPasswordSaving$(): Observable<boolean> {
        return this._entityUsersState.isSavingUserPassword$();
    }

    // Roles and permissions
    loadUserRoles(userId: number): void {
        this._entityUsersState.setRolesListLoading(true);
        this._entityUsersApi.getUserRoles(userId)
            .pipe(finalize(() => this._entityUsersState.setRolesListLoading(false)))
            .subscribe(userRoles => this._entityUsersState.setRolesList(userRoles));
    }

    getUserRoles$(): Observable<EntityUserRole[]> {
        return this._entityUsersState.getRolesList$();
    }

    isUserRolesLoading$(): Observable<boolean> {
        return this._entityUsersState.isRolesListLoading$();
    }

    clearUserRoles(): void {
        this._entityUsersState.setRolesList(null);
    }

    loadAvailableUserRoles(userId: number): void {
        this._entityUsersState.setAvailableRolesListLoading(true);
        this._entityUsersApi.getAvailableUserRoles(userId)
            .pipe(
                finalize(() => this._entityUsersState.setAvailableRolesListLoading(false)))
            .subscribe(userRoles => this._entityUsersState.setAvailableRolesList(userRoles));
    }

    getAvailableUserRoles$(): Observable<EntityUserRole[]> {
        return this._entityUsersState.getAvailableRolesList$();
    }

    isAvailableUserRolesLoading$(): Observable<boolean> {
        return this._entityUsersState.isAvailableRolesListLoading$();
    }

    clearAvailableUserRoles(): void {
        this._entityUsersState.setAvailableRolesList(null);
    }

    sendMfaEmail$(userId: number | 'myself', payload: PostMfaActivation): Observable<PostMfaActivationResponse> {
        this._entityUsersState.mfaActivation.setInProgress(true);
        return this._entityUsersApi.postMfaSendEmail(userId, payload)
            .pipe(finalize(() => this._entityUsersState.mfaActivation.setInProgress(false)));
    }

    activateMfa$(userId: number | 'myself', payload: PostMfaActivation): Observable<PostMfaActivationResponse> {
        this._entityUsersState.mfaActivation.setInProgress(true);
        return this._entityUsersApi.postMfaActivation(userId, payload)
            .pipe(finalize(() => this._entityUsersState.mfaActivation.setInProgress(false)));
    }

    isMfaActivationLoading$(): Observable<boolean> {
        return this._entityUsersState.mfaActivation.isInProgress$();
    }

}
