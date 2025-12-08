import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { FilterItem } from './filter-item.model';
import { FilterComponent } from './filter.component';
import { ListFiltersService } from './list-filters.service';

@Component({
    template: '',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export abstract class ListFilteredComponent implements OnDestroy {

    private _destroy = new Subject<void>();

    protected listFiltersService = inject(ListFiltersService);
    protected router = inject(Router);
    protected activatedRoute = inject(ActivatedRoute);

    ngOnDestroy(): void {
        this._destroy.next();
        this._destroy.complete();
    }

    initListFilteredComponent(components: FilterComponent[]): void {
        this._destroy.next();
        this.listFiltersService.registerFilterComponents(components, this._destroy);
        this.listFiltersService.onFilterValuesApplied$()
            .pipe(takeUntil(this._destroy))
            .subscribe(filters => this.loadData(filters));
        this.listFiltersService.onFilterValuesModified$()
            .pipe(takeUntil(this._destroy))
            .subscribe(filters => this.addFiltersToUrl(filters));
        this.activatedRoute.queryParamMap
            .pipe(
                map(queryParamMap => queryParamMap.keys.reduce(
                    (result, key) => {
                        result[key] = queryParamMap.get(key);
                        return result;
                    }, {}
                ) as Params),
                takeUntil(this._destroy)
            )
            .subscribe(queryParams => this.listFiltersService.applyFiltersByUrlParams(queryParams));
    }

    refresh(): void {
        this.loadData(this.listFiltersService.getFilters());
    }

    abstract loadData(filters: FilterItem[]): void;

    private addFiltersToUrl(filters: FilterItem[]): void {
        const params = filters
            .reduce((result, filter) => Object.assign(result, filter.urlQueryParams), {});
        this.router.navigate(
            ['.'],
            {
                queryParams: params,
                relativeTo: this.activatedRoute,
                replaceUrl: !!this.activatedRoute.snapshot.queryParamMap.keys.length
            }
        ).catch(console.error);
    }
}
