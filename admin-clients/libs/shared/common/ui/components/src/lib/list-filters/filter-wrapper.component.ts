import { Params } from '@angular/router';
import { Observable, of } from 'rxjs';
import { FilterItem } from './filter-item.model';
import { FilterComponent } from './filter.component';

export class FilterWrapperComponent extends FilterComponent {

    private _component: FilterComponent;

    setInnerFilterComponent(component: FilterComponent): void {
        this._component = component;
    }

    applyFiltersByUrlParams$(queryParams: Params): Observable<FilterItem[]> {
        if (this._component) {
            return this._component.applyFiltersByUrlParams$(queryParams);
        }
        return of([]);
    }

    getFilters(): FilterItem[] {
        if (this._component) {
            return this._component.getFilters();
        }
        return [];
    }

    override getFilters$(): Observable<FilterItem[]> {
        if (this._component) {
            return this._component.getFilters$();
        }
        return of([]);
    }

    removeFilter(key: string, value: unknown): void {
        if (this._component) {
            this._component.removeFilter(key, value);
        }
    }

    resetFilters(): void {
        if (this._component) {
            this._component.resetFilters();
        }
    }
}
