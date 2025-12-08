import { Metadata } from '@OneboxTM/utils-state';
import { AfterContentInit, ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import {
    debounceTime, distinctUntilChanged, filter, first, map, scan, shareReplay, startWith, switchMap, take, takeUntil, tap, withLatestFrom
} from 'rxjs/operators';
import {
    ChannelPromotionEventScope, ChannelPromotionPriceTypesScope,
    ChannelPromotionsService
} from '@admin-clients/cpanel-channels-promotions-data-access';
import {
    GetSaleRequestPriceTypesRequest, SalesRequestsStatus,
    SalesRequestsService, provideSalesRequestService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { differenceWith, unionWith } from '@admin-clients/shared/utility/utils';
import { PromotionEventListElement } from '../events/promotion-events.model';
import { ChannelPromotionPriceTypeListElement as PriceTypeElement } from './channel-promotion-price-types.model';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-channel-promotion-price-types',
    templateUrl: './channel-promotion-price-types.component.html',
    styleUrls: ['./channel-promotion-price-types.component.scss'],
    providers: [
        provideSalesRequestService()
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionPriceTypesComponent implements OnInit, AfterContentInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _eventsScope: ChannelPromotionEventScope;
    private _filters: GetSaleRequestPriceTypesRequest = {
        limit: PAGE_SIZE
    };

    readonly scope = ChannelPromotionPriceTypesScope;
    readonly pageSize = PAGE_SIZE;
    readonly showSelectedOnlyClick = new EventEmitter<void>();

    eventSelectForm: UntypedFormControl;
    priceTypesForm: UntypedFormGroup;

    events$: Observable<IdName[]>;
    metadata$: Observable<Metadata>;
    salesRequestsloading$: Observable<boolean>;
    selectedOnly$: Observable<boolean>;
    selectedPriceTypes$: Observable<PriceTypeElement[]>;
    priceTypes$: Observable<PriceTypeElement[]>;
    totalPriceTypes$: Observable<number>;
    isSelectionDisabled: boolean;

    @Input() channelId: number;
    @Input() entityId: number;
    @Input() form: UntypedFormGroup;
    @Input() promotionId: number;

    constructor(
        private _salesRequestsService: SalesRequestsService,
        private _channelPromotionsService: ChannelPromotionsService
    ) { }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    ngOnInit(): void {
        this.priceTypesForm = this.form.get('price_types') as UntypedFormGroup;

        this.eventSelectForm = new UntypedFormControl();

        // EVENT SELECTION OBSERVERS

        this.eventSelectForm.valueChanges.pipe(
            debounceTime(200),
            filter(value => !!value),
            map(value => value?.[0]),
            distinctUntilChanged((x, y) => x?.id === y?.id),
            takeUntil(this._onDestroy)
        )
            .subscribe(() => {
                this._filters = { ...this._filters, offset: 0 };
                this.loadPriceTypes();
            });

        const selectableEvents$ =
            this.form.get('events.type').valueChanges
                .pipe(
                    distinctUntilChanged(),
                    switchMap(eventScope => {
                        this._eventsScope = eventScope;
                        if (eventScope === ChannelPromotionEventScope.restricted) {
                            return parentSelectedEvents$;
                        } else if (eventScope === ChannelPromotionEventScope.all) {
                            this.loadEvents();
                            return allEvents$;
                        } else {
                            return of([]);
                        }
                    }),
                    takeUntil(this._onDestroy),
                    shareReplay(1)
                );

        this.form.get('events').valueChanges
            .pipe(
                debounceTime(550),
                switchMap(() => selectableEvents$.pipe(take(1)))
            )
            .subscribe(events => {
                this.priceTypesForm.get('type').enable();
                if (events?.length > 0) {
                    this.isSelectionDisabled = false;
                } else {
                    if (this._eventsScope === null) {
                        this.priceTypesForm.get('type').disable();
                    }
                    this.isSelectionDisabled = true;
                    this.eventSelectForm.setValue(null);
                }
            });

        const parentSelectedEvents$: Observable<IdName[]> =
            this.form.get('events.selected').valueChanges.pipe(
                startWith(this.form.get('events.selected').value || []),
                debounceTime(500), // avoids a processing data while user is clicking several options
                map((selected: PromotionEventListElement[]) =>
                    selected.map(elem => ({
                        id: elem.saleReqId,
                        name: elem.name
                    })).sort((a, b) =>
                        a.name.localeCompare(b.name)
                    )
                ),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        parentSelectedEvents$.subscribe((events: IdName[]) => {
            if (events?.length && !events.find(event => event.id === this.selectedEventId)) {
                this.eventSelectForm.setValue([events[0]]);
            }
        });

        const allEvents$: Observable<IdName[]> =
            this._salesRequestsService.getSalesRequestsListData$().pipe(
                filter(list => !!list),
                map(list => list.map(elem => ({
                    id: elem.id,
                    name: elem.event.name
                }))
                ),
                tap(events => {
                    if (events?.length && !this.eventSelectForm.value) {
                        this.eventSelectForm.setValue([events[0]]);
                    }
                })
            );

        this.events$ = selectableEvents$;

        // PRICE TYPES OBSERVERS

        this.selectedOnly$ = this.showSelectedOnlyClick.pipe(
            scan((isSelectedOnlyMode: boolean) => !isSelectedOnlyMode, false),
            startWith(false),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.selectedPriceTypes$ =
            combineLatest([
                this.priceTypesForm.get('selected').valueChanges,
                this.eventSelectForm.valueChanges
            ])
                .pipe(
                    filter(([priceTypesSelected]: [PriceTypeElement[], IdName]) => !!priceTypesSelected),
                    map(([selected]) => selected.filter(pricetype => // filter PriceTypes not in this sale req
                        pricetype.catalog_sale_request_id === this.selectedEventId)),
                    map(selected => selected?.sort((a, b) =>
                        a.venue_template?.name?.localeCompare(b.venue_template?.name))), // sort by venue template name
                    takeUntil(this._onDestroy),
                    shareReplay(1)
                );

        this.selectedPriceTypes$.subscribe();

        // all selectable events extracted from sales requests
        const allPriceTypes$: Observable<PriceTypeElement[]> =
            this._salesRequestsService.getSaleRequestPriceTypes$().pipe(
                filter(pricetypes => !!pricetypes),
                map((pricetypes => pricetypes.map(pricetype => ({
                    id: pricetype.id,
                    name: pricetype.name,
                    venue_template: pricetype.venue_template,
                    catalog_sale_request_id: this.selectedEventId
                }))))
            );

        this.priceTypes$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ? this.selectedPriceTypes$ : allPriceTypes$)
        );

        this.metadata$ = this.selectedOnly$.pipe(
            switchMap(isActive => isActive ?
                this.selectedPriceTypes$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
                this._salesRequestsService.getSaleRequestPriceTypesMetadata$()
            ),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

        this.salesRequestsloading$ = this._salesRequestsService.isSaleRequestPriceTypesLoading$();

        this.priceTypesForm.get('type').valueChanges
            .pipe(takeUntil(this._onDestroy), distinctUntilChanged())
            .subscribe((scope: ChannelPromotionPriceTypesScope) => {
                scope === ChannelPromotionPriceTypesScope.restricted ?
                    this.priceTypesForm.get('selected').enable() : this.priceTypesForm.get('selected').disable();
            });

        this.totalPriceTypes$ = this._salesRequestsService.getSaleRequestPriceTypesMetadata$().pipe(map(metadata => metadata?.total));
    }

    ngAfterContentInit(): void {
        this._channelPromotionsService.getPromotionPriceTypes$().pipe(
            filter(value => !!value),
            takeUntil(this._onDestroy)
        ).subscribe(priceTypes => {
            setTimeout(() => {
                this.priceTypesForm.reset({
                    type: priceTypes.type || null,
                    selected: priceTypes.price_types || []
                });
            });
        });
    }

    /**
     * selects all filtered price types
     */
    selectAll(change?: MatCheckboxChange): void {
        this._salesRequestsService.loadAllSaleRequestPriceTypes({
            saleRequestId: this.selectedEventId.toString(),
            ...this._filters
        });
        this._salesRequestsService.getAllSaleRequestPriceTypes$()
            .pipe(
                first(priceTypes => !!priceTypes),
                map(priceTypes => priceTypes?.map(priceType => ({
                    id: priceType.id,
                    name: priceType.name,
                    venue_template: priceType.venue_template,
                    catalog_sale_request_id: this.selectedEventId
                }))
                )
            )
            .subscribe(priceTypes => {
                if (change?.checked) {
                    this.priceTypesForm.get('selected').patchValue(unionWith(this.priceTypesForm.get('selected').value, priceTypes));
                } else {
                    this.priceTypesForm.get('selected').patchValue(differenceWith(this.priceTypesForm.get('selected').value, priceTypes));
                }
                this.priceTypesForm.markAsTouched();
                this.priceTypesForm.markAsDirty();
            });

    }

    getSelectedPriceTypesInEvent(event?: IdName): number {
        return this.priceTypesForm.get('selected').value?.filter((priceTypes: PriceTypeElement) =>
            priceTypes.catalog_sale_request_id === (event?.id || this.selectedEventId)).length || 0;
    }

    filterChangeHandler(filters: Partial<GetSaleRequestPriceTypesRequest>): void {
        this._filters = {
            ...this._filters,
            ...filters
        };
        this.loadPriceTypes();
    }

    loadPriceTypes = (): void => {
        if (!this.selectedEventId) {
            return;
        }
        // cancel prev requests so it keeps consistency
        this._salesRequestsService.cancelSaleRequestPriceTypes();
        this._salesRequestsService.loadSaleRequestPriceTypes({
            ...this._filters,
            saleRequestId: this.selectedEventId.toString()
        });

        // change to non selected only view if table content loaded
        this._salesRequestsService.getSaleRequestPriceTypes$().pipe(
            withLatestFrom(this.selectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnly]) => {
            if (isSelectedOnly) {
                this.showSelectedOnlyClick.emit();
            }
        });
    };

    loadEvents = (filter?: string): void => {
        if (this._eventsScope === ChannelPromotionEventScope.all) {
            this._salesRequestsService.loadSalesRequestsList({
                q: filter,
                sort: 'event.name:asc',
                eventEntity: this.entityId,
                channel: this.channelId,
                status: [SalesRequestsStatus.accepted],
                include_third_party_entity_events: true,
                fields: ['event.id', 'event.name', 'event.start_date']
            });
        }
    };

    get selectedEventId(): number {
        return this.eventSelectForm.value?.[0]?.id;
    }

    get selectedEventName(): string {
        return this.eventSelectForm.value?.[0]?.name;
    }
}
