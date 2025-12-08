import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { RestrictedRatesListComponent } from '@admin-clients/cpanel/promoters/shared/feature/rate-restrictions';
import { IsAvetEventPipe, IsSgaEventPipe } from '@admin-clients/cpanel-promoters-events-utils';
import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, viewChild, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable } from 'rxjs';
import { EventRatesListComponent } from './list/event-rates-list.component';
import { EventRatesGroupListComponent } from './rates-group-list/event-rates-group-list.component';
import { EventSgaRatesGroupListComponent } from './sga-rates-group-list/event-sga-rates-group-list.component';

@Component({
    selector: 'app-event-rates',
    imports: [
        TranslatePipe, FormContainerComponent, IsAvetEventPipe, MatProgressSpinner, TabsMenuComponent, TabDirective,
        RestrictedRatesListComponent, IsSgaEventPipe, EventRatesListComponent, EventRatesGroupListComponent,
        EventSgaRatesGroupListComponent
    ],
    templateUrl: './event-rates.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventRatesComponent implements OnDestroy {
    readonly #eventsSrv = inject(EventsService);
    readonly #authenticationService = inject(AuthenticationService);

    private _$restrictedRatesList = viewChild(RestrictedRatesListComponent);

    readonly $event = toSignal(this.#eventsSrv.event.get$());
    readonly $eventRates = toSignal(this.#eventsSrv.eventRates.get$());
    readonly $eventRatesGroup = toSignal(this.#eventsSrv.ratesGroup.get$());
    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#eventsSrv.ratesGroup.loading$(),
        this.#eventsSrv.eventPrices.inProgress$(),
        this.#eventsSrv.eventRates.inProgress$(),
        this.#eventsSrv.ratesGroup.loading$(),
        this.#eventsSrv.sgaProducts.loading$()
    ]));

    readonly $isOperator = toSignal(this.#authenticationService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));

    constructor() {
        this.#eventsSrv.event.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed()
        ).subscribe(event => {
            if (event.additional_config?.inventory_provider === ExternalInventoryProviders.sga) {
                this.#eventsSrv.ratesGroup.load(event.id, 'RATE');
            } else {
                this.#eventsSrv.eventRates.load(event.id.toString());
            }
        });
    }

    ngOnDestroy(): void {
        this.#eventsSrv.eventRates.clear();
    }

    canDeactivate(): Observable<boolean> {
        return this._$restrictedRatesList()?.canDeactivate();
    }
}
