import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EventPostBookingQuestionsSettings } from '../models/event-design-questions-settings.model';
import { GetPostBookingQuestionResponse } from '../models/get-post-booking-questions-response.model';
import { PutEventPostBookingQuestionsSettings } from '../models/put-event-design-questions-settings.model';

@Injectable({ providedIn: 'root' })
export class EventQuestionsApi {
    private readonly _http = inject(HttpClient);
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly MGMT_API = `${this.BASE_API}/mgmt-api/v1`;
    private readonly EVENTS_API = `${this.MGMT_API}/events`;
    private readonly QUESTIONS = 'post-booking-questions';

    getEventPbQuestions(eventId: number): Observable<EventPostBookingQuestionsSettings> {
        return this._http.get<EventPostBookingQuestionsSettings>(`${this.EVENTS_API}/${eventId}/${this.QUESTIONS}`);
    }

    putEventPbQuestions(eventId: number, request: PutEventPostBookingQuestionsSettings): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}/${this.QUESTIONS}`, request);
    }

    getPostBookingQuestions(filters: PageableFilter): Observable<GetPostBookingQuestionResponse> {
        const params = buildHttpParams(filters);
        return this._http.get<GetPostBookingQuestionResponse>(`${this.MGMT_API}/${this.QUESTIONS}`, { params });
    }
}
