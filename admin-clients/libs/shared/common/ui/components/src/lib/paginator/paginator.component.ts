import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { Params } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, of, Subject } from 'rxjs';
import { filter, map, take } from 'rxjs/operators';
import { FilterItem, FilterItemValue } from '../list-filters/filter-item.model';
import { FilterComponent } from '../list-filters/filter.component';
import { ListFiltersService } from '../list-filters/list-filters.service';

@Component({
    imports: [
        AsyncPipe, MatButton, MatIcon, MatTooltip, TranslatePipe
    ],
    selector: 'app-paginator',
    templateUrl: './paginator.component.html',
    styleUrls: ['./paginator.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaginatorComponent extends FilterComponent {
    private readonly _filterItem: FilterItem;

    private _pageState: Subject<void> = new Subject();
    private _length: number;
    private _pageSize: number;
    private _pageIndex = 0;
    readonly pageChange: EventEmitter<number> = new EventEmitter();

    readonly prevButtonDisabled$ = this._pageState.pipe(
        map(() => this.pageIndex === 0)
    );

    readonly nextButtonDisabled$ = this._pageState.pipe(
        map(() => this.pageIndex >= Math.ceil(this.length / this.pageSize) - 1)
    );

    @Input() canChange$ = of(true);

    constructor(listFiltersService: ListFiltersService) {
        super();
        this._filterItem = new FilterItem('PAGINATION', null);
        this.pageChange.subscribe(() => this.filtersSubject.next(this.getFilters()));
        listFiltersService.addListenerBeforeUseFilterValuesModified(modified => this.goToFirstPageIfNeeded(modified));
    }

    firstPage(): void {
        this._pageIndex = 0;
        this._pageState.next();
    }

    decreasePage(): void {
        this.canChange$.pipe(
            filter(canChange => canChange),
            take(1)
        ).subscribe(() => {
            this._pageIndex--;
            this.pageChange.emit(this._pageIndex);
            this._pageState.next();
        });
    }

    increasePage(): void {
        this.canChange$.pipe(
            filter(canChange => canChange),
            take(1)
        ).subscribe(() => {
            this._pageIndex++;
            this.pageChange.emit(this._pageIndex);
            this._pageState.next();
        });
    }

    get length(): number {
        return this._length;
    }

    @Input()
    set length(value: number) {
        this._length = value;
        this._pageState.next();
    }

    get pageSize(): number {
        return this._pageSize;
    }

    @Input()
    set pageSize(value: number) {
        this._pageSize = value;
        this._pageState.next();
    }

    get pageIndex(): number {
        return this._pageIndex;
    }

    @Input()
    set pageIndex(value: number) {
        this._pageIndex = value;
        this.pageChange.emit(this._pageIndex);
        this._pageState.next();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        if (params['offset']) {
            const offset = Number(params['offset']);
            this._pageIndex = Math.floor(offset / this.pageSize);
            this._pageState.next();
        } else {
            this.firstPage();
        }
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        const offsetValue = this.pageIndex * this.pageSize;
        this._filterItem.values = [new FilterItemValue({
            limit: this.pageSize,
            offset: offsetValue
        }, null)];
        if (offsetValue > 0) {
            this._filterItem.urlQueryParams['offset'] = offsetValue;
        } else {
            this._filterItem.urlQueryParams = {};
        }
        return [this._filterItem];
    }

    removeFilter(_key_: string, _value_: unknown): void {
        this.firstPage();
    }

    resetFilters(): void {
        this.firstPage();
    }

    private goToFirstPageIfNeeded(filterItems: FilterItem[]): void {
        if (this._pageIndex > 0 && filterItems.some(filterItem => filterItem.key !== 'PAGINATION')) {
            this.firstPage();
            this._filterItem.values[0].value.offset = 0;
            this._filterItem.urlQueryParams = {};
        }
    }
}
