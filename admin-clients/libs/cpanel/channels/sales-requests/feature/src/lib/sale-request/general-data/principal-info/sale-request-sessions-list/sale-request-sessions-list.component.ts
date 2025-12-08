import { Metadata } from '@OneboxTM/utils-state';
import { SaleRequestSession, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-sale-request-sessions-list',
    templateUrl: './sale-request-sessions-list.component.html',
    styleUrls: ['./sale-request-sessions-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestSessionsListComponent implements OnInit {
    saleRequestSessionsLoading$: Observable<boolean>;
    saleRequestSessions$: Observable<SaleRequestSession[]>;
    salesRequestsSessionsMetadata$: Observable<Metadata>;
    displayedColumns = ['start_date', 'name', 'type', 'status', 'publication_date', 'sales_start'];
    sessionsPageSize = 10;
    dateTimeFormats = DateTimeFormats;

    @Input() saleRequestId: number;

    constructor(private _salesRequestsService: SalesRequestsService) { }

    ngOnInit(): void {
        this.saleRequestSessions$ = this._salesRequestsService.getSaleRequestSessions$();
        this.salesRequestsSessionsMetadata$ = this._salesRequestsService.getSaleRequestSessionsMetadata$();
        this.saleRequestSessionsLoading$ = this._salesRequestsService.isSaleRequestSessionsLoading$();
        this.loadSessionsData({ pageIndex: 0 });
    }

    loadSessionsData(pageOptions: Partial<PageEvent>): void {
        const request = {
            saleRequestId: this.saleRequestId.toString(),
            limit: this.sessionsPageSize,
            offset: this.sessionsPageSize * pageOptions.pageIndex
        };

        this._salesRequestsService.loadSaleRequestSessions(request);
    }
}
