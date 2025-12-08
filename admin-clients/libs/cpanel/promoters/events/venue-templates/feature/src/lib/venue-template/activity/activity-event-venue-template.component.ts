import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import {
    ActVenueTplsApi, ActVenueTplService, ActVenueTplsState
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-activity-details',
    templateUrl: './activity-event-venue-template.component.html',
    styleUrls: ['./activity-event-venue-template.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ActVenueTplService, ActVenueTplsState, ActVenueTplsApi],
    imports: [NavTabsMenuComponent, RouterOutlet, AsyncPipe]
})
export class ActivityEventVenueTemplateComponent {
    private readonly _eventSrv = inject(EventsService);
    readonly hideGroupsTab$ = this._eventSrv.event.get$().pipe(map(event => !event?.settings?.groups?.allowed));
    readonly isSga$ = this._eventSrv.event.get$().pipe(map(event => event?.additional_config?.inventory_provider === 'SGA'));
}
