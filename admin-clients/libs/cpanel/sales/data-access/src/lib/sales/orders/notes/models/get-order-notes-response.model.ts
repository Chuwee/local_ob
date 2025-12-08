import { ListResponse } from '@OneboxTM/utils-state';
import { Note } from '@admin-clients/cpanel/common/feature/notes';

export interface GetOrderNotesResponse extends ListResponse<Note> {
}
