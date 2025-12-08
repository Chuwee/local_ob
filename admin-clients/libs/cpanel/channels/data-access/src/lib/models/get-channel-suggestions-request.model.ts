import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { ChannelSuggestionType } from './channel-suggestion-type';

export interface GetChannelSuggestionsRequest extends PageableFilter {
    offset?: number;
    limit?: number;
    q?: string;
    published?: boolean;
    source_type?: ChannelSuggestionType;
    session_id?: number[];
    event_id?: number[];
}
