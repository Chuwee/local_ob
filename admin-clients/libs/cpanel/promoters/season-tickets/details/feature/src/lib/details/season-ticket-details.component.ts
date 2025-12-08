import { IsB2bEntityPipe } from '@admin-clients/cpanel/organizations/entities/utils';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    DateTimeModule, EphemeralMessageService, GoBackComponent, MessageType, NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    Topic, WebsocketsService, WsEventMsgType, WsMsg, WsMsgStatus, WsSeasonTicketMsgType, WsSessionMsg
} from '@admin-clients/shared/core/data-access';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';
import { distinctUntilChanged, filter, map, shareReplay } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-details',
    templateUrl: './season-ticket-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        DragDropModule, DateTimeModule, NavTabsMenuComponent, CommonModule, FlexLayoutModule,
        MaterialModule, TranslatePipe, ReactiveFormsModule, GoBackComponent, RouterModule,
        IsB2bEntityPipe
    ],
    providers: [PrefixPipe.provider('SEASON_TICKET.')]
})
export class SeasonTicketDetailsComponent implements OnInit, OnDestroy {
    readonly #seasonTicketsSrv = inject(SeasonTicketsService);
    readonly #router = inject(Router);
    readonly #ws = inject(WebsocketsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesBaseService);

    readonly entity$ = this.#entitiesSrv.getEntity$();
    readonly seasonTicket$ = this.#seasonTicketsSrv.seasonTicket.get$();
    readonly isGenerationStatusInProgress$ =
        this.#seasonTicketsSrv.seasonTicketStatus
            .isGenerationStatusInProgress$()
            .pipe(distinctUntilChanged(), shareReplay(1));

    readonly isGenerationStatusError$ =
        this.#seasonTicketsSrv.seasonTicketStatus
            .isGenerationStatusError$()
            .pipe(distinctUntilChanged(), shareReplay(1));

    async ngOnInit(): Promise<void> {
        const seasonTicket = await firstValueFrom(
            this.#seasonTicketsSrv.seasonTicket.get$()
        );
        const wsSessionMsgs$ = this.#ws
            .getMessages$<WsSessionMsg>(Topic.event, seasonTicket.id)
            .pipe(filter(msg => msg?.type === WsEventMsgType.session));
        this.#seasonTicketsSrv.setSeasonTicketUpdatingCapacityUpdater(
            wsSessionMsgs$,
            this.#destroyRef
        );
        this.#ws
            .getMessages$<WsMsg<WsSeasonTicketMsgType>>(
                Topic.seasonTicket,
                seasonTicket.id
            )
            .pipe(
                map(msg => ({ msg, seasonTicket })),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(({ msg, seasonTicket }) => {
                if (
                    msg?.type === WsSeasonTicketMsgType.creation &&
                    String(msg?.id) === String(seasonTicket.id)
                ) {
                    this.#seasonTicketsSrv.seasonTicket.load(
                        seasonTicket.id.toString()
                    );
                    this.#seasonTicketsSrv.seasonTicketStatus.load(
                        seasonTicket.id.toString()
                    );
                    // show for snack bar with result
                    if (msg.status === WsMsgStatus.done) {
                        this.#ephemeralMessageSrv.show({
                            type: MessageType.success,
                            msgKey: 'SEASON_TICKET.CREATION_SUCCESS'
                        });
                    } else if (msg.status === WsMsgStatus.error) {
                        this.#ephemeralMessageSrv.show({
                            type: MessageType.warn,
                            msgKey: 'SEASON_TICKET.CREATION_ERROR'
                        });
                        if (
                            !this.#router.url.includes('general-data') &&
                            !this.#router.url.includes('communication')
                        ) {
                            this.#router.navigate([
                                `/season-tickets/${seasonTicket.id}/general-data`
                            ]);
                        }
                    }
                }
            });
    }

    async ngOnDestroy(): Promise<void> {
        const seasonTicket = await firstValueFrom(this.seasonTicket$);
        this.#ws.unsubscribeMessages(Topic.seasonTicket, seasonTicket.id);
        this.#ws.unsubscribeMessages(Topic.event, seasonTicket.id);
        this.#seasonTicketsSrv.seasonTicketList.clear();
        this.#seasonTicketsSrv.seasonTicketStatus.clear();
    }
}
