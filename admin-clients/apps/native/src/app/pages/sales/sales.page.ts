import { User, AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    GetOrdersWithFieldsRequest, OrderWithFieldsItem, OrdersService, VmOrderWithFields, ordersListFields
} from '@admin-clients/cpanel-sales-data-access';
import { GetTicketsRequest, ticketsBaseProviders, TicketsBaseService, TicketState } from '@admin-clients/shared/common/data-access';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { AggregatedMetric } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { Location } from '@angular/common';
import { Component, ViewChild, inject, ChangeDetectionStrategy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { InfiniteScrollCustomEvent, ModalController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { combineLatest, filter, map } from 'rxjs';
import { CalendarSelectorComponent } from '../../core/components/calendar-selector/calendar-selector.component';
import { SelectedTime } from '../../core/components/calendar-selector/models/calendar-selector.model';
import { getInitials } from '../../helpers/string.utils';
import { AuthService } from '../../modules/auth/services/auth.service';
import { roleTickets, roleTransaction } from '../../modules/auth/services/role-constant';
import { FiltersComponent } from '../../modules/filters/filters.component';
import { salesFilters } from './data/filter-list';
import { KpiTicketNames, KpiTransactionNames, SalesModel, SegmentType } from './models/sales.page.model';

@Component({
    selector: 'sales-page',
    templateUrl: 'sales.page.html',
    styleUrls: ['sales.page.scss'],
    providers: [eventsProviders, ticketsBaseProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SalesPage {
    @ViewChild('calendarSelector') readonly calendarSelector: CalendarSelectorComponent;
    readonly #modalCtrl = inject(ModalController);
    readonly #authService = inject(AuthService);
    readonly #i18nSrv = inject(I18nService);
    readonly #ticketsBaseSrv = inject(TicketsBaseService);
    readonly #ordersBaseSrv = inject(OrdersService);
    readonly #translateService = inject(TranslateService);
    readonly #activeRoute = inject(ActivatedRoute);
    readonly #location = inject(Location);
    readonly #router = inject(Router);
    readonly #defaultFilters = {
        limit: 10,
        offset: 0,
        aggs: true
    };

    readonly $currencies = toSignal(this.#authService.getLoggedUser$().pipe(
        filter(Boolean),
        map(AuthenticationService.operatorCurrencies)));

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#ticketsBaseSrv.ticketList.loading$(),
        this.#ticketsBaseSrv.currencyAggregatedData.loading$(),
        this.#ordersBaseSrv.isOrdersWithFieldsListLoading$(),
        this.#ordersBaseSrv.currencyAggregatedData.loading$()
    ]));

    readonly $initials = toSignal(this.#authService.getLoggedUser$().pipe(filter(Boolean),
        map((user: User) => getInitials(user.name, user.last_name))));

    readonly $totalTickets = toSignal(this.#ticketsBaseSrv.ticketList.getMetaData$().pipe(filter(Boolean), map(metadata => {
        this.sales.tickets.totalResultsCounter = metadata.total;
    })));

    readonly $totalTransactions = toSignal(this.#ordersBaseSrv.getOrdersWithFieldsListMetadata$().pipe(filter(Boolean), map(metadata => {
        this.sales.transactions.totalResultsCounter = metadata.total;
    })));

    readonly $tiketsList = toSignal(this.#ticketsBaseSrv.ticketList.getData$()
        .pipe(filter(Boolean), map(response => {
            if (this.sales.tickets.appliedSearchFilters.offset === 0) {
                this.sales.tickets.searchedResults = response;
            } else {
                this.sales.tickets.searchedResults = [
                    ...this.sales.tickets.searchedResults,
                    ...response
                ];
            }
            if (this.currentEvent) {
                this.currentEvent.target.complete();
            }
        })));

    readonly $transactionsList = toSignal(this.#ordersBaseSrv.getOrdersWithFieldsListData$()
        .pipe(
            filter(Boolean),
            map(orders => {
                const vmOrders: VmOrderWithFields[] = orders;
                vmOrders.map(order => {
                    order.eventColumnData = this.prepareEventData(order.items);
                });
                return vmOrders;
            }),
            map(response => {
                if (this.sales.transactions.appliedSearchFilters.offset === 0) {
                    this.sales.transactions.searchedResults = response;
                } else {
                    this.sales.transactions.searchedResults = [
                        ...this.sales.transactions.searchedResults,
                        ...response
                    ];
                }
                if (this.currentEvent) {
                    this.currentEvent.target.complete();
                }
            })
        ));

    readonly $ticketsAggregatedData = toSignal(this.#ticketsBaseSrv.currencyAggregatedData.getCombined$().pipe(filter(Boolean),
        map(response => {
            const kpiData = response.overall;
            this.loadKpis(kpiData);
        })));

    readonly $transactionsAggregatedData = toSignal(this.#ordersBaseSrv.currencyAggregatedData.getCombined$()
        .pipe(filter(Boolean), map(response => {
            const kpiData = response.overall;
            this.loadKpis(kpiData);
        })));

    readonly $user = toSignal(this.#authService.getLoggedUser$().pipe(filter(Boolean)));
    readonly initSales$ = this.#authService.getLoggedUser$().pipe(filter(Boolean)).subscribe(() => {
        this.restartScrollOfKpis();
        this.initSales();
    });

    readonly loadData$ = combineLatest([
        this.#authService.getLoggedUser$(),
        this.#activeRoute.queryParams
    ]).pipe(filter(Boolean)).subscribe(([user, queryParams]) => {
        this.isRoleTickets = AuthService.isSomeRoleInUserRoles(user, roleTickets);
        this.isRoleTransactions = AuthService.isSomeRoleInUserRoles(user, roleTransaction);
        if (Object.keys(queryParams).length > 0) {
            if (queryParams['segment']) {
                this.segmentValue = queryParams['segment'];
                if (this.segmentValue === 'tickets') this.sales.tickets.appliedSearchFilters.state = undefined;
            }
            if (queryParams['filter'] && queryParams['filter'] === 'yesterday') {
                this.sales[this.segmentValue].appliedSearchFilters.purchase_date_from =
                    moment().subtract(1, 'days').startOf('day').toJSON();
                this.sales[this.segmentValue].appliedSearchFilters.purchase_date_to =
                    moment().subtract(1, 'days').endOf('day').toJSON();
            }
            if (queryParams['filter'] && queryParams['filter'] === 'today') {
                this.sales[this.segmentValue].appliedSearchFilters.purchase_date_from =
                    moment().startOf('day').toJSON();
                this.sales[this.segmentValue].appliedSearchFilters.purchase_date_to =
                    moment().endOf('day').toJSON();
            }
            // If we have currency when coming directly from the Home accesses
            if (queryParams['currency']) {
                this.sales[this.segmentValue].appliedSearchFilters.currency_code = queryParams['currency'];
            }
        }
        this.loadData();
    });

    sales: SalesModel;
    segmentValue: SegmentType = 'tickets';
    calendarModalIsOpen = false;
    listOfFilterBubbles = [];
    ionInfinite = false;
    inputValue = null;
    currentEvent: InfiniteScrollCustomEvent;
    isRoleTickets: boolean;
    isRoleTransactions: boolean;

    initSales(): void {
        this.sales = {
            tickets: {
                kpis: [],
                searchedResults: [],
                totalResultsCounter: 0,
                appliedSearchFilters: {
                    ...this.#defaultFilters,
                    sort: 'purchase_date:desc',
                    purchase_date_from: moment().startOf('day').toJSON(),
                    purchase_date_to: moment().endOf('day').toJSON(),
                    state: TicketState.purchase
                } as GetTicketsRequest & GetOrdersWithFieldsRequest
            },
            transactions: {
                kpis: [],
                searchedResults: [],
                totalResultsCounter: 0,
                appliedSearchFilters: {
                    ...this.#defaultFilters,
                    sort: 'date:desc',
                    purchase_date_from: moment().startOf('day').toJSON(),
                    purchase_date_to: moment().endOf('day').toJSON()
                } as GetTicketsRequest & GetOrdersWithFieldsRequest
            }
        };
    }

    handleRefresh = (event): void => {
        this.sales[this.segmentValue].appliedSearchFilters.offset = 0;
        this.loadData();
        setTimeout(() => {
            event.target.complete();
        }, 1000);
    };

    onIonInfinite(event: InfiniteScrollCustomEvent): void {
        this.ionInfinite = true;
        this.currentEvent = event;
        if (this.sales[this.segmentValue].appliedSearchFilters.offset <= this.sales[this.segmentValue].totalResultsCounter) {
            this.sales[this.segmentValue].appliedSearchFilters.offset += 10;
            this.loadData();
        } else {
            event.target.complete();
        }
    }

    onSearch(searchInput: CustomEvent): void {
        this.sales[this.segmentValue].searchedResults = [];
        this.sales[this.segmentValue].appliedSearchFilters.offset = 0;
        this.sales[this.segmentValue].appliedSearchFilters.q = searchInput.detail.value;
        this.loadData();
    }

    onClear(): void {
        const currentUrl = this.#router.url.split('?')[0]; // Obtén solo la URL base
        this.#location.replaceState(currentUrl);

        this.sales[this.segmentValue].searchedResults = [];
        this.inputValue = null;
        this.sales[this.segmentValue].appliedSearchFilters.q = null;
        this.loadData();
    }

    async goToFilters(): Promise<void> {
        const listEventFilter = [...salesFilters.filter(
            filter => filter.target === this.segmentValue
        )];
        // The currency filter only appears if it is Multicurrency with more than one currency
        if (this.$currencies() && this.$currencies().length > 1) {
            listEventFilter.unshift({
                target: this.segmentValue,
                key: 'currency',
                filterName: 'CURRENCY.CURRENCY',
                filterType: 'picker',
                filterTitle: 'CURRENCY.SELECT-CURRENCY',
                filterplaceHolder: 'CURRENCY.FIND-CURRENCY-PLACEHOLDER',
                filterOptions: [],
                value: [],
                isMultiple: false
            });
        }
        const modal = await this.#modalCtrl.create({
            component: FiltersComponent,
            componentProps: {
                target: this.segmentValue,
                listOfFilters: listEventFilter,
                appliedParams: this.sales[this.segmentValue].appliedSearchFilters
            }
        });
        await modal.present();
        const { data, role } = await modal.onWillDismiss();

        if (role === 'confirm') {
            let currencyFilter: string;
            if (data.filters.currency) {
                currencyFilter = data.filters.currency;
                delete data.filters.currency;
            }
            this.sales[this.segmentValue].appliedSearchFilters = {
                ...this.#defaultFilters,
                q: this.sales[this.segmentValue].appliedSearchFilters.q,
                purchase_date_from: this.sales[this.segmentValue].appliedSearchFilters.purchase_date_from,
                purchase_date_to: this.sales[this.segmentValue].appliedSearchFilters.purchase_date_to,
                ...data.filters
            };
            if (currencyFilter) {
                this.sales[this.segmentValue].appliedSearchFilters.currency_code = currencyFilter;
            } else {
                delete this.sales[this.segmentValue].appliedSearchFilters.currency_code;
            }
            this.loadData();
        }
    }

    goToCalendar(): void {
        this.calendarModalIsOpen = true;
    }

    closeCalendar(): void {
        this.calendarModalIsOpen = false;
    }

    goToProfile(): void {
        this.#router.navigate(['/profile']);
    }

    removeBubbleFilter = (key: string): void => {
        if (key === 'purchase_date') {
            this.sales[this.segmentValue].appliedSearchFilters.purchase_date_from = null;
            this.sales[this.segmentValue].appliedSearchFilters.purchase_date_to = null;
        } else {
            this.sales[this.segmentValue].appliedSearchFilters[key] = null;
        }
        this.sales[this.segmentValue].appliedSearchFilters.offset = 0;
        this.loadData();
    };

    updateFiltersCalendar = (newTimes: SelectedTime): void => {
        if (newTimes.timeFrom && newTimes.timeTo) {
            this.sales[this.segmentValue].searchedResults = [];
            this.sales[this.segmentValue].appliedSearchFilters.offset = 0;
            this.sales[this.segmentValue].appliedSearchFilters.purchase_date_from = moment(newTimes.timeFrom).toJSON();
            this.sales[this.segmentValue].appliedSearchFilters.purchase_date_to = moment(newTimes.timeTo).toJSON();
            this.loadData();
        }
    };

    onSegmentValueChange(e?: CustomEvent): void {
        this.segmentValue = e?.detail.value || this.segmentValue;
        this.restartScrollOfKpis();
        this.initSales();
        this.loadData();
        this.#router.navigate([], {
            queryParams: {},
            replaceUrl: true
        });
    }

    private loadData(): void {
        // If the operator is monocurrency -> we load the screen with the agregatedData
        //TODO: remove check monocurrency when all operators are multicurrency
        if (!this.$currencies() && this.$user()) {
            this.sales[this.segmentValue].appliedSearchFilters.currency_code = this.$user().currency;
        }
        // If the operator is multicurrency with one currency -> we load the screen with the agregatedData
        if (this.$currencies() && this.$currencies().length === 1) {
            this.sales[this.segmentValue].appliedSearchFilters.currency_code = this.$currencies()[0].code;
        }
        this.loadListOfFilters();
        const requestFields = {
            fields: ordersListFields
        };

        if (this.segmentValue === 'tickets') {
            if (this.sales[this.segmentValue].appliedSearchFilters.currency_code) {
                this.#ticketsBaseSrv.currencyAggregatedData.load({
                    ...this.sales[this.segmentValue].appliedSearchFilters,
                    currency_code: this.sales[this.segmentValue].appliedSearchFilters.currency_code,
                    state: this.sales[this.segmentValue].appliedSearchFilters.state ?
                        this.sales[this.segmentValue].appliedSearchFilters.state : TicketState.purchase
                });
                this.#ticketsBaseSrv.ticketList.load({
                    ...this.sales[this.segmentValue].appliedSearchFilters,
                    currency_code: this.sales[this.segmentValue].appliedSearchFilters.currency_code,
                    state: this.sales[this.segmentValue].appliedSearchFilters.state ?
                        this.sales[this.segmentValue].appliedSearchFilters.state : TicketState.purchase
                });
            } else {
                this.#ticketsBaseSrv.ticketList.load({
                    ...this.sales[this.segmentValue].appliedSearchFilters,
                    state: this.sales[this.segmentValue].appliedSearchFilters.state ?
                        this.sales[this.segmentValue].appliedSearchFilters.state : TicketState.purchase
                });
            }
        }
        if (this.segmentValue === 'transactions') {
            if (this.sales[this.segmentValue].appliedSearchFilters.currency_code) {
                this.#ordersBaseSrv.currencyAggregatedData.load({
                    ...this.sales[this.segmentValue].appliedSearchFilters,
                    currency_code: this.sales[this.segmentValue].appliedSearchFilters.currency_code
                });
                this.#ordersBaseSrv.loadOrdersWithFieldsList({
                    ...this.sales[this.segmentValue].appliedSearchFilters,
                    currency_code: this.sales[this.segmentValue].appliedSearchFilters.currency_code
                }, requestFields);
            } else {
                this.#ordersBaseSrv.loadOrdersWithFieldsList(this.sales[this.segmentValue].appliedSearchFilters, requestFields);
            }
        }

        this.#location.replaceState(this.#router.url.split('?')[0]);
    }

    private loadKpis(kpiData: AggregatedMetric): void {
        const kpiNames = KpiTicketNames && KpiTransactionNames;
        const kpis = [];
        const kpiAmounts = {
            [kpiNames.products]: kpiData.aggData['totalTickets'] ? kpiData.aggData['totalTickets'].value : kpiData.aggData['totalProducts'].value,
            [kpiNames.basePrice]: kpiData.aggData['totalBasePrice'].value,
            [kpiNames.discountsAndPromotions]: kpiData.aggData['totalPromotions'].value,
            [kpiNames.recharges]: kpiData.aggData['totalCharges'].value,
            [kpiNames.total]: kpiData.aggData['totalFinalPrice'].value
        };

        if (this.segmentValue === 'transactions') {
            kpiAmounts[KpiTransactionNames.transactions] = kpiData.aggData['totalOperations'].value;
            kpiAmounts[KpiTransactionNames.paymentMethodCharges] = kpiData.aggData['paymentMethodCharges'].value;
        }

        for (const kpiName in kpiNames) {
            if (kpiAmounts[kpiName] !== undefined) {
                kpis.push({
                    name: kpiName as keyof typeof kpiNames,
                    amount: kpiAmounts[kpiName]
                });
            }
        }
        this.sales[this.segmentValue].kpis = kpis;
    }

    private loadListOfFilters(): void {
        this.listOfFilterBubbles = [];
        const appliedSearchFilters = this.sales[this.segmentValue].appliedSearchFilters;

        if (appliedSearchFilters.purchase_date_from && appliedSearchFilters.purchase_date_to) {
            const filterBubble = {
                name: 'purchase_date',
                label: `${moment(appliedSearchFilters.purchase_date_from).format('DD/MM/YYYY - HH:mm')}
                - ${moment(appliedSearchFilters.purchase_date_to).format('DD/MM/YYYY - HH:mm')}`
            };
            this.listOfFilterBubbles.push(filterBubble);
        }
        // We only add the filter on the screen if it is multicurrency with more than one currency
        if (appliedSearchFilters.currency_code && (this.$currencies() && this.$currencies().length > 1)) {
            const filterBubble = {
                name: 'currency_code',
                label: this.#translateService.instant('FILTERS.BUBBLES.CURRENCY',
                    { value: this.#i18nSrv.getCurrencyPartialTranslation(appliedSearchFilters.currency_code) }),
                value: appliedSearchFilters.currency_code
            };
            this.listOfFilterBubbles.push(filterBubble);
        }

        if (appliedSearchFilters.state) {
            const filterBubble = {
                name: 'state',
                label: this.#translateService.instant('FILTERS.BUBBLES.STATE', {
                    value: this.#translateService.instant('FILTERS.TICKET-STATE.' + appliedSearchFilters.state)
                }),
                value: appliedSearchFilters.state
            };
            this.listOfFilterBubbles.push(filterBubble);
        }

        if (appliedSearchFilters.print) {
            const filterBubble = {
                name: 'print',
                label: this.#translateService.instant('FILTERS.BUBBLES.PRINT', {
                    value: this.#translateService.instant('FILTERS.' + appliedSearchFilters.print)
                }),
                value: appliedSearchFilters.state
            };
            this.listOfFilterBubbles.push(filterBubble);
        }

        for (const key in appliedSearchFilters) {
            const omittedKeys = [
                'q',
                'limit',
                'offset',
                'aggs',
                'purchase_date_from',
                'purchase_date_to',
                'state',
                'sort',
                'print',
                'currency_code'
            ];

            if (!omittedKeys.includes(key) && Object.prototype.hasOwnProperty.call(appliedSearchFilters, key)
                && appliedSearchFilters[key]) {
                let textOfValues: string;

                if (Array.isArray(appliedSearchFilters[key])) {
                    textOfValues = this.#translateService.instant('FILTERS.SELECTED-FILTERS',
                        { number: appliedSearchFilters[key].length }
                    );
                } else {
                    textOfValues = appliedSearchFilters[key];
                }

                const filterBubble = {
                    name: key,
                    label: this.#translateService.instant('FILTERS.BUBBLES.' + key.toUpperCase(), { value: textOfValues })
                };
                this.listOfFilterBubbles.push(filterBubble);
            }
        }
    }

    private restartScrollOfKpis(): void {
        const kpis = document.querySelector('.sales-page__kpis');
        if (kpis) {
            document.querySelector('.sales-page__kpis').scrollTo(0, 0);
        }
    }

    private prepareEventData(items: OrderWithFieldsItem[]): string {
        let eventData = '';

        //Prevent event name duplicates
        const eventNamesSet = new Set<string>();
        const eventNames = items.reduce<string[]>((acc, item) => {
            const eventName = item?.ticket?.allocation?.event?.name;
            if (eventName && !eventNamesSet.has(eventName)) {
                eventNamesSet.add(eventName);
                acc.push(eventName);
            }
            return acc;
        }, []);

        //Build content
        eventData = eventNames.join(', ');
        return eventData;
    }
}
