import { Metadata } from '@OneboxTM/utils-state';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { Observable, Subject } from 'rxjs';
import {
    debounceTime, distinctUntilChanged, filter, first, map, scan, shareReplay, startWith, switchMap, take, takeUntil, withLatestFrom
} from 'rxjs/operators';
import { ChannelPromotionEventScope, ChannelPromotionsService } from '@admin-clients/cpanel-channels-promotions-data-access';
import {
    GetSalesRequestsRequest, SalesRequestsStatus,
    SalesRequestsService, SalesRequestsEventStatus,
    provideSalesRequestService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { SearchablePaginatedSelectionLoadEvent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { differenceWith, unionWith } from '@admin-clients/shared/utility/utils';
import { PromotionEventListElement } from './promotion-events.model';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-channel-promotion-events',
    templateUrl: './promotion-events.component.html',
    styleUrls: ['./promotion-events.component.scss'],
    providers: [
        provideSalesRequestService()
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionEventsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _filters: Partial<GetSalesRequestsRequest> = {
        limit: PAGE_SIZE,
        sort: 'event.name:asc',
        status: [SalesRequestsStatus.accepted],
        event_status: [
            SalesRequestsEventStatus.planned,
            SalesRequestsEventStatus.inProgramming,
            SalesRequestsEventStatus.ready,
            SalesRequestsEventStatus.inProgress
        ],
        include_third_party_entity_events: true,
        fields: ['event.id', 'event.name', 'event.start_date']
    };

    readonly scope = ChannelPromotionEventScope;
    readonly dateTimeFormats = DateTimeFormats;
    readonly showSelectedOnlyClick = new EventEmitter<void>();
    readonly pageSize = PAGE_SIZE;
    eventList$: Observable<PromotionEventListElement[]>;
    metadata$: Observable<Metadata>;
    selectedOnlyEvents$: Observable<boolean>;
    salesRequestsloading$: Observable<boolean>;
    hasSelectableEvents$: Observable<boolean>;
    totalEvents$: Observable<number>;
    totalFilteredEvents$: Observable<number>;

    @Input() form: UntypedFormGroup;
    @Input() entityId: number;
    @Input() channelId: number;
    @Input() promotionId: number;

    constructor(
        private _channelSalesReqsService: SalesRequestsService,
        private _channelPromotionsService: ChannelPromotionsService
    ) { }

    ngOnInit(): void {

        this._filters = {
            ...this._filters,
            eventEntity: this.entityId,
            channel: this.channelId
        };

        this.selectedOnlyEvents$ = this.showSelectedOnlyClick.pipe(
            scan((isActive: boolean) => !isActive, false),
            startWith(false),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        const selectedEvents$: Observable<PromotionEventListElement[]> =
            this.form.get('selected').valueChanges.pipe(
                filter(promoEvents => !!promoEvents),
                map((selected: PromotionEventListElement[]) => selected?.sort((a, b) => a.name.localeCompare(b.name))),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        selectedEvents$.subscribe();

        // all selectable events extracted from sales requests
        const allEvents$: Observable<PromotionEventListElement[]> =
            this._channelSalesReqsService.getSalesRequestsListData$().pipe(
                filter(salesRequests => !!salesRequests),
                map((saleRequests => saleRequests.map(saleReq => ({
                    id: saleReq.event.id,
                    name: saleReq.event.name,
                    start_date: saleReq.event.start_date,
                    saleReqId: saleReq.id
                }))))
            );

        this.eventList$ = this.selectedOnlyEvents$.pipe(
            switchMap(isActive => isActive ? selectedEvents$ : allEvents$)
        );

        this.metadata$ = this.selectedOnlyEvents$.pipe(
            switchMap(isActive => isActive ?
                selectedEvents$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
                this._channelSalesReqsService.getSalesRequestsListMetadata$()
            ),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.totalEvents$ = this._channelSalesReqsService.getSalesRequestsListMetadata$()
            .pipe(first(val => !!val), map(metadata => metadata?.total));

        this.totalFilteredEvents$ = this._channelSalesReqsService.getSalesRequestsListMetadata$()
            .pipe(map(metadata => metadata?.total));

        this.salesRequestsloading$ = this._channelSalesReqsService.isSalesRequestsListLoading$();

        this.hasSelectableEvents$ = allEvents$.pipe(
            take(1), // just first time bc it doesn't have filters
            map(eventList => !!eventList?.length),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.form.get('type').valueChanges
            .pipe(takeUntil(this._onDestroy), distinctUntilChanged())
            .subscribe((scope: ChannelPromotionEventScope) => {
                scope === ChannelPromotionEventScope.restricted ?
                    this.form.get('selected').enable() : this.form.get('selected').disable();
            });

        this._channelPromotionsService.getPromotionEvents$().pipe(
            filter(value => !!value),
            debounceTime(10),
            takeUntil(this._onDestroy)
        ).subscribe(events => {
            this.form.reset({
                type: events.type,
                selected: events.events?.map(event => ({
                    id: event.id,
                    name: event.name,
                    start_date: event.start_date,
                    saleReqId: event.catalog_sale_request_id
                })) || []
            });
        });

    }

    get selectedEvents(): number {
        return this.form.get('selected')?.value?.length;
    }

    /**
     * selects all filtered events
     */
    selectAll(change?: MatCheckboxChange): void {
        this._channelSalesReqsService.loadAllSalesRequests(this._filters);
        this._channelSalesReqsService.getAllSalesRequests$()
            .pipe(
                first(saleRequests => !!saleRequests),
                map((saleRequests => saleRequests.map(saleReq => ({
                    id: saleReq.event.id,
                    name: saleReq.event.name,
                    start_date: saleReq.event.start_date,
                    saleReqId: saleReq.id
                }))))
            )
            .subscribe(events => {
                if (change?.checked) {
                    this.form.get('selected').patchValue(unionWith(this.form.get('selected').value, events));
                } else {
                    this.form.get('selected').patchValue(differenceWith(this.form.get('selected').value, events));
                }
                this.form.markAsTouched();
                this.form.markAsDirty();
            });
    }

    loadSalesRequests({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this._filters = { ...this._filters, limit, offset, q };

        // cancel prev requests so it keeps consistency
        this._channelSalesReqsService.cancelSalesRequestsList();
        this._channelSalesReqsService.loadSalesRequestsList(this._filters);

        // change to non selected only view if a search is made
        this._channelSalesReqsService.getSalesRequestsListData$().pipe(
            withLatestFrom(this.selectedOnlyEvents$),
            take(1)
        ).subscribe(([, isSelectedOnlyMode]) => {
            if (isSelectedOnlyMode) {
                this.showSelectedOnlyClick.emit();
            }
        });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
