/* eslint-disable @typescript-eslint/dot-notation */
import {
    SalesRequestsFilterField, SalesRequestsStatus,
    SalesRequestsService, GetFilterRequest
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
import { FilterOption } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldOnServerStream$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Inject, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-sales-requests-list-filter',
    templateUrl: './sales-requests-list-filter.component.html',
    styleUrls: ['./sales-requests-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SalesRequestsListFilterComponent extends FilterWrapped implements OnInit {
    private readonly _statusStructure = {
        accepted: false,
        pending: false,
        rejected: false
    };

    private readonly _formStructure: Record<string, unknown> = {
        channelEntity: null,
        eventEntity: null,
        channel: null,
        startDate: null,
        endDate: null
    };

    filtersForm: UntypedFormGroup;
    channels$: Observable<FilterOption[]>;
    channelEntities$: Observable<FilterOption[]>;
    eventEntities$: Observable<FilterOption[]>;
    salesRequestsFilterField = SalesRequestsFilterField;
    readonly dateFormat = moment.localeData().longDateFormat(this.formats.display.dateInput).toLowerCase();

    @Input() canSelectEntity$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _translate: TranslateService,
        @Inject(MAT_DATE_FORMATS) private readonly formats: MatDateFormats,
        private _salesRequestsService: SalesRequestsService) {
        super();
    }

    ngOnInit(): void {
        // Init reactive form:
        const status = this._fb.group(Object.assign({}, this._statusStructure));
        this.filtersForm = this._fb.group(Object.assign({ status }, this._formStructure));

        this.canSelectEntity$.subscribe(
            (canSelectEntity => {
                if (canSelectEntity) {
                    this.channelEntities$ = this._salesRequestsService.getFilterChannelEntityListData$();
                    this.loadFilter(SalesRequestsFilterField.channelEntity);
                }
            }));

        this.channels$ = this._salesRequestsService.getFilterChannelListData$();
        this.eventEntities$ = this._salesRequestsService.getFilterEventEntityListData$();
        this.loadFilter(SalesRequestsFilterField.channels);
        this.loadFilter(SalesRequestsFilterField.eventEntity);
    }

    loadFilterByQ(field: SalesRequestsFilterField): (q: string) => void {
        return (q: string): void => this.loadFilter(field, q);
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterChannelEntity(),
            this.getFilterEventEntity(),
            this.getFilterChannel(),
            this.getFilterStartDate(),
            this.getFilterEndDate(),
            this.getFilterStatus()
        ].filter(element => !!element);
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'CHANNEL_ENTITY') {
            this.filtersForm.get('channelEntity').reset();
        } else if (key === 'EVENT_ENTITY') {
            this.filtersForm.get('eventEntity').reset();
        } else if (key === 'CHANNEL') {
            this.filtersForm.get('channel').reset();
        } else if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        } else if (key === 'STATUS') {
            const statusCheck = Object.keys(SalesRequestsStatus)
                .find(channelKey => SalesRequestsStatus[channelKey] === value);
            this.filtersForm.get(`status.${statusCheck}`).reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const status = Object.assign({}, this._statusStructure);
        const formFields: Record<string, any> = Object.assign({ status }, this._formStructure);

        if (params['status']) {
            params['status'].split(',').forEach((statusValue: string) => {
                const statusKey = Object.keys(SalesRequestsStatus)
                    .find(key => SalesRequestsStatus[key] === statusValue) || null;
                if (statusKey) {
                    formFields['status'][statusKey] = true;
                }
            });
        }
        if (params['startDate']) {
            formFields['startDate'] = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields['endDate'] = moment(params['endDate']).tz(moment().tz()).format();
        }
        const asyncFields = [
            applyAsyncFieldOnServerStream$(formFields, 'channelEntity', params['channelEntity'], this.channelEntities$),
            applyAsyncFieldOnServerStream$(formFields, 'eventEntity', params['eventEntity'], this.eventEntities$),
            applyAsyncFieldOnServerStream$(formFields, 'channel', params['channel'], this.channels$)
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    private loadFilter(field: SalesRequestsFilterField, q: string = null): void {
        const paramsChanged: GetFilterRequest = { q };
        this._salesRequestsService.loadFilter(field, paramsChanged);
    }

    private getFilterChannelEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('CHANNEL_ENTITY')
            .labelKey('SALES_REQUESTS.CHANNEL_ENTITY')
            .queryParam('channelEntity')
            .value(this.filtersForm.value.channelEntity)
            .build();
    }

    private getFilterEventEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('EVENT_ENTITY')
            .labelKey('SALES_REQUESTS.EVENT_ENTITY')
            .queryParam('eventEntity')
            .value(this.filtersForm.value.eventEntity)
            .build();
    }

    private getFilterChannel(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('CHANNEL')
            .labelKey('SALES_REQUESTS.CHANNEL')
            .queryParam('channel')
            .value(this.filtersForm.value.channel)
            .build();
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this._translate.instant('SALES_REQUESTS.DATE_FROM'));
        const value = this.filtersForm.value.startDate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this.formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE', this._translate.instant('SALES_REQUESTS.DATE_TO'));
        const value = this.filtersForm.value.endDate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this.formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this._translate.instant('SALES_REQUESTS.STATUS.LABEL'));
        const value = this.filtersForm.value.status;
        const channelStatusAux = Object.keys(value).filter(statusCheck => value[statusCheck]);
        if (channelStatusAux.length > 0) {
            filterItem.values = channelStatusAux.map(statusCheck =>
                new FilterItemValue(
                    SalesRequestsStatus[statusCheck],
                    this._translate.instant(`SALES_REQUESTS.STATUS_OPTS.${SalesRequestsStatus[statusCheck]}`)
                ));
            filterItem.urlQueryParams['status'] = channelStatusAux
                .map(statusCheck => SalesRequestsStatus[statusCheck]).join(',');
        }
        return filterItem;
    }
}

