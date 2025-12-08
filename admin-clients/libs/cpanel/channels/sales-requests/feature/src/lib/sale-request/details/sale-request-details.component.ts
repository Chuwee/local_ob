import { ChannelType, channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { SaleRequest, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-details',
    templateUrl: './sale-request-details.component.html',
    styleUrls: ['./sale-request-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestDetailsComponent implements OnInit, OnDestroy {
    saleRequest$: Observable<SaleRequest>;
    channelTypesCommunication = channelWebTypes.concat([ChannelType.boxOffice]);

    constructor(private _salesRequestsService: SalesRequestsService) { }

    ngOnInit(): void {
        this.saleRequest$ = this._salesRequestsService.getSaleRequest$();
    }

    ngOnDestroy(): void {
        this._salesRequestsService.clearSaleRequest();
    }
}
