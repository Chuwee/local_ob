import { Collective, CollectiveType, CollectivesService } from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    selector: 'app-collective-details',
    templateUrl: './collective-details.component.html',
    styleUrls: ['./collective-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CollectiveDetailsComponent implements OnInit, OnDestroy {
    collective$: Observable<Collective>;
    showCollectiveCodes$: Observable<boolean>;

    constructor(
        private _collectiveServiceSrv: CollectivesService,
        private _auth: AuthenticationService
    ) { }

    ngOnInit(): void {
        this.collective$ = this._collectiveServiceSrv.getCollective$();

        this.showCollectiveCodes$ = combineLatest([
            this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]),
            this._collectiveServiceSrv.getCollective$().pipe(filter(Boolean))
        ]).pipe(
            map(([isOperator, collective]) =>
                collective.type === CollectiveType.internal && (!collective.generic || (collective.generic && isOperator))
            )
        );
    }

    ngOnDestroy(): void {
        this._collectiveServiceSrv.clearCollective();
    }
}
