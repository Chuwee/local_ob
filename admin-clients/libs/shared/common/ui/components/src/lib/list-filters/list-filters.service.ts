import { Injectable } from '@angular/core';
import { Params } from '@angular/router';
import { forkJoin, Observable, Subject } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';
import { FilterItem } from './filter-item.model';
import { FilterComponent } from './filter.component';

@Injectable()
export class ListFiltersService {
    #components: FilterComponentData[] = [];
    #filterValuesApplied: Subject<FilterItem[]> = new Subject();
    #filterValuesApplied$ = this.#filterValuesApplied.asObservable();
    #filterValuesModified: Subject<FilterItem[]> = new Subject();
    #filterValuesModified$ = this.#filterValuesModified.asObservable();
    #listenersBeforeUseFilterValuesModified: ((filterItems: FilterItem[]) => void)[] = [];

    registerFilterComponents(components: FilterComponent[], destroy: Subject<void>): void {
        this.#components = components.map(component => {
            const data = new FilterComponentData();
            data.filterComponent = component;
            data.filterItems = [];
            data.filterComponent.getFilters$().pipe(takeUntil(destroy)).subscribe(filterItems => {
                data.filterItems = filterItems;
                this.notifyFiltersModified(filterItems);
            });
            return data;
        });
    }

    onFilterValuesModified$(): Observable<FilterItem[]> {
        return this.#filterValuesModified$;
    }

    onFilterValuesApplied$(): Observable<FilterItem[]> {
        return this.#filterValuesApplied$;
    }

    removeFilter(key: string, value: unknown): void {
        this.#components.forEach(component => {
            component.filterComponent.removeFilter(key, value);
            component.filterItems = component.filterComponent.getFilters();
        });
        this.notifyFiltersModified(null);
    }

    resetFilters(omitKey: string | string[] = null): void {
        if (Array.isArray(omitKey)) {
            this.#components.forEach(component => {
                if (!component.filterComponent.getFilters().find(filter => omitKey.includes(filter.key))?.key) {
                    component.filterComponent.resetFilters();
                    component.filterItems = component.filterComponent.getFilters();
                }
            });
        } else {
            this.#components.forEach(component => {
                if (!component.filterComponent.getFilters().find(filter => filter.key === omitKey)?.key) {
                    component.filterComponent.resetFilters();
                    component.filterItems = component.filterComponent.getFilters();
                }
            });
        }

        this.notifyFiltersModified(null);
    }

    applyFiltersByUrlParams(params: Params): void {
        const observables = this.#components.map(component => component.filterComponent.applyFiltersByUrlParams$(params)
                    .pipe(tap(filterItems => {
                        component.filterItems = filterItems;
                    }))
        );
        forkJoin(observables).subscribe(() => this.#filterValuesApplied.next(this.getFilters()));
    }

    addListenerBeforeUseFilterValuesModified(listener: (filterItems: FilterItem[]) => void): void {
        this.#listenersBeforeUseFilterValuesModified.push(listener);
    }

    getFilters(): FilterItem[] {
        return this.#components.flatMap(c => c.filterItems);
    }

    private notifyFiltersModified(modified: FilterItem[]): void {
        this.#listenersBeforeUseFilterValuesModified.forEach(listener => listener(modified));
        this.#filterValuesModified.next(this.getFilters());
    }
}

class FilterComponentData {
    filterComponent: FilterComponent;
    filterItems: FilterItem[];
}
