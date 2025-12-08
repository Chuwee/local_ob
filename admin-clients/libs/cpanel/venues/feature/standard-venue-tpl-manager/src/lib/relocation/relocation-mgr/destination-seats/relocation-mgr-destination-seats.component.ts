import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { Seat, VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnDestroy, OnInit, Signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { delay, filter, first, map, tap } from 'rxjs/operators';
import { VenueTemplateLabelGroupType } from '../../../models/label-group/venue-template-label-group-type.enum';
import { VenueTemplateSelectionChange } from '../../../models/venue-template-selection-change.model';
import { StandardVenueTemplateBaseService } from '../../../services/standard-venue-template-base.service';
import { StandardVenueTemplateFilterService } from '../../../services/standard-venue-template-filter.service';
import { StandardVenueTemplateRelocationService } from '../../../services/standard-venue-template-relocation.service';
import { StandardVenueTemplateSelectionService } from '../../../services/standard-venue-template-selection.service';

const MAX_SEATS_PER_SELECTION = 1;

@Component({
    selector: 'app-relocation-mgr-destination-seats',
    templateUrl: './relocation-mgr-destination-seats.component.html',
    styleUrl: './relocation-mgr-destination-seats.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [TranslatePipe, MatIcon, MatIconButton]
})
export class RelocationMgrDestinationSeatsComponent implements OnInit, OnDestroy {
    readonly #standardVenueTemplateBaseSrv: StandardVenueTemplateBaseService = inject(StandardVenueTemplateBaseService);
    readonly #standardVenueTemplateFilterSrv: StandardVenueTemplateFilterService = inject(StandardVenueTemplateFilterService);
    readonly #standardVenueTemplateSelectionSrv: StandardVenueTemplateSelectionService = inject(StandardVenueTemplateSelectionService);
    readonly #standardVenueTplRelocationSrv = inject(StandardVenueTemplateRelocationService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);

    readonly #$selectedDestinationSeats = toSignal(
        this.#standardVenueTplRelocationSrv.selectedDestinationSeats.get$()
    );

    readonly $selectedSeats: Signal<{ code: string; venueItems: Seat[][]; isConsecutive: boolean }[]> = toSignal(
        combineLatest([
            this.#standardVenueTemplateBaseSrv.getVenueItems$(),
            this.#standardVenueTplRelocationSrv.selectedOriginSeatIds.get$(),
            this.#standardVenueTplRelocationSrv.selectedOriginOrderItems.get$(),
            this.#standardVenueTplRelocationSrv.selectedDestinationSeats.get$()
        ])
            .pipe(
                tap(([, , , selectedDest]) => { // Update relocaiton status based on selected destination seats
                    const canStartRelocation = Object.values(selectedDest).every(Boolean);
                    this.#standardVenueTemplateBaseSrv.relocation.updateStatus({ canStartRelocation });
                }),
                map(([venueItems, selectedSeats, orderItemList, selectedDestSeats]) => {
                    // Makes a set with all available order codes filtered by selected seats
                    const availableItemsOrders = new Set(
                        orderItemList
                            .filter(orderItem => selectedSeats.includes(orderItem.seatId))
                            .map(orderItem => orderItem.orderCode)
                    );
                    // builds result mixing orderCode, order orderItems and venueItems (seats)
                    const result = Array.from(availableItemsOrders)
                        .map(code => {
                            const orderItems = orderItemList.filter(orderItem =>
                                orderItem.orderCode === code && selectedSeats.includes(orderItem.seatId));
                            const orderVenueItems = orderItems.map(orderItem => {
                                const originSeatId = orderItem.seatId;
                                return [venueItems.seats.get(originSeatId), venueItems.seats.get(selectedDestSeats?.[originSeatId])];
                            });
                            return { code, venueItems: orderVenueItems, isConsecutive: this.#isConsecutiveSelection(orderVenueItems) };
                        });
                    return result;
                })
            )
    );

    readonly $totalSelectedTickets = toSignal(
        this.#standardVenueTplRelocationSrv.selectedOriginSeatIds.get$().pipe(map(seatIds => seatIds?.length || 0))
    );

    readonly $totalSelectedDestSeats = computed(() => Object.values(this.#$selectedDestinationSeats()).filter(Boolean).length);

    ngOnInit(): void {
        this.#standardVenueTemplateSelectionSrv.unselectAll(); // Unselect the previous origin seats
        this.#filterAvailableSeats();
        this.#loadSelectedSeats();
        combineLatest([ // Handle seat selection
            this.#standardVenueTemplateSelectionSrv.getSelectedVenueItems$(),
            this.#standardVenueTemplateSelectionSrv.getSelectionQueue$()
        ])
            .pipe(
                delay(0),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(([selectedItems, itemsSelection]) => this.#handleSeatSelection(selectedItems, itemsSelection));
    }

    ngOnDestroy(): void {
        // When ending / canceling relocation process the filters are restored
        this.#standardVenueTemplateFilterSrv.filterLabel({ id: 'FREE', labelGroupId: VenueTemplateLabelGroupType.state, filtering: false });
    }

    unselectSeats(seatIds: number[], isNNZ: boolean = false): void {
        if (isNNZ) {
            this.#standardVenueTemplateSelectionSrv.selectNNZs(seatIds, true);
        } else {
            this.#standardVenueTemplateSelectionSrv.selectSeats(seatIds, true);
        }
    }

    #filterAvailableSeats(): void { // On destination step filter by free seats
        this.#standardVenueTemplateFilterSrv.filterLabel({ id: 'FREE', labelGroupId: VenueTemplateLabelGroupType.state, filtering: true });
        this.#standardVenueTemplateSelectionSrv.checkFilteredSelectedItems();
    }

    #loadSelectedSeats(): void {
        this.#standardVenueTplRelocationSrv.selectedDestinationSeats.get$()
            .pipe(
                first(),
                filter(Boolean),
                map(selectedSeats => Object.values(selectedSeats).filter(Boolean))
            )
            .subscribe(selectedSeats => {
                selectedSeats?.length && this.#standardVenueTemplateSelectionSrv.selectSeats(selectedSeats, true);
            });
    }

    #handleSeatSelection(selectedItems: { seats: Set<number>; nnzs: Set<number> }, itemsSelection: VenueTemplateSelectionChange): void {
        if (itemsSelection) {
            const seatSelection = itemsSelection.items.filter(item => item.itemType === VenueTemplateItemType.seat) ?? [];
            if (itemsSelection.select) {
                // If nnzs are selected, remove them
                if (selectedItems.nnzs?.size) {
                    this.#standardVenueTemplateSelectionSrv.selectNNZs(Array.from(selectedItems.nnzs), true);
                }
                // Selected destination seat ids from state
                const selectedDestinationSeatIds = Object.values(this.#$selectedDestinationSeats());
                // New selected seats (excluding the ones from state)
                const newSelectedSeats =
                    seatSelection.filter(seat => !selectedDestinationSeatIds.includes(seat.id)).map(seat => seat.id);

                if (seatSelection.length > MAX_SEATS_PER_SELECTION ||
                    ((this.$totalSelectedDestSeats() + newSelectedSeats.length) > this.#standardVenueTplRelocationSrv.selectionLimit())) {
                    if (seatSelection.length > MAX_SEATS_PER_SELECTION) {
                        this.#showWarnMessage('VENUE_TPLS.TITLES.DIALOG_INDIVIDUAL_DEST_SELECTION',
                            'VENUE_TPLS.FORMS.INFOS.DIALOG_INDIVIDUAL_DEST_SELECTION');
                        this.#standardVenueTemplateSelectionSrv.selectSeats(newSelectedSeats, true);
                    } else {
                        this.#showWarnMessage('VENUE_TPLS.TITLES.DIALOG_DESTINATION_SELECTION',
                            'VENUE_TPLS.FORMS.INFOS.DIALOG_DESTINATION_SELECTION');
                        this.#standardVenueTemplateSelectionSrv.selectSeats(newSelectedSeats, true);
                    }
                } else {
                    // First available origin seat
                    const originSeat =
                        this.$selectedSeats().flatMap(orderItem => orderItem.venueItems).find(seatRow => !seatRow.at(1))?.at(0);

                    const selectedDestinationSeats = {
                        ...this.#$selectedDestinationSeats(),
                        [originSeat.id]: seatSelection.at(0)?.id
                    };
                    this.#standardVenueTplRelocationSrv.selectedDestinationSeats.set(selectedDestinationSeats);
                }
            } else {
                // Unselected seat id
                const selectedItemId = seatSelection.at(0)?.id;
                // Origin seat id corresponding to the unselected seat id
                const originSeatId =
                    Object.entries(this.#$selectedDestinationSeats()).find(seatRow => seatRow[1] === selectedItemId)?.at(0);

                if (originSeatId) {
                    // Update destination seats state
                    const selectedDestinationSeats = {
                        ...this.#$selectedDestinationSeats(),
                        [originSeatId]: undefined
                    };
                    this.#standardVenueTplRelocationSrv.selectedDestinationSeats.set(selectedDestinationSeats);
                }
            }
        }
    }

    #isConsecutiveSelection(orderVenueItems: Seat[][]): boolean { // Check if the selected seats are consecutive in the same order
        const destinationSeats = orderVenueItems.map(orderVenueItem => orderVenueItem.at(1))
            .filter(Boolean);

        return destinationSeats.length ?
            new Set<number>(destinationSeats.map(seat => seat.rowId)).size === 1 && // All seats must be in the same row
            new Set<string>(destinationSeats.map(seat => seat.rowBlock)).size === 1 && // All seats must be in the same block
            destinationSeats // All seats must be consecutive
                .map(seat => seat.order)
                .sort()
                .every((seatOrder, index, arr) => arr.length === index + 1 ? true : seatOrder + 1 === arr.at(index + 1)) : true;
    }

    #showWarnMessage(title: string, message: string): void {
        this.#messageDialogSrv.showWarn({
            title, message,
            showCancelButton: false,
            actionLabel: 'ACTIONS.CONFIRM',
            size: DialogSize.SMALL
        });
    }

}
