import { ErrorsInterceptor, HeadersInterceptor, JwtInterceptor } from '@admin-clients/shared/core/data-access';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Provider } from '@angular/core';

export const HTTP_INTERCEPTORS_LIST: Provider[] = [
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
        useFactory: () => new HeadersInterceptor({ origin: 'ob-panel-app' }),
        multi: true
    }
];
