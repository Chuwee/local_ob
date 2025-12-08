import { ActionsHistoryType, ActionsHistory } from '@admin-clients/shared/common/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIcon } from '@angular/material/icon';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-ticket-actions-history',
    templateUrl: './ticket-actions-history.component.html',
    styleUrls: ['./ticket-actions-history.component.scss'],
    imports: [
        MatTableModule, TranslatePipe, MatTooltip, EllipsifyDirective,
        MatPaginator, DateTimePipe, NgIf, AsyncPipe, MatIcon, FlexLayoutModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TicketActionsHistoryComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _actionsPage = new BehaviorSubject<number>(0);

    readonly dateTimeFormats = DateTimeFormats;
    readonly actionsHistoryPageSize = PAGE_SIZE;

    @Input() actions$: Observable<ActionsHistory[]>;
    @Input() total: number;
    @Input() type: ActionsHistoryType;

    printsColumns: Observable<string[]>;
    actionsPage$ = this._actionsPage.asObservable();
    pagedActions$: Observable<ActionsHistory[]>;

    constructor() { }

    ngOnInit(): void {
        this.printsColumns = this.actions$.pipe(
            map(actions => {
                const columns = ['date'];
                if (actions.find(action => !!action?.channel?.name)) {
                    columns.push('channel');
                }
                columns.push('user');
                if (actions.find(action => !!action?.additional_data?.resend_email)) {
                    columns.push('resend_email');
                }
                if (actions.find(action => !!action?.additional_data?.resend_whatsapp)) {
                    columns.push('resend_whatsapp');
                }
                if (actions.find(action => !!action?.ticket_format)) {
                    columns.push('ticket_format');
                }
                if (!this.type && actions.find(action => !!action?.type)) {
                    columns.push('ticket_action');
                }
                return columns;
            })
        );

        this.pagedActions$ = combineLatest([
            this.actions$,
            this._actionsPage
        ])
            .pipe(
                map(([actions, page]) =>
                    actions.slice(page * this.actionsHistoryPageSize, (page + 1) * this.actionsHistoryPageSize)
                ),
                shareReplay(1)
            );
    }

    additionalUserInfo(additionalData: ActionsHistory['additional_data']): string {
        return `${additionalData?.external_user_id || ''} ${additionalData?.external_username || ''}
            ${additionalData?.external_center_code || ''} ${additionalData?.external_company_code || ''}`;
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
