import { FilterWrapped, FilterItem, FilterItemValue, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldOnServerStream$ } from '@admin-clients/shared/utility/utils';
import { MatchingStatus } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { filter, forkJoin, map, Observable, shareReplay, Subject } from 'rxjs';
import { MatchingsService } from '../../matchings.service';

@Component({
    imports: [CommonModule, TranslatePipe, ReactiveFormsModule, MaterialModule, FlexLayoutModule, SelectSearchComponent],
    selector: 'app-matchings-list-filter',
    templateUrl: './matchings-list-filter.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MatchingsListFilterComponent extends FilterWrapped implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _matchingsService = inject(MatchingsService);

    private readonly _formStructure = {
        status: null,
        country_code: null,
        shi_taxonomies: null,
        supplier_taxonomies: null,
        date: null,
        date_end: null
    };

    readonly filtersForm = this._fb.group({
        status: [null as { id: MatchingStatus; name: string }[]],
        country_code: [null as IdName[]],
        shi_taxonomies: [null as IdName[]],
        supplier_taxonomies: [null as IdName[]],
        date: '',
        date_end: ''
    });

    readonly showClearSelection$ = this.filtersForm.controls.country_code.valueChanges
        .pipe(map(value => Boolean(value?.length)));

    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();

    readonly statusList = Object.values(MatchingStatus)
        .map(type => ({ id: type, name: `MATCHINGS.STATUS_OPTS.${type}` }));

    readonly countries$ = this._matchingsService.countries.getCountriesData$().pipe(
        filter(Boolean),
        map(countriesList => countriesList.map(country => ({ id: country, name: country }))),
        shareReplay(1)
    );

    readonly shiTaxonomies$ = this._matchingsService.shiTaxonomies.get$().pipe(
        filter(Boolean),
        map(taxonomiesList => taxonomiesList.map(taxonomy => ({ id: taxonomy.name, name: taxonomy.name }))),
        shareReplay(1)
    );

    readonly supplierTaxonomies$ = this._matchingsService.supplierTaxonomies.get$().pipe(
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
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterStatus(),
            this.getFilterCountry(),
            this.getFilterShiTaxonomies(),
            this.getFilterSupplierTaxonomies(),
            this.getFilterDate(),
            this.getFilterDateEnd()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'STATUS') {
            const statusValues = this.filtersForm.controls.status.value;
            this.filtersForm.controls.status.setValue(statusValues.filter(status => status.id !== value));
        } else if (key === 'COUNTRY_CODE') {
            const countriesValues = this.filtersForm.controls.country_code.value;
            this.filtersForm.controls.country_code.setValue(countriesValues.filter(country => country.id !== value));
        } else if (key === 'SHI_TAXONOMIES') {
            const shiTaxonomiesValues = this.filtersForm.controls.shi_taxonomies.value;
            this.filtersForm.controls.shi_taxonomies.setValue(shiTaxonomiesValues.filter(taxonomy => taxonomy.id !== value));
        } else if (key === 'SUPPLIER_TAXONOMIES') {
            const supplierTaxonomiesValues = this.filtersForm.controls.supplier_taxonomies.value;
            this.filtersForm.controls.supplier_taxonomies.setValue(supplierTaxonomiesValues.filter(taxonomy => taxonomy.id !== value));
        } else if (key === 'DATE') {
            this.filtersForm.controls.date.reset();
        } else if (key === 'DATE_END') {
            this.filtersForm.controls.date_end.reset();
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
        if (params['status']) {
            formFields.status = params['status'].split(';;').map(status =>
                this.statusList.find(statusObj => statusObj.id === status) || null
            );
        }
        if (params['date']) {
            formFields.date = params['date'];
        }
        if (params['date_end']) {
            formFields.date_end = params['date_end'];
        }

        const selectedCountries = params['country_code']?.split(';;');
        const selectedShiTaxonomies = params['shi_taxonomies']?.split(';;');
        const selectedSupplierTaxonomies = params['supplier_taxonomies']?.split(';;');

        const asyncFields = [
            applyAsyncFieldOnServerStream$(formFields, 'country_code', selectedCountries, this.countries$),
            applyAsyncFieldOnServerStream$(formFields, 'shi_taxonomies', selectedShiTaxonomies, this.shiTaxonomies$),
            applyAsyncFieldOnServerStream$(formFields, 'supplier_taxonomies', selectedSupplierTaxonomies, this.supplierTaxonomies$)
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

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this._translate.instant('MATCHINGS.STATUS'));
        const value = this.filtersForm.value.status;

        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this._translate.instant(valueItem.name)));
            filterItem.urlQueryParams['status'] = value.map(valueItem => valueItem.id).join(';;');
        }
        return filterItem;
    }

    private getFilterCountry(): FilterItem {
        const filterItem = new FilterItem('COUNTRY_CODE', this._translate.instant('MATCHINGS.COUNTRY_CODE'));
        const value = this.filtersForm.value.country_code;
        if (value?.length > 0) {
            filterItem.values = value.filter(Boolean).map(valueItem => new FilterItemValue(valueItem.id, valueItem.name));
            filterItem.urlQueryParams['country_code'] = value.map(valueItem => valueItem?.id).join(';;');
        }
        return filterItem;
    }

    private getFilterShiTaxonomies(): FilterItem {
        const filterItem = new FilterItem('SHI_TAXONOMIES', this._translate.instant('MATCHINGS.MATCHING_LIST_FILTER.SHI_TAXONOMIES'));
        const value = this.filtersForm.value.shi_taxonomies;
        if (value?.length > 0) {
            filterItem.values = value.filter(Boolean).map(valueItem => new FilterItemValue(valueItem.id, valueItem.name));
            filterItem.urlQueryParams['shi_taxonomies'] = value.map(valueItem => valueItem?.id).join(';;');
        }
        return filterItem;
    }

    private getFilterSupplierTaxonomies(): FilterItem {
        const filterItem = new FilterItem(
            'SUPPLIER_TAXONOMIES', this._translate.instant('MATCHINGS.MATCHING_LIST_FILTER.SUPPLIER_TAXONOMIES')
        );
        const value = this.filtersForm.value.supplier_taxonomies;
        if (value?.length > 0) {
            filterItem.values = value.filter(Boolean).map(valueItem => new FilterItemValue(valueItem.id, valueItem.name));
            filterItem.urlQueryParams['supplier_taxonomies'] = value.map(valueItem => valueItem?.id).join(';;');
        }
        return filterItem;
    }

    private getFilterDate(): FilterItem {
        const filterItem = new FilterItem('DATE', this._translate.instant('MATCHINGS.DATE'));
        const value = this.filtersForm.value.date;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['date'] = valueId;
        }
        return filterItem;
    }

    private getFilterDateEnd(): FilterItem {
        const filterItem = new FilterItem('DATE_END', this._translate.instant('MATCHINGS.DATE_END'));
        const value = this.filtersForm.value.date_end;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['date_end'] = valueId;
        }
        return filterItem;
    }
}
