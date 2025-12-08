import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { SeasonTicketRenewalMappingStatus } from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { first } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-general-data',
    templateUrl: './season-ticket-general-data.component.html',
    styleUrls: ['./season-ticket-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe,
        TranslatePipe,
        RouterModule,
        ContextNotificationComponent,
        LastPathGuardListenerDirective,
        MatButton,
        MatButtonToggle,
        MatButtonToggleGroup
    ]
})
export class SeasonTicketGeneralDataComponent implements OnInit, OnDestroy {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #destroyRef = inject(DestroyRef);

    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly isGenerationStatusError$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusError$();
    readonly isSomeRenewalWithoutSeatAssigned$ = this.#seasonTicketSrv.getSeasonTicketValidations$()
        .pipe(map(validations => validations?.has_pending_renewals));

    ngOnInit(): void {
        this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isGenerationStatusReady => {
                if (!isGenerationStatusReady) return;

                this.#seasonTicketSrv.seasonTicket.get$()
                    .pipe(first())
                    .subscribe(st => {
                        this.#seasonTicketSrv.loadSeasonTicketValidations(
                            st.id.toString(),
                            false,
                            false,
                            true
                        );
                    });
            });
    }

    ngOnDestroy(): void {
        this.#seasonTicketSrv.clearSeasonTicketValidations();
    }

    goToRenewals(): void {
        this.#router.navigate(['../renewals'], {
            relativeTo: this.#route,
            // TODO: change to not assigned? new enum for new filter in renewals list?
            queryParams: {
                mapping_status: SeasonTicketRenewalMappingStatus.notMapped
            }
        });
    }
}
