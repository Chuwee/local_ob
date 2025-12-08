import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { GetProductEventsRequest, ProductEventStatus } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { ChangeDetectionStrategy, Component, inject, output, viewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatBadge } from '@angular/material/badge';
import { MatIconButton, MatButton } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardFooter } from '@angular/material/card';
import { MAT_DATE_FORMATS, MatOption } from '@angular/material/core';
import { MatDatepicker, MatDatepickerModule } from '@angular/material/datepicker';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput, MatSuffix } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';

@Component({
    selector: 'app-product-events-list-filter',
    imports: [
        ReactiveFormsModule, TranslatePipe, SatPopoverModule, MatBadge, MatCard, MatCardContent,
        MatCardFooter, MatFormField, MatInput, MatSelect, MatDatepicker, MatIcon, MatIconButton, MatButton,
        MatDivider, MatOption, MatDatepickerModule, MatSuffix
    ],
    templateUrl: './product-events-list-filter.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventsListFilterComponent {
    readonly #fb = inject(FormBuilder);
    readonly #FORMATS = inject(MAT_DATE_FORMATS);
    private _filterPopover = viewChild<SatPopoverComponent>('filterPopover');
    #appliedFilters: GetProductEventsRequest = {
        event_status: null,
        product_event_status: null,
        start_date: null
    };

    readonly form = this.#fb.group({
        status: [null as ProductEventStatus],
        event_date: [null as string],
        event_status: [null as EventStatus]
    });

    statuses = [
        { id: ProductEventStatus.active, name: 'ACTIVE' },
        { id: ProductEventStatus.inactive, name: 'INACTIVE' }
    ];

    eventStatuses = Object.values(EventStatus).map(type => ({ id: type, name: `EVENTS.STATUS_OPTS.${type}` }));

    readonly dateFormat = moment.localeData().longDateFormat(this.#FORMATS.display.dateInput).toLowerCase();

    filterChange = output<GetProductEventsRequest>();

    apply(): void {
        this.#appliedFilters = {
            event_status: this.form.value.event_status || null,
            product_event_status: this.form.value.status || null,
            start_date: this.form.value.event_date ? moment(this.form.value.event_date).utc().format() : null
        };
        this.filterChange.emit(this.#appliedFilters);
        this.close();
    }

    clear(): void {
        this.form.reset();
        this.apply();
    }

    close(): void {
        this._filterPopover().close();
    }

    open(): void {
        this.form.reset({
            event_status: this.#appliedFilters.event_status,
            status: this.#appliedFilters.product_event_status,
            event_date: this.#appliedFilters.start_date
        });
        this._filterPopover().open();
    }

    get activeFilters(): number {
        return Object.values(this.#appliedFilters).filter(Boolean)?.length || 0;
    }

}
