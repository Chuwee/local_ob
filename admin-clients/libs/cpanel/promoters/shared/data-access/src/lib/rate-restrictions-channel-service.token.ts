import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { RateRestrictionsChannel } from './models/rate-restrictions-channel-model';

export interface RateRestrictionsChannelsService {
    get$(): Observable<RateRestrictionsChannel[]>;
    load(id: number): void;
    clear(): void;
    channelsPath: string[];
}

export const RATE_RESTRICTIONS_CHANNELS_SERVICE =
    new InjectionToken<RateRestrictionsChannelsService>('RATE_RESTRICTIONS_CHANNELS_SERVICE_TOKEN');
