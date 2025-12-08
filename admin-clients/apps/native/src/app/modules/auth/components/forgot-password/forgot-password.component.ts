import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { NavController, Platform } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'forgot-password',
    templateUrl: './forgot-password.component.html',
    styleUrls: ['./forgot-password.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ForgotPasswordComponent implements OnInit {
    private readonly _auth = inject(AUTHENTICATION_SERVICE);
    private readonly _formBuilder = inject(UntypedFormBuilder);
    private readonly _translateService = inject(TranslateService);
    private readonly _platform = inject(Platform);
    private readonly _navCtrl = inject(NavController);

    readonly form = this._formBuilder.group({
        email: [null as string, [Validators.required, Validators.email]]
    });

    readonly reqInProgress$: Observable<boolean> = this._auth.isForgotPwdLoading$();

    alertButtons = [];
    alertOpen = false;

    ngOnInit(): void {
        this._platform.backButton.subscribeWithPriority(10, () => {
            this._navCtrl.back();
        });
        this.loadModalButtons();
    }

    onSubmit(): void {
        if (this.form.valid) {
            this._auth.forgotPassword(this.form.value.email)
                .subscribe(() => {
                    this.toggleAlert();
                });
        }
    }

    private toggleAlert(): void {
        this.alertOpen = !this.alertOpen;
    }

    private loadModalButtons(): void {
        this._translateService.get(['BUTTONS.CONFIRM'])
            .subscribe(translation => {
                this.alertButtons = [
                    {
                        text: translation['BUTTONS.CONFIRM'],
                        role: 'confirm',
                        cssClass: 'ob-btn primary size--small',
                        // eslint-disable-next-line @typescript-eslint/naming-convention
                        htmlAttributes: { 'data-override-styles': '' },
                        handler: () => {
                            this._navCtrl.back();
                        }
                    }
                ];
            });
    }
}
