import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export interface SessionPackService {
    linkSeatsToSessionPack(
        eventId: number,
        sessionId: number,
        seatIds: number[],
        target: string, // FREE or BR-id
        quota: string // quota id
    ): Observable<void>;

    unlinkSeatsToSessionPack(
        eventId: number,
        sessionId: number,
        seatIds: number[],
        target: string, // FREE or BR-id
        quota: string // quota id
    ): Observable<void>;

    linkNnzToSessionPack(
        eventId: number,
        sessionId: number,
        nnzId: number,
        source: string, // FREE or BR-id
        target: string, // FREE or BR-id
        count: number
    ): Observable<void>;

    unlinkNnzToSessionPack(
        eventId: number,
        sessionId: number,
        nnzId: number,
        source: string, // FREE or BR-id
        target: string, // FREE or BR-id
        count: number
    ): Observable<void>;

    isLinkToSessionPackSaving$(): Observable<boolean>;
}

export const SESSION_PACK_SERVICE = new InjectionToken<SessionPackService>('SESSION_PACK_TOKEN');
