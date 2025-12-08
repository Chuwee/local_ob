import {
    SeasonTicketGenerationStatus, GetSeasonTicketStatusResponse, SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    GetSeasonTicketSessionsEventsRequest, SeasonTicketSessionsEvent, SeasonTicketSessionsService, SeasonTicketSessionStatus
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { filter, map, shareReplay, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-sessions-list-filter',
    templateUrl: './season-ticket-sessions-list-filter.component.html',
    styleUrls: ['./season-ticket-sessions-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketSessionsListFilterComponent extends FilterWrapped implements OnInit {
    private readonly _formats = inject(MAT_DATE_FORMATS);
    private readonly _formStructure = {
        event: null,
        status: null,
        startDate: null,
        endDate: null
    };

    private _statusList = Object.values(SeasonTicketSessionStatus).map(type => ({
        id: type,
        name: `SEASON_TICKET.STATUS_OPTS.${type}`
    }));

    filtersForm: UntypedFormGroup;
    events$: Observable<SeasonTicketSessionsEvent[]>;
    statusListTempl = [null].concat(this._statusList);
    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();
    @Input() operatorMode$: Observable<boolean>;

    constructor(
        private _seasonTicketSessionsSrv: SeasonTicketSessionsService,
        private _seasonTicketSrv: SeasonTicketsService,
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
        this.initForm();
        this.model();
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);
        if (params['status']) {
            formFields.status = this._statusList.find(item => {
                const itemKey = item.id;
                return itemKey === params['status'];
            }) || null;
        }
        if (params['startDate']) {
            formFields.startDate = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields.endDate = moment(params['endDate']).tz(moment().tz()).format();
        }

        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'event', params['event_id'], this.events$, 'id')
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEvent(),
            this.getFilterStatus(),
            this.getFilterStartDate(),
            this.getFilterEndDate()
        ];
    }

    removeFilter(key: string): void {
        if (key === 'STATUS') {
            this.filtersForm.get('status').reset();
        } else if (key === 'EVENT') {
            this.filtersForm.get('event').reset();
        } else if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        }
    }

    private initForm(): void {
        this.filtersForm = this._fb.group(Object.assign({}, this._formStructure));
    }

    private model(): void {
        // Events:
        this._seasonTicketSrv.seasonTicketStatus.get$()
            .pipe(
                filter(value => value !== null),
                tap((seasonTicketStatus: GetSeasonTicketStatusResponse) => {
                    if (seasonTicketStatus.generation_status === SeasonTicketGenerationStatus.ready) {
                        const request: GetSeasonTicketSessionsEventsRequest = {
                            limit: 999
                        };
                        this._seasonTicketSessionsSrv.loadSessionsEventsList(
                            seasonTicketStatus.season_ticket_id.toString(), request
                        );
                    }
                }),
                takeUntil(this.destroy)
            ).subscribe();
        this.events$ = this._seasonTicketSessionsSrv.getSessionsEventsList$()
            .pipe(
                filter(value => value !== null),
                takeUntil(this.destroy),
                shareReplay(1)
            );
    }

    private getFilterEvent(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('EVENT')
            .labelKey('SEASON_TICKET.SESSIONS_EVENT')
            .queryParam('event_id')
            .value(this.filtersForm.value.event)
            .build();
    }

    private getFilterStatus(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('STATUS')
            .labelKey('SEASON_TICKET.SESSION_STATUS')
            .queryParam('status')
            .value(this.filtersForm.value.status)
            .build();
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this._translate.instant('SEASON_TICKET.SESSION_DATE_FROM'));
        const value = this.filtersForm.value.startDate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE', this._translate.instant('SEASON_TICKET.SESSION_DATE_TO'));
        const value = this.filtersForm.value.endDate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this._formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDate'] = valueId;
        }
        return filterItem;
    }
}
