import { EventsService, EventVenueTpl } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, SessionGroupType, SessionListFilters, SessionStatus
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { weekdays } from '@admin-clients/shared/utility/utils';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { SatPopoverComponent } from '@ncstate/sat-popover';
import moment from 'moment-timezone';
import { Observable, Subject } from 'rxjs';
import { first, map, take, takeUntil, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-sessions-list-filter',
    templateUrl: './sessions-list-filter.component.html',
    styleUrls: ['./sessions-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionsListFilterComponent implements OnInit, OnDestroy {
    private _fb = inject(UntypedFormBuilder);
    private _eventsSrv = inject(EventsService);
    private _sessionsSrv = inject(EventSessionsService);
    private readonly formats = inject(MAT_DATE_FORMATS);

    private _onDestroy = new Subject<void>();
    private _disabled = false;
    @ViewChild('filterPopover')
    private _filterPopover: SatPopoverComponent;

    readonly dateFormat = moment.localeData().longDateFormat(this.formats.display.dateInput).toLowerCase();
    readonly maxHourRanges = 5;
    venueTpls$: Observable<EventVenueTpl[]>;
    appliedFiltersCounter$: Observable<number>;
    filtersForm: UntypedFormGroup;
    groupTypeCtrl: UntypedFormControl;
    weekdays = weekdays();
    sessionGroupTypes = Object.values(SessionGroupType);
    statusList = Object.values(SessionStatus)
        .map(type => ({ id: type, name: `EVENTS.SESSION.STATUS_OPTS.${type}` }));

    get hourRanges(): UntypedFormArray {
        return this.filtersForm?.get('hourRanges') as UntypedFormArray;
    }

    @Input()
    set disabled(value: boolean) {
        this._disabled = coerceBooleanProperty(value);
        if (this.groupTypeCtrl) {
            if (this._disabled) {
                this.groupTypeCtrl.disable();
            } else {
                this.groupTypeCtrl.enable();
            }
        }
    }

    get disabled(): boolean {
        return this._disabled;
    }

    constructor(
    ) { }

    ngOnInit(): void {
        // Init reactive form:
        this.filtersForm = this._fb.group({
            venueTplId: null,
            status: [[]],
            initStartDate: null,
            finalStartDate: null,
            weekdays: null,
            hourRanges: this._fb.array([])
        });
        this.groupTypeCtrl = this._fb.control({ value: null, disabled: this._disabled });
        // Map data observables to component:
        this.venueTpls$ = this._eventsSrv.event.get$()
            .pipe(
                first(v => v !== null),
                map(event => event.venue_templates)
            );
        this.appliedFiltersCounter$ = this._sessionsSrv.getSessionListFilters$()
            .pipe(
                map(_ => {
                    const filters = this.filtersForm.value;
                    let counter = 0;
                    for (const key in filters) {
                        if ((Array.isArray(filters[key]) && filters[key].length) || (!Array.isArray(filters[key]) && filters[key])) {
                            counter++;
                        }
                    }
                    return counter;
                })
            );
        // Update form with centralized data
        this._sessionsSrv.getSessionListFilters$()
            .pipe(take(1))
            .subscribe(filters => {
                this.filtersForm.patchValue({
                    venueTplId: filters.venueTplId || null,
                    status: filters.status || [],
                    initStartDate: filters.initStartDate ? moment.utc(filters.initStartDate) : null,
                    finalStartDate: filters.finalStartDate ? moment.utc(filters.finalStartDate) : null,
                    weekdays: filters.weekdays || null
                });
                this.groupTypeCtrl.setValue(filters.groupType, { emitEvent: false });
            });

        this.initFormChangesHandlers();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    onCloseBtnClick(): void {
        this._filterPopover.close();
    }

    onApplyBtnClick(): void {
        const data = this.filtersForm.value;
        const filters: SessionListFilters = {
            groupType: this.groupTypeCtrl.value
        };
        if (data.venueTplId) {
            filters.venueTplId = data.venueTplId;
        }
        if (data.status) {
            filters.status = data.status;
        }
        if (data.initStartDate) {
            const startDate = this.getParseZoneDate(data.initStartDate);
            filters.initStartDate = startDate.format();
        }
        if (data.finalStartDate) {
            const endDate = this.getParseZoneDate(data.finalStartDate);
            filters.finalStartDate = endDate.endOf('day').format();
        }
        if (data.weekdays) {
            filters.weekdays = data.weekdays;
        }
        filters.hourRanges = data.hourRanges
            .filter(({ startTime, endTime }: { startTime: string; endTime: string }) =>
                !!startTime && !!endTime
            )
            .map(({ startTime, endTime }: { startTime: string; endTime: string }) =>
                moment.utc(startTime, 'HH:mm').format().split('T')[1] + '::' + moment.utc(endTime, 'HH:mm').format().split('T')[1]
            );

        this._sessionsSrv.setSessionListFilters(filters);
        this.onCloseBtnClick();
    }

    onRemoveFiltersBtnClick(): void {
        this.filtersForm.reset();
        this.hourRanges.clear();
        this.onApplyBtnClick();
    }

    addHourRange(): void {
        if (this.hourRanges.length < this.maxHourRanges) {
            this.hourRanges.push(this._fb.group({
                startTime: ['00:00', Validators.required],
                endTime: ['23:59', Validators.required]
            }));
        }
    }

    deleteHourRange(index: number): void {
        this.hourRanges.removeAt(index);
    }

    getInitStartDateStartDay(): moment.Moment {
        const finalStartDate = this.filtersForm.get('finalStartDate')?.value;
        return moment.isMoment(finalStartDate) ? moment.min(moment(), this.filtersForm.get('finalStartDate')?.value) : moment();
    }

    getFinalStartDateStartDay(): moment.Moment {
        const initStartDate = this.filtersForm.get('initStartDate')?.value;
        return moment.isMoment(initStartDate) ? moment.max(moment(), this.filtersForm.get('initStartDate')?.value) : moment();
    }

    private initFormChangesHandlers(): void {
        // apply 'group' filter on select change
        this.groupTypeCtrl.valueChanges
            .pipe(
                withLatestFrom(this._sessionsSrv.getSessionListFilters$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([groupType, filters]) => {
                if (groupType !== filters.groupType) {
                    this._sessionsSrv.setSessionListFilters({
                        ...filters,
                        groupType
                    });
                }
            });
    }

    private getParseZoneDate(date: string | moment.Moment): moment.Moment {
        const { years, months, date: days, hours, minutes, seconds, milliseconds } = moment(date).parseZone().toObject();
        return moment.utc({ years, months, date: days, hours, minutes, seconds, milliseconds });
    }
}
