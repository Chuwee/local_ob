import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { GetProductsRequest, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { GetFilterRequest, TicketsService } from '@admin-clients/cpanel-sales-data-access';
import { OrderItemType, TicketState } from '@admin-clients/shared/common/data-access';
import {
    ContextNotificationComponent, DateTimeModule, FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode, DateTimeFormats, FilterOption } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldWithServerReq$, cloneObject, deepEqual } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { debounceTime, filter, forkJoin, Observable, tap } from 'rxjs';
import { map, pairwise, startWith } from 'rxjs/operators';
import { CurrencySalesFilterComponent, FILTER_CURRENCY_LIST } from '../../../filter-currency-list/filter-currency-list.component';

@Component({
    selector: 'app-products-sales-list-filter',
    templateUrl: './products-sales-list-filter.component.html',
    imports: [
        TranslatePipe, ReactiveFormsModule, MaterialModule, FlexLayoutModule, ContextNotificationComponent, SelectServerSearchComponent,
        DateTimeModule, CurrencySalesFilterComponent
    ],
    viewProviders: [{
        provide: FILTER_CURRENCY_LIST, useFactory: () => inject(TicketsService).filterCurrencyList
    }],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsSalesListFilterComponent extends FilterWrapped implements OnInit, OnDestroy {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translate = inject(TranslateService);
    readonly #i18nSrv = inject(I18nService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ticketsService = inject(TicketsService);
    readonly #productsSrv = inject(ProductsService);
    readonly #authSrv = inject(AuthenticationService);

    readonly #formFields = {
        channelEntity: 'channelEntity',
        eventEntity: 'eventEntity',
        channel: 'channel',
        product: 'product',
        variant: 'variant',
        state: 'state',
        currency: 'currency'
    };

    readonly #formStructure = {
        channelEntity: null as number,
        eventEntity: null as number,
        channel: [] as number[],
        product: [] as number[],
        variant: null as string,
        state: { id: TicketState.purchase, name: `PRODUCT.SALE_STATE_OPTS.${TicketState.purchase}` },
        currency: null as { id: string; name: string }
    };

    readonly #selectListLimit = 100;

    readonly currencyFilterRequest: GetFilterRequest = { item_type: OrderItemType.product };
    readonly dateTimeFormats = DateTimeFormats;
    readonly productItemsState = [TicketState.purchase, TicketState.refund];
    readonly statesList = this.productItemsState.map(state => ({ id: state, name: `PRODUCT.SALE_STATE_OPTS.${state}` }));

    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
    // channel entities
    readonly channelEntities$ = this.#ticketsService.filterChannelsEntities.getData$();
    readonly moreChannelEntitiesAvailable$ = this.#ticketsService.filterChannelsEntities.get$().pipe(map(d => !!d?.metadata?.next_cursor));
    // event entities
    readonly eventEntities$ = this.#ticketsService.filterEventsEntities.getData$();
    readonly moreEventEntitiesAvailable$ = this.#ticketsService.filterEventsEntities.get$().pipe(map(d => !!d?.metadata?.next_cursor));
    // channels
    readonly channels$ = this.#ticketsService.filterChannels.getData$();
    readonly moreChannelsAvailable$ = this.#ticketsService.filterChannels.get$().pipe(map(d => !!d?.metadata?.next_cursor));
    // products
    readonly products$ = this.#productsSrv.productsList.getData$().pipe(
        map(products => products?.map(prod => {
            const product: FilterOption = { name: prod.name, id: String(prod.product_id) };
            return product;
        }))
    );

    readonly moreProductsAvailable$ = this.#productsSrv.productsList.getMetadata$()
        .pipe(map(metadata => metadata?.offset + metadata?.limit < metadata?.total));

    //currencies
    readonly $areCurrenciesShown = toSignal(this.#authSrv.getLoggedUser$().pipe(map(user =>
        AuthenticationService.operatorCurrencyCodes(user)?.length > 1)));

    readonly compareWith = compareWithIdOrCode;

    @Input() startDate: string;
    @Input() endDate: string;

    ngOnInit(): void {
        // initial state
        this.filtersForm.get(this.#formFields.variant).disable();
        // nested fields logic
        this.getFormFieldObs(this.#formFields.channelEntity).subscribe(() => this.entityAndCurrencyChangeHandler());
        this.getFormFieldObs(this.#formFields.eventEntity).subscribe(() => this.entityAndCurrencyChangeHandler());
        this.getFormFieldObs(this.#formFields.currency).subscribe(() => this.entityAndCurrencyChangeHandler());
        // this.filtersForm.get(this._formFields.product).valueChanges
        //     .pipe(takeUntil(this._onDestroy))
        //         .subscribe(selectedProducts => {
        //             if (Array.isArray(selectedProducts) && selectedProducts?.length) {
        //                 this.filtersForm.get(this._formFields.variant).enable();
        //             }
        //         });
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#ticketsService.clearFilterListsData();
        this.#ticketsService.clearFilterListsCache();
        this.#productsSrv.productsList.clearCache();
        this.#productsSrv.productsList.clear();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterChannelEntity(),
            this.getFilterEventEntity(),
            this.getFilterChannel(),
            this.getFilterProduct(),
            this.getFilterState(),
            this.getFilterCurrency()
        ].filter(element => !!element);
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'CHANNEL') {
            this.resetFilterMulti('channel', value);
        } else if (key === 'CHANNEL_ENTITY') {
            this.resetFilter('channelEntity');
        } else if (key === 'EVENT_ENTITY') {
            this.resetFilter('eventEntity');
        } else if (key === 'PRODUCT_ID') {
            this.resetFilterMulti('product', value);
        } else if (key === 'STATE') {
            this.resetFilter('state');
        } else if (key === 'CURRENCY') {
            this.resetFilter('currency');
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
            formFields, this.#formFields.currency, params, ids => this.#ticketsService.filterCurrencyList.getNames$(
                ids, { item_type: OrderItemType.product }))
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.channelEntity, params, ids => this.#ticketsService.filterChannelsEntities.getNames$(
                ids, { item_type: OrderItemType.product }))
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.eventEntity, params, ids => this.#ticketsService.filterEventsEntities.getNames$(ids))
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.channel, params, ids => this.#ticketsService.filterChannels.getNames$(
                ids, { item_type: OrderItemType.product }), true)
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.product, params, ids => this.#productsSrv.productsList.getNames$(ids), true)
        );
        formFields.state = this.statesList.find(stateObj => stateObj.id === params['state']) || null;

        return forkJoin(asyncFields).pipe(
            tap(() => {
                this.filtersForm.setValue(formFields, { emitEvent: false });
            }),
            map(() => this.getFilters())
        );
    }

    // selects data load
    loadFilterChannelEntities(q: string, nextPage: boolean): void {
        this.#ticketsService.filterChannelsEntities.load(this.getChannelAndEventEntitiesFilterRequest(q, OrderItemType.product), nextPage);
    }

    loadFilterEventEntities(q: string, nextPage: boolean): void {
        this.#ticketsService.filterEventsEntities.load(this.getChannelAndEventEntitiesFilterRequest(q), nextPage);
    }

    loadFilterChannels(q: string, nextPage: boolean): void {
        this.#ticketsService.filterChannels.load(this.getEntityAndCurrencyFilterRequest(q), nextPage);
    }

    // temporarily using products list until an endpoint for product filter is made
    loadFilterProducts(q: string, nextPage: boolean): void {
        const request: GetProductsRequest = {
            limit: this.#selectListLimit,
            offset: 0,
            q
        };
        this.#productsSrv.productsList.loadMoreAndCache(request, nextPage);
    }

    // FILTER FORM
    private getFormFieldObs<T>(field: string, startValue: T = undefined): Observable<T> {
        return this.filtersForm.get(field).valueChanges
            .pipe(
                debounceTime(100),
                startWith(startValue),
                pairwise(),
                filter(([prev, next]) => !deepEqual(prev, next)),
                map(() => null),
                takeUntilDestroyed(this.#destroyRef)
            );
    }

    private entityAndCurrencyChangeHandler(): void {
        this.filtersForm.get(this.#formFields.channel).setValue([], { emitEvent: false });
        this.#ticketsService.filterChannels.load(this.getEntityAndCurrencyFilterRequest());
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
            event_entity_id: this.filtersForm.get(this.#formFields.eventEntity).value?.id || undefined,
            channel_entity_id: this.filtersForm.get(this.#formFields.channelEntity).value?.id || undefined,
            item_type: OrderItemType.product
        };
    }

    // delete this when getBasicFilterRequest can have item_type param, and that depends on event_entity filter which must implement
    // item_type param, so far it's not supported
    private getChannelAndEventEntitiesFilterRequest(q: string = undefined, itemType?: OrderItemType.product): GetFilterRequest {
        const request = {
            ...this.getBasicFilterRequest(q)
        };
        if (itemType) {
            request.item_type = itemType;
        }
        return request;
    }

    // LIST FILTER

    private resetFilterMulti(formKey: string, value: unknown): void {
        const form = this.filtersForm.get(formKey);
        const values: { id: string }[] = form.value;
        form.reset(values.filter(type => type.id !== value));
    }

    private resetFilter(formKey: string): void {
        this.filtersForm.get(formKey).reset();
    }

    private getFilterChannel(): FilterItem {
        const filterItem = new FilterItem('CHANNEL', this.#translate.instant('TICKET.CHANNEL'));
        return this.getFilterMulti(filterItem, 'channel');
    }

    private getFilterChannelEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('CHANNEL_ENTITY')
            .labelKey('TICKET.CHANNEL_ENTITY')
            .queryParam('channelEntity')
            .value(this.filtersForm.value.channelEntity)
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

    private getFilterProduct(): FilterItem {
        const filterItem = new FilterItem('PRODUCT_ID', this.#translate.instant('FORMS.LABELS.PRODUCT'));
        return this.getFilterMulti(filterItem, 'product');
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
}
