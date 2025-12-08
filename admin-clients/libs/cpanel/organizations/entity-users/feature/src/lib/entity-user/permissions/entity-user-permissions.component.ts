
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'ob-entity-user-permissions',
    templateUrl: './entity-user-permissions.component.html',
    styleUrls: ['./entity-user-permissions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CommonModule, RouterModule, MaterialModule, LastPathGuardListenerDirective, TranslatePipe, FlexLayoutModule]
})
export class EntityUserPermissionsComponent {
    private readonly _router = inject(Router);
    private readonly _route = inject(ActivatedRoute);
    private readonly _auth = inject(AuthenticationService);

    readonly isSysAdmin$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]);

    deepPath$ = getDeepPath$(this._router, this._route);
}
