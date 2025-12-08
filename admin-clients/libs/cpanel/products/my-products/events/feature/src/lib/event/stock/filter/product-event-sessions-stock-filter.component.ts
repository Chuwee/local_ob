import { GetSessionsStockRequest } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { weekdays } from '@admin-clients/shared/utility/utils';
import { Weekdays } from '@admin-clients/shared-utility-models';
import { Component, ChangeDetectionStrategy, Output, EventEmitter, ViewChild, Input, inject } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';

@Component({
    selector: 'app-product-event-sessions-stock-filter',
    templateUrl: './product-event-sessions-stock-filter.component.html',
    imports: [SatPopoverModule, ReactiveFormsModule, TranslatePipe, MaterialModule, FlexLayoutModule, FlexModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventSessionsStockFilterComponent {
    @ViewChild('filterPopover') private _filterPopover: SatPopoverComponent;
    #appliedFilters: GetSessionsStockRequest = {};

    readonly #FORMATS = inject(MAT_DATE_FORMATS);
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        status: [null as ['EDITED', 'UNEDITED']],
        start_date: this.#fb.group({
            from: null,
            to: null
        }),
        weekdays: [null as Weekdays[]]
    });

    readonly dateFormat = moment.localeData().longDateFormat(this.#FORMATS.display.dateInput).toLowerCase();

    @Input() disabled: boolean;
    @Output() filterChange = new EventEmitter<GetSessionsStockRequest>();

    readonly statusList = Object.values(['EDITED', 'UNEDITED'])
        .map(type => ({ id: type, name: `PRODUCT.EVENTS.DETAIL.SESSIONS_CONFIG.${type}` }));

    readonly weekdays = weekdays();

    apply(): void {
        const data = this.form.value;
        const filters: GetSessionsStockRequest = {
            initStartDate: null,
            finalStartDate: null,
            weekdays: null,
            status: null,
            offset: 0
        };
        if (data?.start_date?.from) {
            filters.initStartDate = moment(data.start_date.from).utc().format();
        }
        if (data?.start_date?.to) {
            filters.finalStartDate = moment(data.start_date.to).utc()
                .add(1, 'day').subtract(1, 'second').format();
        }
        if (data?.weekdays?.length) {
            filters.weekdays = data?.weekdays;
        }
        if (data?.status?.length) {
            filters.status = data?.status;
        }
        this.#appliedFilters = filters;
        this.filterChange.emit(this.#appliedFilters);
        this.close();
    }

    clear(): void {
        this.form.reset();
        this.apply();
    }

    close(): void {
        this._filterPopover.close();
    }

    open(): void {
        this.form.reset({
            ...this.#appliedFilters,
            start_date: { from: this.#appliedFilters.initStartDate || null, to: this.#appliedFilters.finalStartDate || null },
            status: this.#appliedFilters.status || null,
            weekdays: this.#appliedFilters.weekdays || null
        });
        this._filterPopover.open();
    }

    get activeFilters(): number {
        return Object.values(this.#appliedFilters).filter(Boolean)?.length || 0;
    }
}
