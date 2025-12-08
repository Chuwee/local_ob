import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { EventPostBookingQuestionsSettings } from '../models/event-design-questions-settings.model';
import { GetPostBookingQuestionResponse } from '../models/get-post-booking-questions-response.model';

@Injectable({ providedIn: 'root' })
export class EventQuestionsState {
    readonly eventPbQuestionsSettings = new StateProperty<EventPostBookingQuestionsSettings>();
    readonly postBookingQuestions = new StateProperty<GetPostBookingQuestionResponse>();
}
