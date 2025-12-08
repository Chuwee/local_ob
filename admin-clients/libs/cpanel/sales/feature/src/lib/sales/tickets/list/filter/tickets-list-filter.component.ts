import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    TicketPrintStatus, TicketValidationStatusFilter, TicketsService, GetFilterSessionDataRequest, GetFilterRequest
} from '@admin-clients/cpanel-sales-data-access';
import { TicketOriginMarket, ticketOriginMarkets, TicketState, TicketType } from '@admin-clients/shared/common/data-access';
import {
    FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue,
    SelectServerSearchComponent,
    ContextNotificationComponent,
    DateTimePickerComponent
} from '@admin-clients/shared/common/ui/components';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode, DateTimeFormats, FilterOption } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import {
    applyAsyncFieldWithServerReq$, cloneObject, deepEqual
} from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, computed, DestroyRef, inject, Input, OnDestroy, OnInit
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { debounceTime, filter, forkJoin, mapTo, Observable, tap } from 'rxjs';
import { map, pairwise, startWith } from 'rxjs/operators';
import { CurrencySalesFilterComponent, FILTER_CURRENCY_LIST } from '../../../filter-currency-list/filter-currency-list.component';

const FORM_FIELDS = {
    channelEntity: 'channelEntity',
    eventEntity: 'eventEntity',
    channel: 'channel',
    state: 'state',
    reallocationRefund: 'reallocationRefund',
    printStatus: 'printStatus',
    validationStatus: 'validationStatus',
    ticketType: 'ticketType',
    event: 'event',
    client: 'client',
    session: 'session',
    sessionDateFrom: 'sessionDateFrom',
    sessionDateTo: 'sessionDateTo',
    sector: 'sector',
    priceType: 'priceType',
    currency: 'currency',
    originMarket: 'originMarket'
} as const satisfies Record<string, string>;

type FormFieldKey = keyof typeof FORM_FIELDS;

