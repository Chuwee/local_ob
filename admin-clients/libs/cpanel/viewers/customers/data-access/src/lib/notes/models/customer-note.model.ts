import { ListResponse } from '@OneboxTM/utils-state';
import { Note } from '@admin-clients/cpanel/common/feature/notes';

export interface GetCustomerNotesResponse extends ListResponse<Note> {
}

export interface PostCustomerNote {
    title: string;
    description: string;
}

export interface PutCustomerNote {
    title: string;
    description: string;
}
