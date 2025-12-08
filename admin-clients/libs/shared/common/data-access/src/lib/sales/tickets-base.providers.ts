import { Provider } from '@angular/core';
import { TicketsBaseApi } from './api/tickets-base.api';
import { TicketsBaseState } from './state/tickets-base.state';
import { TicketsBaseService } from './tickets-base.service';

export const ticketsBaseProviders: Provider[] = [
    TicketsBaseApi,
    TicketsBaseState,
    TicketsBaseService
];
