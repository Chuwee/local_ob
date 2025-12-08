import { B2bClientTransactionType } from '@admin-clients/cpanel/b2b/data-access';
import { FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { first, takeUntil } from 'rxjs/operators';

interface EconomicManagementFilterForm {
    transactionType: {
        id: B2bClientTransactionType;
        name: string;
    };
}
@Component({
    selector: 'app-b2b-client-economic-management-filter',
    templateUrl: './b2b-client-economic-management-filter.component.html',
    styleUrls: ['./b2b-client-economic-management-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientEconomicManagementFilterComponent extends FilterWrapped implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _entityId: number;
    private readonly _formStructure: EconomicManagementFilterForm = {
        transactionType: null
    };

    private _fb = inject(FormBuilder);
    private _translate = inject(TranslateService);

    filtersForm = this._fb.group(this._formStructure);
    @Input() operatorMode$: Observable<boolean>;
    @Input() entityId$: Observable<number>;
    transactionTypes = Object.values(B2bClientTransactionType)
        .map(transactionType => (
            { id: transactionType, name: `B2B_CLIENTS.ECONOMIC_MANAGEMENT.TRANSACTIONS.TRANSACTION_TYPES.${transactionType}` }
        ));

    constructor() {
        super();
    }

    ngOnInit(): void {
        combineLatest([
            this.operatorMode$,
            this.entityId$
        ])
            .pipe(
                first(data => data.every(Boolean)),
                takeUntil(this._onDestroy)
            )
            .subscribe(([isOperator, entityId]) => {
                if (isOperator) {
                    this._entityId = entityId;
                }
            });
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterTransactionType(),
            this.getFilterEntity()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'TRANSACTION_TYPE') {
            this.filtersForm.get('transactionType').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);

        if (params['transactionType']) {
            formFields.transactionType = this.transactionTypes.find(
                transactionTypesObj => transactionTypesObj.id === params['transactionType']
            );
        }

        this.filtersForm.patchValue(formFields, { emitEvent: false });
        return of(this.getFilters());
    }

    private getFilterTransactionType(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('TRANSACTION_TYPE')
            .labelKey('FORMS.LABELS.TRANSACTION_TYPE')
            .queryParam('transactionType')
            .value(this.filtersForm.value.transactionType)
            .translateValue()
            .build();
    }

    private getFilterEntity(): FilterItem {
        const filterItem = new FilterItem('ENTITY', null);
        if (this._entityId) {
            filterItem.values = [new FilterItemValue(this._entityId, null)];
            filterItem.urlQueryParams['entityId'] = this._entityId.toString();
        }
        return filterItem;
    }
}
