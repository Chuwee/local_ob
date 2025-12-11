import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from './common-types';

export type ReviewCriteria = 'ALWAYS' | 'ONLY_IF_VALIDATED' | 'NEVER';
export type ReviewTimeUnit = 'DAYS' | 'HOURS';
export type ReviewScope = 'EVENT' | 'SESSION';

export interface ReviewConfig {
    enable: boolean;
    send_criteria?: ReviewCriteria;
    send_time_unit?: ReviewTimeUnit;
    send_time_value?: number;
}

export interface ReviewConfigElementFilter extends PageableFilter { }

export interface GetReviewConfigElementResponse extends ListResponse<ReviewConfigElement> { }

export interface PutReviewConfigElement {
    send_criteria: ReviewCriteria;
    scope_ids: number[];
}

export interface ReviewConfigElement {
    channel_id: number;
    scope_id: number;
    scope: ReviewScope;
    send_criteria: ReviewCriteria;
    details?: {
        event: {
            name: string;
        };
        session: {
            name: string;
            start_date: string;
        };
    };
}

