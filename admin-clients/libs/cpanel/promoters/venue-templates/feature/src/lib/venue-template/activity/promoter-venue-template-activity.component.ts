import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ActVenueTplsApi, ActVenueTplService, ActVenueTplsState } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { first } from 'rxjs/operators';

@Component({
    selector: 'app-promoter-venue-template-activity',
    templateUrl: './promoter-venue-template-activity.component.html',
    styleUrls: ['./promoter-venue-template-activity.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ActVenueTplService, ActVenueTplsState, ActVenueTplsApi],
    imports: [NavTabsMenuComponent, RouterOutlet, AsyncPipe, FlexLayoutModule]
})
export class PromoterVenueTemplateActivityComponent {
    private readonly _auth = inject(AuthenticationService);
    readonly isOperator$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]).pipe(first());
}
