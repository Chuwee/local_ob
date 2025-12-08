import { BuyersService, Buyer } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-buyer-details',
    templateUrl: './buyer-details.component.html',
    styleUrls: ['./buyer-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BuyerDetailsComponent implements OnInit, OnDestroy {

    buyer$: Observable<Buyer>;

    constructor(private _buyersSrv: BuyersService) { }

    ngOnInit(): void {
        this.buyer$ = this._buyersSrv.getBuyer$();
    }

    ngOnDestroy(): void {
        this._buyersSrv.clearBuyer();
    }
}
