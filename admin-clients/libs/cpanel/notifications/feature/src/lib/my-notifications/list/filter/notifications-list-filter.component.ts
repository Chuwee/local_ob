/* eslint-disable @typescript-eslint/dot-notation */
import { NotificationStatus } from '@admin-clients/cpanel/notifications/data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import {
    FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue,
    SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { FormBuilder, FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { forkJoin, Observable, of, Subject } from 'rxjs';
import { map, shareReplay, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-notifications-list-filter',
    templateUrl: './notifications-list-filter.component.html',
    styleUrls: ['./notifications-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormsModule, MaterialModule, FlexModule,
        ReactiveFormsModule, NgIf, SelectSearchComponent, NgFor, TranslatePipe, AsyncPipe
    ]
})
export class NotificationsListFilterComponent extends FilterWrapped implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _formStructure = {
        entity: null as { id: string; name: string },
        status: null as NotificationStatus[],
        startDate: null as string,
        endDate: null as string
    };

    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();
    readonly statusList = Object.values(NotificationStatus);
    readonly filtersForm = this._fb.group({
        entity: null as { id: string; name: string },
        status: new FormControl(null as NotificationStatus[]),
        startDate: null as string,
        endDate: null as string
    });

    entities$: Observable<Entity[]>;

    @Input() canSelectEntity$: Observable<boolean>;

    constructor(
        private _fb: FormBuilder,
        private _entitiesService: EntitiesBaseService,
        private _translate: TranslateService,
        @Inject(MAT_DATE_FORMATS) private readonly _formats: MatDateFormats) {
        super();
    }

    ngOnInit(): void {
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        allow_massive_email: true
                    });
                    return this._entitiesService.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );
    }

    override ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterStatus(),
            this.getFilterStartDate(),
            this.getFilterEndDate()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'STATUS') {
            const statusForm = this.filtersForm.controls.status;
            const statusValues = statusForm.value;
            statusForm.setValue(statusValues.filter(status => status !== value));
        } else if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    compareById(option: { id: string; name: string }, selected: { id: string; name: string }): boolean {
        return option?.id === selected?.id;
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);

        if (params['startDate']) {
            formFields.startDate = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields.endDate = moment(params['endDate']).tz(moment().tz()).format();
        }
        if (params['status']) {
            formFields.status = params['status'].split(',').filter(status => Object.values(NotificationStatus).includes(status));
        }
        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id')
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('NOTIFICATIONS.EMAIL_NOTIFICATION.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this._translate.instant('NOTIFICATIONS.EMAIL_NOTIFICATION.STATUS'));
        const value = this.filtersForm.value.status;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(
                valueItem, this._translate.instant('NOTIFICATIONS.EMAIL_NOTIFICATION.STATUS_OPTS.' + valueItem)
            ));
            filterItem.urlQueryParams['status'] = value.map(valueItem => valueItem).join(',');
        }
        return filterItem;
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this._translate.instant('EVENTS.DATE_FROM'));
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
        const filterItem = new FilterItem('END_DATE', this._translate.instant('EVENTS.DATE_TO'));
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
