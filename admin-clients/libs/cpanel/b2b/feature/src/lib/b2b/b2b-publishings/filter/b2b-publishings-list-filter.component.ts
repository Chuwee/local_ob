import { Metadata } from '@OneboxTM/utils-state';
import {
    B2bClientReduced, B2bSeatType, B2bService, GetB2bClientsRequest, GetB2bSeatsFiltersRequest
} from '@admin-clients/cpanel/b2b/data-access';
import { Channel, ChannelType, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, Session
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService, EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import { FilterItem, FilterItemValue, FilterItemBuilder, FilterWrapped } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldValue$, applyAsyncFieldWithServerReq$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnInit, inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { map, shareReplay, startWith, switchMap, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-b2b-publishings-list-filter',
    templateUrl: './b2b-publishings-list-filter.component.html',
    styleUrls: ['./b2b-publishings-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bPublishingsListFilterComponent extends FilterWrapped implements OnInit {
    private readonly _channelsSrv = inject(ChannelsService);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _b2bSrv = inject(B2bService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _eventsSrv = inject(EventsService);
    private readonly _eventsSessionsSrv = inject(EventSessionsService);
    private readonly _translate = inject(TranslateService);
    private readonly _formats: MatDateFormats = inject(MAT_DATE_FORMATS);
    private readonly _formStructure = {
        channel: null,
        entity: null,
        event: null,
        session: null,
        client: null,
        startDate: null,
        endDate: null,
        status: null
    };

    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();
    readonly dateTimeFormats = DateTimeFormats;
    readonly seatTypes = Object.values(B2bSeatType)
        .map(seatType => ({ id: seatType, name: `ENUMS.B2B_SEAT_TYPE_OPTS.${seatType}` }));

    entityId: number;
    entities$: Observable<Entity[]>;
    channels$: Observable<Channel[]> = this._channelsSrv.channelsList.getList$();
    events$: Observable<IdName[]> = this._b2bSrv.b2bSeatFiltersEventList.getList$();
    moreChannelsAvailable$ = this._channelsSrv.channelsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    moreEventsAvailable$ = this._b2bSrv.b2bSeatFiltersEventList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    moreClientsAvailable$ = this._b2bSrv.b2bClientsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    moreSessionsAvailable$ = this._b2bSrv.b2bSeatFiltersSessionList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    sessions$: Observable<Session[]> = this._b2bSrv.b2bSeatFiltersSessionList.getList$();
    clients$: Observable<B2bClientReduced[]> = this._b2bSrv.b2bClientsList.getList$();

    filtersForm: UntypedFormGroup;
    entity$: Observable<IdName>;
    @Input() canSelectEntity$: Observable<boolean>;

    ngOnInit(): void {
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesSrv.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this._entitiesSrv.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1));

        this.initForm();

        this.entity$.pipe(takeUntil(this.destroy)).subscribe(entity => {
            this.entityId = entity?.id;
            this.resetByChangingEntity();
            this._channelsSrv.channelsList.clear();
            this._b2bSrv.b2bSeatFiltersEventList.clear();
            this._b2bSrv.b2bClientsList.clear();
            this._b2bSrv.b2bSeatFiltersSessionList.clear();

        }
        );
        // Make HTTP calls:

    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterChannel(),
            this.getFilterEntity(),
            this.getFilterEvent(),
            this.getFilterSession(),
            this.getFilterClient(),
            this.getFilterB2bSeatType(),
            this.getFilterStartDate(),
            this.getFilterEndDate()
        ];
    }

    loadChannels(q: string, next = false): void {
        const request = {
            limit: 100,
            offset: 0,
            sort: 'name:asc',
            type: ChannelType.webB2B,
            entityId: this.entityId,
            name: q
        };
        if (!next) {
            this._channelsSrv.channelsList.load(request);
        } else {
            this._channelsSrv.channelsList.loadMore(request);
        }
    }

    loadSessions(q: string, next = false): void {
        const request: GetB2bSeatsFiltersRequest = {
            limit: 100,
            offset: 0,
            event_ids: this.filtersForm.value.event ? [this.filtersForm.value.event.id] : null,
            term: q,
            from: this.filtersForm.value.startDate ? moment(this.filtersForm.value.startDate).utc().format() : null,
            to: this.filtersForm.value.endDate ? moment(this.filtersForm.value.endDate).endOf('day').utc().format() : null
        };
        if (!this.filtersForm.value.event) {
            this._b2bSrv.b2bSeatFiltersSessionList.clear();
            return;
        }
        if (!next) {
            this._b2bSrv.b2bSeatFiltersSessionList.load(request);
        } else {
            this._b2bSrv.b2bSeatFiltersSessionList.loadMore(request);
        }
    }

    loadEvents(q: string, next = false): void {
        const request: GetB2bSeatsFiltersRequest = {
            limit: 100,
            offset: 0,
            entity_ids: this.entityId ? [this.entityId] : null,
            from: this.filtersForm.value.startDate ? moment(this.filtersForm.value.startDate).utc().format() : null,
            to: this.filtersForm.value.endDate ? moment(this.filtersForm.value.endDate).endOf('day').utc().format() : null,
            term: q
        };
        if (!next) {
            this._b2bSrv.b2bSeatFiltersEventList.load(request);
        } else {
            this._b2bSrv.b2bSeatFiltersEventList.loadMore(request);
        }
    }

    loadClients(q: string, next = false): void {
        const request: GetB2bClientsRequest = {
            limit: 100,
            offset: 0,
            sort: 'name:asc',
            entity_id: this.entityId,
            q
        };
        if (!next) {
            this._b2bSrv.b2bClientsList.load(request);
        } else {
            this._b2bSrv.b2bClientsList.loadMore(request);
        }
    }

    removeFilter(key: string, _: unknown): void {
        if (key === 'CHANNEL') {
            this.filtersForm.get('channel').reset();
        } else if (key === 'EVENT') {
            this.filtersForm.get('event').reset();
        } else if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'SESSION') {
            this.filtersForm.get('session').reset();
        } else if (key === 'CLIENT') {
            this.filtersForm.get('client').reset();
        } else if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        } else if (key === 'STATUS') {
            this.filtersForm.get('status').reset();
        }
    }

    resetByChangingEntity(): void {
        this.filtersForm.get('channel').reset();
        this.filtersForm.get('event').reset();
        this.filtersForm.get('session').reset();
        this.filtersForm.get('client').reset();
    }

    resetFilters(): void {
        this.filtersForm.reset();
        this.applyFilters();
    }

    compareById(option: { id: string; name: string }, selected: { id: string; name: string }): boolean {
        return option?.id === selected?.id;
    }

    compareByName(option: { id: string; name: string }, selected: { id: string; name: string }): boolean {
        return option?.name === selected?.name;
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);
        this.loadNestedFilters(params);
        if (params['status']) {
            formFields.status = this.seatTypes.find(typeObj => typeObj.id === params['status']);
        }
        if (params['startDate']) {
            formFields.startDate = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields.endDate = moment(params['endDate']).tz(moment().tz()).format();
        }
        if (params['entity']) {
            this.entityId = params['entity'];
        }
        const asyncFields = [
            applyAsyncFieldWithServerReq$(
                formFields, 'channel', params, ids => this._channelsSrv.getChannelsNames$(ids.map(id => Number(id))), false),
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id'),
            applyAsyncFieldWithServerReq$(
                formFields, 'event', params, ids => this._eventsSrv.getEvents$(ids.map(id => Number(id))), false),
            applyAsyncFieldWithServerReq$(
                formFields, 'session', params,
                ids => this._eventsSessionsSrv.getSessionsNames$(ids.map(id => Number(id)), params['event']), false),
            applyAsyncFieldWithServerReq$(
                formFields, 'client', params,
                ids => this._b2bSrv.getClientsNames$(ids.map(id => Number(id)), this.entityId || params['entityId']), true)
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                if (params['entity']) {
                    this.filtersForm.get('client').enable();
                }
                if (!params['event']) {
                    this.filtersForm.get('session').disable();
                }
                return this.getFilters();
            })
        );
    }

    private initForm(): void {
        this.filtersForm = this._fb.group(Object.assign({}, this._formStructure));
        this.filtersForm.get('event').valueChanges.pipe(takeUntil(this.destroy)).subscribe(event => {
            if (event) {
                this.filtersForm.get('session').enable();
            } else {
                this.filtersForm.get('session').disable();
            }
            this.filtersForm.get('session').reset();
            this._b2bSrv.b2bSeatFiltersSessionList.clear();
        });
        this.filtersForm.controls['startDate'].valueChanges.pipe(takeUntil(this.destroy)).subscribe(_ => {
            this._b2bSrv.b2bSeatFiltersEventList.clear();
            this._b2bSrv.b2bSeatFiltersSessionList.clear();
        });
        this.filtersForm.controls['endDate'].valueChanges.pipe(takeUntil(this.destroy)).subscribe(_ => {
            this._b2bSrv.b2bSeatFiltersEventList.clear();
            this._b2bSrv.b2bSeatFiltersSessionList.clear();
        });

        this.entity$ = this.filtersForm.get('entity').valueChanges;
        this.canSelectEntity$.pipe(
            switchMap(canSelect => {
                if (canSelect) {
                    return this.filtersForm.get('entity').valueChanges.pipe(startWith(this.filtersForm.get('entity').value));
                } else {
                    return of(true);
                }
            }),
            takeUntil(this.destroy)
        ).subscribe(entity => entity ? this.filtersForm.get('client').enable() : this.filtersForm.get('client').disable());
    }

    private loadNestedFilters(params: Params = {}): void {
        const values = this.filtersForm.value;
        const paramsChanged = { limit: 999, onlyInUseVenues: true, channelId: null };
        paramsChanged.channelId = values.channel?.id;
        if (!paramsChanged.channelId && params?.['channel']) {
            paramsChanged.channelId = params['channel'];
        }
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('B2B_PUBLISHINGS.FILTER.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterChannel(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('CHANNEL')
            .labelKey('B2B_PUBLISHINGS.FILTER.CHANNEL')
            .queryParam('channel')
            .value(this.filtersForm.value.channel)
            .build();
    }

    private getFilterEvent(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('EVENT')
            .labelKey('B2B_PUBLISHINGS.FILTER.EVENT')
            .queryParam('event')
            .value(this.filtersForm.value.event)
            .build();
    }

    private getFilterSession(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('SESSION')
            .labelKey('B2B_PUBLISHINGS.FILTER.SESSION')
            .queryParam('session')
            .value(this.filtersForm.value.session)
            .build();
    }

    private getFilterClient(): FilterItem {
        const filterItem = new FilterItem('CLIENT', this._translate.instant('B2B_PUBLISHINGS.FILTER.PROFESSIONAL_CLIENT'));
        return this.getFilterMulti(filterItem, 'client');
    }

    private getFilterB2bSeatType(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('STATUS')
            .labelKey('B2B_PUBLISHINGS.FILTER.STATUS')
            .queryParam('status')
            .value(this.filtersForm.value.status)
            .translateValue()
            .build();
    }

    private getFilterMulti(filterItem: FilterItem, formKey: string): FilterItem {
        const value = this.filtersForm.get(formKey).value as [{ id: string; date_start?: string; name: string }];
        if (value && value.length > 0) {
            filterItem.values = value.filter(i => !!i).map(valueItem => {
                if (valueItem.date_start) {
                    const startDate = moment(valueItem.date_start).format(this.dateTimeFormats.shortDateTime);
                    return new FilterItemValue(valueItem.id, `${startDate} - ${valueItem.name}`);
                } else {
                    return new FilterItemValue(valueItem.id, valueItem.name);
                }
            });
            filterItem.urlQueryParams[formKey] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this._translate.instant('B2B_PUBLISHINGS.FILTER.DATE_FROM'));
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
        const filterItem = new FilterItem('END_DATE', this._translate.instant('B2B_PUBLISHINGS.FILTER.DATE_TO'));
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
