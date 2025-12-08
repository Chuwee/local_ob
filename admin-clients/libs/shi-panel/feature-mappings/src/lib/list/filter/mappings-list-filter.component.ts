import {
    FilterWrapped, FilterItem, FilterItemValue, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { applyAsyncFieldOnServerStream$ } from '@admin-clients/shared/utility/utils';
import { SuppliersApi, SuppliersService, SuppliersState } from '@admin-clients/shi-panel/data-access-grant-suppliers';
import { SupplierName } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { filter, forkJoin, map, Observable, shareReplay, Subject } from 'rxjs';
import { MappingsService } from '../../mappings.service';
import { MappingCategories } from '../../models/mapping-categories.enum';
import { MappingStatus } from '../../models/mapping-status.enum';

@Component({
    imports: [CommonModule, TranslatePipe, ReactiveFormsModule, MaterialModule, FlexLayoutModule, SelectSearchComponent],
    selector: 'app-mappings-list-filter',
    templateUrl: './mappings-list-filter.component.html',
    styleUrls: ['./mappings-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SuppliersService, SuppliersApi, SuppliersState]
})
export class MappingsListFilterComponent extends FilterWrapped implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _mappingsService = inject(MappingsService);
    private readonly _suppliersService = inject(SuppliersService);

    private readonly _formStructure = {
        startDateEvent: null,
        endDateEvent: null,
        startDateUpdate: null,
        endDateUpdate: null,
        status: null,
        supplier: null,
        category: null,
        country_code: null,
        taxonomies: null,
        favorite: null
    };

    readonly filtersForm = this._fb.group({
        startDateEvent: null as string,
        endDateEvent: null as string,
        startDateUpdate: null as string,
        endDateUpdate: null as string,
        status: [null as { id: MappingStatus; name: string }[]],
        supplier: [null as { id: SupplierName; name: string }[]],
        category: [null as { id: MappingCategories; name: string }[]],
        country_code: [null as { id: string; name: string }[]],
        taxonomies: [null as { id: string; name: string }[]],
        favorite: null as string
    });

    readonly showClearSelection$ = this.filtersForm.controls.country_code.valueChanges
        .pipe(map(value => Boolean(value?.length)));

    readonly showTaxonomyClearSelection$ = this.filtersForm.controls.country_code.valueChanges
        .pipe(map(value => Boolean(value?.length)));

    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();

    readonly statusList = Object.values(MappingStatus)
        .map(type => ({ id: type, name: `MAPPINGS.STATUS_OPTS.${type}` }));

    readonly categoriesList = Object.values(MappingCategories)
        .map(type => ({ id: type, name: `MAPPINGS.CATEGORY_OPTS.${type}` }));

    readonly countries$ = this._mappingsService.countries.getCountriesData$().pipe(
        filter(Boolean),
        map(countriesList => countriesList.map(country => ({ id: country.code, name: country.code }))),
        shareReplay(1)
    );

    readonly suppliers$ = this._suppliersService.suppliers.getSuppliers$().pipe(
        filter(Boolean),
        map(suppliersList => suppliersList.map(supplier => ({ id: supplier.name, name: `MAPPINGS.SUPPLIER_OPTS.${supplier.name}` }))),
        shareReplay(1)
    );

    readonly taxonomies$ = this._mappingsService.taxonomies.get$().pipe(
        filter(Boolean),
        map(taxonomiesList => taxonomiesList.map(taxonomy => ({ id: taxonomy.name, name: taxonomy.name }))),
        shareReplay(1)
    );

    constructor(
        private _translate: TranslateService,
        private _fb: FormBuilder,
        @Inject(MAT_DATE_FORMATS) private readonly _formats: MatDateFormats
    ) {
        super();

        this._mappingsService.countries.load();
        this._suppliersService.suppliers.load();
        this._mappingsService.taxonomies.load();
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterStartDate(),
            this.getFilterEndDate(),
            this.getFilterStartDateUpdate(),
            this.getFilterEndDateUpdate(),
            this.getFilterStatus(),
            this.getFilterSupplier(),
            this.getFilterCategory(),
            this.getFilterCountry(),
            this.getFilterTaxonomies(),
            this.getFilterFavorite()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'STATUS') {
            const statusValues = this.filtersForm.controls.status.value;
            this.filtersForm.controls.status.setValue(statusValues.filter(status => status.id !== value));
        } else if (key === 'SUPPLIER') {
            const supplierValues = this.filtersForm.controls.supplier.value;
            this.filtersForm.controls.supplier.setValue(supplierValues.filter(status => status.id !== value));
        } else if (key === 'CATEGORY') {
            const categoriesValues = this.filtersForm.controls.category.value;
            this.filtersForm.controls.category.setValue(categoriesValues.filter(category => category.id !== value));
        } else if (key === 'COUNTRY_CODE') {
            const countriesValues = this.filtersForm.controls.country_code.value;
            this.filtersForm.controls.country_code.setValue(countriesValues.filter(country => country.id !== value));
        } else if (key === 'TAXONOMIES') {
            const taxonomiesValues = this.filtersForm.controls.taxonomies.value;
            this.filtersForm.controls.taxonomies.setValue(taxonomiesValues.filter(taxonomy => taxonomy.id !== value));
        } else if (key === 'START_DATE_EVENT') {
            this.filtersForm.controls.startDateEvent.reset();
        } else if (key === 'END_DATE_EVENT') {
            this.filtersForm.controls.endDateEvent.reset();
        } else if (key === 'START_DATE_UPDATE') {
            this.filtersForm.controls.startDateUpdate.reset();
        } else if (key === 'END_DATE_UPDATE') {
            this.filtersForm.controls.endDateUpdate.reset();
        } else if (key === 'FAVORITE') {
            this.filtersForm.controls.favorite.reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    compareById(option: { id: string; name: string }, selected: { id: string; name: string }): boolean {
        return option?.id === selected?.id;
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);
        if (params['startDateEvent']) {
            formFields.startDateEvent = params['startDateEvent'];
        }
        if (params['endDateEvent']) {
            formFields.endDateEvent = params['endDateEvent'];
        }
        if (params['startDateUpdate']) {
            formFields.startDateUpdate = params['startDateUpdate'];
        }
        if (params['endDateUpdate']) {
            formFields.endDateUpdate = params['endDateUpdate'];
        }
        if (params['favorite']) {
            formFields.favorite = params['favorite'];
        }
        if (params['status']) {
            formFields.status = params['status'].split(';;').map(status =>
                this.statusList.find(statusObj => statusObj.id === status) || null
            );
        }
        if (params['category']) {
            formFields.category = params['category'].split(';;').map(category =>
                this.categoriesList.find(categoryObj => categoryObj.id === category) || null
            );
        }

        const selectedCountries = params['country_code']?.split(';;') || null;
        const selectedSuppliers = params['supplier']?.split(';;') || null;
        const selectedTaxonomies = params['taxonomies']?.split(';;') || null;
        const asyncFields = [
            applyAsyncFieldOnServerStream$(formFields, 'country_code', selectedCountries, this.countries$),
            applyAsyncFieldOnServerStream$(formFields, 'supplier', selectedSuppliers, this.suppliers$),
            applyAsyncFieldOnServerStream$(formFields, 'taxonomies', selectedTaxonomies, this.taxonomies$)
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    deselectAll(): void {
        this.filtersForm.controls.country_code.reset([]);
    }

    deselectAllTaxonomies(): void {
        this.filtersForm.controls.taxonomies.reset([]);
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this._translate.instant('MAPPINGS.STATUS'));
        const value = this.filtersForm.value.status;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this._translate.instant(valueItem.name)));
            filterItem.urlQueryParams['status'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    private getFilterSupplier(): FilterItem {
        const filterItem = new FilterItem('SUPPLIER', this._translate.instant('MAPPINGS.SUPPLIER'));
        const value = this.filtersForm.value.supplier;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this._translate.instant(valueItem.name)));
            filterItem.urlQueryParams['supplier'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    private getFilterCategory(): FilterItem {
        const filterItem = new FilterItem('CATEGORY', this._translate.instant('MAPPINGS.CATEGORY'));
        const value = this.filtersForm.value.category;
        if (value?.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this._translate.instant(valueItem.name)));
            filterItem.urlQueryParams['category'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    private getFilterCountry(): FilterItem {
        const filterItem = new FilterItem('COUNTRY_CODE', this._translate.instant('MAPPINGS.COUNTRY_CODE'));
        const value = this.filtersForm.get('country_code').value;
        if (value?.length > 0) {
            filterItem.values = value.filter(i => !!i).map(valueItem => new FilterItemValue(valueItem.id, valueItem.name));
            filterItem.urlQueryParams['country_code'] = value.map(valueItem => valueItem?.id).join(';;');
        }
        return filterItem;
    }

    private getFilterTaxonomies(): FilterItem {
        const filterItem = new FilterItem('TAXONOMIES', this._translate.instant('MAPPINGS.TAXONOMIES'));
        const value = this.filtersForm.get('taxonomies').value;
        if (value?.length > 0) {
            filterItem.values = value.filter(i => !!i).map(valueItem => new FilterItemValue(valueItem.id, valueItem.name));
            filterItem.urlQueryParams['taxonomies'] = value.map(valueItem => valueItem?.id).join(';;');
        }
        return filterItem;
    }

    private getFilterFavorite(): FilterItem {
        const filterItem = new FilterItem('FAVORITE', this._translate.instant('MAPPINGS.FAVORITE'));
        const value = this.filtersForm.value.favorite;
        if (value) {
            filterItem.values = [new FilterItemValue(value, value === 'true' ? 'MAPPINGS.MAPPINGS_LIST_FILTER.FAVORITE_YES' : 'MAPPINGS.MAPPINGS_LIST_FILTER.FAVORITE_NO')];
            filterItem.urlQueryParams['favorite'] = value;
        }
        return filterItem;
    }

    private getFilterStartDateUpdate(): FilterItem {
        const filterItem = new FilterItem('START_DATE_UPDATE', this._translate.instant('MAPPINGS.UPDATE_DATE_FROM'));
        const value = this.filtersForm.value.startDateUpdate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDateUpdate'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDateUpdate(): FilterItem {
        const filterItem = new FilterItem('END_DATE_UPDATE', this._translate.instant('MAPPINGS.UPDATE_DATE_TO'));
        const value = this.filtersForm.value.endDateUpdate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDateUpdate'] = valueId;
        }
        return filterItem;
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE_EVENT', this._translate.instant('MAPPINGS.EVENT_DATE_FROM'));
        const value = this.filtersForm.value.startDateEvent;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDateEvent'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE_EVENT', this._translate.instant('MAPPINGS.EVENT_DATE_TO'));
        const value = this.filtersForm.value.endDateEvent;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDateEvent'] = valueId;
        }
        return filterItem;
    }
}
