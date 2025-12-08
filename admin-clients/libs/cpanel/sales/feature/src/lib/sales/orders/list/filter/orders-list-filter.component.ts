import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { OrderDeliveryMethodTypes, GetFilterRequest, OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { OrderType } from '@admin-clients/shared/common/data-access';
import {
    FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue, ContextNotificationComponent,
    SelectServerSearchComponent, DateTimePickerComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode, DateTimeFormats, FilterOption } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { applyAsyncFieldWithServerReq$, deepEqual } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { filter, forkJoin, mapTo, Observable, tap } from 'rxjs';
import { debounceTime, map, pairwise, startWith } from 'rxjs/operators';
import { CurrencySalesFilterComponent, FILTER_CURRENCY_LIST } from '../../../filter-currency-list/filter-currency-list.component';

const FORM_FIELDS = {
    type: 'type',
    channel: 'channel',
    channelEntity: 'channelEntity',
    event: 'event',
    eventEntity: 'eventEntity',
    session: 'session',
    merchant: 'merchant',
    user: 'user',
    client: 'client',
    delivery: 'delivery',
    orderAlive: 'orderAlive',
    reallocationRefund: 'reallocationRefund',
    sessionDateFrom: 'sessionDateFrom',
    sessionDateTo: 'sessionDateTo',
    currency: 'currency'
} as const satisfies Record<string, string>;

type FormFieldKey = keyof typeof FORM_FIELDS;

@Component({
    selector: 'app-orders-list-filter',
    templateUrl: './orders-list-filter.component.html',
    styleUrls: ['./orders-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    viewProviders: [
        {
            provide: FILTER_CURRENCY_LIST, useFactory: () => inject(OrdersService).filterCurrencyList
        }
    ],
    imports: [
        AsyncPipe, ReactiveFormsModule, TranslatePipe, MaterialModule, ContextNotificationComponent,
        DateTimePickerComponent, SelectServerSearchComponent, DateTimePipe, CurrencySalesFilterComponent
    ]
})
export class OrdersListFilterComponent extends FilterWrapped implements OnInit, OnDestroy {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translate = inject(TranslateService);
    readonly #authService = inject(AuthenticationService);
    readonly #ordersService = inject(OrdersService);
    readonly #i18nSrv = inject(I18nService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #formStructure: Record<FormFieldKey, unknown> = {
        type: null,
        channel: [],
        channelEntity: null,
        event: [],
        eventEntity: null,
        session: [],
        merchant: null,
        user: null,
        client: [],
        delivery: null,
        orderAlive: false,
        reallocationRefund: false,
        sessionDateFrom: null,
        sessionDateTo: null,
        currency: null
    };

    readonly #selectListLimit = 100;

    readonly formControlNames = FORM_FIELDS;
    readonly dateTimeFormats = DateTimeFormats;
    readonly typesList = Object.values(OrderType).filter(value => value !== OrderType.secMktPurchase)
        .map(type => ({ id: type, name: `ENUMS.TYPE_OPTS.${type}` }));

    readonly deliveryMethods = Object.values(OrderDeliveryMethodTypes)
        .map(deliveryMethod => ({ id: deliveryMethod, name: `ENUMS.DELIVERY_OPTS.${deliveryMethod}` }));

    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
    readonly $filtersFormValueChanges = toSignal(this.filtersForm.valueChanges, { initialValue: this.filtersForm.value });
    readonly isOperatorMode$ = this.#authService.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly isEntityAdmin$ = this.#authService.hasLoggedUserSomeEntityType$(['ENTITY_ADMIN']);
    // channel entities
    readonly channelEntities$ = this.#ordersService.getFilterChannelEntityListData$();
    readonly moreChannelEntitiesAvailable$ = this.#ordersService.getFilterChannelEntityListMetadata$().pipe(map(md => !!md?.next_cursor));
    // event entities
    readonly eventEntities$ = this.#ordersService.getFilterEventEntityListData$();
    readonly moreEventEntitiesAvailable$ = this.#ordersService.getFilterEventEntityListMetadata$().pipe(map(md => !!md?.next_cursor));
    // channels
    readonly channels$ = this.#ordersService.getFilterChannelListData$();
    readonly moreChannelsAvailable$ = this.#ordersService.getFilterChannelListMetadata$().pipe(map(md => !!md?.next_cursor));
    // events
    readonly events$ = this.#ordersService.getFilterEventListData$();
    readonly moreEventsAvailable$ = this.#ordersService.getFilterEventListMetadata$().pipe(map(md => !!md?.next_cursor));
    // sessions
    readonly sessions$ = this.#ordersService.getFilterSessionListData$();
    readonly moreSessionsAvailable$ = this.#ordersService.getFilterSessionListMetadata$().pipe(map(md => !!md?.next_cursor));
    // merchants
    readonly merchants$ = this.#ordersService.getFilterMerchantListData$();
    readonly moreMerchantsAvailable$ = this.#ordersService.getFilterMerchantListMetadata$().pipe(map(md => !!md?.next_cursor));
    // users
    readonly users$ = this.#ordersService.getFilterUserListData$();
    readonly moreUsersAvailable$ = this.#ordersService.getFilterUserListMetadata$().pipe(map(md => !!md?.next_cursor));
    //currencies
    readonly $areCurrenciesShown = toSignal(this.#authService.getLoggedUser$().pipe(map(user =>
        AuthenticationService.operatorCurrencyCodes(user)?.length > 1)));

    readonly $showOrderAlive = computed(() => this.#hasOrderType([OrderType.purchase, OrderType.booking, OrderType.seatReallocation]));
    readonly $showReallocationRefund = computed(() => this.#hasOrderType([OrderType.refund]));

    readonly $showCheckboxesRow = computed(() =>
        this.$showOrderAlive() || this.$showReallocationRefund()
    );

    // professional clients
    readonly clients$ = this.#ordersService.getFilterClientListData$();
    readonly moreClientsAvailable$ = this.#ordersService.getFilterClientListMetadata$().pipe(map(md => !!md?.next_cursor));

    readonly compareWith = compareWithIdOrCode;

    @Input() startDate: string;
    @Input() endDate: string;

    ngOnInit(): void {
        this.filtersForm.get(FORM_FIELDS.session).disable();
        this.filtersForm.get(FORM_FIELDS.sessionDateTo).disable();
        this.#getFormFieldObs(FORM_FIELDS.currency).subscribe(() => this.#entityAndCurrencyChangeHandler());
        this.#getFormFieldObs(FORM_FIELDS.channelEntity).subscribe(() => this.#entityAndCurrencyChangeHandler());
        this.#getFormFieldObs(FORM_FIELDS.eventEntity).subscribe(() => this.#entityAndCurrencyChangeHandler());
        this.#getFormFieldObs(FORM_FIELDS.channel).subscribe(() => this.#channelChangeHandler());
        this.#getFormFieldObs(FORM_FIELDS.event).subscribe(() => this.#eventChangeHandler());
        // session select and session to-from dates logic
        this.filtersForm.get(FORM_FIELDS.session).valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(selecctedSessions => {
                if (Array.isArray(selecctedSessions) && selecctedSessions?.length) {
                    this.filtersForm.get(FORM_FIELDS.sessionDateFrom).disable({ emitEvent: false });
                    this.filtersForm.get(FORM_FIELDS.sessionDateTo).disable({ emitEvent: false });
                } else {
                    this.filtersForm.get(FORM_FIELDS.sessionDateFrom).enable();
                }
            });
        this.filtersForm.get(FORM_FIELDS.sessionDateFrom).valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(sessionDateFrom => {
                if (sessionDateFrom) {
                    this.filtersForm.get(FORM_FIELDS.session).disable({ emitEvent: false });
                    this.filtersForm.get(FORM_FIELDS.sessionDateTo).enable({ emitEvent: false });
                    if (!this.filtersForm.get(FORM_FIELDS.sessionDateTo).value) {
                        this.filtersForm.get(FORM_FIELDS.sessionDateTo).setValue(
                            moment(sessionDateFrom).hour(23).minutes(59).format(),
                            { emitEvent: false }
                        );
                    }
                } else {
                    this.filtersForm.get(FORM_FIELDS.sessionDateTo).disable({ emitEvent: false });
                    if (this.filtersForm.get(FORM_FIELDS.event).value?.length) {
                        this.filtersForm.get(FORM_FIELDS.session).enable({ emitEvent: false });
                    }
                }
            });
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#ordersService.clearFilterListsData();
        this.#ordersService.clearFilterListsCache();
    }

    getFilters(): FilterItem[] {
        return [
            this.#getFilterType(),
            this.#getFilterOrderAlive(),
            this.#getFilterReallocationRefund(),
            this.#getFilterChannel(),
            this.#getFilterChannelEntity(),
            this.#getFilterEvent(),
            this.#getFilterEventEntity(),
            this.#getFilterSession(),
            this.#getFilterMerchant(),
            this.#getFilterDelivery(),
            this.#getFilterUser(),
            this.#getFilterClient(),
            this.#getFilterSessionDateFrom(),
            this.#getFilterSessionDateTo(),
            this.#getFilterCurrency()
        ].filter(element => !!element);
    }

    removeFilter(key: string, value: unknown): void {
        switch (key) {
            case 'TYPE':
                this.#resetFilterMulti(FORM_FIELDS.type, value);
                break;
            case 'ORDER_ALIVE':
                this.#resetFilter(FORM_FIELDS.orderAlive);
                break;
            case 'REALLOCATION_REFUND':
                this.#resetFilter(FORM_FIELDS.reallocationRefund);
                break;
            case 'CHANNEL':
                this.#resetFilterMulti(FORM_FIELDS.channel, value);
                break;
            case 'CHANNEL_ENTITY':
                this.#resetFilter(FORM_FIELDS.channelEntity);
                break;
            case 'EVENT':
                this.#resetFilterMulti(FORM_FIELDS.event, value);
                break;
            case 'EVENT_ENTITY':
                this.#resetFilter(FORM_FIELDS.eventEntity);
                break;
            case 'MERCHANT':
                this.#resetFilter(FORM_FIELDS.merchant);
                break;
            case 'DELIVERY':
                this.#resetFilterMulti(FORM_FIELDS.delivery, value);
                break;
            case 'USER':
                this.#resetFilter(FORM_FIELDS.user);
                break;
            case 'PROFESSIONAL_CLIENT':
                this.#resetFilterMulti(FORM_FIELDS.client, value);
                break;
            case 'SESSION':
                this.#resetFilterMulti(FORM_FIELDS.session, value);
                break;
            case 'SESSION_DATE_FROM':
            case 'SESSION_DATE_TO':
                this.#resetFilter(FORM_FIELDS.sessionDateFrom);
                this.#resetFilter(FORM_FIELDS.sessionDateTo);
                break;
            case 'CURRENCY':
                this.#resetFilter(FORM_FIELDS.currency);
                break;
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);

        if (params['startDate'] && params['endDate']) {
            if (params['startDate'] !== this.startDate || params['endDate'] !== this.endDate) {
                this.#ordersService.clearFilterListsData();
                this.startDate = String(params['startDate']);
                this.endDate = String(params['endDate']);
            }
        } else {
            if (this.startDate || this.endDate) {
                this.#ordersService.clearFilterListsData();
            }
            this.startDate = null;
            this.endDate = null;
        }

        const asyncFields: Observable<FilterOption[]>[] = [
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.currency, params, ids => this.#ordersService.filterCurrencyList.getNames$(ids)),
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.channelEntity, params, ids => this.#ordersService.getFilterChannelEntityNames$(ids)),
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.eventEntity, params, ids => this.#ordersService.getFilterEventEntityNames$(ids)),
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.channel, params, ids => this.#ordersService.getFilterChannelNames$(ids), true),
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.event, params, ids => this.#ordersService.getFilterEventNames$(ids), true),
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.session, params, ids => this.#ordersService.getFilterSessionNames$(ids), true),
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.merchant, params, ids => this.#ordersService.getFilterMerchantNames$(ids)),
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.user, params, ids => this.#ordersService.getFilterUserNames$(ids)),
            applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.client, params, ids => this.#ordersService.getFilterClientNames$(ids), true)

        ];
        if (params['event']) {
            this.filtersForm.get(FORM_FIELDS.session).enable({ emitEvent: false });
        }
        if (params['orderType']) {
            formFields.type = params['orderType'].split(',').map(type => this.typesList.find(typeObj => typeObj.id === type));
        }
        if (params['deliveryMethod']) {
            formFields.delivery = params['deliveryMethod'].split(',')
                .map(delivery => this.deliveryMethods.find(deliveryMethod => deliveryMethod.id === delivery));
        }
        if (params['orderAlive']) {
            formFields.orderAlive = params['orderAlive'] === 'true';
        }

        if (params['reallocationRefund']) {
            formFields.reallocationRefund = params['reallocationRefund'] === 'true';
        }

        if (params['sessionDateFrom']) {
            formFields.sessionDateFrom = moment(params['sessionDateFrom']).tz(moment().tz()).format();
        }
        if (params['sessionDateTo']) {
            formFields.sessionDateTo = moment(params['sessionDateTo']).tz(moment().tz()).format();
        }

        return forkJoin(asyncFields).pipe(
            tap(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
            }),
            map(() => this.getFilters())
        );
    }

    // selects data load
    loadChannelEntities(q: string, next: boolean = false): void {
        this.#ordersService.loadFilterChannelEntityList(this.#getBasicFilterRequest(q), next);
    }

    loadEventEntities(q: string, next = false): void {
        this.#ordersService.loadFilterEventEntityList(this.#getBasicFilterRequest(q), next);
    }

    loadChannels(q: string, next: boolean = false): void {
        this.#ordersService.loadFilterChannelList(this.#getFilterRequestWithEntitiesAndCurrency(q), next);
    }

    loadEvents(q: string, next = false): void {
        this.#ordersService.loadFilterEventList(this.#getFilterRequestWithChannel(q), next);
    }

    loadSessions(q: string, next = false): void {
        this.#ordersService.loadFilterSessionList(this.#getFilterRequestWithEvent(q), next);
    }

    loadMerchants(q: string, next = false): void {
        this.#ordersService.loadFilterMerchantList(this.#getFilterRequestWithEvent(q), next);
    }

    loadUsers(q: string, next = false): void {
        this.#ordersService.loadFilterUserList(this.#getFilterRequestWithEvent(q), next);
    }

    loadClients(q: string, next: boolean = false): void {
        this.#ordersService.loadFilterClientList(this.#getFilterRequestWithChannel(q), next);
    }

    // clear session dates

    clearSessionDates(): void {
        this.filtersForm.get(FORM_FIELDS.sessionDateFrom).setValue(null);
        this.filtersForm.get(FORM_FIELDS.sessionDateTo).setValue(null);
    }

    #getFormFieldObs<T>(field: FormFieldKey, startValue: T = undefined): Observable<T> {
        return this.filtersForm.get(field).valueChanges
            .pipe(
                debounceTime(100),
                startWith(startValue),
                pairwise(),
                filter(([prev, next]) => !deepEqual(prev, next)),
                mapTo(null),
                takeUntilDestroyed(this.#destroyRef)
            );
    }

    #entityAndCurrencyChangeHandler(): void {
        this.filtersForm.get(FORM_FIELDS.channel).setValue(null, { emitEvent: false });
        this.#ordersService.loadFilterChannelList(this.#getFilterRequestWithEntitiesAndCurrency());
        this.#channelChangeHandler();
    }

    #channelChangeHandler(): void {
        this.filtersForm.get(FORM_FIELDS.event).setValue(null, { emitEvent: false });
        this.filtersForm.get(FORM_FIELDS.client).setValue(null, { emitEvent: false });
        this.#ordersService.loadFilterEventList(this.#getFilterRequestWithChannel());
        this.#ordersService.loadFilterClientList(this.#getFilterRequestWithChannel());
        this.#eventChangeHandler();
    }

    #eventChangeHandler(): void {
        this.filtersForm.get(FORM_FIELDS.merchant).setValue(null, { emitEvent: false });
        this.#ordersService.loadFilterMerchantList(this.#getFilterRequestWithEvent());
        this.filtersForm.get(FORM_FIELDS.user).setValue(null, { emitEvent: false });
        this.#ordersService.loadFilterUserList(this.#getFilterRequestWithEvent());
        if (this.filtersForm.get(FORM_FIELDS.event).value?.length && !this.filtersForm.get(FORM_FIELDS.sessionDateFrom).value) {
            this.filtersForm.get(FORM_FIELDS.session).enable();
            this.filtersForm.get(FORM_FIELDS.session).setValue(null, { emitEvent: false });
            this.#ordersService.loadFilterSessionList(this.#getFilterRequestWithEvent());
        } else {
            this.filtersForm.get(FORM_FIELDS.session).setValue(null, { emitEvent: false });
            this.filtersForm.get(FORM_FIELDS.session).disable();
        }
    }

    // selects data load requests

    #getBasicFilterRequest(q: string = undefined): GetFilterRequest {
        return {
            q,
            purchase_date_from: this.startDate,
            purchase_date_to: this.endDate,
            limit: this.#selectListLimit
        };
    }

    #getFilterRequestWithEntitiesAndCurrency(q: string = undefined): GetFilterRequest {
        return {
            ...this.#getBasicFilterRequest(q),
            event_entity_id: this.filtersForm.get(FORM_FIELDS.eventEntity).value?.id || undefined,
            channel_entity_id: this.filtersForm.get(FORM_FIELDS.channelEntity).value?.id || undefined,
            currency_code: this.filtersForm.get(FORM_FIELDS.currency).value?.id || undefined
        };
    }

    #getFilterRequestWithChannel(q: string = undefined): GetFilterRequest {
        return {
            ...this.#getFilterRequestWithEntitiesAndCurrency(q),
            channel_id: this.filtersForm.get(FORM_FIELDS.channel).value?.filter(i => !!i).map(channel => channel.id).join(',') || undefined
        };
    }

    #getFilterRequestWithEvent(q: string = undefined): GetFilterRequest {
        return {
            ...this.#getFilterRequestWithEntitiesAndCurrency(q),
            event_id: this.filtersForm.get(FORM_FIELDS.event).value?.filter(i => !!i).map(event => event.id).join(',') || undefined
        };
    }

    // filter items

    #resetFilterMulti(formKey: FormFieldKey, value: unknown): void {
        const form = this.filtersForm.get(formKey);
        const values: { id: string }[] = form.value;
        form.reset(values.filter(type => type.id !== value));
    }

    #resetFilter(formKey: FormFieldKey): void {
        this.filtersForm.get(formKey).reset();
    }

    #getFilterType(): FilterItem {
        const filterItem = new FilterItem('TYPE', this.#translate.instant('ORDER.TYPE'));
        const value = this.filtersForm.value.type;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translate.instant(valueItem.name)));
            filterItem.urlQueryParams['orderType'] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

    #getFilterOrderAlive(): FilterItem {
        const filterItem = new FilterItem('ORDER_ALIVE', this.#translate.instant('ORDER.ORDER_ALIVE'));
        const value = this.filtersForm.value.orderAlive;
        if (value && this.$showOrderAlive()) {
            filterItem.values = [new FilterItemValue(value, null)];
            filterItem.urlQueryParams['orderAlive'] = value;
        }
        return filterItem;
    }

    #getFilterReallocationRefund(): FilterItem {
        const filterItem = new FilterItem('REALLOCATION_REFUND', this.#translate.instant('ORDER.REFUND_FOR_REALLOCATE'));
        const value = this.filtersForm.value.reallocationRefund;
        if (value && this.$showReallocationRefund()) {
            filterItem.values = [new FilterItemValue(value, null)];
            filterItem.urlQueryParams['reallocationRefund'] = value;
        }
        return filterItem;
    }

    #getFilterChannel(): FilterItem {
        const filterItem = new FilterItem('CHANNEL', this.#translate.instant('ORDER.CHANNEL'));
        return this.#getFilterMulti(filterItem, FORM_FIELDS.channel);
    }

    #getFilterSession(): FilterItem {
        const filterItem = new FilterItem('SESSION', this.#translate.instant('ORDER.SESSION'));
        return this.#getFilterMulti(filterItem, FORM_FIELDS.session);
    }

    #getFilterEvent(): FilterItem {
        const filterItem = new FilterItem('EVENT', this.#translate.instant('ORDER.EVENT'));
        return this.#getFilterMulti(filterItem, FORM_FIELDS.event);
    }

    #getFilterChannelEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('CHANNEL_ENTITY')
            .labelKey('ORDER.ENTITY')
            .queryParam('channelEntity')
            .value(this.filtersForm.value.channelEntity)
            .build();
    }

    #getFilterMerchant(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('MERCHANT')
            .labelKey('ORDER.MERCHANT')
            .queryParam('merchant')
            .value(this.filtersForm.value.merchant)
            .build();
    }

    #getFilterDelivery(): FilterItem {
        const filterItem = new FilterItem('DELIVERY', this.#translate.instant('ORDER.DELIVERY_METHOD'));
        const value = this.filtersForm.value.delivery;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translate.instant(valueItem.name)));
            filterItem.urlQueryParams['deliveryMethod'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    #getFilterUser(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('USER')
            .labelKey('ORDER.USER')
            .queryParam('user')
            .value(this.filtersForm.value.user)
            .build();
    }

    #getFilterClient(): FilterItem {
        const filterItem = new FilterItem('PROFESSIONAL_CLIENT', this.#translate.instant('FORMS.LABELS.PROFESSIONAL_CLIENT'));
        return this.#getFilterMulti(filterItem, FORM_FIELDS.client);
    }

    #getFilterEventEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('EVENT_ENTITY')
            .labelKey('ORDER.EVENT_ENTITY')
            .queryParam('eventEntity')
            .value(this.filtersForm.value.eventEntity)
            .build();
    }

    #getFilterCurrency(): FilterItem {
        const value = this.filtersForm.value.currency ?
            {
                ...this.filtersForm.value.currency,
                name: this.#i18nSrv.getCurrencyPartialTranslation(this.filtersForm.value.currency?.name)
            } :
            null;
        const filterItem = new FilterItemBuilder(this.#translate)
            .key('CURRENCY')
            .labelKey('FORMS.LABELS.CURRENCY')
            .queryParam(FORM_FIELDS.currency)
            .value(value)
            .build();

        // If currency is removed and currencies are shown, add a flag to maintain user choice
        if (!value && this.$areCurrenciesShown()) {
            filterItem.urlQueryParams['noCurrency'] = 'true';
        }

        return filterItem;
    }

    #getFilterMulti(filterItem: FilterItem, formKey: FormFieldKey): FilterItem {
        const value = this.filtersForm.get(formKey).value as [{ id: string; date_start?: string; name: string }];
        if (value && value.length > 0) {
            filterItem.values = value.filter(i => !!i).map(valueItem => {
                if (valueItem.date_start) {
                    const startDate = moment(valueItem.date_start).format(this.dateTimeFormats.shortDateTime);
                    return new FilterItemValue(valueItem.id, `${startDate} - ${valueItem.name}`);
                } else {
                    return new FilterItemValue(valueItem.id, valueItem.name);
                }
            });
            filterItem.urlQueryParams[formKey] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

    #getFilterSessionDateFrom(): FilterItem {
        const filterItem = new FilterItem('SESSION_DATE_FROM', this.#translate.instant('TICKET.SESSION_DATE_FROM'));
        const value = this.filtersForm.value.sessionDateFrom;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this.dateTimeFormats.shortDateTime);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['sessionDateFrom'] = valueId;
        }
        return filterItem;
    }

    #getFilterSessionDateTo(): FilterItem {
        const filterItem = new FilterItem('SESSION_DATE_TO', this.#translate.instant('TICKET.SESSION_DATE_TO'));
        const value = this.filtersForm.value.sessionDateTo;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this.dateTimeFormats.shortDateTime);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['sessionDateTo'] = valueId;
        }
        return filterItem;
    }

    #hasOrderType(orderTypes: OrderType[]): boolean {
        const formValue = this.$filtersFormValueChanges();
        const types: { id: OrderType }[] = formValue?.[FORM_FIELDS.type] ?? [];
        return types.some(type => orderTypes.includes(type.id));
    }
}
