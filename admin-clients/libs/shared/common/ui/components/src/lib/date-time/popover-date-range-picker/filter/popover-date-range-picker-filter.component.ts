import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import {
    AfterViewInit,
    ChangeDetectionStrategy,
    Component,
    inject,
    input,
    Input,
    OnInit,
    ViewChild
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { DateRange } from '@angular/material/datepicker';
import { ActivatedRoute, Params } from '@angular/router';
import moment, { DurationInputArg1, DurationInputArg2 } from 'moment-timezone';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { FilterItem } from '../../../list-filters/filter-item.model';
import { FilterWrapperComponent } from '../../../list-filters/filter-wrapper.component';
import { ListFiltersService } from '../../../list-filters/list-filters.service';
import { PopoverFilterDirective } from '../../../popover/filter/popover-filter.directive';
import { PopoverComponent } from '../../../popover/popover.component';
import { DateRangePickerComponent } from '../../date-range-picker/date-range-picker.component';
import { DateRangePickerFilterDirective } from '../../date-range-picker/filter/date-range-picker-filter.directive';
import { DateTimeModule } from '../../date-time.module';
import {
    PopoverDateRangePickerButtonComponent
} from '../button/popover-date-range-picker-button.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        PopoverComponent,
        DateTimeModule,
        PopoverFilterDirective,
        PopoverDateRangePickerButtonComponent,
        DateRangePickerFilterDirective,
        DateRangePickerComponent,
        FlexLayoutModule
    ],
    selector: 'app-popover-date-range-picker-filter',
    templateUrl: './popover-date-range-picker-filter.component.html'
})
export class PopoverDateRangePickerFilterComponent extends FilterWrapperComponent implements OnInit, AfterViewInit {
    readonly #listFiltersSrv = inject(ListFiltersService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #FORMATS = inject(MAT_DATE_FORMATS);

    @ViewChild(PopoverFilterDirective)
    private _popoverFilter: PopoverFilterDirective;

    readonly dateFormat = moment.localeData().longDateFormat(this.#FORMATS.display.dateInput).toLowerCase();
    readonly dateTimeFormats = DateTimeFormats;
    selectedDate: DateRange<moment.Moment>;

    @Input()
    hideRemoveFiltersBtn = false;

    $disableNoDate = input<boolean>(false, { alias: 'disableNoDate' });
    $maxDateRangeAmount = input<DurationInputArg1>(null, { alias: 'maxDateRangeAmount' });
    $maxDateRangeType = input<DurationInputArg2>(null, { alias: 'maxDateRangeType' });

    isOnRemoveFiltersBtnClickEnabled$ = this.#activatedRoute.queryParamMap
        .pipe(map(queryParamMap => queryParamMap.keys
            ?.filter(paramKey => paramKey === 'startDate' || paramKey === 'endDate')?.length > 0));

    onRemoveFunction = (): void => this.#listFiltersSrv.removeFilter('DATE_RANGE', null);

    ngOnInit(): void {
        this.selectedDate = new DateRange(null, null);
    }

    ngAfterViewInit(): void {
        this.setInnerFilterComponent(this._popoverFilter);
    }

    override applyFiltersByUrlParams$(queryParams: Params): Observable<FilterItem[]> {
        return super.applyFiltersByUrlParams$(queryParams).pipe(
            tap(filterItems => this.onChangeFilters(filterItems))
        );
    }

    private onChangeFilters(filterItems: FilterItem[]): void {
        const dateRangeFilter = filterItems.find(filterItem => filterItem.key === 'DATE_RANGE');
        if (dateRangeFilter?.values && dateRangeFilter.values.length > 0) {
            this.selectedDate = dateRangeFilter.values[0]?.value;
        } else {
            this.selectedDate = new DateRange(null, null);
        }
    }
}
