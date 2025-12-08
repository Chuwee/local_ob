import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { inject, EnvironmentProviders, provideAppInitializer } from '@angular/core';
import { lastValueFrom, firstValueFrom, Observable, first } from 'rxjs';
import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { ErrorsService, I18nService } from '@admin-clients/shared/core/data-access';
import { AppVersionMgmtService } from '@admin-clients/shared/core/features';
import { GtmLoaderService } from '@admin-clients/shared/data-access/trackers';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';

export function trackersLoaderFactory(): void {
    const env = inject(ENVIRONMENT_TOKEN);
    GtmLoaderService.loadLib(env.gtmId).catch(error => console.error('Could not load GTM', error));
}

export function retrieveUserFactory(): Promise<unknown> {
    const auth = inject(AuthenticationService);
    const i18n = inject(I18nService);
    const token = AuthenticationService.getTokenFromStorage();
    const lang = 'en-US'; // TODO: Only english for now
    if (token) {
        auth.setToken(token);
        return lastValueFrom(auth.requestLoggedUser())
            .then(user => {
                if (user?.timezone) {
                    i18n.setTimezone(user.timezone);
                }
                if (user?.language) {
                    return firstValueFrom(i18n.setLocale(lang));
                } else {
                    return firstValueFrom(i18n.setLocale(lang));
                }
            })
            .catch(() => firstValueFrom(i18n.setLocale(lang)));
    }
    return firstValueFrom(i18n.setLocale(lang));
}

export function retrieveSpFactory(): void {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('sp')) {
        sessionStorage.setItem('sp', urlParams.get('sp'));
    }
    if (urlParams.has('fm')) {
        sessionStorage.setItem('fm', urlParams.get('fm'));
    }
}

function errorHandlerFactory(): void {
    const msgDialogSrv = inject(MessageDialogService);
    const errorsSrv = inject(ErrorsService);
    msgDialogSrv.listenErrorMessages(errorsSrv.getErrorMessages$());
}

function versionFactory(): Observable<boolean> {
    const versionMgmtSrv = inject(AppVersionMgmtService);
    return versionMgmtSrv.inited$.pipe(first(Boolean));
}

export const provideInitializers = (): EnvironmentProviders[] => [
    provideAppInitializer(() => errorHandlerFactory()),
    provideAppInitializer(() => retrieveSpFactory()),
    provideAppInitializer(() => retrieveUserFactory()),
    provideAppInitializer(() => trackersLoaderFactory()),
    provideAppInitializer(() => versionFactory())
];
