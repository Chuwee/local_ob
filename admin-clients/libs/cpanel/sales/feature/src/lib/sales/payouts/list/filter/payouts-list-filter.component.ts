import { Metadata } from '@OneboxTM/utils-state';
import { ChannelsService, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventsService, GetEventsRequest } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, GetSessionsRequest } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    FilterWrapped, FilterItem, FilterItemValue, DateTimeModule, SelectServerSearchComponent,
    FilterItemBuilder
} from '@admin-clients/shared/common/ui/components';
import { compareWithIdOrCode, DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { applyAsyncFieldWithServerReq$, deepEqual } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { debounceTime, filter, forkJoin, mapTo, Observable, tap } from 'rxjs';
import { map, pairwise, startWith } from 'rxjs/operators';

@Component({
    selector: 'app-payouts-list-filter',
    templateUrl: './payouts-list-filter.component.html',
    styleUrls: ['./payouts-list-filter.component.scss'],
    imports: [TranslatePipe, DateTimeModule, SelectServerSearchComponent,
        MatFormField, DateTimePipe, ReactiveFormsModule, MatDivider, MatOption, MatSelect],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PayoutsListFilterComponent extends FilterWrapped implements OnInit, OnDestroy {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translate = inject(TranslateService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #eventsSessionsSrv = inject(EventSessionsService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $user = toSignal(this.#authSrv.getLoggedUser$());

    readonly $canFilterByEvents = computed(() =>
        this.$user() ? AuthenticationService.isSomeRoleInUserRoles(this.$user(), [UserRoles.EVN_MGR, UserRoles.ENT_MGR, UserRoles.OPR_MGR, UserRoles.OPR_ANS]) : false);

    readonly #formFields = {
        channel: 'channel',
        event: 'event',
        session: 'session',
        currency: 'currency',
        payout_type: 'payout_type'
    };

    readonly #formStructure = {
        channel: [] as number[],
        event: [] as number[],
        session: [] as number[],
        currency: null as { id: string; name: string },
        payout_type: null as { id: string; name: string }
    };

    readonly payoutTypes = ['MANUAL', 'STRIPE'].map(value => ({ id: value, name: 'PAYOUT.TYPE_OPTS.' + value }));

    readonly dateTimeFormats = DateTimeFormats;
    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
    // channels
    readonly channels$ = this.#channelsSrv.channelsList.getList$();
    readonly moreChannelsAvailable$ = this.#channelsSrv.channelsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    // events
    readonly events$ = this.#eventsSrv.eventsList.getData$();
    readonly moreEventsAvailable$ = this.#eventsSrv.eventsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    // sessions
    readonly sessions$ = this.#eventsSessionsSrv.sessionList.getData$();
    readonly moreSessionsAvailable$ = this.#eventsSessionsSrv.sessionList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly compareWith = compareWithIdOrCode;

    @Input() startDate: string;
    @Input() endDate: string;

    ngOnInit(): void {
        // initial state
        this.filtersForm.get(this.#formFields.session).disable();
        // nested fields logic
        this.getFormFieldObs(this.#formFields.event, this.filtersForm.get(this.#formFields.event).value)
            .subscribe(() => this.eventChangeHandler());
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.#channelsSrv.channelsList.clear();
        this.#eventsSrv.eventsList.clear();
        this.#eventsSessionsSrv.sessionList.clear();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterChannel(),
            this.getFilterEvent(),
            this.getFilterSession(),
            this.getFilterType()
        ].filter(element => !!element);
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'CHANNEL') {
            this.resetFilterMulti('channel', value);
        } else if (key === 'EVENT') {
            this.resetFilterMulti('event', value);
        } else if (key === 'SESSION') {
            this.resetFilterMulti('session', value);
        } else if (key === 'CURRENCY') {
            this.resetFilter('currency');
        } else if (key === 'PAYOUT_TYPE') {
            this.resetFilter('payout_type');
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);
        const asyncFields: Observable<IdName[]>[] = [];
        if (params['startDate'] && params['endDate']) {
            if (params['startDate'] !== this.startDate || params['endDate'] !== this.endDate) {
                this.startDate = String(params['startDate']);
                this.endDate = String(params['endDate']);
            }
        } else {
            this.startDate = null;
            this.endDate = null;
        }
        if (params['type']) {
            formFields.payout_type = params['type'];
        }
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, 'channel', params, ids => this.#channelsSrv.getChannelsNames$(ids.map(id => Number(id))), true)
        );
        asyncFields.push(applyAsyncFieldWithServerReq$(
            formFields, 'event', params, ids => this.#eventsSrv.getEvents$(ids.map(id => Number(id))), true)
        );

        if (params[this.#formFields.event] && !params[this.#formFields.event].includes(',')) {
            this.filtersForm.get(this.#formFields.session).enable({ emitEvent: false });
        } else {
            this.filtersForm.get(this.#formFields.session).disable({ emitEvent: false });
        }
        if (params['session']) {
            asyncFields.push(applyAsyncFieldWithServerReq$(
                formFields, 'session', params,
                ids => this.#eventsSessionsSrv.getSessionsNames$(ids.map(id => Number(id)), params['event']), true)
            );
        }

        return forkJoin(asyncFields).pipe(
            tap(() => this.filtersForm.setValue(formFields, { emitEvent: false })),
            map(() => this.getFilters())
        );
    }

    // selects data load

    loadChannels(q: string, next = false): void {
        const request = {
            limit: 100,
            offset: 0,
            sort: 'name:asc',
            type: ChannelType.web,
            name: q
        };
        if (!next) {
            this.#channelsSrv.channelsList.load(request);
        } else {
            this.#channelsSrv.channelsList.loadMore(request);
        }
    }

    loadSessions(q: string, next = false): void {
        const request: GetSessionsRequest = {
            limit: 100,
            offset: 0,
            q,
            initStartDate: this.filtersForm.value.startDate ? moment(this.filtersForm.value.startDate).utc().format() : null,
            finalEndDate: this.filtersForm.value.endDate ? moment(this.filtersForm.value.endDate).endOf('day').utc().format() : null
        };
        if (!this.filtersForm.value.event) {
            this.#eventsSessionsSrv.sessionList.clear();
            return;
        }
        if (!next) {
            this.#eventsSessionsSrv.sessionList.load(this.filtersForm.value.event[0]?.id, request);
        } else {
            this.#eventsSessionsSrv.sessionList.loadMore(this.filtersForm.value.event[0]?.id, request);
        }
    }

    loadEvents(q: string, next = false): void {
        const request: GetEventsRequest = {
            limit: 100,
            offset: 0,
            q
        };
        if (!next) {
            this.#eventsSrv.eventsList.load(request);
        } else {
            this.#eventsSrv.eventsList.loadMore(request);
        }
    }

    // FILTER FORM
    private getFormFieldObs<T>(field: string, startValue: T = undefined): Observable<T> {
        return this.filtersForm.get(field).valueChanges
            .pipe(
                debounceTime(100),
                startWith(startValue),
                pairwise(),
                filter(([prev, next]) => !deepEqual(prev, next)),
                mapTo(null),
                takeUntilDestroyed(this.#destroyRef)
            );
    }

    private eventChangeHandler(): void {
        this.filtersForm.get(this.#formFields.session).setValue([], { emitEvent: false });
        if (this.filtersForm.get(this.#formFields.event).value?.length === 1) {
            this.filtersForm.get(this.#formFields.session).enable();
        } else {
            this.filtersForm.get(this.#formFields.session).disable();
        }
    }

    // LIST FILTER

    private resetFilterMulti(formKey: string, value: unknown): void {
        const form = this.filtersForm.get(formKey);
        const values: { id: string }[] = form.value;
        form.reset(values.filter(type => type.id !== value));
    }

    private resetFilter(formKey: string): void {
        this.filtersForm.get(formKey).reset();
    }

    private getFilterChannel(): FilterItem {
        const filterItem = new FilterItem('CHANNEL', this.#translate.instant('PAYOUT.CHANNEL'));
        return this.getFilterMulti(filterItem, 'channel');
    }

    private getFilterType(): FilterItem {
        return this.filtersForm.value.payout_type ? new FilterItemBuilder(this.#translate)
            .key('PAYOUT_TYPE')
            .labelKey('FORMS.LABELS.PAYOUT_TYPE')
            .queryParam('type')
            .value({ id: this.filtersForm.value.payout_type, name: 'PAYOUT.TYPE_OPTS.' + this.filtersForm.value.payout_type })
            .translateValue()
            .build() : null;
    }

    private getFilterSession(): FilterItem {
        const filterItem = new FilterItem('SESSION', this.#translate.instant('PAYOUT.SESSION'));
        return this.getFilterMulti(filterItem, 'session');
    }

    private getFilterEvent(): FilterItem {
        const filterItem = new FilterItem('EVENT', this.#translate.instant('PAYOUT.EVENT'));
        return this.getFilterMulti(filterItem, 'event');
    }

    private getFilterMulti(filterItem: FilterItem, formKey: string): FilterItem {
        const value = this.filtersForm.get(formKey).value as [{ id: string; date_start?: string; name: string }];
        if (value && value.length > 0) {
            filterItem.values = value.filter(i => !!i).map(valueItem => {
                if (valueItem.date_start) {
                    const startDate = moment(valueItem.date_start).format(DateTimeFormats.shortDateTime);
                    return new FilterItemValue(valueItem.id, `${startDate} - ${valueItem.name}`);
                } else {
                    return new FilterItemValue(valueItem.id, valueItem.name);
                }
            });
            filterItem.urlQueryParams[formKey] = value.map(valueItem => valueItem?.id).join(',');
        }
        return filterItem;
    }

}
