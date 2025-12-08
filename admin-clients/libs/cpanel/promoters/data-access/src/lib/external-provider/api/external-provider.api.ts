import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ExternalProviderEvents, ExternalProviderEventsQuery } from '../models/external-provider-events.model';
import {
    ExternalProviderPresales, ExternalProviderSessionsPresalesQuery
} from '../models/external-provider-presales.model';
import { ExternalProviderSessions, ExternalProviderSessionsQuery } from '../models/external-provider-sessions.model';

@Injectable({
    providedIn: 'root'
})
export class PromotersExternalProviderApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EXT_EVENTS_API = `${this.BASE_API}/mgmt-api/v1/external-provider-events`;
    private readonly EXT_SESSIONS_API = `${this.BASE_API}/mgmt-api/v1/external-provider-sessions`;
    private readonly EXT_PRESALES_API = `${this.BASE_API}/mgmt-api/v1/external-provider-presales`;
    private readonly _http = inject(HttpClient);

    getExternalProviderEvents(req: ExternalProviderEventsQuery): Observable<ExternalProviderEvents[]> {
        const params = buildHttpParams(req);
        return this._http.get<ExternalProviderEvents[]>(`${this.EXT_EVENTS_API}`, { params });
    }

    getExternalProviderSessions(req: ExternalProviderSessionsQuery): Observable<ExternalProviderSessions[]> {
        const params = buildHttpParams(req);
        return this._http.get<ExternalProviderSessions[]>(`${this.EXT_SESSIONS_API}`, { params });
    }

    getExternalProviderSessionsPresales(req: ExternalProviderSessionsPresalesQuery): Observable<ExternalProviderPresales[]> {
        return this._http.get<ExternalProviderPresales[]>(
            `${this.EXT_PRESALES_API}/events/${req.event_id}/sessions/${req.session_id}/external-presales?skip_used=${req.skip_used}`
        );
    }

    getExternalProviderSeasonTicketsPresales(seasonTicketId: number, skipUsed?: boolean): Observable<ExternalProviderPresales[]> {
        return this._http.get<ExternalProviderPresales[]>(
            `${this.EXT_PRESALES_API}/season-tickets/${seasonTicketId}/external-presales?skip_used=${skipUsed}`
        );
    }
}
