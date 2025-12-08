import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { VoucherOrdersService, GetFilterRequest } from '@admin-clients/cpanel-sales-data-access';
import {
    FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue
} from '@admin-clients/shared/common/ui/components';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode, FilterOption } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldWithServerReq$, deepEqual } from '@admin-clients/shared/utility/utils';
import {
    ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { filter, forkJoin, mapTo, Observable, tap } from 'rxjs';
import { debounceTime, map, pairwise, startWith } from 'rxjs/operators';
import { FILTER_CURRENCY_LIST } from '../../../filter-currency-list/filter-currency-list.component';

@Component({
    selector: 'app-voucher-orders-list-filter',
    templateUrl: './voucher-orders-list-filter.component.html',
    styleUrls: ['./voucher-orders-list-filter.component.scss'],
    viewProviders: [
        {
            provide: FILTER_CURRENCY_LIST, useFactory: () => inject(VoucherOrdersService).filterCurrencyList
        }
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherOrdersListFilterComponent extends FilterWrapped implements OnInit, OnDestroy {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translate = inject(TranslateService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #voucherOrdersSrv = inject(VoucherOrdersService);
    readonly #i18nSrv = inject(I18nService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #formFields = {
        channelEntity: 'channelEntity',
        channel: 'channel',
        currency: 'currency',
        merchant: 'merchant'
    };

    readonly #formStructure = {
        channelEntity: null,
        channel: [],
        currency: null,
        merchant: null
    };

    readonly #selectsListLimit = 100;

    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
    readonly $isOperatorMode = toSignal(this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));
    readonly $isEntityAdmin = toSignal(this.#authSrv.hasLoggedUserSomeEntityType$(['ENTITY_ADMIN']));
    readonly channels$ = this.#voucherOrdersSrv.getFilterChannelListData$();
    readonly moreChannelsAvailable$ = this.#voucherOrdersSrv.getFilterChannelListMetadata$().pipe(map(meta => !!meta?.next_cursor));
    readonly channelEntities$ = this.#voucherOrdersSrv.getFilterChannelEntitiesListData$();
    readonly moreChannelEntitiesAvailable$ = this.#voucherOrdersSrv.getFilterChannelEntitiesListMetadata$()
        .pipe(map(meta => !!meta?.next_cursor));

    //currencies
    readonly $areCurrenciesShown = toSignal(this.#authSrv.getLoggedUser$().pipe(map(user =>
        AuthenticationService.operatorCurrencyCodes(user)?.length > 1)));

    readonly compareWith = compareWithIdOrCode;

    // merchants
    readonly merchants$ = this.#voucherOrdersSrv.filterMerchantList.getData$();
    readonly moreMerchantsAvailable$ = this.#voucherOrdersSrv.filterMerchantList.getMetaData$().pipe(map(md => !!md?.next_cursor));

    @Input() startDate: string;
    @Input() endDate: string;

    ngOnInit(): void {
        this.getFormFieldObs(this.#formFields.currency).subscribe(() => this.entityAndCurrencyChangeHandler());
        this.getFormFieldObs(this.#formFields.channelEntity).subscribe(() => this.entityAndCurrencyChangeHandler());
        this.getFormFieldObs(this.#formFields.merchant).subscribe(() => this.entityAndCurrencyChangeHandler());
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#voucherOrdersSrv.clearFilterListsData();
        this.#voucherOrdersSrv.clearFilterListsCache();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterChannel(),
            this.getFilterChannelEntity(),
            this.getFilterCurrency(),
            this.getFilterMerchant()
        ].filter(element => !!element);
    }

    removeFilter(key: string, value: string): void {
        if (key === 'CHANNEL') {
            this.resetFilterMulti(this.#formFields.channel, value);
        } else if (key === 'CHANNEL_ENTITY') {
            this.resetFilter(this.#formFields.channelEntity);
        } else if (key === 'CURRENCY') {
            this.resetFilter('currency');
        } else if (key === 'MERCHANT') {
            this.resetFilter('merchant');
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);
        const asyncFields: Observable<FilterOption[]>[] = [];

        if (params['startDate'] && params['endDate']) {
            if (params['startDate'] !== this.startDate || params['endDate'] !== this.endDate) {
                this.#voucherOrdersSrv.clearFilterListsData();
                this.startDate = String(params['startDate']);
                this.endDate = String(params['endDate']);
            }
        } else {
            if (this.startDate || this.endDate) {
                this.#voucherOrdersSrv.clearFilterListsData();
            }
            this.startDate = null;
            this.endDate = null;
        }

        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.currency, params, ids => this.#voucherOrdersSrv.filterCurrencyList.getNames$(ids)));
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.channelEntity, params, ids => this.#voucherOrdersSrv.getFilterChannelEntitiesNames$(ids)
        ));

        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.channel, params, ids => this.#voucherOrdersSrv.getFilterChannelNames$(ids), true)
        );

        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.merchant, params, ids => this.#voucherOrdersSrv.filterMerchantList.getNames$(ids)));

        return forkJoin(asyncFields).pipe(
            tap(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
            }),
            map(() => this.getFilters())
        );
    }

    loadFilterChannelEntities(q: string = undefined, next = false): void {
        this.#voucherOrdersSrv.loadFilterChannelEntitiesList(this.getBasicFilterRequest(q), next);
    }

    loadFilterChannels(q: string = undefined, next = false): void {
        this.#voucherOrdersSrv.loadFilterChannelList(this.getFilterRequestWithEntitiesAndCurrency(q), next);
    }

    loadMerchants(q: string, next = false): void {
        this.#voucherOrdersSrv.filterMerchantList.load(this.getFilterRequestWithEntitiesAndCurrency(q), next);
    }

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
        this.filtersForm.get(this.#formFields.channel).setValue(null, { emitEvent: false });
        this.#voucherOrdersSrv.loadFilterChannelList(this.getFilterRequestWithEntitiesAndCurrency());
    }

    private getBasicFilterRequest(q: string = undefined): GetFilterRequest {
        return {
            q,
            purchase_date_from: this.startDate,
            purchase_date_to: this.endDate,
            limit: this.#selectsListLimit
        };
    }

    private getFilterRequestWithEntitiesAndCurrency(q: string = undefined): GetFilterRequest {
        return {
            ...this.getBasicFilterRequest(q),
            channel_entity_id: this.filtersForm.get('channelEntity').value?.id || undefined,
            currency_code: this.filtersForm.get('currency').value?.id || undefined
        };
    }

    private resetFilterMulti(formKey: string, value: string): void {
        const form = this.filtersForm.get(formKey);
        form.reset(form.value.filter(type => type.id !== value));
    }

    private resetFilter(formKey: string): void {
        this.filtersForm.get(formKey).reset();
    }

    private getFilterChannel(): FilterItem {
        const filterItem = new FilterItem('CHANNEL', this.#translate.instant('VOUCHER_ORDER.CHANNEL'));
        return this.getFilterMulti(filterItem, this.#formFields.channel);
    }

    private getFilterMulti(filterItem: FilterItem, formKey: string): FilterItem {
        const value = this.filtersForm.get(formKey).value as [{ id: string; name: string }];
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, valueItem.name));
            filterItem.urlQueryParams[formKey] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

    private getFilterChannelEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('CHANNEL_ENTITY')
            .labelKey('VOUCHER_ORDER.ENTITY')
            .queryParam('channelEntity')
            .value(this.filtersForm.value.channelEntity)
            .build();
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
            .build();
    }

    private getFilterMerchant(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('MERCHANT')
            .labelKey('FORMS.LABELS.MERCHANTS')
            .queryParam('merchant')
            .value(this.filtersForm.value.merchant)
            .build();
    }
}
