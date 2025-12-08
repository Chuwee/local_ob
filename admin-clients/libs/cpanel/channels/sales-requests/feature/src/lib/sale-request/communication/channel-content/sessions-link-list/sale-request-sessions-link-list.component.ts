import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, inject, Input } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';

@Component({
    selector: 'app-sale-request-sessions-link-list',
    templateUrl: './sale-request-sessions-link-list.component.html',
    styleUrls: ['./sale-request-sessions-link-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestSessionsLinkListComponent {
    private readonly _salesRequestService = inject(SalesRequestsService);

    private _language: string;

    sessionLinks$ = this._salesRequestService.salesRequestSessionLinks.get$();
    sessionLinksMetadata$ = this._salesRequestService.salesRequestSessionLinks.getSessionLinksMetadata$();
    sessionLinksLoading$ = this._salesRequestService.salesRequestSessionLinks.inProgress$();

    readonly pageSize = 10;
    dateTimeFormats = DateTimeFormats;

    @Input() saleRequestId: number;
    @Input() set language(language: string) {
        this._language = language;
        this.loadSessionsData({ pageIndex: 0 });
    }

    loadSessionsData(pageOptions: Partial<PageEvent>): void {
        const request = {
            saleRequestId: this.saleRequestId,
            language: this._language,
            limit: this.pageSize,
            sort: 'start_date:asc',
            offset: this.pageSize * pageOptions.pageIndex
        };
        this._salesRequestService.salesRequestSessionLinks.load(request);
    }

}
