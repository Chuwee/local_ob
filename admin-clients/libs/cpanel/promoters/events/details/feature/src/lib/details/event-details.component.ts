import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { IsB2bEntityPipe } from '@admin-clients/cpanel/organizations/entities/utils';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { IsAvetEventPipe, IsSessionPackEventPipe } from '@admin-clients/cpanel-promoters-events-utils';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map } from 'rxjs/operators';

@Component({
    selector: 'app-event-details',
    imports: [
        RouterOutlet, NavTabsMenuComponent, GoBackComponent,
        AsyncPipe, IsB2bEntityPipe, IsSessionPackEventPipe, IsAvetEventPipe, TranslatePipe
    ],
    templateUrl: './event-details.component.html',
    styleUrls: ['./event-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventDetailsComponent {
    readonly #eventSrv = inject(EventsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #auth = inject(AuthenticationService);
    readonly event$ = this.#eventSrv.event.get$();
    readonly entity$ = this.#entitiesSrv.getEntity$();
    readonly secondaryMarketEnabled$ = this.entity$.pipe(map(entity => entity?.settings?.allow_secondary_market));
    readonly v4Enabled$ = this.entity$.pipe(map(entity => entity?.settings?.enable_v4_configs));
    readonly isOperator$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]).pipe(first());
}
