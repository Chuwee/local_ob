import { AuthResponseState } from '@admin-clients/cpanel/core/data-access';
import { ContextNotificationComponent, DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { AUTHENTICATION_SERVICE, AuthError, AuthErrorAction, AuthErrorCode } from '@admin-clients/shared/core/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { trigger, transition, style, animate } from '@angular/animations';
import { AsyncPipe, NgClass, UpperCasePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, output, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, DefaultUrlSerializer, Router, UrlTree } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { finalize, Observable, switchMap, EMPTY } from 'rxjs';
import { debounceTime, filter, map, shareReplay, take, withLatestFrom } from 'rxjs/operators';
import { LoginPages } from '../../models/login-pages.enum';

const RETURN_URL_KEY = 'returnUrl';

@Component({
    selector: 'app-login-form',
    templateUrl: 'login-form.component.html',
    styleUrls: ['./login-form.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, MatFormFieldModule, MatInputModule, NgClass, TranslatePipe, MatOptionModule, AsyncPipe,
        ContextNotificationComponent, UpperCasePipe, MatIconModule, MatProgressSpinnerModule, MatButtonModule,
        MatSelectModule
    ],
    animations: [
        trigger('slideIn', [
            transition(':enter', [
                style({ transform: 'translateX(100%)' }),
                animate('360ms ease-in', style({ transform: 'translateX(0%)' }))
            ])
        ])
    ]
})
export class LoginFormComponent implements OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #auth = inject(AUTHENTICATION_SERVICE);
    readonly #destroyRef = inject(DestroyRef);

    readonly $goToPage = output<LoginPages>({ alias: 'goToPage' });
    readonly $trySubmit = output<boolean>({ alias: 'trySubmit' });

    readonly $operators = signal<[{ code: string; name: string }]>(null);
    readonly $accountBlocked = signal<{ show: boolean; type: AuthErrorAction }>({ show: false, type: null });
    readonly $showPasswordValue = signal(false);
    readonly $submitted = signal(false);
    readonly authErrorCode = AuthErrorCode;
    readonly authErrorAction = AuthErrorAction;
    readonly loginPages = LoginPages;
    #submittedUsername: string;

    form = this.#fb.group({
        username: ['', Validators.required],
        password: ['', Validators.required],
        operator: '',
        mfa: this.#fb.group({
            type: '',
            code: ''
        })
    });

    loading$ = booleanOrMerge([
        this.#auth.isTokenLoading$(),
        this.#auth.isLoggedUserLoading$()
    ]);

    error$: Observable<AuthError> = this.#auth.getLoginError$().pipe(
        map((response: HttpErrorResponse) => {
            if (!response?.error) return null;

            const { error } = response;
            return {
                code: error.code || (error.state === AuthResponseState.fail ?
                    (this.form.controls.mfa.value.code ? this.authErrorCode.WRONG_CREDENTIALS : this.authErrorCode.INVALID)
                    : undefined),
                description: error.properties?.error_description || error.properties?.message,
                operators: error.properties?.operators,
                mfaType: error.properties?.mfa_type,
                user: {
                    isBlocked: error.properties?.action === this.authErrorAction.USER_TEMPORAL_BLOCKED ||
                        error.properties?.action === this.authErrorAction.USER_PERMANENT_BLOCKED,
                    blockedType: error.properties?.action
                },
                password: {
                    expired: error.properties?.action === this.authErrorAction.RESET_PASSWORD,
                    max_password_storage: error.properties?.max_password_storage || 0,
                    reset_token: error.properties?.reset_token || null
                }
            };
        }),
        shareReplay(1));

    constructor() {
        this.error$
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                filter(error => !!error))
            .subscribe((error: AuthError) => {
                if (error.user?.isBlocked) return this.#handleUserBlockedError(error);
                if (error?.password.expired) return this.#handleExpiredPasswordError(error);

                if (error.code) this.#onCredentialsError();
                this.$operators.set(null);
                this.#operatorCtrl.setValidators(null);
                this.#operatorCtrl.setValue(null);

                if (error.operators) {
                    this.$operators.set(error.operators);
                    this.#operatorCtrl.setValue(error.operators[0].code);
                    this.#operatorCtrl.setValidators([Validators.required]);
                    this.$goToPage.emit(LoginPages.login);
                } else if (error.mfaType) {
                    this.form.controls.mfa.controls.type.setValue(error.mfaType);
                    this.$goToPage.emit(LoginPages.mfa);
                }
            });

        this.#usernameCtrl.valueChanges
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                debounceTime(200),
                filter(username => this.$submitted() && username !== this.#submittedUsername)
            )
            .subscribe(_ => {
                this.#auth.logout();
                this.$submitted.set(false);
                this.#submittedUsername = null;
                this.#operatorCtrl.setValidators(null);
                this.#operatorCtrl.setValue(null);
                this.#operatorCtrl.updateValueAndValidity();
                this.$operators.set(null);
            });
    }

    ngOnDestroy(): void {
        this.#auth.clearLoginError$();
    }

    onSubmit(): void {
        if (this.form.valid) {
            this.$submitted.set(true);
            this.#submittedUsername = this.#usernameCtrl.value;
            this.form.disable();
            this.loading$
                .pipe(
                    take(1),
                    switchMap(loading => loading ? EMPTY : this.#auth.login(this.form.getRawValue())),
                    take(1),
                    withLatestFrom(this.#auth.getToken$().pipe(filter(token => !!token))),
                    finalize(() => setTimeout(() => this.form.enable(), 500))
                )
                .subscribe({
                    next: () => {
                        // get return url from route parameters or default to '/'
                        const returnUrl = (this.#route.snapshot.queryParams[RETURN_URL_KEY] || '/').replace('login', '');
                        const urlObj: UrlTree = new DefaultUrlSerializer().parse(returnUrl);
                        this.#router.navigate([returnUrl.split('?')[0]], {
                            queryParams: urlObj.queryParams
                        });
                    },
                    error: ({ status }: HttpErrorResponse) => {
                        // error handling
                        this.form.markAsPristine();
                        if (status === 500) {
                            // TODO: show banner instead of alert
                            this.#msgDialogService.showAlert({
                                size: DialogSize.SMALL,
                                title: 'TITLES.ERROR_DIALOG',
                                message: 'API_ERRORS.GENERIC_ERROR'
                            });
                        }
                    }
                });
        }
    }

    onValidateMFA(mfaCode: string): void {
        this.form.controls.mfa.controls.code.setValue(mfaCode);
        this.onSubmit();
    }

    handleShowPasswordValue(): void {
        this.$showPasswordValue.update(value => !value);
    }

    returnToLoginProcess(): void {
        this.$accountBlocked.set({ show: false, type: null });
        this.#operatorCtrl.setValidators(null);
        this.#operatorCtrl.setValue(null);
        this.#operatorCtrl.updateValueAndValidity();
        this.$operators.set(null);
    }

    handleSubmit(): void {
        this.$trySubmit.emit(true);
    }

    navigateToForgotPassword(): void {
        this.$goToPage.emit(LoginPages.forgot);
    }

    #handleExpiredPasswordError(error: AuthError): void {
        this.#router.navigate(['new-password'], {
            relativeTo: this.#route,
            queryParams: {
                token: error.password.reset_token,
                storage: error.password?.max_password_storage || 0,
                expired: true
            }
        });
    }

    #handleUserBlockedError(error: AuthError): void {
        this.$accountBlocked.set({ show: true, type: error.user.blockedType });
        this.form.reset();
        this.$submitted.set(false);
    }

    #onCredentialsError(): void {
        this.$goToPage.emit(LoginPages.login);
        this.form.controls.mfa.reset();
    }

    get #operatorCtrl(): AbstractControl {
        return this.form.get('operator');
    }

    get #usernameCtrl(): AbstractControl {
        return this.form.get('username');
    }
}
