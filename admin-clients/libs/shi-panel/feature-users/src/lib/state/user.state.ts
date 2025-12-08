import { StateProperty } from '@OneboxTM/utils-state';
import { User } from '@admin-clients/shi-panel/utility-models';
import { Injectable } from '@angular/core';
import { GetUsersResponse } from '../models/get-users-response.model';

@Injectable()
export class UsersState {
    readonly usersList = new StateProperty<GetUsersResponse>();
    readonly userDetails = new StateProperty<User>();
    readonly userPermissions = new StateProperty<void>();
}
