import { MatSort, SortDirection } from '@angular/material/sort';
import { Params } from '@angular/router';
import { Observable, of } from 'rxjs';
import { switchMap, take, tap } from 'rxjs/operators';
import { FilterItem, FilterItemValue } from './filter-item.model';
import { FilterComponent } from './filter.component';

export class SortFilterComponent extends FilterComponent {

    private readonly _filterItem: FilterItem;
    private readonly _defaultColumn: string;
    private readonly _defaultDirection: SortDirection;
    private _actualColumn: string;
    private _actualDirection: SortDirection;
    canChange$ = of(true);

    constructor(private _matSort: MatSort, canChange$?: Observable<boolean>) {
        super();
        this._filterItem = new FilterItem('SORT', null);
        this._defaultColumn = this._matSort.active;
        this._defaultDirection = this._matSort.direction;
        this.setCanChange(canChange$);
        this.setChange();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        if (params['sort']) {
            const sortParts = params['sort'].split(':');
            this.setSort(sortParts[0], sortParts.length > 1 ? sortParts[1] : this._defaultDirection);
        } else {
            this.setSort(this._defaultColumn, this._defaultDirection);
        }
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        const column = this._matSort.active;
        const direction = this._matSort.direction;
        this._filterItem.values = [new FilterItemValue(column + ':' + direction, null)];
        if (column && (column !== this._defaultColumn || direction !== this._defaultDirection)) {
            this._filterItem.urlQueryParams['sort'] = column + ':' + direction;
        } else {
            this._filterItem.urlQueryParams = {};
        }
        return [this._filterItem];
    }

    removeFilter(key: string, _: unknown): void {
        if (key === 'SORT') {
            this.setSort(this._defaultColumn, this._defaultDirection);
        }
    }

    resetFilters(): void {
        this.setSort(this._defaultColumn, this._defaultDirection);
    }

    private setCanChange(canChange$): void {
        if (canChange$) {
            this.canChange$ = canChange$;
        }
    }

    private setChange(): void {
        this._matSort.sortChange
            .pipe(
                switchMap(change => this.canChange$.pipe(
                    tap(canChange => {
                        if (canChange) {
                            this._actualColumn = change.active;
                            this._actualDirection = change.direction;
                            this.filtersSubject.next(this.getFilters());
                        } else {
                            this.setSort(this._actualColumn, this._actualDirection);
                        }
                    }),
                    take(1)
                ))
            ).subscribe();
    }

    private setSort(column: string, direction: SortDirection): void {
        this._matSort.active = column;
        this._matSort.direction = direction;
        this._actualColumn = column;
        this._actualDirection = direction;
        this._matSort._stateChanges.next();
    }
}
