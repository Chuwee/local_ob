import { TicketsPassbookService } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { first, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-ticket-passbook-download',
    templateUrl: './ticket-passbook-download.component.html',
    styleUrls: ['./ticket-passbook-download.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatButton, MatIcon,
        TranslatePipe
    ]
})
export class TicketPassbookDownloadComponent {

    @Input() language: string;
    @Input() svgName = 'passbook-template-detail.svg';

    constructor(
        private _ticketsPassbookService: TicketsPassbookService
    ) { }

    downloadPassbook(): void {
        this._ticketsPassbookService.getTicketPassbook$()
            .pipe(
                first(ticketPassbook => !!ticketPassbook),
                switchMap(ticketPassbook => this._ticketsPassbookService
                    .getDownloadUrlTicketPassbookPreview$(ticketPassbook.code, ticketPassbook.entity_id.toString(), this.language))
            ).subscribe((res: { download_url: string }) => window.open(res.download_url, '_blank'));
    }
}
