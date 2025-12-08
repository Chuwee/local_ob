import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { inject, Injectable } from '@angular/core';

const GRECAPTCHA_SRC = `https://www.google.com/recaptcha/api.js`;

declare global {
    interface Window {
        ['GoogleAnalyticsObject']: string;
        ga: any;
        grecaptcha: any;
        reCAPTCHACallback: any;
        grecaptchaOnLoad: () => void;
    }
}

@Injectable()
export class CaptchaService {
    private _rejectCaptcha: (reason?: any) => void;
    private _resolveCaptcha: (value: boolean | PromiseLike<boolean>) => void;
    private _captchaResponseToken: string;
    private _captcha = new Promise<boolean>((resolve, reject) => {
        this._resolveCaptcha = resolve;
        this._rejectCaptcha = reject;
    });

    private readonly _environment = inject(ENVIRONMENT_TOKEN);

    constructor() {
        this.loadCaptcha();
    }

    renderCaptcha(captchaId: string, captchaResolved: () => void): void {
        this._captcha?.then(() => {
            window.reCAPTCHACallback = (captchaResponse: string): void => {
                if (captchaResponse == null) {
                    this.captchaReset();
                } else {
                    this._captchaResponseToken = captchaResponse;
                    captchaResolved();
                }
            };
            setTimeout(() => {
                window.grecaptcha?.render(captchaId, {
                    sitekey: this._environment.captchaSiteKey,
                    size: 'invisible',
                    callback: 'reCAPTCHACallback',
                    badge: 'bottomright'
                });
                this.captchaReset();
            });

        });
    }

    captchaReset(): void {
        this._captcha?.then(() => {
            this._captchaResponseToken = null;
            window.grecaptcha?.reset();
        });
    }

    captchaExecute(): void {
        this._captcha?.then(() => {
            window.grecaptcha?.execute();
        });
    }

    getCaptchaResponse(): string {
        return this._captchaResponseToken;
    }

    isCaptchaResolved(): boolean {
        // return true; // uncomment this to serve to production in local, WARNING, never commit this line uncommented
        return !!this._captchaResponseToken || !this._environment.production;
    }

    loadCaptcha(): void {
        if (this._environment.production) {
            const f = document.getElementsByTagName('script')[0];
            const j = document.createElement('script');
            window.grecaptchaOnLoad = (): void => {
                this._resolveCaptcha(true);
            };
            j.async = true;
            j.defer = true;
            j.src = `${GRECAPTCHA_SRC}?onload=grecaptchaOnLoad`;
            j.onload = (): void => null;
            j.onerror = (): void => this._rejectCaptcha('Error loading grecaptcha');
            f.parentNode.insertBefore(j, f);
        }
    }
}
