import { EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ChannelB2bAssignations, ProfessionalSellingService } from '@admin-clients/cpanel/promoters/shared/data-access';
import { Id } from '@admin-clients/shared/data-access/models';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

@Injectable()
export class EventsProfessionalSellingService implements ProfessionalSellingService {

    readonly #eventsSrv = inject(EventsService);
    readonly #eventChannelSrv = inject(EventChannelsService);

    readonly context = 'EVENT';

    readonly channel = {
        get$: () => this.#eventChannelSrv.eventChannel.get$(),
        inProgress$: () => this.#eventChannelSrv.eventChannel.inProgress$(),
        clear: () => this.#eventChannelSrv.eventChannel.clear()
    };

    getEntityId(): Observable<number> {
        return this.#eventsSrv.event.get$().pipe(map(event => event?.entity?.id));
    }

    loadB2bAssignations(eventId: number, channelId: number): void {
        this.#eventChannelSrv.loadB2bAssignations(eventId, channelId);
    }

    getB2bAssignations$(): Observable<ChannelB2bAssignations> {
        return this.#eventChannelSrv.getB2bAssignations$();
    }

    updateB2bAssignations(eventId: number, channelId: number, assignations: ChannelB2bAssignations<Id>): Observable<void> {
        return this.#eventChannelSrv.updateB2bAssignations(eventId, channelId, assignations);
    }

    clearB2bAssignations(): void {
        this.#eventChannelSrv.clearB2bAssignations();
    }

    isB2bAssignationsInProgress$(): Observable<boolean> {
        return this.#eventChannelSrv.isB2bAssignationsInProgress$();
    }

}