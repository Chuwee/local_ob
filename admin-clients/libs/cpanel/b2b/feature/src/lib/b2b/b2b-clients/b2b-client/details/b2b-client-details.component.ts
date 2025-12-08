import { B2bClient, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    selector: 'app-b2b-client-details',
    templateUrl: './b2b-client-details.component.html',
    styleUrls: ['./b2b-client-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientDetailsComponent implements OnInit, OnDestroy {
    b2bClient$: Observable<B2bClient>;
    entityIdQueryParam$: Observable<{ entityId: string }>;

    constructor(
        private _b2bSrv: B2bService,
        private _authSrv: AuthenticationService
    ) { }

    ngOnInit(): void {
        this.entityIdQueryParam$ = combineLatest([
            this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]),
            this._b2bSrv.getB2bClient$().pipe(filter(b2bClient => !!b2bClient))
        ]).pipe(map(([isOperator, b2bClient]) => isOperator ? { entityId: b2bClient.entity?.id?.toString() } : null));
        this.b2bClient$ = this._b2bSrv.getB2bClient$();
    }

    ngOnDestroy(): void {
        this._b2bSrv.clearB2bClient();
    }

}
