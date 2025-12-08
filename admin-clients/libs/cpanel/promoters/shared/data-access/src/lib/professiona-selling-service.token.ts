import { Id } from '@admin-clients/shared/data-access/models';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { ChannelB2bAssignations, ProfessionalSellingChannel } from './models/professional-selling-channel.model';

export interface ProfessionalSellingService {
    context: 'EVENT' | 'SEASON_TICKET';
    getEntityId(): Observable<number>;
    isB2bAssignationsInProgress$(): Observable<boolean>;
    loadB2bAssignations(eventId: number, channelId: number): void;
    getB2bAssignations$(): Observable<ChannelB2bAssignations>;
    clearB2bAssignations(): void;
    updateB2bAssignations(eventId: number, channelId: number, assignations: ChannelB2bAssignations<Id>): Observable<void>;
    channel: {
        get$: () => Observable<ProfessionalSellingChannel>;
        inProgress$: () => Observable<boolean>;
        clear: () => void;
    };
}

export const PROFESSIONAL_SELLING_SERVICE = new InjectionToken<ProfessionalSellingService>('PROFESSIONAL_SELLING_SERVICE_TOKEN');
