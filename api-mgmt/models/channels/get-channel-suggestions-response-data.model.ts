import { ChannelSuggestion } from './channel-suggestion';

export interface GetChannelSuggestionsDataResponse {
    source: ChannelSuggestion;
    targets: ChannelSuggestion[];
}
