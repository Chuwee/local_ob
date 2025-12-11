import { ListResponse } from '@OneboxTM/utils-state';
import { ChannelEvent } from './channel-event.model';

export interface GetChannelEventsResponse extends ListResponse<ChannelEvent> {
}
