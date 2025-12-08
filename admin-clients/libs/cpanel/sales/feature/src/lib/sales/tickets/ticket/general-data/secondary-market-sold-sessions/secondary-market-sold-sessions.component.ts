import { TicketDetail } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-secondary-market-sold-sessions',
    templateUrl: './secondary-market-sold-sessions.component.html',
    styleUrls: ['./secondary-market-sold-sessions.component.scss'],
    imports: [
        MatPaginator, MaterialModule, TranslatePipe, MatTooltip, AsyncPipe,
        DateTimePipe, FlexLayoutModule, EllipsifyDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SecondaryMarketSoldSessionsComponent implements OnInit {
    readonly #sessionsPage = signal(0);

    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);

    readonly dateTimeFormats = DateTimeFormats;
    readonly sessionsPageSize = PAGE_SIZE;

    @Input() sessions$: Observable<TicketDetail['subitems']>;
    @Input() total: number;

    printsColumns: Observable<string[]>;
    sessionsPage$ = toObservable(this.#sessionsPage);
    pagedSessions$: Observable<TicketDetail['subitems']>;

    constructor() { }

    ngOnInit(): void {
        this.printsColumns = this.sessions$.pipe(
            map(sessions => {
                const columns = [];
                if (sessions?.find(item => !!item?.event?.name)) {
                    columns.push('event');
                }
                if (sessions?.find(item => !!item?.secondary_market?.purchase_date)) {
                    columns.push('date');
                }
                if (sessions?.find(item => !!item?.session?.name)) {
                    columns.push('session');
                }
                if (sessions?.find(item => !!item?.secondary_market?.purchase_order)) {
                    columns.push('code');
                }
                return columns;
            })
        );

        this.pagedSessions$ = combineLatest([
            this.sessions$,
            this.sessionsPage$
        ])
            .pipe(
                map(([sessions, page]) =>
                    sessions?.slice(page * this.sessionsPageSize, (page + 1) * this.sessionsPageSize)
                ),
                shareReplay(1)
            );
    }

    goToPurchasedItemRoute = (ticketId: string, purchaseOrder: string): void => {
        if (this.#route.snapshot.parent?.params?.['orderCode']) {
            this.#router.navigate([`../../`
                + `transactions/${purchaseOrder}/tickets/${ticketId}`]);
        } else {
            this.#router.navigate([`../../`
                + `tickets/${purchaseOrder}-${ticketId}`]);
        }

    };

    pageFilter(pageOptions: PageEvent): void {
        this.#sessionsPage.set(pageOptions.pageIndex);
    }

    getPaginatorStartItem(page: number): number {
        return (page * this.sessionsPageSize) + 1;
    }

    getPaginatorEndItem(page: number, sessionsLength: number): number {
        return Math.min((page + 1) * this.sessionsPageSize, sessionsLength);
    }
}
