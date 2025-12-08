import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    PostSeasonTicketRenewals, RenewalCandidateToImport, RenewalCandidateTypeEnum, RenewalsGenerationStatus,
    SeasonTicketRenewalsService, seasonTicketRenewalsProviders
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { distinctUntilChanged, filter, first, map, startWith, take } from 'rxjs/operators';
import { NewSeasonTicketRenewalDialogComponent } from '../create/new-season-ticket-renewal-dialog.component';

@Component({
    selector: 'app-season-ticket-renewals-container',
    templateUrl: './season-ticket-renewals-container.component.html',
    styleUrls: ['./season-ticket-renewals-container.component.scss'],
    providers: [seasonTicketRenewalsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketRenewalsContainerComponent implements OnInit, OnDestroy {
    private readonly _seasonTicketsSrv = inject(SeasonTicketsService);
    private readonly _seasonTicketRenewalsSrv = inject(SeasonTicketRenewalsService);
    private readonly _auth = inject(AuthenticationService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _destroyRef = inject(DestroyRef);

    readonly isGenerationStatusReady$ = this._seasonTicketsSrv.seasonTicketStatus.isGenerationStatusReady$();
    readonly seasonTicket$ = this._seasonTicketsSrv.seasonTicket.get$();
    readonly canLoggedUserWrite$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR,
    UserRoles.ENT_MGR, UserRoles.EVN_MGR, UserRoles.CNL_MGR, UserRoles.CRM_MGR]);

    readonly isListShown$ = this._seasonTicketRenewalsSrv.renewalsList.getSummary$()
        .pipe(
            filter(Boolean),
            map(renewalsSummary => renewalsSummary.generation_status !== RenewalsGenerationStatus.notImported),
            distinctUntilChanged(),
            startWith(false)
        );

    readonly isNoneRenewalsCandidatesCompatible$ = this._seasonTicketsSrv.getSeasonTicketValidations$()
        .pipe(
            filter(Boolean),
            map(sTValidations => !sTValidations.has_linkable_seats || !sTValidations.has_assigned_sessions)
        );

    readonly isImporting$ = this._seasonTicketRenewalsSrv.renewalsImport.inProgress$();
    readonly isGenerationStatusInProgress$ = this._seasonTicketsSrv.seasonTicketStatus.isGenerationStatusInProgress$();
    readonly $updateSubstatusLoading = signal(false);

    readonly isLoading$ = booleanOrMerge([
        this._seasonTicketsSrv.seasonTicketStatus.inProgress$(),
        this._seasonTicketRenewalsSrv.renewalsList.inProgress$(),
        this._seasonTicketsSrv.isSeasonTicketValidationsInProgress$(),
        this._seasonTicketRenewalsSrv.renewalsSubstatus.inProgress$(),
        toObservable(this.$updateSubstatusLoading)
    ]);

    isNoneRenewalsCandidatesCompatibleUnderstood = false;

    ngOnInit(): void {
        this._seasonTicketsSrv.seasonTicketStatus.isGenerationStatusReady$()
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(isGenerationStatusReady => {
                if (!isGenerationStatusReady) return;

                this._seasonTicketsSrv.seasonTicket.get$()
                    .pipe(first())
                    .subscribe(st => {
                        this._seasonTicketsSrv.loadSeasonTicketValidations(st.id.toString(), true, true, false);
                    });
            });
    }

    ngOnDestroy(): void {
        this._seasonTicketsSrv.clearSeasonTicketValidations();
    }

    openNewSeasonTicketRenewalDialog(): void {
        this._matDialog.open(
            NewSeasonTicketRenewalDialogComponent,
            new ObMatDialogConfig()
        ).beforeClosed()
            .subscribe((renewalCandidate: RenewalCandidateToImport) => {
                if (renewalCandidate) {
                    const { renewalCandidateId, renewalRates, type, includeBalance } = renewalCandidate;
                    let postRenewalCandidate: PostSeasonTicketRenewals;

                    if (type === RenewalCandidateTypeEnum.internal || type === RenewalCandidateTypeEnum.internalAllEntities) {
                        postRenewalCandidate = {
                            renewal_season_ticket: renewalCandidateId,
                            rates: renewalRates,
                            include_all_entities: type === RenewalCandidateTypeEnum.internalAllEntities,
                            include_balance: includeBalance
                        };
                    } else if (type === RenewalCandidateTypeEnum.external) {
                        postRenewalCandidate = {
                            renewal_external_event: renewalCandidateId,
                            is_external_event: true,
                            rates: renewalRates,
                            include_balance: includeBalance
                        };
                    }
                    this._seasonTicketsSrv.seasonTicket.get$()
                        .pipe(take(1))
                        .subscribe(seasonTicket => this._seasonTicketRenewalsSrv.renewalsImport.import(
                            seasonTicket.id, postRenewalCandidate
                        ));
                }
            });
    }
}
