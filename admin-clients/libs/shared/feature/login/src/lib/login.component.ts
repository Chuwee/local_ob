import { NgStyle } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, InjectionToken, OnDestroy, SecurityContext } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';

export const LOGIN_BACKGROUND_URL = new InjectionToken<string>('LOGIN_BACKGROUND_URL');

@Component({
    selector: 'app-login',
    templateUrl: 'login.component.html',
    styleUrls: ['./login.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, NgStyle, RouterOutlet
    ]
})
export class LoginComponent implements OnDestroy {
    private _onDestroy = new Subject<void>();

    backgroundUrl: string;

    constructor(
        private _sanitizer: DomSanitizer,
        @Inject(LOGIN_BACKGROUND_URL) private _backgroundUrl: string
    ) {
        const backgroundUrl = this._backgroundUrl + `/background_${Math.floor(Math.random() * 8) + 1}.jpg`;

        this.backgroundUrl = this._sanitizer.sanitize(SecurityContext.STYLE, 'url(' + backgroundUrl + ')');
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
    }
}
