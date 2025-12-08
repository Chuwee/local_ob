import {
    SeasonTicketRenewalMappingStatus, SeasonTicketRenewalsAction, SeasonTicketRenewalsListActionsService, SeasonTicketRenewalsListState,
    SeasonTicketRenewalsService, SeasonTicketRenewalsSummary, VmSeasonTicketRenewal
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { HeaderSummaryComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, map, shareReplay, switchMap, take } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-renewals-list-summary',
    templateUrl: 'season-ticket-renewals-list-summary.component.html',
    styleUrls: ['season-ticket-renewals-list-summary.component.scss'],
    imports: [
        AsyncPipe, HeaderSummaryComponent, TranslatePipe, EllipsifyDirective, MatTooltip,
        LocalDateTimePipe, MatIcon, LocalNumberPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketRenewalsListSummaryComponent {
    readonly #seasonTicketRenewalsSrv = inject(SeasonTicketRenewalsService);
    readonly #listState = inject(SeasonTicketRenewalsListState);
    readonly #actionsSrv = inject(SeasonTicketRenewalsListActionsService);

    #paginatedMappedSessionsValue = 0;

    // To show mapped renewals coherently. The data from backend isn't update fast enough, so we have to falsify the data in the front
    // when editing renewals
    readonly summary$ = combineLatest([
        this.#seasonTicketRenewalsSrv.renewalsList.getSummary$(),
        this.#listState.getRenewalsList$()
    ]).pipe(
        filter(([summary]) => !!summary),
        switchMap(([summary, renewals]) =>
            this.#actionsSrv.getAction$().pipe(
                filter(renewalsAction =>
                    renewalsAction !== SeasonTicketRenewalsAction.toggleTableRows
                ),
                take(1),
                map(renewalsAction => {
                    if (
                        renewalsAction === SeasonTicketRenewalsAction.none ||
                        renewalsAction === SeasonTicketRenewalsAction.init ||
                        renewalsAction === SeasonTicketRenewalsAction.tableAction ||
                        renewalsAction === SeasonTicketRenewalsAction.deleteAction
                    ) {
                        this.setTotalPaginatedMappedRenewalsValue(
                            renewals,
                            summary,
                            renewalsAction === SeasonTicketRenewalsAction.init ||
                            renewalsAction === SeasonTicketRenewalsAction.tableAction ||
                            renewalsAction === SeasonTicketRenewalsAction.deleteAction
                        );
                        return summary;
                    } else if (
                        renewalsAction === SeasonTicketRenewalsAction.saveEdits
                    ) {
                        return this.getUpdatedSummary(renewals, summary);
                    } else {
                        return undefined;
                    }
                })
            )
        ),
        shareReplay(1)
    );

    readonly dateTimeFormats = DateTimeFormats;

    private setTotalPaginatedMappedRenewalsValue(
        renewals: VmSeasonTicketRenewal[],
        summary: SeasonTicketRenewalsSummary,
        isCondition: boolean
    ): void {
        if (renewals?.length && summary && isCondition) {
            this.#paginatedMappedSessionsValue = 0;
            renewals.forEach(renewal => {
                if (renewal.mapping_status === SeasonTicketRenewalMappingStatus.mapped) {
                    this.#paginatedMappedSessionsValue++;
                }
            });
        }
    }

    private getUpdatedSummary(renewals: VmSeasonTicketRenewal[], summary: SeasonTicketRenewalsSummary): SeasonTicketRenewalsSummary {
        let mappedRenewals = 0;
        renewals.forEach(renewal => {
            if (renewal.mapping_status === SeasonTicketRenewalMappingStatus.mapped) {
                mappedRenewals++;
            }
        });
        const diff = mappedRenewals - this.#paginatedMappedSessionsValue;
        return {
            ...summary,
            mapped_imports: summary.mapped_imports + diff,
            not_mapped_imports: summary.not_mapped_imports - diff
        };
    }
}
