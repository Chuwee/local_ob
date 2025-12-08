import { User } from '@admin-clients/cpanel/core/data-access';
import { Event, EventsService, eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import moment from 'moment';
import { filter, map, of, shareReplay, startWith, switchMap } from 'rxjs';
import { PickerDataItem } from '../../core/components/picker/models/pickerData';
import { DeviceStorage } from '../../core/services/deviceStorage';
import { TrackingService } from '../../core/services/tracking.service';
import { getInitials } from '../../helpers/string.utils';
import { AuthService } from '../../modules/auth/services/auth.service';
import { roleEvent, roleSales } from '../../modules/auth/services/role-constant';

@Component({
    selector: 'home-page',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss'],
    providers: [eventsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class HomeComponent implements OnDestroy {
    readonly #authService = inject(AuthService);
    readonly #ordersService = inject(OrdersService);
    readonly #deviceStorage = inject(DeviceStorage);
    readonly #eventsSrv = inject(EventsService);
    readonly #router = inject(Router);
    readonly #tracking = inject(TrackingService);
    readonly #i18nSrv = inject(I18nService);
    readonly $user = toSignal(this.#authService.getLoggedUser$());
    readonly $entity = toSignal(this.#authService.getLoggedUser$().pipe(filter(Boolean),
        map(user => user.entity.name),
        shareReplay({ refCount: true, bufferSize: 1 })));

    // Indicates whether there have been sales in the last 7 days in the case of multicurrency with more than one currency
    readonly $isNoSales = signal(false);
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#ordersService.weeklyCurrencyAggregatedData.loading$(),
        this.#ordersService.isOrdersListLoading$(),
        this.#authService.isLoggedUserLoading$(),
        toObservable(this.$isNoSales)
    ]));

    readonly chartData$ = this.#ordersService.weeklyCurrencyAggregatedData.get$()
        .pipe(
            filter(Boolean),
            map(weekAggregates => weekAggregates.map(wA => ({
                value: wA.aggData.overall.aggData['totalFinalPrice'].value,
                indexOfWeek: wA.weekDay,
                isToday: wA.weekDay === moment().clone().day()
            })).reverse()),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly todaySales$ = this.chartData$.pipe(map(weekAggregates => weekAggregates[weekAggregates.length - 1].value));
    readonly yesterdaySales$ = this.chartData$.pipe(map(weekAggregates => weekAggregates[weekAggregates.length - 2].value));
    readonly yesterdayTickets$ = this.#ordersService.getOrdersListAggregatedData$().pipe(
        filter(Boolean),
        map(aggregatedData => aggregatedData.overall.aggData['totalTickets'].value)
    );

    readonly events$ = this.#authService.getLoggedUser$().pipe(filter(Boolean), switchMap(user => {
        if (AuthService.isSomeRoleInUserRoles(user, roleEvent)) {
            return this.#deviceStorage.getItem('last_events').pipe(
                switchMap(eventIds => {
                    if (Array.isArray(eventIds)) {
                        return this.#eventsSrv.getEvents$(eventIds);
                    } else {
                        return of([]);
                    }
                }),
                startWith(null)
            );
        } else {
            return of([]);
        }
    }), shareReplay({ bufferSize: 1, refCount: true }));

    readonly user$ = this.#authService.getLoggedUser$().pipe(filter(Boolean)).subscribe((user: User) => {
        this.initials = (getInitials(user.name, user.last_name));
        this.checkRoleSales(user);
        this.ckeckRoleEvents(user);
    });

    // List of currencies with sales in the last 7 days
    readonly currencies$ = this.#ordersService.filterCurrencyList.getData$().pipe(filter(Boolean)).subscribe(currencies => {
        this.checkCurrencyDefault(currencies, this.$user());
    });

    isRoleSales: boolean;
    isRoleEvent: boolean;
    // Indicates if it is multicurrency with more than one currency to display the currency selector
    isMultiCurrency: boolean;
    initials: string;
    currencyRequest: string;
    currenciesPicker: PickerDataItem[] = [];

    checkRoleSales(user: User): void {
        if (AuthService.isSomeRoleInUserRoles(user, roleSales)) {
            this.isRoleSales = true;
            if (user?.operator?.currencies?.selected.map(currency => currency.code).length > 0) {
                this.#ordersService.filterCurrencyList.load({
                    purchase_date_from: moment().subtract(6, 'd').startOf('day').toJSON(),
                    purchase_date_to: moment().endOf('day').toJSON(),
                    limit: 200
                });
            } else {
                this.isMultiCurrency = false;
                this.$isNoSales.set(false);
                this.currencyRequest = user.currency;
                this.loadDataSales();
            }
        } else {
            this.isRoleSales = false;
        }
    }

    loadDataSales(): void {
        sessionStorage.setItem('currency', this.currencyRequest);
        this.#ordersService.weeklyCurrencyAggregatedData.load({ currency_code: this.currencyRequest }, moment());
        this.#ordersService.loadOrdersList({
            limit: 1,
            aggs: true,
            currency_code: this.currencyRequest,
            purchase_date_from: moment().subtract(1, 'days').startOf('day').toJSON(),
            purchase_date_to: moment().subtract(1, 'days').endOf('day').toJSON()
        });
    }

    checkCurrencyDefault(currencies: FilterOption[], user: User): void {
        const defaultCurrency = user?.operator?.currencies?.default_currency;
        // If it is multicurrency with more than one currency
        // see if the default currency is in the array of sales in the last 7 days or get the first one in the array by default
        if (currencies?.length > 1) {
            this.isMultiCurrency = true;
            this.$isNoSales.set(false);
            // Check if there is already a default currency in session
            const defaultCurrencyUser = sessionStorage.getItem('currency');
            if (!!!defaultCurrencyUser) {
                const defaultOperator = currencies.find(condCurrency => condCurrency.id === defaultCurrency);
                if (defaultOperator) {
                    this.currencyRequest = defaultCurrency;
                } else {
                    this.currencyRequest = currencies[0].id;
                }
            } else {
                this.currencyRequest = defaultCurrencyUser;
            }
        } else {
            // Case multiCurrency and no sales last 7 years
            if (user?.operator?.currencies?.selected.length > 1 && currencies.length === 0) {
                this.$isNoSales.set(true);
            } else {
                // Case MultiCurrency but only 1 currency we treat it as if it is monocurrency
                this.$isNoSales.set(false);
                this.isMultiCurrency = false;
                this.currencyRequest = currencies.length > 0 ? currencies[0].id : user.currency;
            }
        }
        if (!this.$isNoSales()) {
            this.currenciesPicker = currencies.map(price => ({
                label: this.#i18nSrv.getCurrencyPartialTranslation(price.id),
                value: price.id,
                isSelected: price.id === this.currencyRequest
            }));
            this.loadDataSales();
        }
    }

    ckeckRoleEvents(user: User): void {
        if (AuthService.isSomeRoleInUserRoles(user, roleEvent)) {
            this.#tracking.info('Home In Role Event');
            this.isRoleEvent = true;
        } else {
            this.isRoleEvent = false;
        }
    }

    ngOnDestroy(): void {
        this.#ordersService.weeklyCurrencyAggregatedData.clear();
        this.#ordersService.clearOrdersList();
        this.#ordersService.filterCurrencyList.clear();
    }

    goToEventDetail(event: Event): void {
        this.#router.navigate(['event-detail', event.id]);
    }

    goToTransactions(segment: string, filter: string): void {
        this.#router.navigate([`/tabs/sales`], { queryParams: { filter, currency: this.currencyRequest, segment } });
    }

    goToProfile(): void {
        this.#router.navigate(['/profile']);
    }

    goToWeeklySalesDetail(): void {
        sessionStorage.setItem('currency', this.currencyRequest);
        this.#router.navigate(['/weekly-sales-detail']);
    }

    selectCurrency(currency: string): void {
        this.currencyRequest = currency;
        this.loadDataSales();
    }
}
