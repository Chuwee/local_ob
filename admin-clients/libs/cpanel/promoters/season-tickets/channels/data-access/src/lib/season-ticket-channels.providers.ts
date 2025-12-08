import { SeasonTicketChannelsApi } from './api/season-ticket-channels.api';
import { SeasonTicketChannelsService } from './season-ticket-channels.service';
import { SeasonTicketChannelsState } from './state/season-ticket-channels.state';

export const seasonTicketChannelsProviders = [
    SeasonTicketChannelsApi,
    SeasonTicketChannelsState,
    SeasonTicketChannelsService
];