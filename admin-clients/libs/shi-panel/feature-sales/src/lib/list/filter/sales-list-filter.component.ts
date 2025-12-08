import {
    FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldOnServerStream$ } from '@admin-clients/shared/utility/utils';
import { SuppliersApi, SuppliersService, SuppliersState } from '@admin-clients/shi-panel/data-access-grant-suppliers';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MAT_DATE_FORMATS, MatDateFormats, MatOption } from '@angular/material/core';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { filter, forkJoin, map, Observable, shareReplay } from 'rxjs';
import { DeliveryMethod } from '../../models/delivery-method.enum';
import { SaleStatus } from '../../models/sale-status.enum';
import { SalesService } from '../../sales.service';

@Component({
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, SelectSearchComponent, MatFormField, MatInput, MatDatepickerInput, MatIconButton,
        MatIcon, MatDatepicker, MatSelect, MatOption, EllipsifyDirective, MatTooltipModule
    ],
    selector: 'app-sales-list-filter',
    templateUrl: './sales-list-filter.component.html',
    styleUrls: ['./sales-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SuppliersService, SuppliersApi, SuppliersState]
})
export class SalesListFilterComponent extends FilterWrapped implements OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #suppliersService = inject(SuppliersService);
    readonly #salesService = inject(SalesService);
    readonly #translateSrv = inject(TranslateService);
    readonly #formats = inject<MatDateFormats>(MAT_DATE_FORMATS);

    readonly #formStructure = {
        startDate: null,
        endDate: null,
        startDateUpdate: null,
        endDateUpdate: null,
        startInHandDate: null,
        endInHandDate: null,
        delivery_method: null,
        status: null,
        supplier: null,
        country: null,
        currency: null,
        taxonomies: null,
        daysToEventLte: null,
        daysToEventGte: null,
        last_error_description: null
    };

    readonly filtersForm = this.#fb.group({
        startDate: null,
        endDate: null,
        startDateUpdate: null as string,
        endDateUpdate: null as string,
        startInHandDate: null as string,
        endInHandDate: null as string,
        delivery_method: [null as { id: DeliveryMethod; name: string }[]],
        status: [null as { id: SaleStatus; name: string }[]],
        supplier: [null as { id: SupplierName; name: string }[]],
        country: [null as { id: string; name: string }[]],
        currency: [null as { id: string; name: string }[]],
        taxonomies: [null as { id: string; name: string }[]],
        daysToEventLte: null as number,
        daysToEventGte: null as number,
        last_error_description: [null as { id: string; name: string }[]]
    });

    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();

    readonly statusList = Object.values(SaleStatus)
        .map(type => ({ id: type, name: `SALES.STATUS_OPTS.${type}` }));

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

    readonly lastErrors$ = this.#salesService.lastErrors.get$().pipe(
        filter(Boolean),
        map(errorsList => errorsList.map(error => ({ id: error.name, name: error.name }))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly countries$ = this.#salesService.countries.getData$().pipe(
        filter(Boolean),
        map(countriesList => countriesList.map(country => ({ id: country.code, name: country.code }))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly showClearCountriesSelection$ = this.filtersForm.controls.country.valueChanges
        .pipe(map(value => Boolean(value?.length)));

    readonly showClearLastErrorsSelection$ = this.filtersForm.controls.last_error_description.valueChanges
        .pipe(map(value => Boolean(value?.length)));

    readonly currencies$ = this.#salesService.currencies.getData$().pipe(
        filter(Boolean),
        map(currenciesList => currenciesList.map(currency => ({ id: currency.code, name: currency.code }))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly showTaxonomyClearSelection$ = this.filtersForm.controls.country.valueChanges
        .pipe(map(value => Boolean(value?.length)));

    readonly taxonomies$ = this.#salesService.taxonomies.getData$().pipe(
        filter(Boolean),
        map(taxonomiesList => taxonomiesList.map(taxonomy => ({ id: taxonomy.name, name: taxonomy.name }))),
        shareReplay(1)
    );

    constructor() {
        super();

        this.#suppliersService.suppliers.load();
        this.#salesService.deliveryMethods.load();
        this.#salesService.countries.load();
        this.#salesService.currencies.load();
        this.#salesService.taxonomies.load();
        this.#salesService.lastErrors.load();
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    getFilters(): FilterItem[] {
        return [
            this.#getFilterStartDateUpdate(),
            this.#getFilterEndDateUpdate(),
            this.#getFilterStartInHandDate(),
            this.#getFilteredInHandDate(),
            this.#getFilterStatus(),
            this.#getFilterDelivery(),
            this.#getFilterSupplier(),
            this.#getFilterCountry(),
            this.#getFilterCurrency(),
            this.#getFilterTaxonomies(),
            this.#getFilterDaysToEventLte(),
            this.#getFilterDaysToEventGte(),
            this.#getFilterLastError()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'STATUS') {
            const statusForm = this.filtersForm.controls.status;
            const statusValues = statusForm.value;
            statusForm.setValue(statusValues.filter(status => status.id !== value));
        } else if (key === 'DELIVERY') {
            const deliveryForm = this.filtersForm.controls.delivery_method;
            const deliveryValues = deliveryForm.value;
            deliveryForm.setValue(deliveryValues.filter(delivery => delivery.id !== value));
        } else if (key === 'SUPPLIER') {
            const supplierForm = this.filtersForm.controls.supplier;
            const supplierValues = supplierForm.value;
            supplierForm.setValue(supplierValues.filter(supplier => supplier.id !== value));
        } else if (key === 'START_DATE_UPDATE') {
            this.filtersForm.controls.startDateUpdate.reset();
        } else if (key === 'END_DATE_UPDATE') {
            this.filtersForm.controls.endDateUpdate.reset();
        } else if (key === 'START_DATE_INHAND') {
            this.filtersForm.controls.startInHandDate.reset();
        } else if (key === 'END_DATE_INHAND') {
            this.filtersForm.controls.endInHandDate.reset();
        } else if (key === 'COUNTRY') {
            const countriesForm = this.filtersForm.controls.country;
            const countriesValues = countriesForm.value;
            countriesForm.setValue(countriesValues.filter(countries => countries.id !== value));
        } else if (key === 'CURRENCY') {
            const currencyForm = this.filtersForm.controls.currency;
            const currencyValues = currencyForm.value;
            currencyForm.setValue(currencyValues.filter(currency => currency.id !== value));
        } else if (key === 'TAXONOMIES') {
            const taxonomiesValues = this.filtersForm.controls.taxonomies.value;
            this.filtersForm.controls.taxonomies.setValue(taxonomiesValues.filter(taxonomy => taxonomy.id !== value), { emitEvent: false });
        } else if (key === 'DAYS_TO_EVENT_LTE') {
            this.filtersForm.controls.daysToEventLte.reset(null, { emitEvent: false });
        } else if (key === 'DAYS_TO_EVENT_GTE') {
            this.filtersForm.controls.daysToEventGte.reset(null, { emitEvent: false });
        } else if (key === 'LAST_ERROR') {
            const lastErrorForm = this.filtersForm.controls.last_error_description;
            const lastErrorValues = lastErrorForm.value;
            lastErrorForm.setValue(lastErrorValues.filter(error => error.name !== value));
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
        if (params['startDateUpdate']) {
            formFields.startDateUpdate = params['startDateUpdate'];
        }
        if (params['endDateUpdate']) {
            formFields.endDateUpdate = params['endDateUpdate'];
        }
        if (params['startInHandDate']) {
            formFields.startInHandDate = params['startInHandDate'];
        }
        if (params['endInHandDate']) {
            formFields.endInHandDate = params['endInHandDate'];
        }
        if (params['status']) {
            formFields.status = params['status'].split(';;').map(status =>
                this.statusList.find(statusObj => statusObj.id === status) || null
            );
        }
        if (params['daysToEventLte']) {
            formFields.daysToEventLte = Number(params['daysToEventLte']);
        }
        if (params['daysToEventGte']) {
            formFields.daysToEventGte = Number(params['daysToEventGte']);
        }

        const selectedSuppliers = params['supplier']?.split(';;') || null;
        const selectedDeliveryMethods = params['delivery_method']?.split(';;') || null;
        const selectedCountries = params['country']?.split(';;') || null;
        const selectedCurrencies = params['currency']?.split(';;') || null;
        const selectedTaxonomies = params['taxonomies']?.split(';;') || null;
        const selectedLatestErrors = params['last_error_description']?.split(';;') || null;

        const asyncFields = [
            applyAsyncFieldOnServerStream$(formFields, 'supplier', selectedSuppliers, this.suppliers$),
            applyAsyncFieldOnServerStream$(formFields, 'delivery_method', selectedDeliveryMethods, this.deliveryMethods$),
            applyAsyncFieldOnServerStream$(formFields, 'country', selectedCountries, this.countries$),
            applyAsyncFieldOnServerStream$(formFields, 'currency', selectedCurrencies, this.currencies$),
            applyAsyncFieldOnServerStream$(formFields, 'taxonomies', selectedTaxonomies, this.taxonomies$),
            applyAsyncFieldOnServerStream$(formFields, 'last_error_description', selectedLatestErrors, this.lastErrors$)
        ];

        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    deselectAllCountries(): void {
        this.filtersForm.controls.country.reset([]);
    }

    deselectAllTaxonomies(): void {
        this.filtersForm.controls.taxonomies.reset([], { emitEvent: false });
    }

    deselectAllLastErrors(): void {
        this.filtersForm.controls.last_error_description.reset([]);
    }

    #getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this.#translateSrv.instant('SALES.STATUS'));
        const value = this.filtersForm.value.status;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['status'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    #getFilterDelivery(): FilterItem {
        const filterItem = new FilterItem('DELIVERY', this.#translateSrv.instant('SALES.DELIVERY_METHOD'));
        const value = this.filtersForm.value.delivery_method;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['delivery_method'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    #getFilterLastError(): FilterItem {
        const filterItem = new FilterItem('LAST_ERROR', this.#translateSrv.instant('SALES.LAST_ERROR'));
        const value = this.filtersForm.value.last_error_description;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, valueItem.name));
            filterItem.urlQueryParams['last_error_description'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    #getFilterSupplier(): FilterItem {
        const filterItem = new FilterItem('SUPPLIER', this.#translateSrv.instant('SALES.SUPPLIER'));
        const value = this.filtersForm.value.supplier;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['supplier'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    #getFilterStartDateUpdate(): FilterItem {
        const filterItem = new FilterItem('START_DATE_UPDATE', this.#translateSrv.instant('SALES.DATE_FROM'));
        const value = this.filtersForm.value.startDateUpdate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDateUpdate'] = valueId;
        }
        return filterItem;
    }

    #getFilterEndDateUpdate(): FilterItem {
        const filterItem = new FilterItem('END_DATE_UPDATE', this.#translateSrv.instant('SALES.DATE_TO'));
        const value = this.filtersForm.value.endDateUpdate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDateUpdate'] = valueId;
        }
        return filterItem;
    }

    #getFilterStartInHandDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE_INHAND', this.#translateSrv.instant('SALES.DATE_FROM'));
        const value = this.filtersForm.value.startInHandDate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startInHandDate'] = valueId;
        }
        return filterItem;
    }

    #getFilteredInHandDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE_INHAND', this.#translateSrv.instant('SALES.DATE_TO'));
        const value = this.filtersForm.value.endInHandDate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endInHandDate'] = valueId;
        }
        return filterItem;
    }

    #getFilterCountry(): FilterItem {
        const filterItem = new FilterItem('COUNTRY', this.#translateSrv.instant('SALES.COUNTRY'));
        const value = this.filtersForm.value.country;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['country'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    #getFilterCurrency(): FilterItem {
        const filterItem = new FilterItem('CURRENCY', this.#translateSrv.instant('SALES.CURRENCY'));
        const value = this.filtersForm.value.currency;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translateSrv.instant(valueItem.name)));
            filterItem.urlQueryParams['currency'] = value.map(valueItem => valueItem?.id).join(';;');
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
