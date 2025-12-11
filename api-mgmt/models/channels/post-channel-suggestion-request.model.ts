import { Id } from './common-types';
import { ChannelSuggestionType } from './channel-suggestion-type';

export interface PostChannelSuggestionReq extends Id {
    type: ChannelSuggestionType;
}
