import { ListResponse } from '@OneboxTM/utils-state';

export interface EventAttendantField {
    sid?: string;
    field_id: number;
    min_length: number;
    max_length: number;
    order: number;
    mandatory: boolean;
}

export interface GetEventAttendantFields extends ListResponse<EventAttendantField> {
}
