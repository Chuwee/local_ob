import { getListData, getMetadata, StateManager } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { inject, Injectable } from '@angular/core';
import { EventQuestionsApi } from './api/event-questions.api';
import { PutEventPostBookingQuestionsSettings } from './models/put-event-design-questions-settings.model';
import { EventQuestionsState } from './state/event-questions.state';

@Injectable({ providedIn: 'root' })
export class EventQuestionsService {
    readonly #api = inject(EventQuestionsApi);
    readonly #state = inject(EventQuestionsState);

    readonly eventPbQuestionsSettings = Object.freeze({
        load: (eventId: number) => StateManager.load(
            this.#state.eventPbQuestionsSettings,
            this.#api.getEventPbQuestions(eventId)
        ),
        get$: () => this.#state.eventPbQuestionsSettings.getValue$(),
        update$: (eventId: number, settings: PutEventPostBookingQuestionsSettings) => StateManager.inProgress(
            this.#state.eventPbQuestionsSettings,
            this.#api.putEventPbQuestions(eventId, settings)
        ),
        loading$: () => this.#state.eventPbQuestionsSettings.isInProgress$(),
        error$: () => this.#state.eventPbQuestionsSettings.getError$(),
        cancelLoad: () => this.#state.eventPbQuestionsSettings.triggerCancellation(),
        clear: () => this.#state.eventPbQuestionsSettings.setValue(null)
    });

    readonly postBookingQuestionsList = Object.freeze({
        load: (filters: PageableFilter) => StateManager.load(
            this.#state.postBookingQuestions,
            this.#api.getPostBookingQuestions(filters)
        ),
        getData$: () => this.#state.postBookingQuestions.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.postBookingQuestions.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.postBookingQuestions.isInProgress$(),
        error$: () => this.#state.postBookingQuestions.getError$(),
        cancelLoad: () => this.#state.postBookingQuestions.triggerCancellation(),
        clear: () => this.#state.postBookingQuestions.setValue(null)
    });
}
