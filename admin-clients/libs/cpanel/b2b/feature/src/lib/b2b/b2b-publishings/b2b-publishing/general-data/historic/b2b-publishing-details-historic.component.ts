import { B2bSeatHistoricEntry } from '@admin-clients/cpanel/b2b/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { BehaviorSubject, Observable, Subject, map, shareReplay } from 'rxjs';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-b2b-publishing-details-historic',
    templateUrl: './b2b-publishing-details-historic.component.html',
    styleUrls: ['./b2b-publishing-details-historic.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bPublishingDetailsHistoricComponent implements OnDestroy, OnInit {
    private _onDestroy = new Subject<void>();
    private _actionsPage = new BehaviorSubject<number>(0);

    readonly dateTimeFormats = DateTimeFormats;
    readonly actionsHistoryPageSize = PAGE_SIZE;

    @Input() actions: B2bSeatHistoricEntry[];

    printsColumns: string[];
    actionsPage$ = this._actionsPage.asObservable();
    pagedActions$: Observable<B2bSeatHistoricEntry[]>;

    constructor() { }

    ngOnInit(): void {
        this.printsColumns = ['date', 'type', 'publisher'];

        this.pagedActions$ =
            this._actionsPage
                .pipe(
                    map(page =>
                        this.actions.slice(page * this.actionsHistoryPageSize, (page + 1) * this.actionsHistoryPageSize)
                    ),
                    shareReplay(1)
                );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    pageFilter(pageOptions: PageEvent): void {
        this._actionsPage.next(pageOptions.pageIndex);
    }

    getPaginatorStartItem(page: number): number {
        return (page * this.actionsHistoryPageSize) + 1;
    }

    getPaginatorEndItem(page: number, actionsLength: number): number {
        return Math.min((page + 1) * this.actionsHistoryPageSize, actionsLength);
    }

}
