import { StateManager } from '@OneboxTM/utils-state';
import { UserRoles } from '@admin-clients/shi-panel/utility-models';
import { Injectable, inject } from '@angular/core';
import { GrantPermissionsApi } from './grant-permissions.api';
import { GrantPermissionsState } from './grant-permissions.state';

@Injectable()
export class GrantPermissionsService {
    private readonly _permissionsApi = inject(GrantPermissionsApi);
    private readonly _permissionsState = inject(GrantPermissionsState);

    availablePermissions = Object.freeze({
        load: (role: UserRoles) => StateManager.load(
            this._permissionsState.availablePermissions,
            this._permissionsApi.getRoleAvailablePermissions(role)
        ),
        getRoleAvailablePermissions$: () => this._permissionsState.availablePermissions.getValue$(),
        loading$: () => this._permissionsState.availablePermissions.isInProgress$()
    });
}
