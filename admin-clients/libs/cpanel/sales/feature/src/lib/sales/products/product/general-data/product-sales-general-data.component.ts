import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { OrdersService, TicketsService } from '@admin-clients/cpanel-sales-data-access';
import {
    ActionsHistoryType, TicketDetail
} from '@admin-clients/shared/common/data-access';
import { MessageDialogService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe, LocalDateTimePipe, ObfuscateStringPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatFormField, MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import {
    TicketActionsHistoryComponent
} from '../../../tickets/ticket/general-data/ticket-actions-history/ticket-actions-history.component';
import { TicketValidationComponent } from '../../../tickets/ticket/general-data/ticket-validation/ticket-validation.component';
import { ProductSalesPriceComponent } from './product-sales-ticket-price/product-sales-price.component';

@Component({
    selector: 'app-product-sales-general-data',
    templateUrl: './product-sales-general-data.component.html',
    styleUrls: ['./product-sales-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, DateTimePipe, LocalDateTimePipe, ObfuscateStringPipe,
        FormContainerComponent, ProductSalesPriceComponent, TicketValidationComponent, MatButton, MatIcon, MatTooltip, MatFormField,
        MatSelect, MatOption, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatDivider, MatProgressSpinner,
        MatExpansionPanelTitle, TicketActionsHistoryComponent
    ]
})
export class ProductSalesGeneralDataComponent implements OnInit, OnDestroy {
    readonly #route = inject(ActivatedRoute);
    readonly #ticketsService = inject(TicketsService);
    readonly #ordersService = inject(OrdersService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #onDestroy = new Subject<void>();

    readonly dateTimeFormats = DateTimeFormats;
    readonly actionsHistoryType = ActionsHistoryType;
    readonly actionHistory$ = this.#ticketsService.ticketDetail.get$()
        .pipe(
            filter(Boolean),
            map(ticketDetail => ticketDetail.actions_history)
        );

    readonly isOperatorUser$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

    previousPath: string;
    loading$ = booleanOrMerge([
        this.#ticketsService.ticketDetail.loading$(),
        this.#ticketsService.ticketDetail.link.loading$(),
        this.#ticketsService.ticketDetail.stateHistory.loading$()
    ]);

    ticketDetail$ = this.#ticketsService.ticketDetail.get$();
    stateHistory$ = this.#ticketsService.ticketDetail.stateHistory.getData$();
    isSkipGeneration$: Observable<boolean>;

    ngOnInit(): void {
        if (this.#route.snapshot.params?.['orderCode']) {
            this.previousPath = `/transactions/${this.#route.snapshot.params['orderCode']}`;
        } else {
            this.previousPath = '/products-sales';
        }

        this.isSkipGeneration$ = this.#ticketsService.ticketDetail.get$()
            .pipe(
                filter(Boolean),
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
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    showTicket(orderCode: string, ticketId: number): void {
        this.#ticketsService.ticketDetail.link.get$()
            .subscribe(link => {
                if (link) {
                    window.open(link, '_blank');
                    //We have to wait our backend register the new 'download' action
                    setTimeout(() => this.#ticketsService.ticketDetail.load(orderCode, ticketId.toString()), 500);
                } else {
                    this.#messageDialogService.showInfo({
                        size: DialogSize.MEDIUM,
                        title: 'ACTIONS.SEE_TICKETS.KO.TITLE',
                        message: 'ACTIONS.SEE_TICKETS.KO.MESSAGE'
                    });
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
                    this.#messageDialogService.showInfo({
                        size: DialogSize.MEDIUM,
                        title: 'ACTIONS.SEE_TICKETS.KO.TITLE',
                        message: 'ACTIONS.SEE_TICKETS.KO.MESSAGE'
                    });
                }
            });
    }
}
