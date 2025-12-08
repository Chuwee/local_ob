
import { EventChannelsScopeType } from '@admin-clients/cpanel/promoters/events/data-access';
import { PostBookingQuestion } from './post-booking-question.model';

export interface EventPostBookingQuestionsSettings {
    enabled: boolean;
    questions: PostBookingQuestion[];
    channels: {
        selection_type: EventChannelsScopeType;
        ids?: number[];
    };
};
