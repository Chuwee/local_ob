import { Directive, inject, Input, OnDestroy } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { ListFiltersService } from '../../list-filters/list-filters.service';
import { Chip, ChipsComponent } from '../chips.component';

@Directive({
    standalone: true,
    selector: 'app-chips[appFilter]'
})
export class ChipsFilterDirective implements OnDestroy {
    private readonly _listFiltersSrv = inject(ListFiltersService);
    private readonly _filterChipsComponent = inject(ChipsComponent);

    private readonly _onDestroy = new Subject<void>();

    @Input() canChange = true;

    constructor() {
        this._filterChipsComponent.labelText = 'FORMS.APPLIED_FILTERS';
        this._filterChipsComponent.removeText = 'FORMS.REMOVE_FILTERS_BTN';
        this._filterChipsComponent.showDivider = true;

        this._filterChipsComponent.chips$ = this._listFiltersSrv.onFilterValuesApplied$().pipe(
            startWith([]),
            map(filters => {
                const chips = [] as Chip[];
                filters
                    .filter(filterItem => filterItem.label && filterItem.values)
                    .forEach(filterItem => {
                        filterItem.values
                            .forEach(val => {
                                chips.push({
                                    key: filterItem.key,
                                    label: filterItem.label,
                                    value: val.value,
                                    valueText: val.text
                                });
                            });
                    });
                return chips;
            }));

        this._filterChipsComponent.removeEmitter
            .pipe(takeUntil(this._onDestroy))
            .subscribe(chip => {
                if (this.canChange) {
                    this._listFiltersSrv.removeFilter(chip.key, chip.value);
                }
            });

        this._filterChipsComponent.removeAllEmitter
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                if (this.canChange) {
                    this._listFiltersSrv.resetFilters('DATE_RANGE');
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
