import { SeasonTicketRate, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { SeasonTicketRenewalRate, SeasonTicketRenewalsService } from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-renewal-external-rates',
    templateUrl: './season-ticket-renewal-external-rates.component.html',
    styleUrls: ['./season-ticket-renewal-external-rates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketRenewalExternalRatesComponent implements OnInit {
    seasonTicketRates$: Observable<SeasonTicketRate[]>;
    renewalRates$: Observable<SeasonTicketRenewalRate[]>;

    @Input() ratesFormGroup: UntypedFormGroup;
    @Input() externalRatesControlName: string;

    constructor(
        private _seasonTicketSrv: SeasonTicketsService,
        private _seasonTicketRenewalSrv: SeasonTicketRenewalsService
    ) {
    }

    ngOnInit(): void {
        this.seasonTicketRates$ = this._seasonTicketSrv.getSeasonTicketRates$()
            .pipe(first(value => !!value));

        this.renewalRates$ = this._seasonTicketRenewalSrv.externalRenewalRates.get$();
    }
}
