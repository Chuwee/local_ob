import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';
import { AUTHENTICATION_SERVICE } from './authentication.token';

/*
 The JWT interceptor intercepts the incoming requests from the application/user and adds JWT token
 to the request's Authorization header, only if the user is logged in.

 This JWT token in the request header is required to access the SECURE API ENDPOINTS on the server
 */

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    private static readonly API_PREFIX = 'api/';

    private _auth = inject(AUTHENTICATION_SERVICE);

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        // NOTE: Only add the auth token to API endpoints.
        if (JwtInterceptor.isApiEndpoint(request.url)) {
            return this.addToken(request).pipe(
                first(),
                mergeMap((requestWithToken: HttpRequest<unknown>) => next.handle(requestWithToken))
            );
        } else {
            return next.handle(request);
        }
    }

    /**
     * Determines if the requested URL is an API endpoint.
     * */
    private static isApiEndpoint(url = ''): boolean {
        return url.toLowerCase().indexOf(JwtInterceptor.API_PREFIX) > -1;
    }

    /**
     * Adds the JWT token to the request's header.
     */
    private addToken(request: HttpRequest<unknown>): Observable<HttpRequest<unknown>> {
        // NOTE: DO NOT try to immediately setup this selector in the constructor or as an assignment in a
        // class member variable as there's no stores available when this interceptor first fires up and
        // as a result it'll throw a runtime error.
        return this._auth.getToken$()
            .pipe(
                first(),
                mergeMap((token: string) => {
                    // if the user making the request is logged in, he will have JWT token,
                    // which is set by Authorization Service during login/startup process
                    if (token) {
                        // clone the incoming request and add JWT token in the cloned request's Authorization Header
                        request = request.clone({
                            headers: request.headers.set('Authorization', `Bearer ${token}`)
                            // withCredentials: true
                        });
                    }
                    // handle any other requests which went unhandled
                    return of(request);
                })
            );
    }

}
