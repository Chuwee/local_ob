import { SeasonTicketRate, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketRenewalRateMapping, SeasonTicketRenewalsService
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-renewal-internal-rates',
    templateUrl: './season-ticket-renewal-internal-rates.component.html',
    styleUrls: ['./season-ticket-renewal-internal-rates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketRenewalInternalRatesComponent implements OnInit {
    seasonTicketRates$: Observable<SeasonTicketRate[]>;
    renewalRates$: Observable<SeasonTicketRate[]>;

    @Input() ratesFormGroup: UntypedFormGroup;
    @Input() internalRatesControlName: string;

    constructor(
        private _seasonTicketSrv: SeasonTicketsService,
        private _seasonTicketRenewalSrv: SeasonTicketRenewalsService
    ) {
    }

    ngOnInit(): void {
        this.seasonTicketRates$ = this._seasonTicketSrv.getSeasonTicketRates$()
            .pipe(first(value => !!value));

        this.renewalRates$ = this._seasonTicketRenewalSrv.renewalRates.get$();
    }

    compareRateById(
        option1: SeasonTicketRenewalRateMapping,
        option2: SeasonTicketRenewalRateMapping
    ): boolean {
        return option1?.new_rate_id === option2?.new_rate_id;
    }

}
