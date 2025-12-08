import { booleanAttribute, Directive, inject, input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { FilterItem, FilterItemValue } from '../../../list-filters/filter-item.model';
import { FilterWrapped } from '../../../list-filters/filter-wrapped.component';
import { DateRangePickerComponent } from '../date-range-picker.component';

@Directive({
    standalone: true,
    selector: 'app-date-range-picker[appFilter]',
    exportAs: 'appFilter'
})
export class DateRangePickerFilterDirective extends FilterWrapped {
    readonly #dateRangePicker = inject(DateRangePickerComponent, { host: true });
    readonly #activatedRoute = inject(ActivatedRoute);

    disableNoDate = input(false, { transform: booleanAttribute });

    applyFiltersByUrlParams$(params: { startDate: string; endDate: string }): Observable<FilterItem[]> {
        if (!this.#dateRangePicker.setParams(params)) {
            this.resetFilters();
        }
        return of(this.getFilters());
    }

    override applyFilters(): void {
        if (this.disableNoDate && !this.#dateRangePicker.selectedDate?.end) {
            this.#activatedRoute.queryParamMap.subscribe(params => {
                this.#dateRangePicker.setParams({ startDate: params.get('startDate'), endDate: params.get('endDate') });
            });
        } else {
            this.filtersSubject.next(this.getFilters());
        }
    }

    getFilters(): FilterItem[] {
        const filterItem = new FilterItem('DATE_RANGE', null);
        if (this.#dateRangePicker.selectedDate?.end) {
            const dateRange = this.#dateRangePicker.getDateRange();
            filterItem.values = [new FilterItemValue(dateRange, this.#dateRangePicker.getDateRangeFormatted())];
            filterItem.urlQueryParams = {
                startDate: dateRange.start,
                endDate: dateRange.end
            };
        } else {
            filterItem.urlQueryParams = {
                noDate: true
            };
        }
        return [filterItem];
    }

    removeFilter(key: string): void {
        if (key === 'DATE_RANGE') {
            this.resetFilters();
        }
    }

    resetFilters(): void {
        this.#dateRangePicker.resetParams();
    }
}

