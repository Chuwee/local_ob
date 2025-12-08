import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-sale-request-general-data',
    templateUrl: './sale-request-general-data.component.html',
    styleUrls: ['./sale-request-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestGeneralDataComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void>;

    deepPath$ = getDeepPath$(this._router, this._route);

    constructor(
        private _router: Router,
        private _route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        this._onDestroy = new Subject<void>();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
