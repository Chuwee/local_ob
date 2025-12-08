import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, filter, map } from 'rxjs';

@Component({
    selector: 'app-event-communication',
    templateUrl: './event-communication.component.html',
    styleUrls: ['./event-communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventCommunicationComponent {
    private readonly _entitiesSrv = inject(EntitiesBaseService);

    readonly isEntityV4$ = this._entitiesSrv.getEntity$().pipe(filter(Boolean), map(entity => !!entity.settings.enable_v4_configs));

    deepPath$: Observable<string> = getDeepPath$(this._router, this._route);

    constructor(
        private _route: ActivatedRoute,
        private _router: Router) {
    }
}
