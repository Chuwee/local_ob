import { SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { GetSaleRequestSessionsRequest } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { weekdays } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, EventEmitter, Inject, OnInit, Output, ViewChild } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDateFormats, MAT_DATE_FORMATS } from '@angular/material/core';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';

@Component({
    selector: 'app-cross-selling-sessions-filter',
    templateUrl: './cross-selling-sessions-filter.component.html',
    styleUrls: ['./cross-selling-sessions-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MaterialModule, TranslatePipe, SatPopoverModule, ReactiveFormsModule, FlexModule, FlexLayoutModule]
})
export class CrossSellingSessionsFilterComponent implements OnInit {
    @ViewChild('filterPopover') private _filterPopover: SatPopoverComponent;
    private _appliedFilters: GetSaleRequestSessionsRequest = {};

    form: FormGroup;
    statusList = Object.values(SessionStatus)
        .map(type => ({ id: type, name: `EVENTS.SESSION.STATUS_OPTS.${type}` }));

    readonly weekdays = weekdays();
    readonly dateFormat = moment.localeData().longDateFormat(this.FORMATS.display.dateInput).toLowerCase();

    @Output() filterChange = new EventEmitter<GetSaleRequestSessionsRequest>();

    constructor(
        private _fb: FormBuilder,
        @Inject(MAT_DATE_FORMATS) private readonly FORMATS: MatDateFormats
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({
            status: null,
            initStartDate: null,
            finalStartDate: null,
            weekdays: null
        });
    }

    applyFilters(): void {
        const data = this.form.value;
        const filters: GetSaleRequestSessionsRequest = {
            status: null,
            start_date: { from: null, to: null },
            weekdays: null,
            offset: 0
        };
        if (data?.status?.length) {
            filters.status = data?.status;
        }
        if (data?.weekdays?.length) {
            filters.weekdays = data?.weekdays;
        }
        if (data?.initStartDate) {
            filters.start_date.from = moment(data.initStartDate).utc().format();
        }
        if (data?.finalStartDate) {
            filters.start_date.to = moment(data.finalStartDate).endOf('day').utc().format();
        }
        this._appliedFilters = filters;
        this.filterChange.emit(this._appliedFilters);
        this.closeFilters();
    }

    clearFilters(): void {
        this.form.reset();
        this.applyFilters();
    }

    closeFilters(): void {
        this._filterPopover.close();
    }

    openFilters(): void {
        this.form.reset({
            ...this._appliedFilters,
            initStartDate: this._appliedFilters.start_date?.from || null,
            finalStartDate: this._appliedFilters.start_date?.to || null
        });
        this._filterPopover.open();
    }

    get activeFilters(): number {
        let counter = 0;
        for (const key in this._appliedFilters) {
            const filter = this._appliedFilters[key];
            if (filter && (key !== 'start_date' || (filter.from || filter.to))) {
                counter += (filter.from && filter.to) ? 2 : 1;
            }
        }
        return counter;
    }

}
