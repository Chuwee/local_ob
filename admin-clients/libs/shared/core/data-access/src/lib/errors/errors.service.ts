import { TrackingService } from '@admin-clients/shared/data-access/trackers';
import { inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ApiErrorResponse } from './model/api-error-response';
import { ErrorsNotInterceptable } from './model/errors-not-interceptable.enum';
import { ErrorsState } from './state/errors.state';

const API_ERRORS = 'API_ERRORS.';
const API_ERROR_GENERIC = 'GENERIC_ERROR';
const API_SERVER_ERROR = 'SERVER_ERROR';
const API_ERROR_GENERIC_KEY = API_ERRORS + API_ERROR_GENERIC;
const API_TRACE_ID_HEADER = 'ob-audit-trace-id';

@Injectable({
    providedIn: 'root'
})
export class ErrorsService {
    private readonly _errorsState = inject(ErrorsState);
    private readonly _translate = inject(TranslateService);
    private readonly _trackingService = inject(TrackingService);

    setError(error: ApiErrorResponse): void {
        this._errorsState.setError(error);
    }

    getError$(): Observable<ApiErrorResponse> {
        return this._errorsState.getError$();
    }

    getErrorMessages$(): Observable<{ message: string; subMessages?: string[] }> {
        return this._errorsState.getError$()
            .pipe(
                filter(Boolean),
                filter(body => !((body.error?.code || body.error?.error) in ErrorsNotInterceptable)),
                map(body => {
                    const code = body.error?.code || body.error?.error;
                    const msg = this.getErrorMessages(code, body);

                    //Send event to GoogleAnalytics when generic error
                    if (msg.subMessages?.length) {
                        this._trackingService.sendEventTrack(code, 'Error', body.headers.get(API_TRACE_ID_HEADER), body.status);
                    }

                    console.error('API error encountered', [body]);
                    return msg;
                })
            );
    }

    private getErrorMessages(code: string, body: ApiErrorResponse): { message: string; subMessages?: string[] } {
        const key = code ? API_ERRORS + code : null;
        let message = code ? this._translate.instant(key) : null;
        let subMessages: string[];
        if (!message || message === key || code === API_ERROR_GENERIC) {
            message = this._translate.instant(API_ERROR_GENERIC_KEY);
            const traceId = body.headers?.get(API_TRACE_ID_HEADER);
            if (code === API_SERVER_ERROR || code === API_ERROR_GENERIC) {
                if (body.headers?.get(API_TRACE_ID_HEADER)) {
                    subMessages = [traceId];
                }
            } else {
                const netErrorMsg = this._translate.instant(API_ERRORS + body.status);
                if (netErrorMsg && netErrorMsg !== API_ERRORS + body.status) {
                    message = netErrorMsg;
                    subMessages = [traceId];
                } else {
                    subMessages = [
                        code ? `Error: ${code}` : null,
                        !code ? `Status code: ${body.status}` : null,
                        body.error?.message,
                        traceId
                    ];
                }
            }
        }
        subMessages = subMessages?.filter(m => !!m);
        return { message, subMessages };
    }

}
