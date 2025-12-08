import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-event-container',
    templateUrl: './event-container.component.html',
    styleUrls: ['./event-container.component.scss'],
    imports: [RouterOutlet],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventContainerComponent implements OnDestroy {
    private readonly _eventSrv = inject(EventsService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _venueTplSrv = inject(VenueTemplatesService);

    ngOnDestroy(): void {
        // these clears are triggered here because this component contains all event details tabs and venue-template editor too
        this._eventSrv.event.clear();
        this._entitiesSrv.clearEntity();
        this._venueTplSrv.venueTpl.clear();
    }
}
