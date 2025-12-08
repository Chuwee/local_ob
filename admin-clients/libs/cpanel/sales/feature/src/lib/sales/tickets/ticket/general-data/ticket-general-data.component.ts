import { DetailOverlayService } from '@OneboxTM/detail-overlay';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { OrdersService, TicketsService } from '@admin-clients/cpanel-sales-data-access';
import {
    ActionsHistory, ActionsHistoryType, EventType, StateHistory, TicketDetail
} from '@admin-clients/shared/common/data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ObfuscatePattern, DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { BuyerDataComponent } from './buyer-data/buyer-data.component';
import { OrderDataComponent } from './order-data/order-data.component';
import { SecondaryMarketSoldSessionsComponent } from './secondary-market-sold-sessions/secondary-market-sold-sessions.component';
import { TicketActionsHistoryComponent } from './ticket-actions-history/ticket-actions-history.component';
import { TicketAttendeeHistoryComponent } from './ticket-attendee-history/ticket-attendee-history.component';
import { TicketDataComponent } from './ticket-data/ticket-data.component';
import { TicketPriceComponent } from './ticket-price/ticket-price.component';
import { TicketValidationComponent } from './ticket-validation/ticket-validation.component';

@Component({
    selector: 'app-ticket-general-data',
    templateUrl: './ticket-general-data.component.html',
    styleUrls: ['./ticket-general-data.component.scss'],
    providers: [DatePipe, DetailOverlayService],
    imports: [
        CommonModule,
        MatIcon, MatFormField, MatSelect, MatOption,
        MatButton, MatTooltip, MatExpansionModule, MatDivider, MatProgressSpinner,
        ReactiveFormsModule, FlexLayoutModule, TranslatePipe,
        DateTimePipe, FormContainerComponent, SecondaryMarketSoldSessionsComponent,
        TicketAttendeeHistoryComponent, TicketValidationComponent, TicketPriceComponent, TicketActionsHistoryComponent,
        BuyerDataComponent, OrderDataComponent, TicketDataComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketDetailsGeneralDataComponent implements OnInit, OnDestroy {
    readonly #route = inject(ActivatedRoute);
    readonly #ticketsService = inject(TicketsService);
    readonly #ordersService = inject(OrdersService);
    readonly #translateService = inject(TranslateService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #authSrv = inject(AuthenticationService);

    readonly eventType = EventType;
    readonly dateTimeFormats = DateTimeFormats;
    readonly actionsHistoryType = ActionsHistoryType;

    previousPath: string;
    loading$: Observable<boolean>;

    obfuscatePattern = ObfuscatePattern;
    ticketDetail$: Observable<TicketDetail> = this.#ticketsService.ticketDetail.get$();

    stateHistory$: Observable<StateHistory[]>;
    actionHistoryPrints$: Observable<ActionsHistory[]>;
    actionHistorySent$: Observable<ActionsHistory[]>;
    actionHistory$: Observable<ActionsHistory[]>;
    secondaryMarketSessionsSold$: Observable<TicketDetail['subitems']>;
    isOperatorUser$: Observable<boolean>;
    isSkipGeneration$: Observable<boolean>;

    ngOnInit(): void {
        this.loading$ = booleanOrMerge([
            this.#ticketsService.ticketDetail.loading$(),
            this.#ticketsService.ticketDetail.link.loading$(),
            this.#ticketsService.ticketDetail.stateHistory.loading$()
        ]);
        this.stateHistory$ = this.#ticketsService.ticketDetail.stateHistory.getData$();
        this.actionHistoryPrints$ = this.#ticketsService.ticketDetail.get$()
            .pipe(
                filter(ticketDetail => !!ticketDetail),
                map(ticketDetail => ticketDetail.actions_history?.filter(action =>
                    [ActionsHistoryType.download, ActionsHistoryType.print].includes(action.type)))
            );
        this.actionHistorySent$ = this.#ticketsService.ticketDetail.get$()
            .pipe(
                filter(ticketDetail => !!ticketDetail),
                map(ticketDetail => ticketDetail.actions_history?.filter(action => action.type === ActionsHistoryType.sent))
            );
        this.actionHistory$ = this.#ticketsService.ticketDetail.get$()
            .pipe(
                filter(ticketDetail => !!ticketDetail),
                map(ticketDetail => ticketDetail.actions_history)
            );

        this.secondaryMarketSessionsSold$ = this.#ticketsService.ticketDetail.get$()
            .pipe(
                filter(ticketDetail => !!ticketDetail),
                map(ticketDetail => ticketDetail.subitems?.filter(subitem => !!subitem.secondary_market))
            );

        if (this.#route.snapshot.parent?.params?.['orderCode']) {
            this.previousPath = `/transactions/${this.#route.snapshot.params['orderCode']}`;
        } else {
            this.previousPath = '/tickets';
        }

        this.isOperatorUser$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

        this.isSkipGeneration$ = this.#ticketsService.ticketDetail.get$()
            .pipe(
                filter(ticketDetail => !!ticketDetail),
                map(ticketDetail =>
                    ticketDetail.actions_history?.sort(
                        (a1, a2) => {
                            const date1 = Date.parse(a1.date);
                            const date2 = Date.parse(a2.date);
                            if (date1 < date2 || (date1 === date2 && a2.type === ActionsHistoryType.sent)) {
                                return 1;
                            } else if (date1 > date2 || (date1 === date2 && a1.type === ActionsHistoryType.sent)) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    )[0].type === ActionsHistoryType.skipGeneration
                )
            );
    }

    ngOnDestroy(): void {
        this.#ticketsService.ticketDetail.clear();
        this.#ticketsService.ticketDetail.stateHistory.clear();
    }

    showTicket(orderCode: string, ticketId: number): void {
        this.#ticketsService.ticketDetail.link.get$()
            .subscribe(link => {
                if (link) {
                    window.open(link, '_blank');
                    //We have to wait our backend register the new 'download' action
                    setTimeout(() => this.#ticketsService.ticketDetail.load(orderCode, ticketId.toString()), 500);
                } else {
                    const title = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.TITLE');
                    const message = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.MESSAGE');
                    this.#messageDialogService.showInfo({ size: DialogSize.MEDIUM, title, message });
                }
            });
    }

    showTickets(ticketDetail: TicketDetail): void {
        this.#ordersService.getTicketsLink$(ticketDetail.order.code)
            .subscribe(link => {
                if (link) {
                    window.open(link, '_blank');
                    //We have to wait our backend to register the new 'download' action
                    setTimeout(() => this.#ticketsService.ticketDetail.load(ticketDetail.order.code, ticketDetail.id.toString()), 500);
                } else {
                    const title = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.TITLE');
                    const message = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.MESSAGE');
                    this.#messageDialogService.showInfo({ size: DialogSize.MEDIUM, title, message });
                }
            });
    }
}
