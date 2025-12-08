import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { MstrUrls } from '../reports-config.model';
import { User } from '../user.model';

@Injectable({
    providedIn: 'root'
})
export class AuthenticationState {
    readonly token = new StateProperty<string>();
    readonly loggedUser = new StateProperty<User>();
    readonly forgotPwd = new StateProperty<void>();
    readonly newPwd = new StateProperty<void>();
    readonly mstrUrls = new StateProperty<MstrUrls>();
    readonly impersonation = new StateProperty<string>();

    // forgot password token verification
    readonly tokenVerification = new StateProperty<void>();
}
