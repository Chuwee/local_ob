import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { GetSessionsRequest, SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { weekdays } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Inject, OnInit, Output, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDateFormats, MAT_DATE_FORMATS } from '@angular/material/core';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { first } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        SatPopoverModule,
        TranslatePipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        CommonModule
    ],
    selector: 'app-event-promotion-sessions-filter',
    templateUrl: './event-promotion-sessions-filter.component.html',
    styleUrls: ['./event-promotion-sessions-filter.component.scss']
})
export class EventPromotionSessionsFilterComponent implements OnInit {
    @ViewChild('filterPopover') private _filterPopover: SatPopoverComponent;
    private _appliedFilters: GetSessionsRequest = {};
    private _userTimezone: string;

    form: FormGroup;
    statusList = Object.values(SessionStatus)
        .map(type => ({ id: type, name: `EVENTS.SESSION.STATUS_OPTS.${type}` }));

    readonly weekdays = weekdays();
    readonly dateFormat = moment.localeData().longDateFormat(this.FORMATS.display.dateInput).toLowerCase();

    @Output() filterChange = new EventEmitter<GetSessionsRequest>();

    constructor(
        private _fb: FormBuilder,
        private _authSrv: AuthenticationService,
        @Inject(MAT_DATE_FORMATS) private readonly FORMATS: MatDateFormats
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({
            status: null,
            initStartDate: null,
            finalStartDate: null,
            weekdays: null
        });
        this._authSrv.getLoggedUser$()
            .pipe(first(user => !!user))
            .subscribe(user => this._userTimezone = user.timezone);
    }

    applyFilters(): void {
        const data = this.form.value;
        const filters: GetSessionsRequest = {
            status: null,
            initStartDate: null,
            finalStartDate: null,
            weekdays: null,
            offset: 0
        };
        if (data?.status?.length) {
            filters.status = data?.status;
        }
        if (data?.weekdays?.length) {
            filters.weekdays = data?.weekdays;
            filters.timezone = this._userTimezone;
        }
        if (data?.initStartDate) {
            filters.initStartDate = moment(data.initStartDate).utc().format();
        }
        if (data?.finalStartDate) {
            filters.finalStartDate = moment(data.finalStartDate).endOf('day').utc().format();
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
            initStartDate: this._appliedFilters.initStartDate || null,
            finalStartDate: this._appliedFilters.finalStartDate || null
        });
        this._filterPopover.open();
    }

    get activeFilters(): number {
        //Timezone doesn't count
        let counter = 0;
        for (const key in this._appliedFilters) {
            if (key !== 'timezone') {
                if (this._appliedFilters[key]) {
                    counter++;
                }
            }
        }
        return counter;
    }

}
