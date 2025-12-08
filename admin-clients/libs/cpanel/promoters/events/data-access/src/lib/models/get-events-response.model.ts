import { ListResponse } from '@OneboxTM/utils-state';
import { Event } from './event.model';

export interface GetEventsResponse extends ListResponse<Event> {
}
