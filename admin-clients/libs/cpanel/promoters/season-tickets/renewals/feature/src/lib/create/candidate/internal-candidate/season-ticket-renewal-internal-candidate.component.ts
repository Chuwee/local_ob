import { Metadata } from '@OneboxTM/utils-state';
import { SeasonTicketRenewalCandidateSearch, SeasonTicketRenewalsService } from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { pageSize, SearchablePaginatedSelectionLoadEvent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-renewal-internal-candidate',
    templateUrl: './season-ticket-renewal-internal-candidate.component.html',
    styleUrls: ['./season-ticket-renewal-internal-candidate.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketRenewalInternalCandidateComponent {
    private readonly _seasonTicketRenewalsSrv = inject(SeasonTicketRenewalsService);
    private readonly _translate = inject(TranslateService);

    private readonly _renewalCandidates$ = this._seasonTicketRenewalsSrv.renewalCandidatesList.getData$()
        .pipe(first(Boolean));

    private readonly _renewalCandidatesPaged = new BehaviorSubject<SeasonTicketRenewalCandidateSearch[]>(null);
    private readonly _renewalCandidatesMetadataPaged = new BehaviorSubject<Metadata>(null);

    readonly isLoading$ = this._seasonTicketRenewalsSrv.renewalCandidatesList.inProgress$();
    readonly renewalCandidatesPaged$ = this._renewalCandidatesPaged.asObservable();
    readonly renewalCandidatesMetadata$ = this._renewalCandidatesMetadataPaged.asObservable();

    @Input() candidateFormGroup: UntypedFormGroup;
    @Input() internalCandidateControlName: string;

    get internalCandidateControl(): UntypedFormControl {
        return this.candidateFormGroup.get(this.internalCandidateControlName) as UntypedFormControl;
    }

    getReasonsLiteral(renewalCandidate: SeasonTicketRenewalCandidateSearch): string {
        return !renewalCandidate.compatible &&
            renewalCandidate.reasons.reduce((acc, reason) =>
                acc + this._translate.instant(`SEASON_TICKET.RENEWALS.DIALOG_RENEWAL_LIST.${reason}_REASON`) + '\n', '');
    }

    loadPagedRenewalCandidates({ offset, q }: SearchablePaginatedSelectionLoadEvent): void {
        this._renewalCandidates$.subscribe(renewalCandidates => {
            if (q) {
                renewalCandidates = renewalCandidates.filter(collectiveEntity =>
                    collectiveEntity.name.toLowerCase().includes(q.toLowerCase()));
            }
            this._renewalCandidatesPaged.next(renewalCandidates.slice(offset, offset + pageSize));
            this._renewalCandidatesMetadataPaged.next(new Metadata({ total: renewalCandidates.length, offset, limit: pageSize }));

        });
    }

    shouldDisableRenewalCandidate: (d: SeasonTicketRenewalCandidateSearch) => boolean =
        (d: SeasonTicketRenewalCandidateSearch) => !d.compatible;
}
