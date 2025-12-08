import {
    FilterWrapped, FilterItem, FilterItemValue, SelectSearchComponent, FilterItemBuilder
} from '@admin-clients/shared/common/ui/components';
import { applyAsyncFieldOnServerStream$ } from '@admin-clients/shared/utility/utils';
import { SuppliersApi, SuppliersService, SuppliersState } from '@admin-clients/shi-panel/data-access-grant-suppliers';
import { DeliveryMethod, SalesService } from '@admin-clients/shi-panel/feature-sales';
import { errorResponsibles, ErrorResponsibleType } from '@admin-clients/shi-panel/sales/error-dashboard-data-access';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, filter, forkJoin, map, of, shareReplay } from 'rxjs';

@Component({
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, SelectSearchComponent, MatFormField, MatSelect, MatOption, MatDivider, MatInput
    ],
    selector: 'app-error-dashboard-filter',
    templateUrl: './error-dashboard-filter.component.html',
    styleUrls: ['./error-dashboard-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SuppliersService, SuppliersApi, SuppliersState]
})
export class ErrorDashboardFilterComponent extends FilterWrapped implements OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #suppliersService = inject(SuppliersService);
    readonly #salesService = inject(SalesService);
    readonly #translateSrv = inject(TranslateService);

    readonly #formStructure = {
        delivery_method: null,
        supplier: null,
        country: null,
        responsible: null,
        currency: null,
        taxonomies: null,
        daysToEventLte: null,
        daysToEventGte: null
    };

    readonly filtersForm = this.#fb.group({
        delivery_method: [null as { id: DeliveryMethod; name: string }[]],
        supplier: [null as { id: SupplierName; name: string }[]],
        country: [null as { id: string; name: string }[]],
        responsible: [null as { id: ErrorResponsibleType; name: string }[]],
        currency: [null as { id: string; name: string }[]],
        taxonomies: [null as { id: string; name: string }[]],
        daysToEventLte: null as number,
        daysToEventGte: null as number
    });

    readonly showClearSelection$ = this.filtersForm.controls.country.valueChanges
        .pipe(map(value => Boolean(value?.length)));

    readonly showTaxonomyClearSelection$ = this.filtersForm.controls.country.valueChanges
        .pipe(map(value => Boolean(value?.length)));

    readonly suppliers$ = this.#suppliersService.suppliers.getSuppliers$().pipe(
        filter(Boolean),
        map(suppliersList =>
            suppliersList.map(supplier => ({ id: supplier.name, name: `SALES.SUPPLIER_OPTS.${supplier.name}` }))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly deliveryMethods$ = this.#salesService.deliveryMethods.get$().pipe(
        filter(Boolean),
        map(methodsList =>
            methodsList.map(method => ({ id: method.name, name: `SALES.DELIVERY_OPTS.${method.name.toUpperCase()}` }))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly countries$ = this.#salesService.countries.getData$().pipe(
        filter(Boolean),
        map(countriesList => countriesList.map(country => ({ id: country.code, name: country.code }))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly currencies$ = this.#salesService.currencies.getData$().pipe(
        filter(Boolean),
        map(currenciesList => currenciesList.map(currency => ({ id: currency.code, name: currency.code }))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly taxonomies$ = this.#salesService.taxonomies.getData$().pipe(
        filter(Boolean),
        map(taxonomiesList => taxonomiesList.map(taxonomy => ({ id: taxonomy.name, name: taxonomy.name }))),
        shareReplay(1)
    );

    readonly errorResponsibles$ = of(errorResponsibles.map(responsible => ({ id: responsible, name: responsible })));

    constructor() {
        super();

        this.#suppliersService.suppliers.load();
        this.#salesService.deliveryMethods.load();
        this.#salesService.countries.load();
        this.#salesService.currencies.load();
        this.#salesService.taxonomies.load();
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    getFilters(): FilterItem[] {
        return [
            this.#getFilterDelivery(),
            this.#getFilterSupplier(),
            this.#getFilterCountry(),
            this.#getFilterResponsible(),
            this.#getFilterCurrency(),
            this.#getFilterTaxonomies(),
            this.#getFilterDaysToEventLte(),
            this.#getFilterDaysToEventGte()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'DELIVERY') {
            const deliveryForm = this.filtersForm.controls.delivery_method;
            const deliveryValues = deliveryForm.value;
            deliveryForm.setValue(deliveryValues.filter(delivery => delivery.id !== value), { emitEvent: false });
        } else if (key === 'SUPPLIER') {
            const supplierForm = this.filtersForm.controls.supplier;
            const supplierValues = supplierForm.value;
            supplierForm.setValue(supplierValues.filter(supplier => supplier.id !== value), { emitEvent: false });
        } else if (key === 'COUNTRY') {
            const countriesForm = this.filtersForm.controls.country;
            const countriesValues = countriesForm.value;
            countriesForm.setValue(countriesValues.filter(countries => countries.id !== value), { emitEvent: false });
        } else if (key === 'RESPONSIBLE') {
            const responsibeForm = this.filtersForm.controls.responsible;
            const responsibleValues = responsibeForm.value;
            responsibeForm.setValue(responsibleValues.filter(responsible => responsible.id !== value), { emitEvent: false });
        } else if (key === 'CURRENCY') {
            const currencyForm = this.filtersForm.controls.currency;
            const currencyValues = currencyForm.value;
            currencyForm.setValue(currencyValues.filter(currency => currency.id !== value), { emitEvent: false });
        } else if (key === 'TAXONOMIES') {
            const taxonomiesValues = this.filtersForm.controls.taxonomies.value;
            this.filtersForm.controls.taxonomies.setValue(taxonomiesValues.filter(taxonomy => taxonomy.id !== value), { emitEvent: false });
        } else if (key === 'DAYS_TO_EVENT_LTE') {
            this.filtersForm.controls.daysToEventLte.reset(null, { emitEvent: false });
        } else if (key === 'DAYS_TO_EVENT_GTE') {
            this.filtersForm.controls.daysToEventGte.reset(null, { emitEvent: false });
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    compareById(option: { id: string; name: string }, selected: { id: string; name: string }): boolean {
        return option?.id === selected?.id;
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);

        const selectedSuppliers = params['supplier']?.split(',') || null;
        const selectedDeliveryMethods = params['delivery_method']?.split(',') || null;
        const selectedCountries = params['country']?.split(',') || null;
        const selectedResponsibles = params['responsible']?.split(',') || null;
        const selectedCurrencies = params['currency']?.split(',') || null;
        const selectedTaxonomies = params['taxonomies']?.split(';;') || null;

        if (params['daysToEventLte']) {
            formFields.daysToEventLte = Number(params['daysToEventLte']);
        }
        if (params['daysToEventGte']) {
            formFields.daysToEventGte = Number(params['daysToEventGte']);
        }

        const asyncFields = [
            applyAsyncFieldOnServerStream$(formFields, 'supplier', selectedSuppliers, this.suppliers$),
            applyAsyncFieldOnServerStream$(formFields, 'delivery_method', selectedDeliveryMethods, this.deliveryMethods$),
            applyAsyncFieldOnServerStream$(formFields, 'country', selectedCountries, this.countries$),
            applyAsyncFieldOnServerStream$(formFields, 'responsible', selectedResponsibles, this.errorResponsibles$),
            applyAsyncFieldOnServerStream$(formFields, 'currency', selectedCurrencies, this.currencies$),
            applyAsyncFieldOnServerStream$(formFields, 'taxonomies', selectedTaxonomies, this.taxonomies$)
        ];

        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    deselectAllCountries(): void {
        this.filtersForm.controls.country.reset([], { emitEvent: false });
    }

    deselectAllTaxonomies(): void {
        this.filtersForm.controls.taxonomies.reset([], { emitEvent: false });
    }

    #getFilterDelivery(): FilterItem {
        const filterItem = new FilterItem('DELIVERY', this.#translateSrv.instant('SALES.DELIVERY_METHOD'));
        const value = this.filtersForm.value.delivery_method;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['delivery_method'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    #getFilterSupplier(): FilterItem {
        const filterItem = new FilterItem('SUPPLIER', this.#translateSrv.instant('SALES.SUPPLIER'));
        const value = this.filtersForm.value.supplier;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['supplier'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    #getFilterCountry(): FilterItem {
        const filterItem = new FilterItem('COUNTRY', this.#translateSrv.instant('SALES.COUNTRY'));
        const value = this.filtersForm.value.country;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['country'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    #getFilterResponsible(): FilterItem {
        const filterItem = new FilterItem('RESPONSIBLE', this.#translateSrv.instant('ERROR_SETTINGS.RESPONSIBLE'));
        const value = this.filtersForm.value.responsible;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['responsible'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    #getFilterCurrency(): FilterItem {
        const filterItem = new FilterItem('CURRENCY', this.#translateSrv.instant('SALES.CURRENCY'));
        const value = this.filtersForm.value.currency;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['currency'] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

    #getFilterTaxonomies(): FilterItem {
        const filterItem = new FilterItem('TAXONOMIES', this.#translateSrv.instant('MAPPINGS.TAXONOMIES'));
        const value = this.filtersForm.value.taxonomies;
        if (value?.length > 0) {
            filterItem.values = value.filter(i => !!i).map(valueItem => new FilterItemValue(valueItem.id, valueItem.name));
            filterItem.urlQueryParams['taxonomies'] = value.map(valueItem => valueItem?.id).join(';;');
        }
        return filterItem;
    }

    #getFilterDaysToEventLte(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('DAYS_TO_EVENT_LTE')
            .labelKey('FORMS.LABELS.DAYS_TO_EVENT_LTE')
            .queryParam('daysToEventLte')
            .value(Number.isInteger(this.filtersForm.value.daysToEventLte) &&
                { id: String(this.filtersForm.value.daysToEventLte), name: String(this.filtersForm.value.daysToEventLte) })
            .translateValue()
            .build();
    }

    #getFilterDaysToEventGte(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('DAYS_TO_EVENT_GTE')
            .labelKey('FORMS.LABELS.DAYS_TO_EVENT_GTE')
            .queryParam('daysToEventGte')
            .value(Number.isInteger(this.filtersForm.value.daysToEventGte) &&
                { id: String(this.filtersForm.value.daysToEventGte), name: String(this.filtersForm.value.daysToEventGte) })
            .translateValue()
            .build();
    }
}
