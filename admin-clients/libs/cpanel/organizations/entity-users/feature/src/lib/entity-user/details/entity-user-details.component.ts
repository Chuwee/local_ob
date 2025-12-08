import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { combineLatest, map } from 'rxjs';
import { MYSELF_USER_DETAILS_TOKEN } from '../entity-user.token';

@Component({
    selector: 'ob-entity-user-details',
    templateUrl: './entity-user-details.component.html',
    styleUrls: ['./entity-user-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        GoBackComponent, NavTabsMenuComponent, RouterModule, CommonModule, FlexLayoutModule
    ]
})
export class EntityUserDetailsComponent implements OnDestroy {

    private readonly _entityUsersService = inject(EntityUsersService);
    private readonly _authSrv = inject(AuthenticationService);

    readonly myselfUser = inject(MYSELF_USER_DETAILS_TOKEN);

    readonly $user = toSignal(this._entityUsersService.getEntityUser$());

    readonly $showPermissionsTab = toSignal(
        combineLatest([this._entityUsersService.getEntityUser$(), this._authSrv.getLoggedUser$()])
            .pipe(map(([entityUser, loggedUser]) => entityUser?.id !== loggedUser?.id))
    );

    ngOnDestroy(): void {
        this._entityUsersService.clearEntityUser();
    }
}
