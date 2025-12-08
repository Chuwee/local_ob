import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ChangeDetectionStrategy, Component, inject, viewChild } from '@angular/core';
import { Observable, of } from 'rxjs';
import {
    EventPriceTypesRatesGroupComponent
} from './price-type-rates-group/event-price-types-rates-group.component';
import {
    EventPriceTypesRatesComponent
} from './price-types-rates/event-price-types-rates.component';
import { EventTiersComponent } from './tiers/event-tiers.component';

@Component({
    selector: 'app-event-price-types',
    templateUrl: './event-price-types.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventPriceTypesComponent implements WritingComponent {
    private readonly _eventsSrv = inject(EventsService);

    private readonly _eventPriceTypesRatesGroupTemp = viewChild<EventPriceTypesRatesGroupComponent>(EventPriceTypesRatesGroupComponent);
    private readonly _eventPriceTypesRatesTemp = viewChild<EventPriceTypesRatesComponent>(EventPriceTypesRatesComponent);
    private readonly _eventTiersTemp = viewChild<EventTiersComponent>(EventTiersComponent);

    readonly event$ = this._eventsSrv.event.get$();

    save$(): Observable<unknown> {
        if (this._eventPriceTypesRatesGroupTemp()) {
            return this._eventPriceTypesRatesGroupTemp().save$();
        } else if (this._eventPriceTypesRatesTemp()) {
            return this._eventPriceTypesRatesTemp().save$();
        } else if (this._eventTiersTemp()) {
            return this._eventTiersTemp().save$();
        }
        console.error('prices component no found');
        return of(null);
    }
}
