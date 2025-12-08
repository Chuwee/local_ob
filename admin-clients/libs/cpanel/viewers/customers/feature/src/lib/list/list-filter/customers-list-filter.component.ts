/* eslint-disable @typescript-eslint/dot-notation */
import { CustomersService, CustomerStatus, CustomerType } from '@admin-clients/cpanel-viewers-customers-data-access';
import { FilterWrapped, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { Observable, of } from 'rxjs';

@Component({
    selector: 'app-customers-list-filter',
    templateUrl: './customers-list-filter.component.html',
    styleUrls: ['./customers-list-filter.component.scss'],
    imports: [ReactiveFormsModule, TranslatePipe, FlexLayoutModule, MatButtonModule,
        MatDatepickerModule, MatInputModule, MatFormFieldModule, MatSelectModule, MatIconModule
    ],
    providers: [
        CustomersService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomersListFilterComponent extends FilterWrapped {
    private readonly _formStructure = this.getFormStructure();

    readonly filtersForm = this.getForm(this._formStructure);
    readonly types = Object.values(CustomerType)
        .map(type => ({ id: type, name: `CUSTOMER.TYPE_OPTS.${type}` }));

    readonly statusList = Object.values(CustomerStatus)
        .map(status => ({ id: status, name: `CUSTOMER.STATUS_OPTS.${status}` }));

    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();

    constructor(
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService,
        @Inject(MAT_DATE_FORMATS) private readonly _formats: MatDateFormats
    ) {
        super();
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);

        if (params['entity']) {
            formFields['entity'] = this.types.find(typeObj => typeObj.id === params['entity']);
        }
        if (params['status']) {
            formFields['status'] = params['status'].split(',').map(status =>
                this.statusList.find(statusObj => statusObj.id === status) || null
            );
        }
        if (params['startDate']) {
            formFields['startDate'] = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields['endDate'] = moment(params['endDate']).tz(moment().tz()).format();
        }

        this.filtersForm.patchValue(formFields, { emitEvent: false });
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterStatus(),
            this.getFilterStartDate(),
            this.getFilterEndDate()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'STATUS') {
            const statusForm = this.filtersForm.get('status');
            const statusValues = statusForm.value;
            statusForm.setValue(statusValues.filter(status => status.id !== value));
        } else if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        }
    }

    private getFormStructure(): { [key: string]: unknown } {
        return {
            status: null,
            startDate: null,
            endDate: null
        };
    }

    private getForm(formStructure: { [key: string]: unknown }): UntypedFormGroup {
        return this._fb.group(Object.assign({}, formStructure));
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this._translate.instant('CUSTOMER.STATUS'));
        const value = this.filtersForm.value.status;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this._translate.instant(valueItem.name)));
            filterItem.urlQueryParams['status'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this._translate.instant('DATES.DATE_FROM'));
        const value = this.filtersForm.value.startDate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE', this._translate.instant('DATES.DATE_TO'));
        const value = this.filtersForm.value.endDate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDate'] = valueId;
        }
        return filterItem;
    }
}
