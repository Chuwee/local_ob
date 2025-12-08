import { APP_BASE_PATH, APP_BASE_URL } from '@OneboxTM/data-access-app-config';
import { FORM_CONTROL_ERRORS } from '@OneboxTM/feature-form-control-errors';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { APP_BASE_HREF, DOCUMENT } from '@angular/common';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { ErrorHandler, importProvidersFrom, inject } from '@angular/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { provideServiceWorker, SwRegistrationOptions } from '@angular/service-worker';
import { provideTranslateService } from '@ngx-translate/core';
import { Angulartics2Module } from 'angulartics2';
import { provideMaterialSettings } from '@admin-clients/shared/common/ui/ob-material';
import {
    AUTHENTICATION_SERVICE, GlobalErrorsHandler
} from '@admin-clients/shared/core/data-access';
import { provideApplicationLocationStrategy } from '@admin-clients/shared/utility/utils';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { provideAppConstants } from './app-constants';
import { routes } from './app.routes';
import { provideInitializers } from './core/app.initializers';
import { provideAppInterceptors } from './core/app.interceptors';
import { FormErrors } from './core/form-control-errors.model';
import { moduleHttpLoaderFactory } from './core/translations.provider';

export const OBHUB_DATEPICKER_FORMATS = {
    parse: {
        dateInput: 'L'
    },
    display: {
        dateInput: 'L',
        dateA11yLabel: 'L',
        monthYearLabel: 'MMM YYYY',
        monthYearA11yLabel: 'MMMM YYYY'
    }
};

export const providers = [
    provideInitializers(),
    provideRouter(routes),
    provideAnimations(),
    provideMaterialSettings(),
    provideHttpClient(withInterceptorsFromDi()),
    provideMomentDateAdapter(OBHUB_DATEPICKER_FORMATS),
    provideServiceWorker(`${document.location.protocol}//${document.location.host}/ngsw-worker.js`),
    provideAppConstants(),
    provideAppInterceptors(),
    provideApplicationLocationStrategy(),
    provideTranslateService({
        loader: moduleHttpLoaderFactory()
    }),
    {
        provide: ErrorHandler,
        useClass: GlobalErrorsHandler
    },
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
        provide: APP_BASE_URL,
        useFactory: (document: Document) => `${document.location.protocol}//${document.location.host}`,
        deps: [DOCUMENT]
    },
    {
        provide: APP_BASE_HREF,
        useFactory: (baseUrl: string, basePath: string) => `${baseUrl}/${basePath}`,
        deps: [APP_BASE_URL, APP_BASE_PATH]
    },
    {
        provide: FORM_CONTROL_ERRORS,
        useClass: FormErrors
    },
    importProvidersFrom(
        Angulartics2Module.forRoot()
    )
];
