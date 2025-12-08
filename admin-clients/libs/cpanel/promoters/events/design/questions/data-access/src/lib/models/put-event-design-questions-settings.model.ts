import { EventChannelsScopeType } from '@admin-clients/cpanel/promoters/events/data-access';

export interface PutEventPostBookingQuestionsSettings {
    enabled: boolean;
    questions: number[];
    channels: {
        selection_type: EventChannelsScopeType;
        ids?: number[];
    };
};
