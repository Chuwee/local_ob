import { StdVenueTplsApi, VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { inject, Injectable } from '@angular/core';
import { combineLatest, finalize, Observable, switchMap } from 'rxjs';
import { map, take, withLatestFrom } from 'rxjs/operators';
import { EdNotNumberedZone, EdRow, EdSeat, EdSector, EdVenueMap, EdVenueMapMaps } from './models/venue-tpl-editor-venue-map-items.model';
import { mapVenueMap } from './models/venue-tpl-editor-venue-map-mapper';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';

@Injectable()
export class VenueTplEditorVenueMapService {

    private readonly _stdVenueTplsApi = inject(StdVenueTplsApi);
    private readonly _venueTplEdState = inject(VenueTplEditorState);

    constructor() {
    }

    isDirty$(): Observable<boolean> {
        return this._venueTplEdState.venueMap.getValue$()
            .pipe(
                switchMap(() => this._venueTplEdState.venueItems.getValue$()),
                map(items =>
                    this.dirtyCol(items?.sectors) || this.dirtyCol(items?.nnzs)
                    || this.dirtyCol(items?.rows) || this.dirtyCol(items?.seats)
                )
            );
    }

    loadVenueMap(templateId: number): void {
        this._venueTplEdState.venueMap.setInProgress(true);
        this._stdVenueTplsApi.getVenueTplVenueMap(templateId)
            .pipe(finalize(() => this._venueTplEdState.venueMap.setInProgress(false)))
            .subscribe(venueMap => {
                const edVenueMap = mapVenueMap(venueMap);
                const venueItems: EdVenueMapMaps = {
                    sectors: new Map<number, EdSector>(),
                    rows: new Map<number, EdRow>(),
                    seats: new Map<number, EdSeat>(),
                    nnzs: new Map<number, EdNotNumberedZone>()
                };
                edVenueMap.sectors.forEach(sector => {
                    venueItems.sectors.set(sector.id, sector);
                    sector.rows.forEach(row => {
                        venueItems.rows.set(row.id, row);
                        row.seats.forEach(seat => {
                            venueItems.seats.set(seat.id, seat);
                        });
                    });
                    sector.notNumberedZones.forEach(nnz => venueItems.nnzs.set(nnz.id, nnz));
                });
                this._venueTplEdState.venueItems.setValue(venueItems);
                this._venueTplEdState.venueMap.setValue(edVenueMap);
            });
    }

    isVenueMapLoading$(): Observable<boolean> {
        return this._venueTplEdState.venueMap.isInProgress$();
    }

    getVenueMap$(): Observable<EdVenueMap> {
        return this._venueTplEdState.venueMap.getValue$();
    }

    getVenueItems$(): Observable<EdVenueMapMaps> {
        return this._venueTplEdState.venueItems.getValue$();
    }

    addSector(inputData: { id: number; name: string; code: string }): Observable<EdSector> {
        return this.getVenueItemsObs$()
            .pipe(
                map(([venueMap, venueItems]) => {
                    const sector: EdSector = {
                        itemType: VenueTemplateItemType.sector,
                        id: inputData.id,
                        ...inputData,
                        rows: [],
                        notNumberedZones: [],
                        create: true
                    };
                    venueItems.sectors.set(sector.id, sector);
                    venueMap.sectors.push(sector);
                    this._venueTplEdState.venueMap.setValue(venueMap);
                    return sector;
                })
            );
    }

    editSector(sectorData: { id: number; name: string; code: string; modify?: boolean }): void {
        this.getVenueItemsObs$()
            .subscribe(([venueMap, venueItems]) => {
                const sector = venueItems.sectors.get(sectorData.id);
                sector.name = sectorData.name;
                sector.code = sectorData.code;
                if (sectorData.modify !== undefined) {
                    sector.modify = sectorData.modify;
                } else if (!sector.create) {
                    sector.modify = true;
                }
                this._venueTplEdState.venueMap.setValue(venueMap);
            });
    }

    deleteSector(sector: EdSector, undo = false): void {
        this.getVenueItemsObs$()
            .subscribe(([venueMap, items]) => {
                sector.delete = !undo;
                this._venueTplEdState.venueMap.setValue(venueMap);
                this._venueTplEdState.venueItems.setValue(items);
            });
    }

    addRows(rows: EdRow[], undo = false): void {
        this.getVenueItemsObs$()
            .subscribe(([venueMap, items]) => {
                const sector = venueMap.sectors.find(sector => sector.id === rows[0].sector);
                if (!undo) {
                    sector.rows.push(...rows);
                    rows.forEach(row => {
                        items.rows.set(row.id, row);
                        row.seats.forEach(seat => items.seats.set(seat.id, seat));
                    });
                } else {
                    sector.rows = sector.rows.filter(row => !rows.includes(row));
                    rows.forEach(row => {
                        items.rows.delete(row.id);
                        row.seats.forEach(seat => items.seats.delete(seat.id));
                    });
                }
                this._venueTplEdState.venueItems.setValue(items);
                this._venueTplEdState.venueMap.setValue(venueMap);
            });
    }

    addSeats(seats: EdSeat[], undo = false): void {
        this.getVenueItemsObs$()
            .subscribe(([venueMap, items]) => {
                const rows = venueMap.sectors.flatMap(sector => sector.rows);
                if (!undo) {
                    seats.forEach(seat => {
                        seat.delete = false;
                        seat.create = true;
                        const rowSeat = rows.find(row => row.id === seat.rowId);
                        if (!rowSeat.seats.includes(seat)) {
                            rowSeat.seats.push(seat);
                        }
                        items.seats.set(seat.id, seat);
                    });
                } else {
                    seats.forEach(seat => {
                        items.seats.delete(seat.id);
                        seat.delete = true;
                    });
                }
                this._venueTplEdState.venueItems.setValue(items);
                this._venueTplEdState.venueMap.setValue(venueMap);
            });
    }

    deleteSeats(seats: EdSeat[], undo = false): void {
        this._venueTplEdState.venueMap.getValue$()
            .pipe(
                take(1),
                withLatestFrom(this._venueTplEdState.venueItems.getValue$())
            )
            .subscribe(([venueMap, items]) => {
                seats.forEach(seat => seat.delete = !undo);
                const seatsRows = new Set(seats.map(seat => seat.rowId));
                venueMap.sectors.forEach(sector =>
                    sector.rows?.forEach(row => {
                        if (seatsRows.has(row.id)) {
                            row.delete = row.seats?.every(seat => seat.delete);
                        }
                    })
                );
                this._venueTplEdState.venueMap.setValue(venueMap);
                this._venueTplEdState.venueItems.setValue(items);
            });

    }

    deleteRow(row: EdRow, undo = false): void {
        this._venueTplEdState.venueMap.getValue$()
            .pipe(
                take(1),
                withLatestFrom(this._venueTplEdState.venueItems.getValue$())
            )
            .subscribe(([venueMap, items]) => {
                row.delete = !undo;
                this._venueTplEdState.venueMap.setValue(venueMap);
                this._venueTplEdState.venueItems.setValue(items);
            });
    }

    editSeats(seats: Partial<EdSeat>[]): void {
        combineLatest([
            this._venueTplEdState.venueMap.getValue$(),
            this._venueTplEdState.venueItems.getValue$()
        ])
            .pipe(take(1))
            .subscribe(([venueMap, items]) => {
                seats.forEach(seat => {
                    const modelSeat = items.seats.get(seat.id);
                    modelSeat.name = seat.name ?? modelSeat.name;
                    modelSeat.weight = seat.weight ?? modelSeat.weight;
                    modelSeat.rowBlock = seat.rowBlock ?? modelSeat.rowBlock;
                    modelSeat.modify = seat.modify;
                });
                this._venueTplEdState.venueMap.setValue(venueMap);
                this._venueTplEdState.venueItems.setValue(items);
            });
    }

    editRow(row: { id: number; name: string }, modify = true): void {
        combineLatest([
            this._venueTplEdState.venueMap.getValue$(),
            this._venueTplEdState.venueItems.getValue$()
        ])
            .pipe(take(1))
            .subscribe(([venueMap, items]) => {
                const modelRow = items.rows.get(row.id);
                modelRow.name = row.name;
                modelRow.modify = modify;
                this._venueTplEdState.venueMap.setValue(venueMap);
                this._venueTplEdState.venueItems.setValue(items);
            });
    }

    addZone(nnzData: { id: number; name: string; sector: number; capacity: number; view: number }, undo = false): void {
        combineLatest([
            this._venueTplEdState.venueMap.getValue$(),
            this._venueTplEdState.venueItems.getValue$()
        ])
            .pipe(take(1))
            .subscribe(([venueMap, venueItems]) => {
                const targetSector = venueMap.sectors.find(sector => sector.id === nnzData.sector);
                if (!undo) {
                    const newNNZ: EdNotNumberedZone = {
                        itemType: VenueTemplateItemType.notNumberedZone,
                        ...nnzData,
                        create: true
                    };
                    targetSector.notNumberedZones.push(newNNZ);
                    venueItems.nnzs.set(newNNZ.id, newNNZ);
                } else {
                    targetSector.notNumberedZones = targetSector.notNumberedZones.filter(nnz => nnz.id !== nnzData.id);
                    venueItems.nnzs.delete(nnzData.id);
                }
                this._venueTplEdState.venueItems.setValue(venueItems);
                this._venueTplEdState.venueMap.setValue(venueMap);
            });
    }

    editNotNumberedZone(nnzData: Partial<EdNotNumberedZone>, modify: boolean): void {
        this._venueTplEdState.venueMap.getValue$()
            .pipe(take(1))
            .subscribe(venueMap => {
                const modelNNZ = venueMap.sectors.flatMap(sector => sector.notNumberedZones).find(zone => zone.id === nnzData.id);
                if (nnzData.sector && nnzData.sector !== modelNNZ.sector) {
                    const oldSector = venueMap.sectors.find(sector => sector.id === modelNNZ.sector);
                    oldSector.notNumberedZones = oldSector.notNumberedZones.filter(zone => zone.id !== modelNNZ.id);
                    modelNNZ.sector = nnzData.sector;
                    const newSector = venueMap.sectors.find(sector => sector.id === modelNNZ.sector);
                    newSector.notNumberedZones.push(modelNNZ);
                }
                modelNNZ.name = nnzData.name;
                modelNNZ.capacity = nnzData.capacity;
                modelNNZ.modify = modify;
                this._venueTplEdState.venueMap.setValue(venueMap);
            });
    }

    deleteNNZs(nnzs: EdNotNumberedZone[], undo = false): void {
        this._venueTplEdState.venueMap.getValue$()
            .pipe(take(1))
            .subscribe(venueMap => {
                nnzs.forEach(nnz => nnz.delete = !undo);
                this._venueTplEdState.venueMap.setValue(venueMap);
            });
    }

    checkNNZNames(): Observable<EdNotNumberedZone> {
        return this._venueTplEdState.venueMap.getValue$()
            .pipe(
                take(1),
                map(venueMap => {
                    const zones = venueMap.sectors.flatMap(sector => sector.notNumberedZones);
                    const invalidZones = [
                        zones.find(nnz => !nnz.name?.length), // required name
                        zones.find(nnz => {
                            venueMap.sectors
                                .find(sector => sector.notNumberedZones.includes(nnz))
                                .notNumberedZones
                                .find(n => n !== nnz && n.sector && n.name === n.name);
                        }) // duplicate name
                    ]
                        .filter(Boolean);
                    return invalidZones.length && invalidZones[0];
                })
            );
    }

    checkSeatNames(): Observable<EdSeat> {
        return this._venueTplEdState.venueItems.getValue$()
            .pipe(
                take(1),
                map(items => [
                    Array.from(items.seats.values())
                        .find(seat => !seat.name || seat.name === '' || seat.name.length > VenueTemplateFieldsRestrictions.seatNameLength)
                    || null,
                    this.getExistentSeat(Array.from(items.rows.values()))
                ].find(Boolean))
            );
    }

    private dirtyCol(items: Map<number, EdSector | EdRow | EdSeat | EdNotNumberedZone>): boolean {
        return items && Array.from(items?.values()).some(item => item.create || item.modify || item.delete);
    }

    private getVenueItemsObs$(): Observable<[EdVenueMap, EdVenueMapMaps]> {
        return combineLatest([this._venueTplEdState.venueMap.getValue$(), this._venueTplEdState.venueItems.getValue$()]).pipe(take(1));
    }

    private getExistentSeat(rows: EdRow[]): EdSeat {
        let invalidSeat: EdSeat = null;
        rows.every(row => {
            const existentSeats = [];
            const result = row.seats.every(seat => {
                if (existentSeats.includes(seat.name)) {
                    invalidSeat = seat;
                    return false;
                } else {
                    existentSeats.push(seat.name);
                    return true;
                }
            });
            return result;
        });
        return invalidSeat;
    }
}
