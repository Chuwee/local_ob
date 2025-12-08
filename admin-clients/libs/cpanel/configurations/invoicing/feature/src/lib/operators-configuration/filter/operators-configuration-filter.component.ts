import { InvoicingEntityOperatorTypes } from '@admin-clients/cpanel-configurations-invoicing-data-access';
import { FilterItem, FilterItemValue, FilterWrapped } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';

@Component({
    selector: 'app-operators-configuration-filter',
    templateUrl: './operators-configuration-filter.component.html',
    styleUrl: './operators-configuration-filter.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, ReactiveFormsModule, TranslatePipe, FlexModule, MaterialModule
    ]
})
export class OperatorsInvoicingConfigurationFilterComponent extends FilterWrapped {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translate = inject(TranslateService);

    readonly filtersForm = this.#fb.group({
        type: this.#fb.group({
            event: false,
            channel: false
        })
    });

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = {
            type: (params['type']?.split(',')
                .reduce((prev, curr) => {
                    prev[curr.toLowerCase()] = true;
                    return prev;
                }, {})) || {}
        };

        this.filtersForm.patchValue(formFields, { emitEvent: false });
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterOperatorType()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'TYPE') {
            const typeCtrl = this.filtersForm.controls['type'].get(String(value).toLowerCase());
            typeCtrl && typeCtrl.reset(false);
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    private getFilterOperatorType(): FilterItem {
        const filterItem = new FilterItem('TYPE', this.#translate.instant('INVOICING.OPERATORS_CONFIGURATION.TITLES.TYPE'));
        const operatorTypeForm = this.filtersForm.value.type;
        const values = Object.keys(operatorTypeForm)
            .filter(key => operatorTypeForm[key]).map(key => new FilterItemValue(InvoicingEntityOperatorTypes[key],
                `INVOICING.OPERATORS_CONFIGURATION.TITLES.${InvoicingEntityOperatorTypes[key]}`
            ));

        if (values.length) {
            filterItem.values = values;
            filterItem.urlQueryParams['type'] = values.map(valueItem => valueItem.value).join(',');
        }

        return filterItem;
    }

}
