import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Provider } from '@angular/core';
import { JwtInterceptor, SPInterceptor, ErrorsInterceptor, HeadersInterceptor } from '@admin-clients/shared/core/data-access';

export const provideApplicationInterceptors: () => Provider[] = () => [
    {
        provide: HTTP_INTERCEPTORS,
        useClass: SPInterceptor,
        multi: true
    },
    {
        provide: HTTP_INTERCEPTORS,
        useFactory: () => new HeadersInterceptor({ origin: 'cpanel-client' }),
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
    }
];
