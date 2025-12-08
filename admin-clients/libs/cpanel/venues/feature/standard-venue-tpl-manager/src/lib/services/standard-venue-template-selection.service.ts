import { NotNumberedZone, Row, Seat, Sector, VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { Injectable } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { map, take, withLatestFrom } from 'rxjs/operators';
import { VenueTemplateSelectionChange } from '../models/venue-template-selection-change.model';
import { StandardVenueTemplateState } from '../state/standard-venue-template.state';

@Injectable()
export class StandardVenueTemplateSelectionService {

    constructor(private _standardVenueTemplateState: StandardVenueTemplateState) {
    }

    getSelectedVenueItems$(): Observable<{ seats: Set<number>; nnzs: Set<number> }> {
        return this._standardVenueTemplateState.getSelectedVenueItems$();
    }

    getSelectionQueue$(): Observable<VenueTemplateSelectionChange> {
        return this._standardVenueTemplateState.getSelectionQueue$();
    }

    unselectAll(): void {
        this.setSelectionToQueue(false);
    }

    selectSeats(seatIds: number[], invertSelection = false): void {
        this._standardVenueTemplateState.getFilteredVenueItems$()
            .pipe(
                take(1),
                map(filteredItems => filteredItems?.seats?.size
                    && seatIds?.filter(seatId => !filteredItems.seats.has(seatId))
                    || seatIds),
                withLatestFrom(this._standardVenueTemplateState.getSelectedVenueItems$()),
                map(([filteredSeatIds, selectedItems]) => {
                    if (filteredSeatIds?.length) {
                        if (invertSelection) {
                            return {
                                seatIdsToSelect: filteredSeatIds.filter(seatId => !selectedItems?.seats.has(seatId)),
                                seatIdsToUnselect: filteredSeatIds.filter(seatId => selectedItems?.seats.has(seatId))
                            };
                        } else {
                            return {
                                seatIdsToSelect: filteredSeatIds
                            };
                        }
                    } else {
                        return {};
                    }
                }),
                withLatestFrom(this._standardVenueTemplateState.getVenueItems$()),
                map(([selectionData, venueItems]) => ({
                    seatsToSelect: selectionData.seatIdsToSelect?.map(item => venueItems.seats.get(item)),
                    seatsToUnselect: selectionData.seatIdsToUnselect?.map(item => venueItems.seats.get(item))
                }))
            ).subscribe(selectionData => {
                if (selectionData.seatsToSelect?.length) {
                    this.setSelectionToQueue(true, selectionData.seatsToSelect);
                }
                if (selectionData.seatsToUnselect?.length) {
                    this.setSelectionToQueue(false, selectionData.seatsToUnselect);
                }
            });
    }

    selectNNZs(nnzIds: number[], invertSelection = false): void {
        this._standardVenueTemplateState.getFilteredVenueItems$()
            .pipe(
                take(1),
                map(filteredVenueItems => filteredVenueItems?.nnzs?.size
                    && nnzIds?.filter(nnzId => !filteredVenueItems.nnzs.has(nnzId))
                    || nnzIds),
                withLatestFrom(this._standardVenueTemplateState.getSelectedVenueItems$()),
                map(([filteredNNZIds, selectedVenueItems]) => {
                    if (invertSelection) {
                        return {
                            nnzIdsToSelect: filteredNNZIds?.filter(nnzId => !selectedVenueItems?.nnzs?.has(nnzId)),
                            nnzIdsToUnselect: filteredNNZIds?.filter(nnzId => selectedVenueItems?.nnzs?.has(nnzId))
                        };
                    } else {
                        return {
                            nnzIdsToSelect: filteredNNZIds
                        };
                    }
                }),
                withLatestFrom(this._standardVenueTemplateState.getVenueItems$()),
                map(([selectionData, venueItems]) => ({
                    nnzsToSelect: selectionData?.nnzIdsToSelect?.map(nnzId => venueItems.nnzs.get(nnzId)),
                    nnzsToUnselect: selectionData?.nnzIdsToUnselect?.map(nnzId => venueItems.nnzs.get(nnzId))
                }))
            ).subscribe(selectionData => {
                if (selectionData.nnzsToSelect?.length) {
                    this.setSelectionToQueue(true, selectionData.nnzsToSelect);
                }
                if (selectionData.nnzsToUnselect?.length) {
                    this.setSelectionToQueue(false, selectionData.nnzsToUnselect);
                }
            });
    }

    selectRow(row: Row): void {
        combineLatest([
            this._standardVenueTemplateState.getFilteredVenueItems$(),
            this._standardVenueTemplateState.getSelectedVenueItems$()
        ])
            .pipe(take(1))
            .subscribe(([filteredItems, selectedItems]) => {
                const filteredRowSeats = filteredItems.seats?.size ?
                    row.seats.filter(seat => !filteredItems.seats.has(seat.id)) : row.seats;
                const seatsToSelect = filteredRowSeats.filter(seat => !selectedItems.seats.has(seat.id));
                if (seatsToSelect?.length) {
                    this.selectSeats(seatsToSelect.map(s => s.id), false);
                } else if (filteredRowSeats?.length) {
                    this.selectSeats(filteredRowSeats.map(s => s.id), true);
                }
            });
    }

    selectSectors(sectors: Sector[]): void {
        this._standardVenueTemplateState.getFilteredVenueItems$()
            .pipe(
                map(filteredVenueItems => ({
                    seatIds: sectors.map(sector => sector.rows)
                        .reduce((prev, current) => prev.concat(current))
                        .map(row => row.seats)
                        .reduce((previousSeats, currentSeats) => previousSeats.concat(currentSeats), [])
                        .filter(seat => !filteredVenueItems?.seats?.size || !filteredVenueItems.seats.has(seat.id))
                        .map(seat => seat.id),
                    nnzs: sectors.map(sector => sector.notNumberedZones)
                        .reduce((prev, current) => prev.concat(current))
                        .filter(nnz => !filteredVenueItems?.nnzs?.size || !filteredVenueItems.nnzs.has(nnz.id))
                        .map(zone => zone.id)
                })),
                withLatestFrom(this._standardVenueTemplateState.getSelectedVenueItems$()),
                take(1)
            )
            .subscribe(([itemIds, selectedVenueItems]) => {
                const unselectedSeats = itemIds.seatIds.filter(seatId => !selectedVenueItems.seats.has(seatId));
                const unselectedNnzs = itemIds.nnzs.filter(zoneId => !selectedVenueItems.nnzs.has(zoneId));
                // if there's no unselected items (all is selected) and wants to invert selection, unselects all
                if (unselectedSeats.length === 0 && unselectedNnzs.length === 0) {
                    if (itemIds.seatIds.length > 0) {
                        this.selectSeats(itemIds.seatIds, true);
                    }
                    if (itemIds.nnzs.length > 0) {
                        this.selectNNZs(itemIds.nnzs, true);
                    }
                } else { // otherwise, selects all sector
                    if (unselectedSeats.length > 0) {
                        this.selectSeats(unselectedSeats, false);
                    }
                    if (unselectedNnzs.length > 0) {
                        this.selectNNZs(unselectedNnzs, false);
                    }
                }
            });
    }

    checkFilteredSelectedItems(): void {
        combineLatest([
            this._standardVenueTemplateState.getVenueItems$(),
            this._standardVenueTemplateState.getSelectedVenueItems$(),
            this._standardVenueTemplateState.getFilteredVenueItems$()
        ])
            .subscribe(([venueItems, selectedItems, filteredItems]) => {
                this.unselectFilteredItems(venueItems.seats, filteredItems.seats, selectedItems.seats);
                this.unselectFilteredItems(venueItems.nnzs, filteredItems.nnzs, selectedItems.nnzs);
            });
    }

    private unselectFilteredItems(
        itemMap: Map<number, Seat | NotNumberedZone>, filteredItems: Set<number>, selectedItems: Set<number>
    ): void {
        if (filteredItems?.size) {
            const seatsToUnselect = Array.from(filteredItems.keys())
                .filter(filteredSeatId => selectedItems.has(filteredSeatId))
                .map(filteredSeatId => itemMap.get(filteredSeatId));
            if (seatsToUnselect.length) {
                this.setSelectionToQueue(false, seatsToUnselect);
            }
        }
    }

    private setSelectionToQueue(select = false, items: (Seat | NotNumberedZone)[] = null): void {
        combineLatest([
            this._standardVenueTemplateState.getVenueItems$(),
            this._standardVenueTemplateState.getSelectedVenueItems$()
        ])
            .pipe(take(1))
            .subscribe(([venueItems, selectedVenueItems]) => {
                if (!items?.length) {
                    selectedVenueItems?.seats.clear();
                    selectedVenueItems?.nnzs.clear();
                    if (select && venueItems.seats) {
                        if (venueItems.seats) {
                            Array.from(venueItems.seats.keys()).forEach(seatId => selectedVenueItems.seats.add(seatId));
                        }
                        if (venueItems.nnzs) {
                            Array.from(venueItems.nnzs.keys()).forEach(nnzId => selectedVenueItems.nnzs.add(nnzId));
                        }
                    }
                } else {
                    const typeActions = new Map<VenueTemplateItemType, (id: number) => void>();
                    if (select) {
                        typeActions.set(VenueTemplateItemType.seat, id => selectedVenueItems.seats.add(id));
                        typeActions.set(VenueTemplateItemType.notNumberedZone, id => selectedVenueItems.nnzs.add(id));
                    } else {
                        typeActions.set(VenueTemplateItemType.seat, id => selectedVenueItems.seats.delete(id));
                        typeActions.set(VenueTemplateItemType.notNumberedZone, id => selectedVenueItems.nnzs.delete(id));
                    }
                    items.forEach(item => typeActions.get(item.itemType)(item.id));
                }
                this._standardVenueTemplateState.enqueueSelection({ select, items });
            });
    }
}
