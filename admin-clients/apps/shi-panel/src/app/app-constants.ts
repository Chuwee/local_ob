import { APP_BASE_PATH } from '@OneboxTM/data-access-app-config';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { Provider } from '@angular/core';
import { APP_BASE_API, APP_BASE_API_OAUTH, APP_NAME } from '@admin-clients/shared/core/data-access';

export const provideAppConstants = (): Provider[] => [
    {
        provide: APP_BASE_API,
        useValue: '/api'
    },
    {
        provide: APP_BASE_API_OAUTH,
        useValue: '/api'
    },
    {
        provide: ENVIRONMENT_TOKEN,
        useValue: window.__environment
    },
    {
        provide: APP_BASE_PATH,
        useValue: ''
    },
    {
        provide: APP_NAME,
        useValue: 'Onebox Hub'
    }
];
