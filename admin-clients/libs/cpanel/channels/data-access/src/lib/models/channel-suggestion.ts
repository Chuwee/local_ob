import { ChannelSuggestionType } from './channel-suggestion-type';

export interface ChannelSuggestion {
    id: number;
    type: ChannelSuggestionType;
    name: string;
    parent_name?: string;
    start_date?: string; // Example: 2021-01-01T08:00:00Z
    currency?: string;
}
