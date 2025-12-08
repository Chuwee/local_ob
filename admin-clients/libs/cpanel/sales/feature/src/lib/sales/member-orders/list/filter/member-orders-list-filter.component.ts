import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { MemberOrdersService, MemberOrderState, MemberOrderType, GetFilterRequest } from '@admin-clients/cpanel-sales-data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode, FilterOption } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldWithServerReq$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, tap } from 'rxjs';
import { map } from 'rxjs/operators';
import { FILTER_CURRENCY_LIST } from '../../../filter-currency-list/filter-currency-list.component';

@Component({
    selector: 'app-member-orders-list-filter',
    templateUrl: './member-orders-list-filter.component.html',
    styleUrls: ['./member-orders-list-filter.component.scss'],
    viewProviders: [
        {
            provide: FILTER_CURRENCY_LIST, useFactory: () => inject(MemberOrdersService).filterCurrencyList
        }
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberOrdersListFilterComponent extends FilterWrapped implements OnDestroy {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #i18nSrv = inject(I18nService);
    readonly #translate = inject(TranslateService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #memberOrdersSrv = inject(MemberOrdersService);

    readonly #formFields = {
        channelEntity: 'channelEntity',
        type: 'type',
        state: 'state',
        currency: 'currency'
    };

    readonly #formStructure = {
        channelEntity: null,
        type: null,
        state: null,
        currency: null
    };

    readonly #selectListLimit = 100;

    readonly statesList = Object.values(MemberOrderState).map(state => ({ id: state, name: `MEMBER_ORDER.STATE_OPS.${state}` }));
    readonly typesList = Object.values(MemberOrderType)
        .filter(type => type !== MemberOrderType.none)
        .map(type => ({ id: type, name: `MEMBER_ORDER.TYPE_OPTS.${type}` }));

    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));

    // channel entities
    readonly channelEntities$ = this.#memberOrdersSrv.getFilterChannelEntityListData$();
    readonly moreChannelEntitiesAvailable$ = this.#memberOrdersSrv.getFilterChannelEntityList$()
        .pipe(map(d => !!d?.metadata?.next_cursor));

    readonly $isOperatorMode = toSignal(this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));
    readonly $isEntityAdmin = toSignal(this.#authSrv.hasLoggedUserSomeEntityType$(['ENTITY_ADMIN']));

    //currencies
    readonly $areCurrenciesShown = toSignal(this.#authSrv.getLoggedUser$().pipe(map(user =>
        AuthenticationService.operatorCurrencyCodes(user)?.length > 1)));

    readonly compareWith = compareWithIdOrCode;

    @Input() startDate: string;
    @Input() endDate: string;

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#memberOrdersSrv.clearFilterListsData();
        this.#memberOrdersSrv.clearFilterListsCache();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterState(),
            this.getFilterType(),
            this.getFilterChannelEntity(),
            this.getFilterCurrency()
        ].filter(element => !!element);
    }

    removeFilter(key: string, value: string): void {
        if (key === 'TYPE') {
            this.resetFilterMulti('type', value);
        } else if (key === 'STATE') {
            this.resetFilterMulti('state', value);
        } else if (key === 'CHANNEL_ENTITY') {
            this.resetFilter('channelEntity');
        } else if (key === 'CURRENCY') {
            this.resetFilter('currency');
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
                this.#memberOrdersSrv.clearFilterListsData();
                this.startDate = String(params['startDate']);
                this.endDate = String(params['endDate']);
            }
        } else {
            if (this.startDate || this.endDate) {
                this.#memberOrdersSrv.clearFilterListsData();
            }
            this.startDate = null;
            this.endDate = null;
        }

        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.currency, params, ids => this.#memberOrdersSrv.filterCurrencyList.getNames$(ids)));
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, this.#formFields.channelEntity, params, ids => this.#memberOrdersSrv.getFilterChannelEntityNames$(ids))
        );

        if (params['type']) {
            formFields.type = params['type'].split(',').map(type => this.typesList.find(typeObj => typeObj.id === type));
        }

        if (params['state']) {
            formFields.state = params['state'].split(',').map(state => this.statesList.find(typeObj => typeObj.id === state));
        }

        return forkJoin(asyncFields).pipe(
            tap(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
            }),
            map(() => this.getFilters())
        );
    }

    loadFilterChannelEntities(q: string, nextPage: boolean): void {
        this.#memberOrdersSrv.loadFilterChannelEntityList(this.getBasicFilterRequest(q), nextPage);
    }

    private getBasicFilterRequest(q: string = undefined): GetFilterRequest {
        return {
            q,
            purchase_date_from: this.startDate,
            purchase_date_to: this.endDate,
            limit: this.#selectListLimit
        };
    }

    private resetFilterMulti(formKey: string, value: unknown): void {
        const form = this.filtersForm.get(formKey);
        const values: { id: string }[] = form.value;
        form.reset(values.filter(type => type.id !== value));
    }

    private resetFilter(formKey: string): void {
        this.filtersForm.get(formKey).reset();
    }

    private getFilterType(): FilterItem {
        const filterItem = new FilterItem('TYPE', this.#translate.instant('MEMBER_ORDER.TYPE'));
        const value = this.filtersForm.value.type;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translate.instant(valueItem.name)));
            filterItem.urlQueryParams['type'] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

    private getFilterState(): FilterItem {
        const filterItem = new FilterItem('STATE', this.#translate.instant('MEMBER_ORDER.STATE'));
        const value = this.filtersForm.value.state;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translate.instant(valueItem.name)));
            filterItem.urlQueryParams['state'] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

    private getFilterChannelEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('CHANNEL_ENTITY')
            .labelKey('MEMBER_ORDER.CHANNEL_ENTITY')
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
}
