import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { RateRestrictions } from './models';

export interface RateRestrictionsService {
    ratesRestrictions: {
        load(id: number): void;
        update(id: number, rateId: number, reqBody: Partial<RateRestrictions>): Observable<void>;
        delete(id: number, rateId: number): Observable<void>;
        get$(): Observable<RateRestrictions[]>;
        clear(): void;
        inProgress$(): Observable<boolean>;
    };
}

export const RATE_RESTRICTIONS_SERVICE = new InjectionToken<RateRestrictionsService>('RATE_RESTRICTIONS_SERVICE_TOKEN');
