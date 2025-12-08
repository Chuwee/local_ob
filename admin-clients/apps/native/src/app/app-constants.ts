import { APP_BASE_URL } from '@OneboxTM/data-access-app-config';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { APP_BASE_API, APP_BASE_API_OAUTH } from '@admin-clients/shared/core/data-access';
import { APP_BASE_HREF, DOCUMENT } from '@angular/common';
import { Provider, inject } from '@angular/core';
import { Environment } from '../environments/environment-model';

export const APP_CONSTANTS: Provider[] = [
    {
        provide: APP_BASE_URL,
        useFactory: (document: Document) => document.location.origin,
        deps: [DOCUMENT]
    },
    {
        provide: ENVIRONMENT_TOKEN,
        useValue: window.__environment
    },
    {
        provide: APP_BASE_HREF,
        useFactory: (baseUrl: string) => baseUrl,
        deps: [APP_BASE_URL]
    },
    {
        provide: APP_BASE_API,
        useFactory: () => {
            const environment = inject<Environment>(ENVIRONMENT_TOKEN);
            return environment.gatewayUrl;
        },
        deps: [ENVIRONMENT_TOKEN]
    },
    {
        provide: APP_BASE_API_OAUTH,
        useFactory: () => {
            const environment = inject<Environment>(ENVIRONMENT_TOKEN);
            return environment.oauthUrl;
        },
        deps: [ENVIRONMENT_TOKEN]
    }
];
