import { Provider } from '@angular/core';
import { SeasonTicketRenewalsApi } from './api/season-ticket-renewals.api';
import { SeasonTicketRenewalsService } from './season-ticket-renewals.service';
import { SeasonTicketRenewalsState } from './state/season-ticket-renewals.state';

export const seasonTicketRenewalsProviders: Provider[] = [
    SeasonTicketRenewalsApi, SeasonTicketRenewalsState, SeasonTicketRenewalsService
];
