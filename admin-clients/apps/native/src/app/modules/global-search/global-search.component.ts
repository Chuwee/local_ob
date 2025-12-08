import { Event, EventsService, eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { TicketsBaseService, ticketsBaseProviders } from '@admin-clients/shared/common/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnInit, ViewChild, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Keyboard } from '@capacitor/keyboard';
import { IonInput, Platform } from '@ionic/angular';
import { BehaviorSubject, filter, first, map } from 'rxjs';

@Component({
    selector: 'global-search',
    templateUrl: './global-search.component.html',
    styleUrls: ['./global-search.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ticketsBaseProviders, eventsProviders],
    standalone: false
})
export class GlobalSearchComponent implements OnInit {
    private readonly _ticketsBaseSrv = inject(TicketsBaseService);
    private readonly _eventsSrv = inject(EventsService);
    private readonly _ordersSrv = inject(OrdersService);
    private readonly _activeRoute = inject(ActivatedRoute);
    private readonly _platform = inject(Platform);
    private readonly _router = inject(Router);

    @ViewChild('searchBarInput', { static: false }) private readonly _inputElement: IonInput;
    readonly isFirstLoad$ = new BehaviorSubject<boolean>(true);
    readonly foundTickets$ = this._ticketsBaseSrv.ticketList.getData$().pipe(filter(Boolean));
    readonly totalTickets$ = this._ticketsBaseSrv.ticketList.getMetaData$().pipe(filter(Boolean), map(reponse => reponse.total));
    readonly foundEvents$ = this._eventsSrv.eventsList.getData$().pipe(filter(Boolean));
    readonly totalEvents$ = this._eventsSrv.eventsList.getMetadata$().pipe(filter(Boolean), map(reponse => reponse.total));
    readonly foundTransactions$ = this._ordersSrv.getOrdersListData$().pipe(filter(Boolean));
    readonly totalTransactions$ = this._ordersSrv.getOrdersListMetadata$().pipe(filter(Boolean), map(reponse => reponse.total));
    readonly isLoading$ = booleanOrMerge([
        this._ticketsBaseSrv.ticketList.loading$(),
        this._eventsSrv.eventsList.loading$(),
        this._ordersSrv.isOrdersListLoading$()
    ]);

    qrButtonActivated = true;
    inputValue: string;

    ngOnInit(): void {
        this._activeRoute.queryParams.pipe(
            first(),
            filter(Boolean)
        ).subscribe(params => {
            if (params['q']) {
                this.inputValue = params['q'];
                this.onSearch(params['q']);
            }
        });
    }

    onSearch(event: CustomEvent): void {
        if (this._platform.is('hybrid')) {
            Keyboard.hide();
        }
        this.isFirstLoad$.next(false);
        this.inputValue = event.detail.value;
        const requestConf = {
            q: event.detail.value,
            limit: 5,
            offset: 0
        };
        this._ticketsBaseSrv.ticketList.load(requestConf);
        this._eventsSrv.eventsList.load(requestConf);
        this._ordersSrv.loadOrdersList(requestConf);
    }

    ionViewDidEnter(): void {
        this._inputElement.setFocus();
    }

    toggleQrButtonActivated(): void {
        this.qrButtonActivated = !this.qrButtonActivated;
    }

    goToEventDetail(event: Event): void {
        this._router.navigate(['event-detail', event.id]);
    }

    seeAll(type: string): void {
        const query = '?q=' + this.inputValue;
        switch (type) {
            case 'tickets':
                this._router.navigate(['/tabs/sales'], {
                    queryParams: {
                        q: query,
                        segment: 'tickets'
                    }
                });
                break;
            case 'events':
                this._router.navigate(['/tabs/events'], {
                    queryParams: {
                        q: query
                    }
                });
                break;
            case 'transactions':
                this._router.navigate(['/tabs/sales'], {
                    queryParams: {
                        q: query,
                        segment: 'transactions'
                    }
                });
                break;
        }
    }
}
