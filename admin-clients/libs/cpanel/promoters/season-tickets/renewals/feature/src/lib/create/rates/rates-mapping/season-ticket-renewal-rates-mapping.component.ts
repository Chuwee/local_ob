import { SeasonTicketRate } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketRenewalRate, SeasonTicketRenewalRateMapping
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-season-ticket-renewal-rates-mapping',
    templateUrl: './season-ticket-renewal-rates-mapping.component.html',
    styleUrls: ['./season-ticket-renewal-rates-mapping.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketRenewalRatesMappingComponent {
    displayedColumns = ['old', 'new'];

    @Input() seasonTicketRates$: Observable<SeasonTicketRate[]>;
    @Input() renewalRates$: Observable<SeasonTicketRenewalRate[]>;
    @Input() ratesFormGroup: UntypedFormGroup;
    @Input() ratesControlName: string;

    constructor() {
    }

    compareRateById(
        option1: SeasonTicketRenewalRateMapping,
        option2: SeasonTicketRenewalRateMapping
    ): boolean {
        return option1?.new_rate_id === option2?.new_rate_id;
    }

}
