import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { RestrictedRatesListComponent } from '@admin-clients/cpanel/promoters/shared/feature/rate-restrictions';
import { ContextNotificationComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, viewChild, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { distinctUntilChanged, filter, switchMap } from 'rxjs/operators';
import { SeasonTicketRatesListComponent } from './list/season-ticket-rates-list.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        ContextNotificationComponent,
        TranslatePipe,
        MaterialModule,
        SeasonTicketRatesListComponent,
        TabsMenuComponent,
        TabDirective,
        RestrictedRatesListComponent
    ],
    selector: 'app-season-ticket-rates',
    templateUrl: './season-ticket-rates.component.html',
    styleUrls: ['./season-ticket-rates.component.scss']
})
export class SeasonTicketRatesComponent implements OnDestroy {

    readonly #seasonTicketsSrv = inject(SeasonTicketsService);
    readonly #destroyRef = inject(DestroyRef);

    private _$restrictedRatesList = viewChild(RestrictedRatesListComponent);

    readonly $seasonTicket = toSignal(this.#seasonTicketsSrv.seasonTicket.get$().pipe(filter(Boolean)));
    readonly $seasonTicketRates = toSignal(this.#seasonTicketsSrv.getSeasonTicketRates$().pipe(filter(Boolean)));
    readonly $isGenerationStatusInProgress = toSignal(this.#seasonTicketsSrv.seasonTicketStatus.isGenerationStatusInProgress$()
        .pipe(distinctUntilChanged()));

    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#seasonTicketsSrv.seasonTicket.inProgress$(),
        this.#seasonTicketsSrv.seasonTicketStatus.inProgress$(),
        this.#seasonTicketsSrv.isSeasonTicketRatesInProgress$()
    ]));

    constructor() {
        this.#seasonTicketsSrv.seasonTicketStatus.isGenerationStatusReady$()
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                filter(isGenerationStatusReady => isGenerationStatusReady),
                switchMap(() => this.#seasonTicketsSrv.seasonTicket.get$().pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef)))
            )
            .subscribe(st => {
                this.#seasonTicketsSrv.loadSeasonTicketRates(st.id.toString());
            });
    }

    ngOnDestroy(): void {
        this.#seasonTicketsSrv.clearSeasonTicketRates();
    }

    canDeactivate(): Observable<boolean> {
        return this._$restrictedRatesList()?.canDeactivate();
    }
}
