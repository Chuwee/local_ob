import { FORM_CONTROL_ERRORS } from '@OneboxTM/feature-form-control-errors';
import { provideSentryErrorHandler } from '@OneboxTM/sentry';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { importProvidersFrom, inject } from '@angular/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideServiceWorker, SwRegistrationOptions } from '@angular/service-worker';
import { provideTranslateService } from '@ngx-translate/core';
import { Angulartics2Module } from 'angulartics2';
import { AuthenticationService, FormErrors } from '@admin-clients/cpanel/core/data-access';
import { provideMaterialSettings } from '@admin-clients/shared/common/ui/ob-material';
import { AUTHENTICATION_SERVICE, translateHttpLoaderFactory } from '@admin-clients/shared/core/data-access';
import { provideApplicationLocationStrategy } from '@admin-clients/shared/utility/utils';
import { Environment } from '../environments/environment.model';
import { provideEnvironmentConstants } from './app.constants';
import { routes } from './app.routes';
import { provideApplicationInitializers } from './core/app.initializers';
import { provideApplicationInterceptors } from './core/app.interceptors';
import { CPANEL_DATEPICKER_FORMATS } from './core/date-picker-formats';

export const providers = [
    provideEnvironmentConstants(),
    provideApplicationInitializers(),
    provideApplicationInterceptors(),
    provideApplicationLocationStrategy(),
    provideMaterialSettings(),
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(withInterceptorsFromDi()),
    provideMomentDateAdapter(CPANEL_DATEPICKER_FORMATS),
    provideServiceWorker(`${document.location.protocol}//${document.location.host}/ngsw-worker.js`),
    provideTranslateService({
        loader: translateHttpLoaderFactory()
    }),
    provideSentryErrorHandler(() => {
        const environment = inject<Environment>(ENVIRONMENT_TOKEN);
        return {
            dsn: 'https://221a3016d15187d1cde7935accd89073@o258474.ingest.us.sentry.io/4508919297736704',
            environment: environment.env,
            release: environment.version?.replace('v', ''),
            production: environment.production
        };
    }),
    {
        provide: SwRegistrationOptions,
        useFactory: () => {
            const environment = inject(ENVIRONMENT_TOKEN);
            return { enabled: environment.env === 'pre' || environment.env === 'pro' };
        },
        deps: [ENVIRONMENT_TOKEN]
    },
    {
        provide: AUTHENTICATION_SERVICE,
        useExisting: AuthenticationService
    },
    {
        provide: FORM_CONTROL_ERRORS,
        useClass: FormErrors
    },
    importProvidersFrom([
        Angulartics2Module.forRoot()
    ])
];
