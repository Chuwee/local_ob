import {
    AfterContentInit,
    ContentChild,
    Directive,
    inject,
    Input,
    OnInit
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntil } from 'rxjs';
import { map } from 'rxjs/operators';
import { FilterWrapped } from '../../list-filters/filter-wrapped.component';
import { FilterWrapperComponent } from '../../list-filters/filter-wrapper.component';
import { ListFiltersService } from '../../list-filters/list-filters.service';
import { PopoverComponent } from '../popover.component';

@Directive({
    standalone: true,
    selector: 'app-popover[appFilter]'
})
export class PopoverFilterDirective extends FilterWrapperComponent implements OnInit, AfterContentInit {
    private readonly _listFiltersSrv = inject(ListFiltersService);
    private readonly _activatedRoute = inject(ActivatedRoute);
    private readonly _popover = inject(PopoverComponent, { host: true });
    private _canChange = true;

    @ContentChild('filterContent')
    private _filterComponent: FilterWrapped;

    @Input() isOnRemoveFiltersBtnClickEnabled$ = this._activatedRoute.queryParamMap
        .pipe(map(queryParamMap => queryParamMap.keys
            ?.filter(paramKey => paramKey !== 'startDate' && paramKey !== 'endDate')?.length > 0));

    @Input()
    set canChange(value: boolean) {
        this._canChange = value;
    }

    constructor() {
        super();

        this._popover.buttonText = 'FORMS.OPEN_FILTER_BTN';
        this._popover.removeButtonText = 'FORMS.REMOVE_FILTERS_BTN';
        this._popover.removeButtonTooltipText = 'FORMS.REMOVE_FILTERS_BTN_TOOLTIP';

        this._popover.removeEmitter
            .pipe(takeUntil(this.destroy))
            .subscribe(() => {
                this.onRemoveFunction();
            });

        this._popover.changesEmitter
            .pipe(takeUntil(this.destroy))
            .subscribe(() => {
                if (this._canChange) {
                    this._filterComponent.applyFilters();
                }
            });
    }

    @Input()
    onRemoveFunction = (): void => this._listFiltersSrv.resetFilters('DATE_RANGE');

    ngOnInit(): void {
        this.isOnRemoveFiltersBtnClickEnabled$
            .pipe(takeUntil(this.destroy))
            .subscribe(isOnRemoveBtnClickEnabled => {
                this._popover.isOnRemoveBtnClickEnabled = isOnRemoveBtnClickEnabled;
            });
    }

    ngAfterContentInit(): void {
        if (this._filterComponent === undefined) {
            throw Error('FilterComponentWithinPopover @ContentChild with #filterContent selector should be provided');
        }
        this.setInnerFilterComponent(this._filterComponent);
    }
}
