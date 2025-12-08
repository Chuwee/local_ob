import { BaseHrefInterceptor } from '@OneboxTM/data-access-app-config';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Provider } from '@angular/core';
import { SPInterceptor, JwtInterceptor, ErrorsInterceptor, HeadersInterceptor } from '@admin-clients/shared/core/data-access';

export const provideAppInterceptors = (): Provider[] => [
    {
        provide: HTTP_INTERCEPTORS,
        useClass: SPInterceptor,
        multi: true
    },
    {
        provide: HTTP_INTERCEPTORS,
        useFactory: () => new HeadersInterceptor({ origin: 'shi-panel' }),
        multi: true
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: JwtInterceptor,
        multi: true
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: ErrorsInterceptor,
        multi: true
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: BaseHrefInterceptor,
        multi: true
    }
];
