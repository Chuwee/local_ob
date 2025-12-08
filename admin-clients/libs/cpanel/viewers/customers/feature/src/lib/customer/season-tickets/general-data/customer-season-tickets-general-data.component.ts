import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { TicketsBaseService } from '@admin-clients/shared/common/data-access';
import { CopyTextComponent, DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatSpinner } from '@angular/material/progress-spinner';
import { Router, RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { filter } from 'rxjs';

@Component({
    imports: [MatButtonModule, TranslatePipe, MatExpansionModule, MatIcon, RouterLink,
        DateTimePipe, LocalCurrencyPipe, CopyTextComponent, MatSpinner, FormContainerComponent
    ],
    selector: 'app-customer-season-tickets-general-data',
    templateUrl: './customer-season-tickets-general-data.component.html',
    styleUrls: ['./customer-season-tickets-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerSeasonTicketsGeneralDataComponent {

    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #ticketsSrv = inject(TicketsBaseService);
    readonly #router = inject(Router);
    readonly #translateService = inject(TranslateService);
    readonly #messageDialogService = inject(MessageDialogService);

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(filter(Boolean)));
    readonly $seasonTicketStatus = toSignal(this.#seasonTicketSrv.seasonTicketStatus.get$().pipe(filter(Boolean)));
    readonly $orderDetail = toSignal(this.#ticketsSrv.ticketDetail.get$().pipe(filter(Boolean)));
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#ticketsSrv.ticketDetail.loading$(),
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$()
    ]));

    readonly dateTimeFormats = DateTimeFormats;

    showTicket(): void {
        this.#ticketsSrv.ticketDetail.link.get$()
            .subscribe(link => {
                if (link) {
                    window.open(link, '_blank');
                    //We have to wait our backend register the new 'download' action
                    setTimeout(() => this.#ticketsSrv.ticketDetail.load(this.$orderDetail()?.order.code, this.$orderDetail()?.id.toString()), 500);
                } else {
                    const title = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.TITLE');
                    const message = this.#translateService.instant('ACTIONS.SEE_TICKETS.KO.MESSAGE');
                    this.#messageDialogService.showInfo({ size: DialogSize.MEDIUM, title, message });
                }
            });
    }

}
