import { Directive, OnDestroy } from '@angular/core';
import { Params } from '@angular/router';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { FilterItem } from './filter-item.model';

@Directive()
export abstract class FilterComponent implements OnDestroy {
    private readonly _destroy = new Subject<void>();
    private readonly _filtersSubject = new BehaviorSubject<FilterItem[]>([]);
    private readonly _filtersObservable = this._filtersSubject.asObservable().pipe(takeUntil(this._destroy));

    protected readonly destroy = this._destroy;
    protected readonly filtersSubject = this._filtersSubject;

    isLoading$: Observable<boolean>;

    constructor() { }

    ngOnDestroy(): void {
        this._destroy.next(null);
        this._destroy.complete();
    }

    getFilters$(): Observable<FilterItem[]> {
        return this._filtersObservable;
    }

    abstract applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]>;

    abstract getFilters(): FilterItem[];

    abstract removeFilter(key: string, value: unknown): void;

    abstract resetFilters(): void;
}
