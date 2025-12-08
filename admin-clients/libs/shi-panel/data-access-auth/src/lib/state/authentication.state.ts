import { StateProperty } from '@OneboxTM/utils-state';
import { User } from '@admin-clients/shi-panel/utility-models';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationState {
    readonly token = new StateProperty<string>();
    readonly loggedUser = new StateProperty<User>();
    readonly forgotPwd = new StateProperty<void>();
    readonly newPwd = new StateProperty<void>();

    // forgot password token verification
    readonly tokenVerification = new StateProperty<void>();
}
