import { TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { CaptchaService } from '@admin-clients/shared/core/data-access';
import { ChangeDetectionStrategy, Component, OnDestroy, ViewChild, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { LoginPages } from '../models/login-pages.enum';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { LoginFormComponent } from './login-form/login-form.component';
import { MfaCodeComponent } from './mfa-code/mfa-code.component';

@Component({
    selector: 'app-user-login',
    templateUrl: 'user-login.component.html',
    styleUrls: ['./user-login.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MfaCodeComponent, TabsMenuComponent, TabDirective, LoginFormComponent, ForgotPasswordComponent
    ]
})
export class UserLoginComponent implements OnDestroy, OnInit {
    private _onDestroy = new Subject<void>();

    @ViewChild('loginPagesTabs') private _loginPagesTabs: TabsMenuComponent;
    @ViewChild('loginForm') private _loginForm: LoginFormComponent;
    @ViewChild('forgotPassword') private _forgotPassword: ForgotPasswordComponent;
    @ViewChild('mfaCode') private _mfaCode: MfaCodeComponent;

    constructor(private _captchaSrv: CaptchaService) { }

    ngOnInit(): void {
        this._captchaSrv.renderCaptcha('user-login-captcha', () => this.trySubmit());
    }

    ngOnDestroy(): void {
        this._captchaSrv.captchaReset();
        this._onDestroy.next(null);
    }

    goToPage(page: LoginPages): void {
        this._loginPagesTabs.selectedIndex = page;
    }

    trySubmit(): void {
        if (this._captchaSrv.isCaptchaResolved()) {
            if (this._loginPagesTabs.selectedIndex === LoginPages.login) {
                this._loginForm.onSubmit();
            } else if (this._loginPagesTabs.selectedIndex === LoginPages.forgot) {
                this._forgotPassword.onSubmit();
            } else if (this._loginPagesTabs.selectedIndex === LoginPages.mfa) {
                this._loginForm.onValidateMFA(this._mfaCode.form.value.mfaCode);
            }
        } else {
            this._captchaSrv.captchaExecute();
        }
    }
}
