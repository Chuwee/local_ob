import { StateProperty } from '@OneboxTM/utils-state';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { EntityUserNotification } from '../models/entity-user-notification.model';
import type { UserRateLimit } from '../models/entity-user-rate-limit.model';
import { ResourceServer } from '../models/entity-user-resource-server.model';
import { EntityUserRole } from '../models/entity-user-role.model';
import { EntityUser } from '../models/entity-user.model';
import { GetEntityUsersResponse } from '../models/get-entity-users-response.model';

@Injectable({
    providedIn: 'root'
})
export class EntityUsersState {
    // entity user list
    readonly usersList = new StateProperty<GetEntityUsersResponse>(null);
    // entity user
    readonly user = new StateProperty<EntityUser>();
    readonly userResourceServers = new StateProperty<ResourceServer[]>();
    // post put entity user
    private _savingEntityUser = new BaseStateProp<boolean>();
    readonly setEntityUserSaving = this._savingEntityUser.setInProgressFunction();
    readonly isEntityUserSaving$ = this._savingEntityUser.getInProgressFunction();
    // post refresh api key
    private _refreshingApiKey = new BaseStateProp<boolean>();
    readonly setRefreshingApiKey = this._refreshingApiKey.setInProgressFunction();
    readonly isRefreshingApiKey$ = this._refreshingApiKey.getInProgressFunction();
    // post set entity user password
    private _savingUserPassword = new BaseStateProp<boolean>();
    readonly setSavingUserPassword = this._savingUserPassword.setInProgressFunction();
    readonly isSavingUserPassword$ = this._savingUserPassword.getInProgressFunction();
    // Roles and permissions
    private _rolesList = new BaseStateProp<EntityUserRole[]>();
    readonly getRolesList$ = this._rolesList.getValueFunction();
    readonly setRolesList = this._rolesList.setValueFunction();
    readonly setRolesListLoading = this._rolesList.setInProgressFunction();
    readonly isRolesListLoading$ = this._rolesList.getInProgressFunction();
    // Roles and permissions available
    private _availableRolesList = new BaseStateProp<EntityUserRole[]>();
    readonly getAvailableRolesList$ = this._availableRolesList.getValueFunction();
    readonly setAvailableRolesList = this._availableRolesList.setValueFunction();
    readonly setAvailableRolesListLoading = this._availableRolesList.setInProgressFunction();
    readonly isAvailableRolesListLoading$ = this._availableRolesList.getInProgressFunction();
    // Save roles and permissions
    readonly rolesAndPermissions = new StateProperty<void>();
    // notifications
    readonly userNotifications = new StateProperty<EntityUserNotification[]>();
    // MFA Activation
    readonly mfaActivation = new StateProperty<void>();
    // Rate Limit
    readonly rateLimit = new StateProperty<UserRateLimit>();
}
