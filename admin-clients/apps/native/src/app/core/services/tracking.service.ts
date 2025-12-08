import { Injectable, inject } from '@angular/core';
import { AngularFireAnalytics } from '@angular/fire/compat/analytics';
import { stringify } from '../utils/stringify';

type ParameterValue = string | number | boolean;
type Parameters = Record<string, ParameterValue>;
@Injectable({
    providedIn: 'root'
})
export class TrackingService {

    private _webAnalytics = inject(AngularFireAnalytics);

    constructor(
    ) { }

    info(description: string, data?: Parameters): Promise<void> {
        return this._webAnalytics.logEvent('info', { description, ...data });
    }

    event(event: string, data?: Parameters): Promise<void> {
        return this._webAnalytics.logEvent(event, data);
    }

    exception(params?: { description: string; fatal?: boolean; data?: unknown; error?: any;[key: string]: unknown }): Promise<void> {
        const error = params.error?.stack ? params.error.stack : stringify(params.error);
        const parameters = { ...params, data: stringify(params.data), error };
        return this._webAnalytics.logEvent('exception', parameters);
    }

    enableCollection(): void {
        this._webAnalytics.setAnalyticsCollectionEnabled(true);
    }
}
