import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Adds custom headers to all api requests
 */
export class HeadersInterceptor implements HttpInterceptor {
    private static readonly API_PREFIX = 'api/';

    origin: string;

    constructor({ origin }: { origin: string }) {
        this.origin = origin;
    }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        if (HeadersInterceptor.isApiEndpoint(request.url)) {
            return next.handle(HeadersInterceptor.addHeadersToRequest(request, this.headers()));
        }
        return next.handle(request);
    }

    private headers(): Record<string, string> {
        return { ['ob-app-origin']: this.origin };
    }

    /**
     * Determines if the requested URL is an API endpoint.
     */
    private static isApiEndpoint(url = ''): boolean {
        return url.toLowerCase().indexOf(HeadersInterceptor.API_PREFIX) > -1;
    }

    /**
     * Adds custom headers to request
     * this could be extended to add headers dynamically, getting them for example from a service
     */
    private static addHeadersToRequest(request: HttpRequest<unknown>, headers: Record<string, string>): HttpRequest<unknown> {
        const httpheaders = Object.entries(headers).reduce(
            ((acc, [key, value]) => acc = acc.set(key, value)), request.headers
        );
        return request.clone({ headers: httpheaders });
    }

}
