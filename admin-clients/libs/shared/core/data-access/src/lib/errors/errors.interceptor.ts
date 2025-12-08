import { Location } from '@angular/common';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AUTHENTICATION_SERVICE } from '../auth/authentication.token';
import { ErrorsService } from './errors.service';

@Injectable()
export class ErrorsInterceptor implements HttpInterceptor {
    private readonly _errors = inject(ErrorsService);
    private readonly _router = inject(Router);
    private readonly _location = inject(Location);
    private readonly _auth = inject(AUTHENTICATION_SERVICE);

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        return next.handle(request)
            .pipe(
                catchError(err => {
                    if (err.status === 401) {
                        // auto logout on unauthorized response
                        this._auth.logout();
                        this._router.navigate(['/login'], {
                            queryParams: { returnUrl: this._location.path() }
                        });
                        return of(err);
                    } else {
                        this._errors.setError(err);
                        return throwError(err);
                    }
                })
            );
    }
}
