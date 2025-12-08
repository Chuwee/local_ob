import { SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { ChannelB2bAssignations, ProfessionalSellingService } from '@admin-clients/cpanel/promoters/shared/data-access';
import { Id } from '@admin-clients/shared/data-access/models';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

@Injectable()
export class SeasonTicketProfessionalSellingService implements ProfessionalSellingService {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketChannelSrv = inject(SeasonTicketChannelsService);

    readonly context = 'SEASON_TICKET';

    readonly channel = {
        get$: () => this.#seasonTicketChannelSrv.getSeasonTicketChannel$(),
        inProgress$: () => this.#seasonTicketChannelSrv.isSeasonTicketChannelInProgress$(),
        clear: () => this.#seasonTicketChannelSrv.clearSeasonTicketChannel()
    };

    getEntityId(): Observable<number> {
        return this.#seasonTicketSrv.seasonTicket.get$().pipe(
            map(st => st?.entity.id)
        );
    }

    loadB2bAssignations(seasonTicketId: number, channelId: number): void {
        this.#seasonTicketChannelSrv.b2bAssignations.load(seasonTicketId, channelId);
    }

    getB2bAssignations$(): Observable<ChannelB2bAssignations> {
        return this.#seasonTicketChannelSrv.b2bAssignations.get$();
    }

    updateB2bAssignations(eventId: number, channelId: number, assignations: ChannelB2bAssignations<Id>): Observable<void> {
        return this.#seasonTicketChannelSrv.b2bAssignations.update(eventId, channelId, assignations);
    }

    clearB2bAssignations(): void {
        this.#seasonTicketChannelSrv.b2bAssignations.clear();
    }

    isB2bAssignationsInProgress$(): Observable<boolean> {
        return this.#seasonTicketChannelSrv.b2bAssignations.loading$();
    }
}