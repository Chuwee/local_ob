import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EventRestriction, EventRestrictionList, EventRestrictionStructure } from '../models/event-restrictions.model';

@Injectable()
export class EventRestrictionsApi {
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS_API = `${this.BASE_API}/mgmt-api/v1/events`;
    private readonly _http = inject(HttpClient);

    getRestrictions(eventId: number): Observable<EventRestrictionList> {
        return this._http.get<EventRestrictionList>(`${this.EVENTS_API}/${eventId}/avet-sector-restrictions`);
    }

    getRestriction(eventId: number, sid: string): Observable<EventRestriction> {
        return this._http.get<EventRestriction>(`${this.EVENTS_API}/${eventId}/avet-sector-restrictions/${sid}`);
    }

    postRestriction(eventId: number, restriction: Partial<EventRestriction>): Observable<{ code: string }> {
        return this._http.post<{ code: string }>(`${this.EVENTS_API}/${eventId}/avet-sector-restrictions`, restriction);
    }

    putRestriction(eventId: number, restriction: Partial<EventRestriction>): Observable<void> {
        const sid = restriction?.sid;
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/avet-sector-restrictions/${sid}`, restriction);
    }

    deleteRestriction(eventId: number, restriction: Partial<EventRestriction> | string): Observable<void> {
        const sid = typeof restriction === 'string' ? restriction : restriction?.sid;
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}/avet-sector-restrictions/${sid}`);
    }

    getRestrictionsStructure(eventId: number): Observable<EventRestrictionStructure[]> {
        return this._http.get<EventRestrictionStructure[]>(`${this.EVENTS_API}/${eventId}/avet-restrictions-dynamic-configuration`);
    }
}
