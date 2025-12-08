import { GetSessionsPriceRequest } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { weekdays } from '@admin-clients/shared/utility/utils';
import { Weekdays } from '@admin-clients/shared-utility-models';
import { Component, ChangeDetectionStrategy, Output, EventEmitter, ViewChild, Input, inject } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatBadge } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';

@Component({
    selector: 'app-product-event-sessions-prices-filter',
    imports: [
        SatPopoverModule, ReactiveFormsModule, TranslatePipe, FlexLayoutModule, FlexModule, MatButtonModule, MatIcon, MatCardModule,
        MatBadge, ReactiveFormsModule, MatFormFieldModule, MatDivider, MatDatepickerModule, MatButtonToggleModule, MatInputModule
    ],
    templateUrl: './product-event-sessions-prices-filter.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventSessionsPricesFilterComponent {
    @ViewChild('filterPopover') private _filterPopover: SatPopoverComponent;
    #appliedFilters: GetSessionsPriceRequest = {};

    readonly #FORMATS = inject(MAT_DATE_FORMATS);
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        status: [null as ['EDITED', 'UNEDITED']],
        start_date: this.#fb.group({
            from: [null as string],
            to: [null as string]
        }),
        weekdays: [null as Weekdays[]]
    });

    readonly dateFormat = moment.localeData().longDateFormat(this.#FORMATS.display.dateInput).toLowerCase();

    @Input() disabled: boolean;
    @Output() filterChange = new EventEmitter<GetSessionsPriceRequest>();

    readonly statusList = Object.values(['EDITED', 'UNEDITED'])
        .map(type => ({ id: type, name: `PRODUCT.EVENTS.DETAIL.SESSIONS_CONFIG.${type}` }));

    readonly weekdays = weekdays();

    apply(): void {
        const data = this.form.value;
        const filters: GetSessionsPriceRequest = {
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
