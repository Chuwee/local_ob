import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';

@Injectable()
export class GrantPermissionsState {
    readonly availablePermissions = new StateProperty<string[]>();
}
