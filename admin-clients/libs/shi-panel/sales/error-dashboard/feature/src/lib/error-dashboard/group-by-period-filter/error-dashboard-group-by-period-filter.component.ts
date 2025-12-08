import { FilterComponent, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
import { ErrorDashboardRequest } from '@admin-clients/shi-panel/sales/error-dashboard-data-access';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { Params } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatFormField, MatSelect, MatOption, ReactiveFormsModule, TranslatePipe],
    selector: 'app-error-dashboard-group-by-period-filter',
    templateUrl: './error-dashboard-group-by-period-filter.component.html',
    styleUrls: ['./error-dashboard-group-by-period-filter.component.scss']
})
export class ErrorDashboardGroupByPeriodFilterComponent extends FilterComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);

    readonly periodGroups: ErrorDashboardRequest['group_by_period'][] = ['DAY', 'WEEK', 'MONTH', 'QUARTER'];
    readonly periodGroupCtrl = inject(FormBuilder).nonNullable.control(this.periodGroups[0]);

    ngOnInit(): void {
        this.periodGroupCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.filtersSubject.next(this.getFilters());
            });
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const periodGroup = params['group-by-period'];
        if (periodGroup) {
            this.periodGroupCtrl.setValue(periodGroup, { emitEvent: false });
        } else {
            this.periodGroupCtrl.setValue(this.periodGroups[0], { emitEvent: false });
        }
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        return [this.#getFilterTimeValue()];
    }

    removeFilter(): void {
        // noop
    }

    resetFilters(): void {
        // noop
    }

    #getFilterTimeValue(): FilterItem {
        const filterItem = new FilterItem('GROUP_BY_PERIOD', null);
        filterItem.values = [new FilterItemValue(this.periodGroupCtrl.value, null)];
        filterItem.urlQueryParams = {
            ['group-by-period']: this.periodGroupCtrl.value
        };
        return filterItem;
    }
}
