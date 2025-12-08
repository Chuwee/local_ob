import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'app-sale-request-communication',
    templateUrl: './sale-request-communication.component.html',
    styleUrls: ['./sale-request-communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestCommunicationComponent {

    deepPath$ = getDeepPath$(this._router, this._route);

    constructor(
        private _route: ActivatedRoute,
        private _router: Router) {
    }
}
