import { ChannelType, channelWebTypes, channelVoucherWebTypes } from '@admin-clients/cpanel/channels/data-access';
import { SaleRequest, SalesRequestsStatus, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';

@Component({
    selector: 'app-sale-request-operative',
    templateUrl: './sale-request-operative.component.html',
    styleUrls: ['./sale-request-operative.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestOperativeComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _salesRequestsService = inject(SalesRequestsService);
    private readonly _router = inject(Router);
    private readonly _route = inject(ActivatedRoute);

    salesRequestsStatus = SalesRequestsStatus;
    deepPath$ = getDeepPath$(this._router, this._route);
    saleRequest$: Observable<SaleRequest>;
    updateStatus: (id: number, status: SalesRequestsStatus) => Observable<{ status: SalesRequestsStatus }>;
    channelTypesNotPayments = [ChannelType.external];
    channelWebTypes = channelWebTypes;
    channelVoucherWebTypes = channelVoucherWebTypes;

    ngOnInit(): void {
        this.saleRequest$ = this._salesRequestsService.getSaleRequest$();
        this.updateStatus = (id, status) => this._salesRequestsService.updateSaleRequestStatus(id, status);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

}
