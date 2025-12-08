import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SecondaryMarketConfig } from './models/secondary-market.model';

export class SecondaryMarketApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS = `${this.BASE_API}/mgmt-api/v1/events/`;
    private readonly SESSIONS = `${this.BASE_API}/mgmt-api/v1/sessions/`;
    private readonly SEASON_TICKETS = `${this.BASE_API}/mgmt-api/v1/season-tickets/`;

    readonly #http = inject(HttpClient);

    getEventSecondaryMarket(eventId: number): Observable<SecondaryMarketConfig> {
        return this.#http.get<SecondaryMarketConfig>(this.EVENTS + `${eventId}/secondary-market`);
    }

    postEventSecondaryMarket(eventId: number, config: SecondaryMarketConfig): Observable<void> {
        return this.#http.post<void>(this.EVENTS + `${eventId}/secondary-market`, config);
    }

    getSeasonTicketSecondaryMarket(seasonTicketId: number): Observable<SecondaryMarketConfig> {
        return this.#http.get<SecondaryMarketConfig>(this.SEASON_TICKETS + `${seasonTicketId}/secondary-market`);
    }

    postSeasonTicketSecondaryMarket(seasonTicketId: number, config: SecondaryMarketConfig): Observable<void> {
        return this.#http.post<void>(this.SEASON_TICKETS + `${seasonTicketId}/secondary-market`, config);
    }

    getSessionSecondaryMarket(sessionId: number): Observable<SecondaryMarketConfig> {
        return this.#http.get<SecondaryMarketConfig>(this.SESSIONS + `${sessionId}/secondary-market`);
    }

    postSessionSecondaryMarket(sessionId: number, config: SecondaryMarketConfig): Observable<void> {
        return this.#http.post<void>(this.SESSIONS + `${sessionId}/secondary-market`, config);
    }

    deleteSessionSecondaryMarket(sessionId: number): Observable<void> {
        return this.#http.delete<void>(this.SESSIONS + `${sessionId}/secondary-market`);
    }
}
