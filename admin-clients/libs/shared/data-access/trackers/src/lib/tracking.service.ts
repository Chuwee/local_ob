import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { inject, Injectable } from '@angular/core';
import { Angulartics2, Angulartics2GoogleAnalytics, Angulartics2GoogleTagManager } from 'angulartics2';

@Injectable({ providedIn: 'root' })
export class TrackingService {
    private readonly _angulartics2GA = inject(Angulartics2GoogleAnalytics);
    private readonly _angulartics2GTM = inject(Angulartics2GoogleTagManager);
    private readonly _angulartics2 = inject(Angulartics2);
    private readonly _environment? = inject(ENVIRONMENT_TOKEN, { optional: true });

    startTrackingUA(): void {
        if (this._environment?.uaTrackingId) {
            this._angulartics2GA.startTracking();
        } else {
            throw new Error('UA vendor not configured');
        }
    }

    startTrackingGTM(): void {
        if (this._environment?.gtmId) {
            this._angulartics2GTM.startTracking();
        } else {
            throw new Error('GTM vendor not configured');
        }
    }

    setUserUA(dimensions: Record<string, unknown>): void {
        if (this._environment?.uaTrackingId) {
            this._angulartics2GA.setUserProperties(dimensions);
        } else {
            throw new Error('UA vendor not configured');
        }
    }

    setUserGTM(userId: string): void {
        if (this._environment?.gtmId) {
            this._angulartics2.setUsername.next(userId);
            this.sendRawEventTrack('User logged', { event: 'setUser' });
        } else {
            throw new Error('GTM vendor not configured');
        }
    }

    sendRawEventTrack(eventAction: string, properties: Record<string, unknown>): void {
        this._angulartics2.eventTrack.next({
            action: eventAction,
            properties
        });
    }

    sendEventTrack(eventAction: string, eventCategory: string, eventLabel?: string, eventValue?: number): void {
        this._angulartics2.eventTrack.next({
            action: eventAction,
            properties: {
                category: eventCategory,
                label: eventLabel,
                value: eventValue
            }
        });
    }
}
