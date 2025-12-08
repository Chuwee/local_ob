import { GetOrdersWithFieldsRequest, OrdersService, ordersProviders } from '@admin-clients/cpanel-sales-data-access';
import {
    GetTicketsRequest,
    TicketState,
    TicketsBaseService, ticketsBaseProviders
} from '@admin-clients/shared/common/data-access';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, OnDestroy, signal, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import moment from 'moment';
import { filter, map } from 'rxjs';
import { AuthService } from '../../modules/auth/services/auth.service';
import { SalesOption } from './models/weekly-sales-detail.model';

@Component({
    selector: 'weekly-sales-detail',
    templateUrl: './weekly-sales-detail.page.html',
    styleUrls: ['./weekly-sales-detail.page.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ordersProviders, ticketsBaseProviders, DateTimePipe, UpperCasePipe],
    standalone: false
})
export class WeeklySalesDetailPage implements OnInit, OnDestroy {
    readonly #ordersService = inject(OrdersService);
    readonly #ticketsBaseService = inject(TicketsBaseService);
    readonly #authService = inject(AuthService);
    readonly $user = toSignal(this.#authService.getLoggedUser$());
    readonly $salesOption = signal('tickets');
    readonly $ticketListAggregatedData = toSignal(this.#ticketsBaseService.weeklyticketListAggregatedData.get$().pipe(
        filter(Boolean),
        map(weekAggregates => weekAggregates.map(wA => ({
            value: wA.aggData.aggregated_data.overall[0].value,
            indexOfWeek: wA.weekDay,
            isToday: this.today.isSame(this.referenceDay, 'week') && wA.weekDay === this.today.clone().day()
        })).reverse())));

    readonly $currencyAggregatedData = toSignal(this.#ordersService.weeklyCurrencyAggregatedData.get$().pipe(
        filter(Boolean),
        map(weekAggregates => weekAggregates.map(wA => ({
            value: wA.aggData.overall.aggData['totalFinalPrice'].value,
            indexOfWeek: wA.weekDay,
            isToday: this.today.isSame(this.referenceDay, 'week') && wA.weekDay === this.today.clone().day()
        })).reverse())));

    readonly $weeklyChartData = computed(() => {
        if (this.$salesOption() === 'tickets') {
            return this.$ticketListAggregatedData();
        } else {
            return this.$currencyAggregatedData();
        }
    });

    readonly $weeklyTotal = computed(() => this.$weeklyChartData().reduce((acc, day) => acc + day.value, 0));
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#ordersService.weeklyCurrencyAggregatedData.loading$(),
        this.#ticketsBaseService.weeklyticketListAggregatedData.loading$()
    ]));

    readonly today = moment();

    referenceDay = this.today.clone();
    isDateSelectorOpen = false;
    defaultCurrencyUser: string;
    selectedTime: {
        startDate?: string;
        endDate?: string;
    };

    ngOnInit(): void {
        this.loadWeek(this.referenceDay);
    }

    ngOnDestroy(): void {
        this.#ticketsBaseService.weeklyticketListAggregatedData.clear();
        this.#ordersService.weeklyCurrencyAggregatedData.clear();
    }

    addWeek(): void {
        this.referenceDay.add(1, 'week');
        this.loadWeek(this.referenceDay);
    }

    subtractWeek(): void {
        this.referenceDay.subtract(1, 'week');
        this.loadWeek(this.referenceDay);
    }

    loadWeek(referenceDay: moment.Moment): void {
        this.setChips(referenceDay);
        const lastDayOfWeek = referenceDay.clone().endOf('week');
        this.defaultCurrencyUser = sessionStorage.getItem('currency');
        // Get the currency selected by the user on the home screen, if it does not exist, 
        // get the operator's default, if it does not exist (Monocurrency case), get the user's currency
        // TODO: When all operators are multicurrency, it will no longer be necessary to obtain it from the user.
        this.defaultCurrencyUser = this.defaultCurrencyUser ?? this.$user().operator.currencies?.default_currency ?? this.$user().currency;
        const request = { currency_code: this.defaultCurrencyUser };
        if (this.$salesOption() === 'tickets') {
            const appliedSearchFilters = {
                limit: 0,
                offset: 0,
                aggs: true,
                sort: 'purchase_date:desc',
                purchase_date_from: moment().startOf('day').toJSON(),
                purchase_date_to: moment().endOf('day').toJSON(),
                state: TicketState.purchase,
                currency_code: this.defaultCurrencyUser
            } as GetTicketsRequest & GetOrdersWithFieldsRequest;
            this.#ticketsBaseService.weeklyticketListAggregatedData.load(appliedSearchFilters,
                referenceDay.clone().day() === 0 ? referenceDay.clone() : lastDayOfWeek);
        } else {
            this.#ordersService.weeklyCurrencyAggregatedData.load(
                request,
                referenceDay.clone().day() === 0 ? referenceDay.clone() : lastDayOfWeek
            );
        }

    }

    selectSaleOption(selectedValue: SalesOption): void {
        if (selectedValue !== this.$salesOption()) {
            this.$salesOption.set(selectedValue);
            this.referenceDay = this.today.clone();
            this.loadWeek(this.referenceDay);
        }
    }

    getDayName(index: number): string {
        return this.today.clone().locale('en').day(index).format('dddd');
    }

    getCompleteFormattedDate(chipTime): string {
        return moment(chipTime).format('DD/MM/YYYY');
    }

    loadWeekFromMonthSelector(time: string): void {
        this.referenceDay = moment(time);
        this.loadWeek(this.referenceDay);
    }

    openDateSelector(): void {
        this.isDateSelectorOpen = true;
    }

    closeDateSelector(): void {
        this.isDateSelectorOpen = false;
    }

    private setChips(time: moment.Moment): void {
        this.selectedTime = {
            startDate: time.clone().startOf('week').toJSON(),
            endDate: time.clone().endOf('week').toJSON()
        };
    }
}
