import { FilterWrapped, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
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
import { filter, map, Observable, shareReplay, Subject } from 'rxjs';
import { ListingStatus } from '../../models/listing-status.enum';

@Component({
    imports: [CommonModule, TranslatePipe, ReactiveFormsModule, MaterialModule, FlexLayoutModule],
    selector: 'app-listings-list-filter',
    templateUrl: './listings-list-filter.component.html',
    styleUrls: ['./listings-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SuppliersService, SuppliersApi, SuppliersState]
})
export class ListingsListFilterComponent extends FilterWrapped implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _suppliersService = inject(SuppliersService);

    private readonly _formStructure = {
        startDateListing: null,
        endDateListing: null,
        startDateUpdate: null,
        endDateUpdate: null,
        status: null,
        supplier: null
    };

    readonly filtersForm = this._fb.group({
        startDateListing: null as string,
        endDateListing: null as string,
        startDateUpdate: null as string,
        endDateUpdate: null as string,
        status: [null as { id: ListingStatus; name: string }[]],
        supplier: [null as { id: SupplierName; name: string }[]]
    });

    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();

    readonly statusList = Object.values(ListingStatus)
        .map(type => ({ id: type, name: `LISTINGS.STATUS_OPTS.${type}` }));

    readonly suppliers$ = this._suppliersService.suppliers.getSuppliers$().pipe(
        filter(Boolean),
        map(suppliersList => suppliersList.map(supplier => ({ id: supplier.name, name: `LISTINGS.SUPPLIER_OPTS.${supplier.name}` }))),
        shareReplay(1)
    );

    constructor(
        private _translate: TranslateService,
        private _fb: FormBuilder,
        @Inject(MAT_DATE_FORMATS) private readonly _formats: MatDateFormats
    ) {
        super();

        this._suppliersService.suppliers.load();
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterStartDateListing(),
            this.getFilterEndDateListing(),
            this.getFilterStatus(),
            this.getFilterSupplier()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'STATUS') {
            const statusForm = this.filtersForm.controls.status;
            const statusValues = statusForm.value;
            statusForm.setValue(statusValues.filter(status => status.id !== value));
        } else if (key === 'SUPPLIER') {
            const supplierForm = this.filtersForm.controls.supplier;
            const supplierValues = supplierForm.value;
            supplierForm.setValue(supplierValues.filter(supplier => supplier.id !== value));
        } else if (key === 'START_DATE_LISTING') {
            this.filtersForm.controls.startDateListing.reset();
        } else if (key === 'END_DATE_LISTING') {
            this.filtersForm.controls.endDateListing.reset();
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
        if (params['startDateListing']) {
            formFields.startDateListing = params['startDateListing'];
        }
        if (params['endDateListing']) {
            formFields.endDateListing = params['endDateListing'];
        }
        if (params['status']) {
            formFields.status = params['status'].split(',').map(status =>
                this.statusList.find(statusObj => statusObj.id === status) || null
            );
        }

        const selectedSuppliers = params['supplier']?.split(',') || null;
        return applyAsyncFieldOnServerStream$(formFields, 'supplier', selectedSuppliers, this.suppliers$).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );

    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this._translate.instant('LISTINGS.STATUS'));
        const value = this.filtersForm.value.status;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this._translate.instant(valueItem.name)));
            filterItem.urlQueryParams['status'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    private getFilterSupplier(): FilterItem {
        const filterItem = new FilterItem('SUPPLIER', this._translate.instant('LISTINGS.SUPPLIER'));
        const value = this.filtersForm.value.supplier;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this._translate.instant(valueItem.name)));
            filterItem.urlQueryParams['supplier'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    private getFilterStartDateListing(): FilterItem {
        const filterItem = new FilterItem('START_DATE_LISTING', this._translate.instant('LISTINGS.DATE_FROM'));
        const value = this.filtersForm.value.startDateListing;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDateListing'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDateListing(): FilterItem {
        const filterItem = new FilterItem('END_DATE_LISTING', this._translate.instant('LISTINGS.DATE_TO'));
        const value = this.filtersForm.value.endDateListing;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDateListing'] = valueId;
        }
        return filterItem;
    }
}
