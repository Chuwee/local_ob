import { productSaleRequestStatus } from '@admin-clients/cpanel-channels-products-sale-requests-data-access';
import { FilterItem, FilterItemValue, FilterWrapped } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { Observable, of } from 'rxjs';

@Component({
    selector: 'app-products-sale-requests-list-filter',
    imports: [
        TranslatePipe, ReactiveFormsModule,
        MatDivider, MatCheckbox, MatFormField, MatInput, MatDatepickerModule, MatIcon, MatIconButton, MatSuffix
    ],
    templateUrl: './products-sale-requests-list-filter.component.html',
    styleUrls: ['./products-sale-requests-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsSaleRequestsListFilterComponent extends FilterWrapped {
    readonly #translate = inject(TranslateService);
    readonly #formats = inject(MAT_DATE_FORMATS);
    readonly #fb = inject(FormBuilder);

    readonly filtersForm = this.#fb.group({
        status: this.#fb.group({
            accepted: false,
            pending: false,
            rejected: false
        }),
        startDate: null as string,
        endDate: null as string
    });

    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();

    getFilters(): FilterItem[] {
        return [
            this.getFilterStartDate(),
            this.getFilterEndDate(),
            this.getFilterStatus()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        } else if (key === 'STATUS') {
            const statusCheck = productSaleRequestStatus.find(status => status === value);
            console.log('statusCheck', statusCheck);
            this.filtersForm.get(`status.${statusCheck.toLowerCase()}`).reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = {
            status: {
                accepted: false,
                pending: false,
                rejected: false
            },
            startDate: null,
            endDate: null
        };

        if (params['status']) {
            params['status'].split(',').forEach((statusValue: string) => {
                const statusKey = productSaleRequestStatus.find(status => status === statusValue);
                if (statusKey) {
                    formFields.status[statusValue.toLowerCase()] = true;
                }
            });
        }
        if (params['startDate']) {
            formFields.startDate = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields.endDate = moment(params['endDate']).tz(moment().tz()).format();
        }
        this.filtersForm.patchValue(formFields, { emitEvent: false });
        return of(this.getFilters());
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this.#translate.instant('PRODUCTS_SALE_REQUESTS.FORMS.LABELS.DATE_FROM'));
        const value = this.filtersForm.value.startDate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE', this.#translate.instant('PRODUCTS_SALE_REQUESTS.FORMS.LABELS.DATE_TO'));
        const value = this.filtersForm.value.endDate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this.#translate.instant('PRODUCTS_SALE_REQUESTS.FORMS.LABELS.STATUS'));
        const value = this.filtersForm.value.status;
        const mappedValues = Object.keys(value).filter(statusCheck => value[statusCheck]);
        if (mappedValues.length > 0) {
            filterItem.values = mappedValues.map(statusCheck => new FilterItemValue(
                statusCheck.toUpperCase(),
                this.#translate.instant(`PRODUCTS_SALE_REQUESTS.FORMS.STATUS_OPTS.${statusCheck.toUpperCase()}`)
            ));
            filterItem.urlQueryParams['status'] = mappedValues.map(statusCheck => statusCheck.toUpperCase()).join(',');
        }
        return filterItem;
    }
}
