import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { RenewalCandidateTypeEnum, SeasonTicketRenewalsService } from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { Observable, of, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-renewal-rates',
    templateUrl: './season-ticket-renewal-rates.component.html',
    styleUrls: ['./season-ticket-renewal-rates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketRenewalRatesComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    renewalCandidateTypeEnum = RenewalCandidateTypeEnum;
    candidateType$: Observable<RenewalCandidateTypeEnum>;

    @Input() candidateFormGroup: UntypedFormGroup;
    @Input() candidateTypeControlName: string;
    @Input() ratesFormGroup: UntypedFormGroup;
    @Input() internalRatesControlName: string;
    @Input() externalRatesControlName: string;

    @Output() isLoading = new EventEmitter<boolean>();

    get typeControl(): UntypedFormControl {
        return this.candidateFormGroup.get(this.candidateTypeControlName) as UntypedFormControl;
    }

    constructor(
        private _seasonTicketSrv: SeasonTicketsService,
        private _seasonTicketRenewalsSrv: SeasonTicketRenewalsService
    ) {
    }

    ngOnInit(): void {
        this.setLoading();
        this.candidateType$ = of(this.typeControl.value);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private setLoading(): void {
        const isLoading$ = booleanOrMerge([
            this._seasonTicketRenewalsSrv.renewalRates.inProgress$(),
            this._seasonTicketRenewalsSrv.externalRenewalRates.inProgress$(),
            this._seasonTicketSrv.isSeasonTicketRatesInProgress$()
        ]);

        isLoading$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isLoading => this.isLoading.emit(isLoading));
    }

}
