import { CaptchaService, preventLoginGuard } from '@admin-clients/shared/core/data-access';
import { Routes } from '@angular/router';
import { LoginComponent } from './login.component';
import { NewPasswordComponent } from './new-password/new-password.component';
import { UserLoginComponent } from './user-login/user-login.component';

export const routes: Routes = [
    {
        path: '',
        providers: [
            CaptchaService
        ],
        component: LoginComponent,
        canActivate: [preventLoginGuard],
        children: [
            {
                path: '',
                component: UserLoginComponent
            },
            {
                path: 'new-password',
                component: NewPasswordComponent
            }
        ]
    }
];
