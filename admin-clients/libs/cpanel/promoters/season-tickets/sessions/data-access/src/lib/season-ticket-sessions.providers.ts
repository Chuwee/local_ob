import { Provider } from '@angular/core';
import { SeasonTicketSessionsApi } from './api/season-ticket-sessions.api';
import { SeasonTicketSessionsService } from './season-ticket-sessions.service';
import { SeasonTicketSessionsState } from './state/season-ticket-sessions.state';

export const seasonTicketSessionsProviders: Provider[] = [
    SeasonTicketSessionsApi, SeasonTicketSessionsState, SeasonTicketSessionsService
];