@Component({
    selector: 'app-tickets-list-filter',
    templateUrl: './tickets-list-filter.component.html',
    styleUrls: ['./tickets-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatFormField, MatSelect, TranslatePipe, SelectServerSearchComponent, MatOption,
        CurrencySalesFilterComponent, MatDivider, ReactiveFormsModule, ContextNotificationComponent, MatTooltip,
        DateTimePipe, DateTimePickerComponent, MatIcon, AsyncPipe, MatIconButton, MatCheckbox
    ],
    viewProviders: [
        {
            provide: FILTER_CURRENCY_LIST, useFactory: () => inject(TicketsService).filterCurrencyList
        }
    ]
})
export class TicketsListFilterComponent extends FilterWrapped implements OnInit, OnDestroy {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translate = inject(TranslateService);
    readonly #ticketsService = inject(TicketsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #i18nSrv = inject(I18nService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #formStructure: Record<FormFieldKey, unknown> = {
        channelEntity: null as number,
        eventEntity: null as number,
        channel: [] as number[],
        state: { id: TicketState.purchase, name: `TICKET.STATE_OPTS.${TicketState.purchase}` },
        reallocationRefund: false,
        printStatus: null as { id: TicketPrintStatus; name: string },
        validationStatus: null as { id: TicketValidationStatusFilter; name: string },
        ticketType: null as { id: TicketType; name: string },
        event: [] as number[],
        session: [] as number[],
        sessionDateFrom: null as string,
        sessionDateTo: null as string,
        sector: [] as number[],
        priceType: [] as number[],
        client: [] as number[],
        currency: null as { id: string; name: string },
        originMarket: null as { id: TicketOriginMarket; name: string }
    };

    readonly #selectListLimit = 100;

    readonly formControlNames = FORM_FIELDS;
    readonly dateTimeFormats = DateTimeFormats;
    readonly statesList = Object.values(TicketState).filter(value => value !== TicketState.secMktPurchase)
        .map(type => ({ id: type, name: `TICKET.STATE_OPTS.${type}` }));

    readonly originMarketsList = ticketOriginMarkets.map(type => ({ id: type, name: `TICKET.ORIGIN_MARKET_OPTS.${type}` }));
    readonly typesList = Object.values(TicketType).map(type => ({ id: type, name: `TICKET.TYPE_OPTS.${type}` }));
    readonly validationList = Object.values(TicketValidationStatusFilter).map(s => ({ id: s, name: `TICKET.VALIDATION_OPTS.${s}` }));
    readonly printList = Object.values(TicketPrintStatus).map(status => ({ id: status, name: `TICKET.PRINT_OPTS.${status}` }));
    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
    readonly $filtersFormValueChanges = toSignal(this.filtersForm.valueChanges, { initialValue: this.filtersForm.value });
    // channel entities
    readonly channelEntities$ = this.#ticketsService.filterChannelsEntities.getData$();
    readonly moreChannelEntitiesAvailable$ = this.#ticketsService.filterChannelsEntities.get$().pipe(map(d => !!d?.metadata?.next_cursor));
    // event entities
    readonly eventEntities$ = this.#ticketsService.filterEventsEntities.getData$();
    readonly moreEventEntitiesAvailable$ = this.#ticketsService.filterEventsEntities.get$().pipe(map(d => !!d?.metadata?.next_cursor));
    // channels
    readonly channels$ = this.#ticketsService.filterChannels.getData$();
    readonly moreChannelsAvailable$ = this.#ticketsService.filterChannels.get$().pipe(map(d => !!d?.metadata?.next_cursor));
    // events
    readonly events$ = this.#ticketsService.filterEvents.getData$();
    readonly moreEventsAvailable$ = this.#ticketsService.filterEvents.get$().pipe(map(d => !!d?.metadata?.next_cursor));
    // sessions
    readonly sessions$ = this.#ticketsService.filterSessions.getData$();
    readonly moreSessionsAvailable$ = this.#ticketsService.filterSessions.get$().pipe(map(d => !!d?.metadata?.next_cursor));
    // sectors
    readonly sectors$ = this.#ticketsService.filterSectors.get$().pipe(map(resp => resp?.data));
    readonly loadingSectors$ = this.#ticketsService.filterSectors.loading$();
    readonly moreSectorsAvailable$ = this.#ticketsService.filterSectors.get$().pipe(map(d => d?.metadata?.total > d?.data?.length));
    // price types
    readonly priceTypes$ = this.#ticketsService.filterPriceTypes.getData$();
    readonly loadingPriceTypes$ = this.#ticketsService.filterPriceTypes.loading$();
    readonly morePriceTypesAvailable$ = this.#ticketsService.filterPriceTypes.get$()
        .pipe(map(d => d?.metadata?.total > d?.data?.length));

    // professional clients
    readonly clients$ = this.#ticketsService.filterClients.getData$();
    readonly moreClientsAvailable$ = this.#ticketsService.filterClients.get$().pipe(map(d => !!d?.metadata?.next_cursor));

    //currencies
    readonly $areCurrenciesShown = toSignal(this.#authSrv.getLoggedUser$().pipe(map(user =>
        AuthenticationService.operatorCurrencyCodes(user)?.length > 1)));

    readonly $showReallocationRefund = computed(() => this.hasTicketType([TicketState.refund]));

    readonly compareWith = compareWithIdOrCode;

    @Input() startDate: string;
    @Input() endDate: string;

    ngOnInit(): void {
        // initial state
        this.filtersForm.get(FORM_FIELDS.sessionDateTo).disable();
        this.filtersForm.get(FORM_FIELDS.session).disable();
        this.filtersForm.get(FORM_FIELDS.sector).disable();
        this.filtersForm.get(FORM_FIELDS.priceType).disable();
        // nested fields logic
        this.getFormFieldObs(FORM_FIELDS.currency).subscribe(() => this.entityAndCurrencyChangeHandler());
        this.getFormFieldObs(FORM_FIELDS.channelEntity).subscribe(() => this.entityAndCurrencyChangeHandler());
        this.getFormFieldObs(FORM_FIELDS.eventEntity).subscribe(() => this.entityAndCurrencyChangeHandler());
        this.getFormFieldObs(FORM_FIELDS.channel).subscribe(() => this.channelChangeHandler());
        this.getFormFieldObs(FORM_FIELDS.event, this.filtersForm.get(FORM_FIELDS.event).value)
            .subscribe(() => this.eventChangeHandler());
        // session select and session to-from dates logic
        this.filtersForm.get(FORM_FIELDS.session).valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(selectedSessions => {
                this.filtersForm.get(FORM_FIELDS.sector).setValue([]);
                this.filtersForm.get(FORM_FIELDS.priceType).setValue([]);
                this.#ticketsService.filterSectors.clear();
                this.#ticketsService.filterPriceTypes.clear();
                if (Array.isArray(selectedSessions) && selectedSessions?.length) {
                    this.filtersForm.get(FORM_FIELDS.sessionDateFrom).disable({ emitEvent: false });
                    this.filtersForm.get(FORM_FIELDS.sessionDateTo).disable({ emitEvent: false });
                    this.filtersForm.get(FORM_FIELDS.sector).enable();
                    this.filtersForm.get(FORM_FIELDS.priceType).enable();
                } else {
                    this.filtersForm.get(FORM_FIELDS.sector).disable();
                    this.filtersForm.get(FORM_FIELDS.priceType).disable();
                    this.filtersForm.get(FORM_FIELDS.sessionDateFrom).enable();
                }
            });
        this.filtersForm.get(FORM_FIELDS.sessionDateFrom).valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(sessionFromDate => {
                if (sessionFromDate) {
                    this.filtersForm.get(FORM_FIELDS.session).disable({ emitEvent: false });
                    this.filtersForm.get(FORM_FIELDS.sessionDateTo).enable({ emitEvent: false });
                    if (!this.filtersForm.get(FORM_FIELDS.sessionDateTo).value) {
                        this.filtersForm.get(FORM_FIELDS.sessionDateTo).setValue(
                            moment(sessionFromDate).hour(23).minutes(59).format(),
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
        this.#ticketsService.clearFilterListsData();
        this.#ticketsService.clearFilterListsCache();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterState(),
            this.getFilterReallocationRefund(),
            this.getFilterOriginMarket(),
            this.getFilterTicketType(),
            this.getFilterValidationStatus(),
            this.getFilterPrintStatus(),
            this.getFilterChannel(),
            this.getFilterChannelEntity(),
            this.getFilterEvent(),
            this.getFilterEventEntity(),
            this.getFilterSession(),
            this.getFilterSector(),
            this.getFilterPriceType(),
            this.getFilterSessionDateFrom(),
            this.getFilterSessionDateTo(),
            this.getFilterClient(),
            this.getFilterCurrency()
        ].filter(element => !!element);
    }

    removeFilter(key: string, value: unknown): void {
        switch (key) {
            case 'TICKET_TYPE':
                this.resetFilter(FORM_FIELDS.ticketType);
                break;
            case 'VALIDATION':
                this.resetFilter(FORM_FIELDS.validationStatus);
                break;
            case 'PRINT':
                this.resetFilter(FORM_FIELDS.printStatus);
                break;
            case 'CHANNEL':
                this.resetFilterMulti(FORM_FIELDS.channel, value);
                break;
            case 'CHANNEL_ENTITY':
                this.resetFilter(FORM_FIELDS.channelEntity);
                break;
            case 'EVENT':
                this.resetFilterMulti(FORM_FIELDS.event, value);
                break;
            case 'EVENT_ENTITY':
                this.resetFilter(FORM_FIELDS.eventEntity);
                break;
            case 'SESSION_DATE_FROM':
            case 'SESSION_DATE_TO':
                this.resetFilter(FORM_FIELDS.sessionDateFrom);
                this.resetFilter(FORM_FIELDS.sessionDateTo);
                break;
            case 'SESSION':
                this.resetFilterMulti(FORM_FIELDS.session, value);
                break;
            case 'PROFESSIONAL_CLIENT':
                this.resetFilterMulti(FORM_FIELDS.client, value);
                break;
            case 'SECTOR':
                this.resetFilter(FORM_FIELDS.sector);
                break;
            case 'PRICE_TYPE':
                this.resetFilter(FORM_FIELDS.priceType);
                break;
            case 'CURRENCY':
                this.resetFilter(FORM_FIELDS.currency);
                break;
            case 'ORIGIN_MARKET':
                this.resetFilter(FORM_FIELDS.originMarket);
                break;
            case 'REALLOCATION_REFUND':
                this.resetFilter(FORM_FIELDS.reallocationRefund);
                break;
        }
    }

    resetFilters(): void {
        if (!deepEqual(this.filtersForm.getRawValue(), this.#formStructure)) {
            this.filtersForm.reset(cloneObject(this.#formStructure));
        }
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);
        const asyncFields: Observable<FilterOption[]>[] = [];
        if (params['startDate'] && params['endDate']) {
            if (params['startDate'] !== this.startDate || params['endDate'] !== this.endDate) {
                this.#ticketsService.clearFilterListsData();
                this.startDate = String(params['startDate']);
                this.endDate = String(params['endDate']);
            }
        } else {
            if (this.startDate || this.endDate) {
                this.#ticketsService.clearFilterListsData();
            }
            this.startDate = null;
            this.endDate = null;
        }

        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, FORM_FIELDS.currency, params, ids => this.#ticketsService.filterCurrencyList.getNames$(ids))
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, FORM_FIELDS.channelEntity, params, ids => this.#ticketsService.filterChannelsEntities.getNames$(ids))
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, FORM_FIELDS.eventEntity, params, ids => this.#ticketsService.filterEventsEntities.getNames$(ids))
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, FORM_FIELDS.channel, params, ids => this.#ticketsService.filterChannels.getNames$(ids), true)
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, FORM_FIELDS.event, params, ids => this.#ticketsService.filterEvents.getNames$(ids), true)
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, FORM_FIELDS.client, params, ids => this.#ticketsService.filterClients.getNames$(ids), true)
        );
        if (params['reallocationRefund']) {
            formFields.reallocationRefund = params['reallocationRefund'] === 'true';
        }
        if (params[FORM_FIELDS.event]) {
            this.filtersForm.get(FORM_FIELDS.session).enable({ emitEvent: false });
        } else {
            this.filtersForm.get(FORM_FIELDS.session).disable({ emitEvent: false });
        }
        this.filtersForm.get(FORM_FIELDS.sector).disable({ emitEvent: false });
        this.filtersForm.get(FORM_FIELDS.priceType).disable({ emitEvent: false });
        if (params['session']) {
            asyncFields.push(applyAsyncFieldWithServerReq$(
                formFields, FORM_FIELDS.session, params, ids => this.#ticketsService.filterSessions.getNames$(ids), true)
            );
            if (params['session'].split(',').length) {
                const sessionIds = Array.from(params['session'].split(',')).map(id => Number(id));
                this.filtersForm.get(FORM_FIELDS.sector).enable({ emitEvent: false });
                this.filtersForm.get(FORM_FIELDS.priceType).enable({ emitEvent: false });
                if (params['sector']) {
                    asyncFields.push(applyAsyncFieldWithServerReq$(
                        formFields,
                        FORM_FIELDS.sector,
                        params,
                        ids => this.#ticketsService.filterSectors.getNames$(sessionIds, ids),
                        true)
                    );
                }
                if (params['priceType']) {
                    asyncFields.push(applyAsyncFieldWithServerReq$(
                        formFields,
                        FORM_FIELDS.priceType,
                        params,
                        ids => this.#ticketsService.filterPriceTypes.getNames$(sessionIds, ids),
                        true)
                    );
                }
            }
        } else {
            if (params['sessionDateFrom']) {
                formFields.sessionDateFrom = moment(params['sessionDateFrom']).tz(moment().tz()).format();
            }
            if (params['sessionDateTo']) {
                formFields.sessionDateTo = moment(params['sessionDateTo']).tz(moment().tz()).format();
            }
        }
        formFields.state = this.statesList.find(stateObj => stateObj.id === params['state']) || null;
        formFields.originMarket = this.originMarketsList.find(originMarketObj => originMarketObj.id === params['originMarket']) || null;
        if (params['ticketType']) {
            formFields.ticketType = this.typesList.find(stateObj => stateObj.id === params['ticketType']);
        }
        if (params['validation']) {
            formFields.validationStatus = this.validationList.find(stateObj => stateObj.id === params['validation']);
        }
        if (params['print']) {
            formFields.printStatus = this.printList.find(stateObj => stateObj.id === params['print']);
        }

        return forkJoin(asyncFields).pipe(
            tap(() => {
                this.filtersForm.setValue(formFields, { emitEvent: false });
            }),
            map(() => this.getFilters())
        );
    }

    // selects data load
    loadFilterChannelEntities(q: string, nextPage: boolean): void {
        this.#ticketsService.filterChannelsEntities.load(this.getBasicFilterRequest(q), nextPage);
    }

    loadFilterEventEntities(q: string, nextPage: boolean): void {
        this.#ticketsService.filterEventsEntities.load(this.getBasicFilterRequest(q), nextPage);
    }

    loadFilterChannels(q: string, nextPage: boolean): void {
        this.#ticketsService.filterChannels.load(this.getEntityAndCurrencyFilterRequest(q), nextPage);
    }

    loadFilterClients(q: string, nextPage: boolean): void {
        this.#ticketsService.filterClients.load(this.getChannelsFilterRequest(q), nextPage);
    }

    loadFilterEvents(q: string, nextPage: boolean): void {
        this.#ticketsService.filterEvents.load(this.getChannelsFilterRequest(q), nextPage);
    }

    loadFilterSessions(q: string, nextPage: boolean): void {
        this.#ticketsService.filterSessions.load(this.getEventsFilterRequest(q), nextPage);
    }

    loadFilterSectors(q: string, nextPage: boolean): void {
        this.#ticketsService.filterSectors.load(this.getSessionFilterRequest(q), nextPage);
    }

    loadFilterPriceTypes(q: string, nextPage: boolean): void {
        this.#ticketsService.filterPriceTypes.load(this.getSessionFilterRequest(q), nextPage);
    }

    // clear session dates

    clearSessionDates(): void {
        this.filtersForm.get(FORM_FIELDS.sessionDateFrom).setValue(null);
        this.filtersForm.get(FORM_FIELDS.sessionDateTo).setValue(null);
    }

    // FILTER FORM
    private getFormFieldObs<T>(field: string, startValue: T = undefined): Observable<T> {
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

    private entityAndCurrencyChangeHandler(): void {
        this.filtersForm.get(FORM_FIELDS.channel).setValue([], { emitEvent: false });
        this.#ticketsService.filterChannels.load(this.getEntityAndCurrencyFilterRequest());
        this.channelChangeHandler();
    }

    private channelChangeHandler(): void {
        this.filtersForm.get(FORM_FIELDS.event).setValue([], { emitEvent: false });
        this.filtersForm.get(FORM_FIELDS.client).setValue([], { emitEvent: false });
        this.#ticketsService.filterEvents.load(this.getChannelsFilterRequest());
        this.#ticketsService.filterClients.load(this.getChannelsFilterRequest());
        this.eventChangeHandler();
    }

    private eventChangeHandler(): void {
        this.filtersForm.get(FORM_FIELDS.session).setValue([], { emitEvent: false });
        if (this.filtersForm.get(FORM_FIELDS.event).value?.length && !this.filtersForm.get(FORM_FIELDS.sessionDateFrom).value) {
            this.filtersForm.get(FORM_FIELDS.session).enable();
            this.filtersForm.get(FORM_FIELDS.sessionDateFrom).enable();
            this.#ticketsService.filterSessions.load(this.getEventsFilterRequest());
        } else {
            this.filtersForm.get(FORM_FIELDS.session).disable();
        }
    }

    // selects data load requests

    private getBasicFilterRequest(q: string = undefined): GetFilterRequest {
        return {
            q,
            purchase_date_from: this.startDate,
            purchase_date_to: this.endDate,
            limit: this.#selectListLimit
        };
    }

    private getEntityAndCurrencyFilterRequest(q: string = undefined): GetFilterRequest {
        return {
            ...this.getBasicFilterRequest(q),
            event_entity_id: this.filtersForm.get(FORM_FIELDS.eventEntity).value?.id || undefined,
            channel_entity_id: this.filtersForm.get(FORM_FIELDS.channelEntity).value?.id || undefined,
            currency_code: this.filtersForm.get('currency').value?.id || undefined
        };
    }

    private getChannelsFilterRequest(q: string = undefined): GetFilterRequest {
        return {
            ...this.getEntityAndCurrencyFilterRequest(q),
            channel_id: this.filtersForm.get(FORM_FIELDS.channel).value?.filter(i => !!i).map(item => item.id).join(',') || undefined
        };
    }

    private getEventsFilterRequest(q: string = undefined): GetFilterRequest {
        return {
            ...this.getChannelsFilterRequest(q),
            event_id: this.filtersForm.get(FORM_FIELDS.event).value?.filter(i => !!i).map(i => i.id).join(',') || undefined
        };
    }

    private getSessionFilterRequest(q: string = undefined): GetFilterSessionDataRequest {
        return {
            q,
            limit: this.#selectListLimit,
            session_id: this.filtersForm.get(FORM_FIELDS.session).value?.filter(i => !!i).map(i => i.id) || undefined
        };
    }

    // LIST FILTER

    private resetFilterMulti(formKey: FormFieldKey, value: unknown): void {
        const form = this.filtersForm.get(formKey);
        const values: { id: string }[] = form.value;
        form.reset(values.filter(type => type.id !== value));
    }

    private resetFilter(formKey: FormFieldKey): void {
        this.filtersForm.get(formKey).reset();
    }

    private getFilterState(): FilterItem {
        const value = this.filtersForm.value.state?.id;
        const valueLabel = this.filtersForm.value.state ? this.#translate.instant(this.filtersForm.value.state.name) : undefined;
        return {
            key: 'STATE',
            label: null,
            urlQueryParams: { state: value },
            values: [new FilterItemValue(value, valueLabel)]
        };
    }

    private getFilterTicketType(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('TICKET_TYPE')
            .labelKey('TICKET.TICKET_TYPE')
            .queryParam('ticketType')
            .value(this.filtersForm.value.ticketType)
            .translateValue()
            .build();
    }

    private getFilterPrintStatus(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('PRINT')
            .labelKey('TICKET.PRINT_STATUS')
            .queryParam('print')
            .value(this.filtersForm.value.printStatus)
            .translateValue()
            .build();
    }

    private getFilterValidationStatus(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('VALIDATION')
            .labelKey('TICKET.VALIDATION_STATUS')
            .queryParam('validation')
            .value(this.filtersForm.value.validationStatus)
            .translateValue()
            .build();
    }

    private getFilterReallocationRefund(): FilterItem {
        const filterItem = new FilterItem('REALLOCATION_REFUND', this.#translate.instant('ORDER.REFUND_FOR_REALLOCATE'));
        const value = this.filtersForm.value.reallocationRefund;
        if (value && this.$showReallocationRefund()) {
            filterItem.values = [new FilterItemValue(value, null)];
            filterItem.urlQueryParams['reallocationRefund'] = value;
        }
        return filterItem;
    }

    private getFilterChannel(): FilterItem {
        const filterItem = new FilterItem('CHANNEL', this.#translate.instant('TICKET.CHANNEL'));
        return this.getFilterMulti(filterItem, 'channel');
    }

    private getFilterSession(): FilterItem {
        const filterItem = new FilterItem('SESSION', this.#translate.instant('TICKET.SESSION'));
        return this.getFilterMulti(filterItem, 'session');
    }

    private getFilterSector(): FilterItem {
        if (this.filtersForm.get(FORM_FIELDS.session).value?.length) {
            const filterItem = new FilterItem('SECTOR', this.#translate.instant('TICKET.SECTOR'));
            return this.getFilterMulti(filterItem, 'sector');
        } else {
            return null;
        }
    }

    private getFilterPriceType(): FilterItem {
        if (this.filtersForm.get(FORM_FIELDS.session).value?.length) {
            const filterItem = new FilterItem('PRICE_TYPE', this.#translate.instant('TICKET.PRICE_TYPE'));
            return this.getFilterMulti(filterItem, 'priceType');
        } else {
            return null;
        }
    }

    private getFilterEvent(): FilterItem {
        const filterItem = new FilterItem('EVENT', this.#translate.instant('TICKET.EVENT'));
        return this.getFilterMulti(filterItem, 'event');
    }

    private getFilterChannelEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('CHANNEL_ENTITY')
            .labelKey('TICKET.CHANNEL_ENTITY')
            .queryParam('channelEntity')
            .value(this.filtersForm.value.channelEntity)
            .build();
    }

    private getFilterOriginMarket(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('ORIGIN_MARKET')
            .labelKey('TICKET.ORIGIN_MARKET')
            .queryParam('originMarket')
            .value(this.filtersForm.value.originMarket)
            .build();
    }

    private getFilterEventEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('EVENT_ENTITY')
            .labelKey('TICKET.EVENT_ENTITY')
            .queryParam('eventEntity')
            .value(this.filtersForm.value.eventEntity)
            .build();
    }

    private getFilterClient(): FilterItem {
        const filterItem = new FilterItem('PROFESSIONAL_CLIENT', this.#translate.instant('FORMS.LABELS.PROFESSIONAL_CLIENT'));
        return this.getFilterMulti(filterItem, 'client');
    }

    private getFilterCurrency(): FilterItem {
        const value = this.filtersForm.value.currency ?
            {
                ...this.filtersForm.value.currency,
                name: this.#i18nSrv.getCurrencyPartialTranslation(this.filtersForm.value.currency?.name)
            } :
            null;
        return new FilterItemBuilder(this.#translate)
            .key('CURRENCY')
            .labelKey('FORMS.LABELS.CURRENCY')
            .queryParam('currency')
            .value(value)
            .translateValue()
            .build();
    }

    private getFilterMulti(filterItem: FilterItem, formKey: string): FilterItem {
        const value = this.filtersForm.get(formKey).value as [{ id: string; date_start?: string; name: string }];
        if (value && value.length > 0) {
            filterItem.values = value.filter(i => !!i).map(valueItem => {
                if (valueItem.date_start) {
                    const startDate = moment(valueItem.date_start).format(DateTimeFormats.shortDateTime);
                    return new FilterItemValue(valueItem.id, `${startDate} - ${valueItem.name}`);
                } else {
                    return new FilterItemValue(valueItem.id, valueItem.name);
                }
            });
            filterItem.urlQueryParams[formKey] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

    private getFilterSessionDateFrom(): FilterItem {
        const filterItem = new FilterItem('SESSION_DATE_FROM', this.#translate.instant('TICKET.SESSION_DATE_FROM'));
        const value = this.filtersForm.value.sessionDateFrom;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(DateTimeFormats.shortDateTime);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['sessionDateFrom'] = valueId;
        }
        return filterItem;
    }

    private getFilterSessionDateTo(): FilterItem {
        const filterItem = new FilterItem('SESSION_DATE_TO', this.#translate.instant('TICKET.SESSION_DATE_TO'));
        const value = this.filtersForm.value.sessionDateTo;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(DateTimeFormats.shortDateTime);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['sessionDateTo'] = valueId;
        }
        return filterItem;
    }

    private hasTicketType(ticketStates: TicketState[]): boolean {
        const formValue = this.$filtersFormValueChanges();
        const states: { id: TicketState } = formValue?.[FORM_FIELDS.state];
        return states.id && ticketStates.includes(states.id);
    }
}
