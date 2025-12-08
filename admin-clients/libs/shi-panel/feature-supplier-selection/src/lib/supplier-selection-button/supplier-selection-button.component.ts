import { FilterComponent, FilterItem, FilterItemValue, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute, Params } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { filter } from 'rxjs/operators';
import { SupplierSelectionDialogComponent } from '../supplier-selection-dialog/supplier-selection-dialog.component';

@Component({
    imports: [CommonModule, TranslatePipe, MaterialModule],
    selector: 'app-supplier-selection-button',
    templateUrl: './supplier-selection-button.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SupplierSelectionButtonComponent extends FilterComponent {
    readonly #route = inject(ActivatedRoute);
    readonly #filterItem: FilterItem;

    #selectedSupplierName = new BehaviorSubject<string>(null);
    #supplier: string;

    selectedSupplierName$ = this.#selectedSupplierName.asObservable();
    @Output() valueChanged = new EventEmitter<string>();
    @Input()
    set supplier(supplier: string) {
        this.#selectedSupplierName.next(supplier);
    }

    constructor(
        private _matDialog: MatDialog
    ) {
        super();
        this.#filterItem = new FilterItem('SUPPLIER', null);
    }

    openSupplierSelectionDialog(): void {
        this._matDialog.open<SupplierSelectionDialogComponent, MatDialogConfig, string>(
            SupplierSelectionDialogComponent, new ObMatDialogConfig()
        ).beforeClosed().pipe(
            filter(Boolean)
        ).subscribe(supplier => {
            this.#supplier = supplier;
            this.notifyChanges(supplier);
        });
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        this.#supplier = params['supplier'];
        this.#selectedSupplierName.next(params['supplier']);
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        if (this.#supplier) {
            this.#filterItem.values = [new FilterItemValue(this.#supplier, null)];
            this.#filterItem.urlQueryParams['supplier'] = this.#supplier;
        } else {
            this.#filterItem.values = null;
            this.#filterItem.urlQueryParams = {};
        }
        return [this.#filterItem];
    }

    removeFilter(key: string, value: unknown): void {
        // Do nothing
    }

    resetFilters(): void {
        // Do nothing
    }

    resetSupplier(): void {
        this.#route.queryParams.subscribe(params => {
            if (params?.['supplier']) {
                this.#supplier = params['supplier'];
                this.#selectedSupplierName.next(params['supplier']);
            }
        });
    }

    private notifyChanges(value: string): void {
        this.valueChanged.emit(value);
        this.filtersSubject.next(this.getFilters());
    }
}
