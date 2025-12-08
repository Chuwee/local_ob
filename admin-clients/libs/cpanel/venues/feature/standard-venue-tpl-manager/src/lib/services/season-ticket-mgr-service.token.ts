import { IdName, SeasonTicketLinkNNZResponse, SeasonTicketUnlinkNNZResponse } from '@admin-clients/shared/data-access/models';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export interface SeasonTicketMgrService {

    getSeasonTicket$(): Observable<IdName>;

    putNnzLinkable(seasonTicketId: number, id: number, count: number): Observable<SeasonTicketLinkNNZResponse>;

    putNnzNotLinkable(seasonTicketId: number, id: number, count: number): Observable<SeasonTicketUnlinkNNZResponse>;
}

export const SEASON_TICKET_SERVICE = new InjectionToken<SeasonTicketMgrService>('SEASON_TICKET_SERVICE');
