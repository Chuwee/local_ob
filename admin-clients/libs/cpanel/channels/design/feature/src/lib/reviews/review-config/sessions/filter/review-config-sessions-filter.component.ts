import { ChannelSessionsFilter } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { weekdays } from '@admin-clients/shared/utility/utils';
import { Weekdays } from '@admin-clients/shared-utility-models';
import { ChangeDetectionStrategy, Component, computed, inject, output, signal, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatBadge } from '@angular/material/badge';
import { MatButton } from '@angular/material/button';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { MatCard, MatCardContent, MatCardFooter } from '@angular/material/card';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { first, map } from 'rxjs';

@Component({
    standalone: true,
    selector: 'ob-review-config-sessions-filter',
    templateUrl: './review-config-sessions-filter.component.html',
    imports: [
        MatButton, MatBadge, MatIcon, MatCard, MatCardContent, MatCardFooter, SatPopoverModule, MatButtonToggle, MatInput,
        TranslatePipe, ReactiveFormsModule, MatFormField, MatDatepicker, MatDatepickerInput, MatButtonToggleGroup
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewConfigSessionsFilterComponent {
    readonly #fb = inject(FormBuilder);
    readonly #authSrv = inject(AuthenticationService);
    readonly #formats = inject(MAT_DATE_FORMATS);

    readonly $filterPopover = viewChild<SatPopoverComponent>('filterPopover');
    readonly $filterChange = output<ChannelSessionsFilter>({ alias: 'filterChange' });

    readonly #$userTimezone = toSignal(this.#authSrv.getLoggedUser$().pipe(first(user => !!user), map(user => user.timezone)));
    readonly #$appliedFilters = signal<ChannelSessionsFilter>({});
    readonly $activeFilters = computed(
        () => Object.keys(this.#$appliedFilters()).filter(key => key !== 'timezone' && this.#$appliedFilters()[key]).length
    );

    readonly filterForm = this.#fb.group({
        initStartDate: '',
        finalStartDate: '',
        weekdays: [[] as Weekdays[]]
    });

    readonly weekdays = weekdays();
    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();

    applyFilters(): void {
        const data = this.filterForm.getRawValue();
        const filters: ChannelSessionsFilter = {
            initStartDate: null,
            finalStartDate: null,
            weekdays: null,
            offset: 0
        };
        if (data.weekdays?.length) {
            filters.weekdays = data?.weekdays;
            filters.timezone = this.#$userTimezone();
        }
        if (data.initStartDate) {
            filters.initStartDate = moment(data.initStartDate).utc().format();
        }
        if (data.finalStartDate) {
            filters.finalStartDate = moment(data.finalStartDate).endOf('day').utc().format();
        }
        this.#$appliedFilters.set(filters);
        this.$filterChange.emit(filters);
        this.closeFilters();
    }

    clearFilters(): void {
        this.filterForm.reset();
        this.applyFilters();
    }

    closeFilters(): void {
        this.$filterPopover()?.close();
    }

    openFilters(): void {
        this.filterForm.reset({
            ...this.#$appliedFilters(),
            initStartDate: this.#$appliedFilters()?.initStartDate || null,
            finalStartDate: this.#$appliedFilters()?.finalStartDate || null
        });
        this.$filterPopover()?.open();
    }
}
