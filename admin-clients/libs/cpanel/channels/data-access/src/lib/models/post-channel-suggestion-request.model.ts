import { Id } from '@admin-clients/shared/data-access/models';
import { ChannelSuggestionType } from './channel-suggestion-type';

export interface PostChannelSuggestionReq extends Id {
    type: ChannelSuggestionType;
}
