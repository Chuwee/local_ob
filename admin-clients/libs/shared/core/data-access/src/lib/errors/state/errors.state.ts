import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ApiErrorResponse } from '../model/api-error-response';

@Injectable({ providedIn: 'root' })
export class ErrorsState {
    private _error = new BehaviorSubject<ApiErrorResponse>(null);
    private _error$ = this._error.asObservable();

    setError(error: ApiErrorResponse): void {
        this._error.next(Object.assign(new ApiErrorResponse(), error));
    }

    getError$(): Observable<ApiErrorResponse> {
        return this._error$;
    }
}
