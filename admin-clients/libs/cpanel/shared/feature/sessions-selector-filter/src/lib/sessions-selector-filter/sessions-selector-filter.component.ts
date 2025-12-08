import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { SessionStatus, GetSessionsRequest } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { TimePickerComponent } from '@admin-clients/shared/common/ui/components';
import { weekdays } from '@admin-clients/shared/utility/utils';
import { Weekdays } from '@admin-clients/shared-utility-models';
import { ChangeDetectionStrategy, Component, inject, input, output, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormArray, FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatBadge } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { MatCard, MatCardContent, MatCardFooter } from '@angular/material/card';
import { MAT_DATE_FORMATS, MatOption } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { first, map } from 'rxjs';

@Component({
    selector: 'app-sessions-selector-filter',
    templateUrl: './sessions-selector-filter.component.html',
    styleUrls: ['./sessions-selector-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        SatPopoverModule, ReactiveFormsModule, TranslatePipe, TimePickerComponent,
        MatBadge, MatFormFieldModule, MatDivider, MatIconModule, MatCard, MatCardContent,
        MatSelect, MatOption, MatInput, MatButtonToggle, MatButtonToggleGroup, MatCardFooter,
        MatDatepickerModule, MatButtonModule, MatTooltip
    ]
})
export class SessionsSelectorFilterComponent {
    readonly #authSrv = inject(AuthenticationService);
    readonly #FORMATS = inject(MAT_DATE_FORMATS);
    readonly #fb = inject(FormBuilder);

    readonly #$userTimezone = toSignal(this.#authSrv.getLoggedUser$().pipe(first(Boolean), map(user => user.timezone)));
    readonly #validSessionStatuses: SessionStatus[] = [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled];

    private readonly _filterPopover = viewChild<SatPopoverComponent>('filterPopover');

    #appliedFilters: GetSessionsRequest = { status: this.#validSessionStatuses };

    readonly form = this.#fb.group({
        status: this.#fb.control(this.#validSessionStatuses),
        start_date: this.#fb.group({
            from: null,
            to: null
        }),
        weekdays: null as FormControl<Weekdays[]>,
        hourRanges: this.#fb.array([])
    });

    readonly weekdays = weekdays();
    readonly statusList = this.#validSessionStatuses.map(type => ({ id: type, name: `EVENTS.SESSION.STATUS_OPTS.${type}` }));

    readonly dateFormat = moment.localeData().longDateFormat(this.#FORMATS.display.dateInput).toLowerCase();
    readonly maxHourRanges = 5;

    disabled = input<boolean>();
    filterChange = output<GetSessionsRequest>();

    get hourRanges(): FormArray {
        return this.form?.get('hourRanges') as FormArray;
    }

    apply(): void {
        const data = this.form.value;
        const filters: GetSessionsRequest = {
            weekdays: null,
            status: data?.status?.length ? data?.status : this.#validSessionStatuses,
            initStartDate: null,
            finalStartDate: null,
            offset: 0,
            hourRanges: null
        };
        if (data?.weekdays?.length) {
            filters.weekdays = data?.weekdays;
            filters.timezone = this.#$userTimezone();
        }
        if (data?.start_date?.from) {
            filters.initStartDate = moment(data.start_date.from).utc().format();
        }
        if (data?.start_date?.to) {
            filters.finalStartDate = moment(data.start_date.to).utc()
                .add(1, 'day').subtract(1, 'second').format();
        }
        if (data.hourRanges?.length) {
            const hourRanges = data.hourRanges as { startTime: string; endTime: string }[];
            filters.hourRanges = hourRanges.filter(({ startTime, endTime }) => !!startTime && !!endTime)
                .map(({ startTime, endTime }) =>
                    moment.utc(startTime, 'HH:mm').format().split('T')[1] + '::' + moment.utc(endTime, 'HH:mm').format().split('T')[1]
                );
        }

        this.#appliedFilters = filters;
        this.filterChange.emit(this.#appliedFilters);
        this.close();
    }

    clear(): void {
        this.form.reset();
        this.hourRanges.clear();
        this.apply();
    }

    close(): void {
        this._filterPopover().close();
    }

    open(): void {
        this.form.patchValue({
            ...this.#appliedFilters,
            start_date: {
                from: this.#appliedFilters.initStartDate || null,
                to: this.#appliedFilters.finalStartDate || null
            },
            status: this.#appliedFilters.status || this.#validSessionStatuses
        });
        this._filterPopover().open();
    }

    addHourRange(): void {
        if (this.hourRanges.length < this.maxHourRanges) {
            this.hourRanges.push(this.#fb.group({
                startTime: ['00:00', Validators.required],
                endTime: ['23:59', Validators.required]
            }));
        }
    }

    deleteHourRange(index: number): void {
        this.hourRanges.removeAt(index);
    }

    get activeFilters(): number {
        const appliedFilters = { ...this.#appliedFilters };
        delete appliedFilters.timezone;
        return Object.values(appliedFilters).filter(Boolean)?.length || 0;
    }
}
