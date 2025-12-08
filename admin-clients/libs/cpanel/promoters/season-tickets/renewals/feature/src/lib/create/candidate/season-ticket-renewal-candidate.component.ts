import { RenewalCandidateTypeEnum, SeasonTicketRenewalsService } from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output, inject } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-renewal-candidate',
    templateUrl: './season-ticket-renewal-candidate.component.html',
    styleUrls: ['./season-ticket-renewal-candidate.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketRenewalCandidateComponent implements OnInit, OnDestroy {
    private readonly _seasonTicketRenewalsSrv = inject(SeasonTicketRenewalsService);
    private readonly _onDestroy = new Subject<void>();
    private readonly _candidateTypeBS = new BehaviorSubject<RenewalCandidateTypeEnum>(RenewalCandidateTypeEnum.none);
    renewalCandidateTypeEnum = RenewalCandidateTypeEnum;
    candidateType$: Observable<RenewalCandidateTypeEnum>;

    @Input() candidateFormGroup: UntypedFormGroup;
    @Input() candidateTypeControlName: string;
    @Input() internalCandidateControlName: string;
    @Input() externalCandidateControlName: string;
    @Input() hasPreviousRenewals: boolean;

    @Output() isLoading = new EventEmitter<boolean>();

    get typeControl(): UntypedFormControl {
        return this.candidateFormGroup.get(this.candidateTypeControlName) as UntypedFormControl;
    }

    ngOnInit(): void {
        this.setLoading();
        this.setCandidateType();
        this.candidateTypeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private setLoading(): void {
        const isLoading$ = booleanOrMerge([
            this._seasonTicketRenewalsSrv.renewalCandidatesList.inProgress$(),
            this._seasonTicketRenewalsSrv.externalRenewalCandidatesList.inProgress$()
        ]);

        isLoading$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isLoading => this.isLoading.emit(isLoading));
    }

    private setCandidateType(): void {
        this.candidateType$ = this._candidateTypeBS.asObservable();
        this._candidateTypeBS.next(this.typeControl.value);
    }

    private candidateTypeHandler(): void {
        this.typeControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((type: RenewalCandidateTypeEnum) => {
                this._candidateTypeBS.next(type);
            });
    }
}
