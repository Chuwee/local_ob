import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { Seat, VenueTemplateItemType, VmItemsMap } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnDestroy, OnInit, Signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { delay, filter, first, map, startWith, tap, withLatestFrom } from 'rxjs/operators';
import { VenueTemplateLabelGroupType } from '../../../models/label-group/venue-template-label-group-type.enum';
import { VenueTemplateActionType, VenueTemplateGetSeatInfo } from '../../../models/venue-template-action.model';
import { VenueTemplateSelectionChange } from '../../../models/venue-template-selection-change.model';
import { StandardVenueTemplateBaseService } from '../../../services/standard-venue-template-base.service';
import { StandardVenueTemplateFilterService } from '../../../services/standard-venue-template-filter.service';
import { StandardVenueTemplateRelocationService } from '../../../services/standard-venue-template-relocation.service';
import { StandardVenueTemplateSelectionService } from '../../../services/standard-venue-template-selection.service';

interface SelectedSeat {
    code: string;
    venueItems: { seat: Seat; selected: boolean; isInSM: boolean }[];
}

@Component({
    selector: 'app-relocation-mgr-origin-seats',
    templateUrl: './relocation-mgr-origin-seats.component.html',
    styleUrl: './relocation-mgr-origin-seats.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatIcon, MatDivider, MatIconButton, TranslatePipe, MatButton]
})
export class RelocationMgrOriginSeatsComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #standardVenueTemplateBaseSrv: StandardVenueTemplateBaseService = inject(StandardVenueTemplateBaseService);
    readonly #standardVenueTemplateFilterSrv: StandardVenueTemplateFilterService = inject(StandardVenueTemplateFilterService);
    readonly #standardVenueTemplateSelectionSrv: StandardVenueTemplateSelectionService = inject(StandardVenueTemplateSelectionService);
    readonly #standardVenueTplRelocationSrv = inject(StandardVenueTemplateRelocationService);
    readonly #messageDialogSrv = inject(MessageDialogService);

    readonly $selectedSeats: Signal<SelectedSeat[]> = toSignal(
        combineLatest([
            this.#standardVenueTemplateBaseSrv.getVenueItems$(),
            this.#standardVenueTplRelocationSrv.selectedOriginSeatIds.get$(),
            this.#standardVenueTplRelocationSrv.selectedOriginOrderItems.get$()
        ])
            .pipe(
                tap(([, selectedSeats]) => // Update relocation status based on selected origin seats
                    this.#standardVenueTemplateBaseSrv.relocation.updateStatus({ originSeatsSelected: !!selectedSeats?.length })),
                map(([venueItems, selectedSeats, orderItemList]) => {
                    orderItemList ??= [];
                    selectedSeats ??= [];
                    // Makes a set with all available order codes filtered by selected seats, orderItemList can contain unselected seats
                    const availableItemsOrders = new Set(
                        orderItemList
                            .filter(orderItem => selectedSeats.includes(orderItem.seatId))
                            .map(orderItem => orderItem.orderCode)
                    );
                    // builds result mixing orderCode, order orderItems and venueItems (seats)
                    const result: SelectedSeat[] = Array.from(availableItemsOrders)
                        .map(code => {
                            const orderItems = orderItemList.filter(orderItem => orderItem.orderCode === code);
                            const orderVenueItems = orderItems.map(orderItem =>
                            ({
                                seat: venueItems.seats.get(orderItem.seatId), selected: selectedSeats.includes(orderItem.seatId),
                                isInSM: orderItem?.isInSM
                            }));
                            return { code, venueItems: orderVenueItems };
                        });
                    // the orders of some seats may not have been loaded yet
                    const availableItemsWithOrder = new Set(orderItemList.map(orderItem => orderItem.seatId));
                    const seatsWithoutOrder = selectedSeats.filter(seatId => !availableItemsWithOrder.has(seatId));
                    if (seatsWithoutOrder.length) {
                        result.push({
                            code: 'undefined',
                            venueItems: seatsWithoutOrder.map(seatId =>
                                ({ seat: venueItems.seats.get(seatId), selected: true, isInSM: false }))
                        });
                    }
                    return result;
                }),
                tap(result => this.#checkSecondaryMarketSeats(result))
            )
    );

    readonly $showMoreSeatsWarning = computed<{ [orderCode: string]: boolean }>(() =>
        this.$selectedSeats().reduce<{ [orderCode: string]: boolean }>((prev, curr) => {
            prev[curr.code] = curr.venueItems.some(item => !item.selected);
            return prev;
        }, {})
    );

    readonly $totalSelectedTickets = toSignal(
        this.#standardVenueTplRelocationSrv.selectedOriginSeatIds.get$().pipe(map(seatIds => seatIds?.length || 0))
    );

    ngOnInit(): void {
        this.#filterSoldSeats();
        this.#loadSelectedSeats(); // Load selected seats from state if there are any
        this.#standardVenueTemplateSelectionSrv.getSelectionQueue$() // Triggered when selection changes
            .pipe(
                startWith(null),
                withLatestFrom(this.#standardVenueTplRelocationSrv.selectedOriginSeatIds.get$().pipe(first())),
                filter(([itemsSelection, selectedSeats]) => !(!itemsSelection && !!selectedSeats)),
                map(([itemsSelection]) => itemsSelection),
                delay(0), // this handling can revert selections, this delay ensures the right selection rendering
                withLatestFrom(
                    this.#standardVenueTemplateSelectionSrv.getSelectedVenueItems$(),
                    this.#standardVenueTemplateBaseSrv.getVenueItems$(),
                    this.#standardVenueTplRelocationSrv.selectedOriginSeatIds.get$()
                ),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([itemsSelection, selection, venueItems, selectedOriginSeatIds]) =>
                this.#handleSeatSelection(itemsSelection, selection, venueItems, selectedOriginSeatIds));
    }

    ngOnDestroy(): void {
        // When ending / canceling relocation process the filters are restored
        this.#standardVenueTemplateFilterSrv.filterLabel({ id: 'SOLD', labelGroupId: VenueTemplateLabelGroupType.state, filtering: false });
    }

    selectSeats(seatIds: number[], unselect: boolean = false): void {
        this.#standardVenueTemplateSelectionSrv.selectSeats(seatIds, unselect);
    }

    #filterSoldSeats(): void { // When starting a relocation the seats are filtered to show just the available ones
        this.#standardVenueTemplateFilterSrv.filterLabel({ id: 'SOLD', labelGroupId: VenueTemplateLabelGroupType.state, filtering: true });
        this.#standardVenueTemplateSelectionSrv.checkFilteredSelectedItems();
    }

    #loadSelectedSeats(): void {
        this.#standardVenueTplRelocationSrv.selectedOriginSeatIds.get$()
            .pipe(
                first(),
                filter(Boolean)
            )
            .subscribe(selectedSeats => {
                selectedSeats?.length && this.#standardVenueTemplateSelectionSrv.selectSeats(selectedSeats, true);
            });
    }

    #handleSeatSelection(
        itemsSelection: VenueTemplateSelectionChange,
        selectedItems: { seats: Set<number>; nnzs: Set<number> },
        venueItems: VmItemsMap,
        selectedOriginSeatIds: number[]
    ): void {
        if (!itemsSelection || itemsSelection.select) { // Clicking on an unselected seat
            // If nnzs are selected, remove them
            if (selectedItems.nnzs?.size) {
                this.#standardVenueTemplateSelectionSrv.selectNNZs(Array.from(selectedItems.nnzs), true);
            }
            // gets the new selected seats, or all selected seats if the user still haven't done any selection in relocation mode
            const newSelectedSeats = itemsSelection?.items
                .filter(item => (item.itemType === VenueTemplateItemType.seat) && !selectedOriginSeatIds.includes(item.id)) ||
                Array.from(selectedItems.seats).map(seatId => venueItems.seats.get(seatId));
            if (selectedItems.seats.size) {
                if (selectedItems.seats.size > this.#standardVenueTplRelocationSrv.selectionLimit()) {
                    this.#messageDialogSrv.showWarn({
                        title: 'VENUE_TPLS.TITLES.DIALOG_ORIGIN_SELECTION',
                        message: 'VENUE_TPLS.FORMS.INFOS.DIALOG_ORIGIN_SELECTION',
                        showCancelButton: false,
                        actionLabel: 'ACTIONS.CONFIRM',
                        size: DialogSize.SMALL,
                        messageParams: { seatLimit: this.#standardVenueTplRelocationSrv.selectionLimit() }
                    });
                    // If selected seats are more than the limit then show the warning and unselect those extra seats
                    if (selectedItems.seats.size - newSelectedSeats.length <= this.#standardVenueTplRelocationSrv.selectionLimit()) {
                        this.#standardVenueTemplateSelectionSrv.selectSeats(newSelectedSeats.map(item => item.id), true);
                    } else {
                        this.#standardVenueTemplateSelectionSrv.unselectAll();
                    }
                } else {
                    // Load the new selected tickets
                    this.#standardVenueTemplateBaseSrv.emitAction({
                        type: VenueTemplateActionType.selectRelocationOrigin,
                        data: newSelectedSeats.map((seat: Seat) => seat.ticketId)
                    } as VenueTemplateGetSeatInfo);
                }
            }
        }
        this.#standardVenueTplRelocationSrv.selectedOriginSeatIds.set(Array.from(selectedItems.seats));
    }

    #checkSecondaryMarketSeats(selectedSeats: SelectedSeat[]): void {
        const invalidSeats = selectedSeats.flatMap(item => item.venueItems)
            .filter(item => item.selected && item.isInSM).map(item => item.seat.id);
        if (invalidSeats.length) {
            this.selectSeats(invalidSeats, true);
            this.#messageDialogSrv.showAlert({
                title: 'VENUE_TPLS.TITLES.DIALOG_SEAT_ON_SECONDARY_MARKET',
                message: 'VENUE_TPLS.FORMS.INFOS.DIALOG_SEAT_ON_SECONDARY_MARKET',
                showCancelButton: false,
                actionLabel: 'ACTIONS.CONFIRM',
                size: DialogSize.MEDIUM
            });
        }
    }
}
