import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export interface B2bConditionsService {
    context: 'EVENT' | 'SEASON_TICKET';
    getContextIdAndCurrency(): Observable<{ id: number; currency: string }>;
}

export const B2B_CONDITIONS_SERVICE = new InjectionToken<B2bConditionsService>('B2B_CONDITIONS_SERVICE');
