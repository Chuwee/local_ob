import { APP_BASE_PATH, APP_BASE_URL } from '@OneboxTM/data-access-app-config';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { FEVER_ZONE_URL } from '@admin-clients/cpanel-fever-data-access';
import { APP_BASE_HREF, DOCUMENT } from '@angular/common';
import { Provider, inject } from '@angular/core';
import { APP_BASE_API, APP_BASE_API_OAUTH, APP_NAME, GOOGLE_CLOUD_API_KEY } from '@admin-clients/shared/core/data-access';
import { Environment } from '../environments/environment.model';

export const provideEnvironmentConstants = (): Provider[] => [
    {
        provide: ENVIRONMENT_TOKEN,
        useValue: window.__environment
    },
    {
        provide: APP_BASE_URL,
        useFactory: (document: Document) => document.location.origin,
        deps: [DOCUMENT]
    },
    {
        provide: APP_BASE_PATH,
        useValue: ''
    },
    {
        provide: APP_NAME,
        useValue: 'Onebox Panel'
    },
    {
        provide: GOOGLE_CLOUD_API_KEY,
        useFactory: () => {
            const environment = inject<Environment>(ENVIRONMENT_TOKEN);
            return environment.googleCloudApiKey;
        },
        deps: [ENVIRONMENT_TOKEN]
    },
    {
        provide: APP_BASE_HREF,
        useFactory: (baseUrl: string, basePath: string) => `${baseUrl}/${basePath}`,
        deps: [APP_BASE_URL, APP_BASE_PATH]
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
    },
    {
        provide: FEVER_ZONE_URL,
        useFactory: () => {
            const environment = inject<Environment>(ENVIRONMENT_TOKEN);
            return environment.feverZoneUrl;
        },
        deps: [ENVIRONMENT_TOKEN]
    }
];

