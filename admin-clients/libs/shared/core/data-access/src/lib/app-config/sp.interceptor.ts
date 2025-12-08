import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class SPInterceptor implements HttpInterceptor {
    private static readonly API_PREFIX = 'api/';

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        if (SPInterceptor.isApiEndpoint(request.url)) {
            return next.handle(SPInterceptor.addSPHeaders(request));
        } else {
            return next.handle(request);
        }
    }

    /**
     * Determines if the requested URL is an API endpoint.
     */
    private static isApiEndpoint(url = ''): boolean {
        return url.toLowerCase().indexOf(SPInterceptor.API_PREFIX) > -1;
    }

    /**
     * Adds service-preview header if present in sessionStorage.
     */
    private static addSPHeaders(request: HttpRequest<unknown>): HttpRequest<unknown> {
        if (sessionStorage.getItem('sp')) {
            request = request.clone({
                headers: request.headers.set('ob-service-preview', sessionStorage.getItem('sp'))
            });
        }
        if (sessionStorage.getItem('fm')) {
            request = request.clone({
                headers: request.headers.set('ob-forward-mode', sessionStorage.getItem('fm'))
            });
        }
        return request;
    }

}
