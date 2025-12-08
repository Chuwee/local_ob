import { Metadata } from '@OneboxTM/utils-state';
import {
    SeasonTicketExternalRenewalCandidateSearch, SeasonTicketRenewalsService
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { pageSize, SearchablePaginatedSelectionLoadEvent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-renewal-external-candidate',
    templateUrl: './season-ticket-renewal-external-candidate.component.html',
    styleUrls: ['./season-ticket-renewal-external-candidate.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketRenewalExternalCandidateComponent implements OnInit {
    private _renewalCandidates$: Observable<SeasonTicketExternalRenewalCandidateSearch[]>;
    private _renewalCandidatesPaged: BehaviorSubject<SeasonTicketExternalRenewalCandidateSearch[]> = new BehaviorSubject(null);
    private _renewalCandidatesMetadataPaged: BehaviorSubject<Metadata> = new BehaviorSubject(null);

    isLoading$: Observable<boolean>;
    renewalCandidatesPaged$: Observable<SeasonTicketExternalRenewalCandidateSearch[]>;
    renewalCandidatesMetadata$: Observable<Metadata>;

    @Input() candidateFormGroup: UntypedFormGroup;
    @Input() externalCandidateControlName: string;

    get externalCandidateControl(): UntypedFormControl {
        return this.candidateFormGroup.get(this.externalCandidateControlName) as UntypedFormControl;
    }

    constructor(
        private _seasonTicketRenewalsSrv: SeasonTicketRenewalsService,
        private _translate: TranslateService
    ) {
    }

    ngOnInit(): void {
        this.isLoading$ = this._seasonTicketRenewalsSrv.externalRenewalCandidatesList.inProgress$();
        this.renewalCandidatesPaged$ = this._renewalCandidatesPaged.asObservable();
        this.renewalCandidatesMetadata$ = this._renewalCandidatesMetadataPaged.asObservable();

        this._renewalCandidates$ = this._seasonTicketRenewalsSrv.externalRenewalCandidatesList.getData$()
            .pipe(first(renewalCandidates => !!renewalCandidates));
    }

    loadPagedRenewalCandidates({ offset, q }: SearchablePaginatedSelectionLoadEvent): void {
        this._renewalCandidates$.subscribe(renewalCandidates => {
            if (q) {
                renewalCandidates = renewalCandidates.filter(renewalCandidate =>
                    renewalCandidate.event_name.toLowerCase().includes(q.toLowerCase()));
            }
            this._renewalCandidatesPaged.next(renewalCandidates.slice(offset, offset + pageSize));
            this._renewalCandidatesMetadataPaged.next(new Metadata({ total: renewalCandidates.length, offset, limit: pageSize }));

        });
    }
}
