import { ListResponse } from '@OneboxTM/utils-state';

export enum AttendantFieldType {
    integer = 'INTEGER',
    string = 'STRING',
    image = 'IMAGE'
}

export interface AttendantField {
    id: number;
    sid: string;
    max_length: number;
    type: AttendantFieldType;
    group: string;
}

export interface GetAttendantFields extends ListResponse<AttendantField>{
}
