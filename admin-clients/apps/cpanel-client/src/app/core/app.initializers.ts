import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { EnvironmentProviders, inject, provideAppInitializer } from '@angular/core';
import { filter, firstValueFrom, lastValueFrom, Observable, switchMap } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { ErrorsService, I18nService, WebsocketsService } from '@admin-clients/shared/core/data-access';
import { AppVersionMgmtService } from '@admin-clients/shared/core/features';
import { GtmLoaderService, HjLoaderService, UaLoaderService } from '@admin-clients/shared/data-access/trackers';

function errorHandlerFactory(): void {
    const msgDialogSrv = inject(MessageDialogService);
    const errorsSrv = inject(ErrorsService);
    msgDialogSrv.listenErrorMessages(errorsSrv.getErrorMessages$());
}

function trackersLoaderFactory(): void {
    const env = inject(ENVIRONMENT_TOKEN);
    Promise.all<void>([
        UaLoaderService.loadLib(env.uaTrackingId).catch(error => console.error('Could not load UA', error)),
        GtmLoaderService.loadLib(env.gtmId).catch(error => console.error('Could not load GTM', error))
    ]);
}

function hotjarLoaderFactory(): void {
    const env = inject(ENVIRONMENT_TOKEN);
    HjLoaderService.loadLib(env.hjid).catch(error => console.error('Could not load Hotjar', error));
}

function retrieveUserFactory(): Promise<unknown> {
    const auth = inject(AuthenticationService);
    const i18n = inject(I18nService);
    const token = AuthenticationService.getTokenFromStorage();
    if (token) {
        auth.setToken(token);
        return lastValueFrom(auth.requestLoggedUser())
            .then(user => {
                if (user?.timezone) {
                    i18n.setTimezone(user.timezone);
                }
                if (user?.language) {
                    return firstValueFrom(i18n.setLocale(user.language));
                } else {
                    return firstValueFrom(i18n.setLocale());
                }
            })
            .catch(() => firstValueFrom(i18n.setLocale()));
    }
    return firstValueFrom(i18n.setLocale());
};

function retrieveSpFactory(): Promise<void> {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('sp')) {
        sessionStorage.setItem('sp', urlParams.get('sp'));
    }
    if (urlParams.has('fm')) {
        sessionStorage.setItem('fm', urlParams.get('fm'));
    }
    return Promise.resolve();
}

function versionFactory(): Observable<boolean> {
    const versionMgmtSrv = inject(AppVersionMgmtService);
    return versionMgmtSrv.inited$.pipe(first(Boolean));
}

function stompConfigFactory(): void {
    const authSrv = inject(AuthenticationService);
    const versionMgmtSrv = inject(AppVersionMgmtService);
    const wsSrv = inject(WebsocketsService);
    versionMgmtSrv.inited$ // no connection before version management started (versionMgmtFactory)
        .pipe(
            filter(Boolean),
            switchMap(() => authSrv.getLoggedUser$()), // logged user confirms legit token, no user = bad token or logout
            switchMap(user => authSrv.getToken$().pipe(map(token => user ? token : null)))
        )
        .subscribe(token => wsSrv.setToken(token));
}

export const provideApplicationInitializers: () => EnvironmentProviders[] = () => [
    provideAppInitializer(errorHandlerFactory),
    provideAppInitializer(retrieveUserFactory),
    provideAppInitializer(versionFactory),
    provideAppInitializer(stompConfigFactory),
    provideAppInitializer(retrieveSpFactory),
    provideAppInitializer(trackersLoaderFactory),
    provideAppInitializer(hotjarLoaderFactory)
];
