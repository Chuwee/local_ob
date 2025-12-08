import { TicketAllocationType } from '@admin-clients/shared/common/data-access';
import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { MappedInfo } from './models/mapped-info.model';
import { VmSeasonTicketRenewalAvailableNnz } from './models/vm-season-ticket-renewal-available-nnz.model';
import { VmSeasonTicketRenewalAvailableRow } from './models/vm-season-ticket-renewal-available-row.model';
import { VmSeasonTicketRenewalAvailableSeat } from './models/vm-season-ticket-renewal-available-seat.model';
import { VmSeasonTicketRenewalAvailableSector } from './models/vm-season-ticket-renewal-available-sector.model';
import { VmSeasonTicketRenewalEdit } from './models/vm-season-ticket-renewal-edit.model';
import { SeasonTicketRenewalsListEditDialogState } from './state/season-ticket-renewals-list-edit-dialog.state';

@Injectable()
export class SeasonTicketRenewalsListEditDialogLocationService {
    private readonly _dialogState = inject(SeasonTicketRenewalsListEditDialogState);
    private _availableSectors: VmSeasonTicketRenewalAvailableSector[] = [];
    private _recordOfAvailableRows: Record<string, VmSeasonTicketRenewalAvailableRow[]> = {};
    private _recordOfAvailableNnzs: Record<string, VmSeasonTicketRenewalAvailableNnz[]> = {};
    private _recordOfAvailableRowSeats: Record<string, VmSeasonTicketRenewalAvailableSeat[]> = {};
    private _recordOfAvailableNnzSeats: Record<string, VmSeasonTicketRenewalAvailableSeat[]> = {};
    private _numberOfMappedInfoBeingAssigned = 0;
    private _numberOfMappedInfoBeingAssignedBS = new BehaviorSubject(this._numberOfMappedInfoBeingAssigned);

    getAvailableSectors$(): Observable<VmSeasonTicketRenewalAvailableSector[]> {
        return this._dialogState.getAvailableSectors$();
    }

    getRecordOfAvailableRows$(): Observable<Record<string, VmSeasonTicketRenewalAvailableRow[]>> {
        return this._dialogState.getRecordOfAvailableRows$();
    }

    getRecordOfAvailableNnzs$(): Observable<Record<string, VmSeasonTicketRenewalAvailableNnz[]>> {
        return this._dialogState.getRecordOfAvailableNnzs$();
    }

    getRecordOfAvailableRowSeats$(): Observable<Record<string, VmSeasonTicketRenewalAvailableSeat[]>> {
        return this._dialogState.getRecordOfAvailableRowSeats$();
    }

    getRecordOfAvailableNnzSeats$(): Observable<Record<string, VmSeasonTicketRenewalAvailableSeat[]>> {
        return this._dialogState.getRecordOfAvailableNnzSeats$();
    }

    getNumberOfMappedInfoBeingAssigned$(): Observable<number> {
        return this._numberOfMappedInfoBeingAssignedBS.asObservable();
    }

    initNumberOfMappedInfoBeingAssigned(value: number): void {
        this._numberOfMappedInfoBeingAssigned = value;
        this._numberOfMappedInfoBeingAssignedBS.next(this._numberOfMappedInfoBeingAssigned);
    }

    decrementNumberOfMappedInfoBeingAssigned(): void {
        this._numberOfMappedInfoBeingAssigned--;
        this._numberOfMappedInfoBeingAssignedBS.next(this._numberOfMappedInfoBeingAssigned);
    }

    getAvailableSectorsWithMappedInfo(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        availableSectors: VmSeasonTicketRenewalAvailableSector[]
    ): VmSeasonTicketRenewalAvailableSector[] {
        const updatedAvailableSectors: VmSeasonTicketRenewalAvailableSector[] = availableSectors.slice();
        renewalEdits.forEach(renewalEdit => {
            if (!this.isRenewalEditMapped(renewalEdit)) {
                return;
            }

            const mappedSectorIndex = this.getMappedSectorIndex(
                renewalEdit,
                updatedAvailableSectors
            );

            if (renewalEdit.actual_seat.seat_type === TicketAllocationType.numbered) {
                this.updateAvailableSectorsForRowsWithMappedInfo(mappedSectorIndex, renewalEdit, updatedAvailableSectors);
            } else if (renewalEdit.actual_seat.seat_type === TicketAllocationType.notNumbered) {
                this.updateAvailableSectorsForNnzsWithMappedInfo(mappedSectorIndex, renewalEdit, updatedAvailableSectors);
            }
        });

        return updatedAvailableSectors;
    }

    getMappedInfos(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        availableSectors: VmSeasonTicketRenewalAvailableSector[]
    ): MappedInfo[] {
        const mappedInfos: MappedInfo[] = [];
        renewalEdits.forEach((renewalEdit, renewalIndex) => {
            if (!this.isRenewalEditMapped(renewalEdit)) {
                return;
            }

            const mappedSectorIndex = this.getMappedSectorIndex(
                renewalEdit,
                availableSectors
            );

            if (renewalEdit.actual_seat.seat_type === TicketAllocationType.numbered) {
                const mappedRowIndex = this.getMappedRowIndex(
                    renewalEdit,
                    availableSectors[mappedSectorIndex]
                );

                const mappedInfo = this.getMappedInfoForRows(
                    renewalEdit,
                    renewalIndex,
                    availableSectors[mappedSectorIndex],
                    availableSectors[mappedSectorIndex].rows[mappedRowIndex]
                );

                mappedInfos.push(mappedInfo);
            } else if (renewalEdit.actual_seat.seat_type === TicketAllocationType.notNumbered) {
                const mappedNnzIndex = this.getMappedNnzIndex(
                    renewalEdit,
                    availableSectors[mappedSectorIndex]
                );

                const mappedInfo = this.getMappedInfoForNnzs(
                    renewalEdit,
                    renewalIndex,
                    availableSectors[mappedSectorIndex],
                    availableSectors[mappedSectorIndex].not_numbered_zones[mappedNnzIndex]
                );

                mappedInfos.push(mappedInfo);
            }
        });

        return mappedInfos;
    }

    getMappedInfoRecordForRows(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        mappedInfos: MappedInfo[]
    ): Record<string, MappedInfo[]> {
        return mappedInfos
            .filter(mappedInfo => !!mappedInfo.row)
            .reduce((accMappedInfoRecord, mappedInfo) => {
                const firstKey = renewalEdits[mappedInfo.renewalIndex].assignedSectorId;
                const secondKey = renewalEdits[mappedInfo.renewalIndex].assignedRowId;
                const recordKey = `${firstKey}-${secondKey}`;

                if (!accMappedInfoRecord[recordKey]) {
                    accMappedInfoRecord[recordKey] = [];
                }
                accMappedInfoRecord[recordKey].push(mappedInfo);

                return accMappedInfoRecord;
            }, {});
    }

    getMappedInfoRecordForNnz(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        mappedInfos: MappedInfo[]
    ): Record<string, MappedInfo[]> {
        return mappedInfos
            .filter(mappedInfo => !!mappedInfo.nnz)
            .reduce((accMappedInfoRecord, mappedInfo) => {
                const firstKey = renewalEdits[mappedInfo.renewalIndex].assignedSectorId;
                const secondKey = renewalEdits[mappedInfo.renewalIndex].assignedNnzId;
                const recordKey = `${firstKey}-${secondKey}`;

                if (!accMappedInfoRecord[recordKey]) {
                    accMappedInfoRecord[recordKey] = [];
                }
                accMappedInfoRecord[recordKey].push(mappedInfo);

                return accMappedInfoRecord;
            }, {});
    }

    updateRowSeatsWhenResetSeat(renewalEdit: VmSeasonTicketRenewalEdit, previousSeatId: number): void {
        this._recordOfAvailableRowSeats = {
            ...this._recordOfAvailableRowSeats,
            [renewalEdit.rowSeatsRecordKey]: this._recordOfAvailableRowSeats[renewalEdit.rowSeatsRecordKey].map(seat => {
                if (seat.seat_id === previousSeatId) {
                    return { ...seat, assignedTo: null };
                } else {
                    return seat;
                }
            })
        };
        this._dialogState.setRecordOfAvailableRowSeats(this._recordOfAvailableRowSeats);
    }

    updateNnzSeatsWhenResetSeat(renewalEdit: VmSeasonTicketRenewalEdit, previousSeatId: number): void {
        this._recordOfAvailableNnzSeats = {
            ...this._recordOfAvailableNnzSeats,
            [renewalEdit.nnzSeatsRecordKey]: this._recordOfAvailableNnzSeats[renewalEdit.nnzSeatsRecordKey].map(seat => {
                if (seat.seat_id === previousSeatId) {
                    return { ...seat, assignedTo: null };
                } else {
                    return seat;
                }
            })
        };
        this._dialogState.setRecordOfAvailableRowSeats(this._recordOfAvailableRowSeats);
    }

    updateRowSeatWhenAssignRowSeat(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._recordOfAvailableRowSeats = {
            ...this._recordOfAvailableRowSeats,
            [renewalEdit.rowSeatsRecordKey]: this._recordOfAvailableRowSeats[renewalEdit.rowSeatsRecordKey]?.map(seat => {
                if (seat.seat_id === renewalEdit.assignedSeatId) {
                    return { ...seat, assignedTo: renewalEdit.id };
                } else {
                    return seat;
                }
            })
        };
        this._dialogState.setRecordOfAvailableRowSeats(this._recordOfAvailableRowSeats);
    }

    updateRowSeatsWhenChangeRowSeat(renewalEdit: VmSeasonTicketRenewalEdit, previousSeatId: number): void {
        this._recordOfAvailableRowSeats = {
            ...this._recordOfAvailableRowSeats,
            [renewalEdit.rowSeatsRecordKey]: this._recordOfAvailableRowSeats[renewalEdit.rowSeatsRecordKey]?.map(seat => {
                if (seat.seat_id === renewalEdit.assignedSeatId) {
                    return { ...seat, assignedTo: renewalEdit.id };
                } else if (seat.seat_id === previousSeatId) {
                    return { ...seat, assignedTo: null };
                } else {
                    return seat;
                }
            })
        };
        this._dialogState.setRecordOfAvailableRowSeats(this._recordOfAvailableRowSeats);
    }

    setRecordOfAvailableRowSeats(
        availableRowSeats: VmSeasonTicketRenewalAvailableSeat[],
        recordKey: string
    ): void {
        if (!this._recordOfAvailableRowSeats[recordKey]) {
            this._recordOfAvailableRowSeats = {
                ...this._recordOfAvailableRowSeats,
                [recordKey]: availableRowSeats
            };
        }
        this._dialogState.setRecordOfAvailableRowSeats(this._recordOfAvailableRowSeats);
    }

    updateRowSeatsWhenResetRow(): void {
        this._dialogState.setRecordOfAvailableRowSeats(this._recordOfAvailableRowSeats);
    }

    updateNnzSeatsWhenResetNnz(): void {
        this._dialogState.setRecordOfAvailableNnzSeats(this._recordOfAvailableNnzSeats);
    }

    isRowSeatShown(
        renewalEdit: VmSeasonTicketRenewalEdit,
        seat: VmSeasonTicketRenewalAvailableSeat
    ): boolean {
        return Number.isInteger(renewalEdit.assignedRowId) &&
            (
                seat.assignedTo === renewalEdit.id ||
                (
                    (seat.assignedTo === null || seat.assignedTo === undefined) &&
                    (!seat.alreadyMappedTo || seat.alreadyMappedTo === renewalEdit.id)
                )
            );
    }

    initRowSeatsRecordKey(renewal: VmSeasonTicketRenewalEdit): string {
        if (this.isRenewalEditMapped(renewal)) {
            return `${renewal.actual_seat.sector_id}-${renewal.actual_seat.row_id}`;
        } else {
            return this._dialogState.initRowSeatsRecordKey;
        }
    }

    updateRowsWhenResetSeat(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._recordOfAvailableRows = {
            ...this._recordOfAvailableRows,
            [renewalEdit.rowsRecordKey]: this._recordOfAvailableRows[renewalEdit.rowsRecordKey].map(row => {
                if (row.row_id === renewalEdit.assignedRowId && row.assignedTo?.has(renewalEdit.id)) {
                    row.assignedTo.delete(renewalEdit.id);
                    return { ...row, availableSeatsCalc: row.availableSeatsCalc + 1 };
                } else {
                    return row;
                }
            })
        };
        this._dialogState.setRecordOfAvailableRows(this._recordOfAvailableRows);
    }

    updateNnzWhenResetSeat(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._recordOfAvailableNnzs = {
            ...this._recordOfAvailableNnzs,
            [renewalEdit.nnzsRecordKey]: this._recordOfAvailableNnzs[renewalEdit.nnzsRecordKey].map(nnz => {
                if (nnz.not_numbered_zone_id === renewalEdit.assignedNnzId && nnz.assignedTo?.has(renewalEdit.id)) {
                    nnz.assignedTo.delete(renewalEdit.id);
                    return { ...nnz, availableSeatsCalc: nnz.availableSeatsCalc + 1 };
                } else {
                    return nnz;
                }
            })
        };
        this._dialogState.setRecordOfAvailableNnzs(this._recordOfAvailableNnzs);
    }

    updateRowsWhenAssignRowSeat(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._recordOfAvailableRows = {
            ...this._recordOfAvailableRows,
            [renewalEdit.rowsRecordKey]: this._recordOfAvailableRows[renewalEdit.rowsRecordKey]?.map(row => {
                if (row.row_id === renewalEdit.assignedRowId) {
                    row.assignedTo = (row.assignedTo ?? new Set()).add(renewalEdit.id);
                    return { ...row, availableSeatsCalc: (row.availableSeatsCalc ?? row.available_seats) - 1 };
                } else {
                    return row;
                }
            })
        };
        this._dialogState.setRecordOfAvailableRows(this._recordOfAvailableRows);

    }

    loadRowsFromASector(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._availableSectors.find(sector => {
            if (sector.sector_id === renewalEdit.assignedSectorId) {
                if (!this._recordOfAvailableRows[renewalEdit.rowsRecordKey]) {
                    this._recordOfAvailableRows = {
                        ...this._recordOfAvailableRows,
                        [renewalEdit.rowsRecordKey]: sector.rows
                    };
                }
                this._dialogState.setRecordOfAvailableRows(this._recordOfAvailableRows);
                return true;
            }
            return undefined;
        });
    }

    updateRowsWhenResetSector(): void {
        this._dialogState.setRecordOfAvailableRows(this._recordOfAvailableRows);
    }

    updateNnzsWhenResetSector(): void {
        this._dialogState.setRecordOfAvailableNnzs(this._recordOfAvailableNnzs);
    }

    isRowShown(
        renewalEdit: VmSeasonTicketRenewalEdit,
        row: VmSeasonTicketRenewalAvailableRow
    ): boolean {
        return Number.isInteger(renewalEdit.assignedSectorId) &&
            (
                row.availableSeatsCalc === undefined ||
                this.isRowShownByCalculatedAvailableSeatsAndMappedInfo(renewalEdit, row) ||
                row.alreadyMappedTo?.has(renewalEdit.id) ||
                row.assignedTo?.has(renewalEdit.id)
            );
    }

    initRowsRecordKey(renewal: VmSeasonTicketRenewalEdit): string {
        if (this.isRenewalEditMapped(renewal)) {
            return `${renewal.actual_seat.seat_id}`;
        } else {
            return this._dialogState.initRowsRecordKey;
        }
    }

    updateNnzSeatsWhenAssignNnzSeat(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._recordOfAvailableNnzSeats = {
            ...this._recordOfAvailableNnzSeats,
            [renewalEdit.nnzSeatsRecordKey]: this._recordOfAvailableNnzSeats[renewalEdit.nnzSeatsRecordKey]?.map(seat => {
                if (seat.seat_id === renewalEdit.assignedSeatId) {
                    return { ...seat, assignedTo: renewalEdit.id };
                } else {
                    return seat;
                }
            })
        };
        this._dialogState.setRecordOfAvailableNnzSeats(this._recordOfAvailableNnzSeats);
    }

    setRecordOfAvailableNnzSeats(
        availableNnzSeats: VmSeasonTicketRenewalAvailableSeat[],
        recordKey: string
    ): void {
        if (!this._recordOfAvailableNnzSeats[recordKey]) {
            this._recordOfAvailableNnzSeats = {
                ...this._recordOfAvailableNnzSeats,
                [recordKey]: availableNnzSeats
            };
        }
        this._dialogState.setRecordOfAvailableNnzSeats(this._recordOfAvailableNnzSeats);
    }

    getNnzSeatToAssign(renewalEdit: VmSeasonTicketRenewalEdit): VmSeasonTicketRenewalAvailableSeat {
        let seat: VmSeasonTicketRenewalAvailableSeat;
        if (
            renewalEdit.actual_seat &&
            renewalEdit.assignedNnzId === renewalEdit.actual_seat.not_numbered_zone_id
        ) {
            seat = this._recordOfAvailableNnzSeats[renewalEdit.nnzSeatsRecordKey].find(seat => seat.alreadyMappedTo === renewalEdit.id);
        } else {
            seat = this._recordOfAvailableNnzSeats[renewalEdit.nnzSeatsRecordKey].find(seat =>
                !seat.alreadyMappedTo && !seat.assignedTo);
        }
        return seat;
    }

    updateNnzWhenAssignNnzSeat(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._recordOfAvailableNnzs = {
            ...this._recordOfAvailableNnzs,
            [renewalEdit.nnzsRecordKey]: this._recordOfAvailableNnzs[renewalEdit.nnzsRecordKey]?.map(nnz => {
                if (nnz.not_numbered_zone_id === renewalEdit.assignedNnzId) {
                    nnz.assignedTo = (nnz.assignedTo ?? new Set()).add(renewalEdit.id);
                    return { ...nnz, availableSeatsCalc: (nnz.availableSeatsCalc ?? nnz.available_seats) - 1 };
                } else {
                    return nnz;
                }
            })
        };
        this._dialogState.setRecordOfAvailableNnzs(this._recordOfAvailableNnzs);

    }

    loadNnzFromASector(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._availableSectors.find(sector => {
            if (sector.sector_id === renewalEdit.assignedSectorId) {
                if (!this._recordOfAvailableNnzs[renewalEdit.nnzsRecordKey]) {
                    this._recordOfAvailableNnzs = {
                        ...this._recordOfAvailableNnzs,
                        [renewalEdit.nnzsRecordKey]: sector.not_numbered_zones
                    };
                }
                this._dialogState.setRecordOfAvailableNnzs(this._recordOfAvailableNnzs);
                return true;
            }
            return undefined;
        });
    }

    isNnzShown(
        renewalEdit: VmSeasonTicketRenewalEdit,
        nnz: VmSeasonTicketRenewalAvailableNnz
    ): boolean {
        return Number.isInteger(renewalEdit.assignedSectorId) &&
            (
                nnz.availableSeatsCalc === undefined ||
                this.isNnzShownByCalculatedAvailableSeatsAndMappedInfo(renewalEdit, nnz) ||
                nnz.alreadyMappedTo?.has(renewalEdit.id) ||
                nnz.assignedTo?.has(renewalEdit.id)
            );
    }

    initNnzsRecordKey(renewal: VmSeasonTicketRenewalEdit): string {
        if (this.isRenewalEditMapped(renewal)) {
            return `${renewal.actual_seat.seat_id}`;
        } else {
            return this._dialogState.initRowsRecordKey;
        }
    }

    setAvailableSectors(availableSectors: VmSeasonTicketRenewalAvailableSector[]): void {
        this._availableSectors = availableSectors;
        this._dialogState.setAvailableSectors(availableSectors);
    }

    updateSectorsWhenResetSeat(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._availableSectors = this._availableSectors.map(sector => {
            if (sector.sector_id === renewalEdit.assignedSectorId && sector.assignedTo?.has(renewalEdit.id)) {
                sector.assignedTo.delete(renewalEdit.id);
                return { ...sector, availableSeatsCalc: sector.availableSeatsCalc + 1 };
            } else {
                return sector;
            }
        });
        this._dialogState.setAvailableSectors(this._availableSectors);
    }

    updateSectorsWhenAssignSeat(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this._availableSectors = this._availableSectors.map(sector => {
            if (sector.sector_id === renewalEdit.assignedSectorId) {
                sector.assignedTo = (sector.assignedTo ?? new Set()).add(renewalEdit.id);
                return { ...sector, availableSeatsCalc: (sector.availableSeatsCalc ?? sector.available_seats) - 1 };
            } else {
                return sector;
            }
        });
        this._dialogState.setAvailableSectors(this._availableSectors);
    }

    isSectorShown(
        renewalEdit: VmSeasonTicketRenewalEdit,
        sector: VmSeasonTicketRenewalAvailableSector
    ): boolean {
        return sector.availableSeatsCalc === undefined ||
            this.isSectorShownByCalculatedAvailableSeatsAndMappedInfo(renewalEdit, sector) ||
            sector.alreadyMappedTo?.has(renewalEdit.id) ||
            sector.assignedTo?.has(renewalEdit.id);
    }

    private isRowShownByCalculatedAvailableSeatsAndMappedInfo(
        renewalEdit: VmSeasonTicketRenewalEdit,
        row: VmSeasonTicketRenewalAvailableRow
    ): boolean {
        return row.availableSeatsCalc !== 0 &&
            (
                !row.alreadyMappedTo ||
                (
                    !row.alreadyMappedTo.has(renewalEdit.id) &&
                    row.available_seats > row.alreadyMappedTo.size
                )
            );
    }

    private isNnzShownByCalculatedAvailableSeatsAndMappedInfo(
        renewalEdit: VmSeasonTicketRenewalEdit,
        nnz: VmSeasonTicketRenewalAvailableNnz
    ): boolean {
        return nnz.availableSeatsCalc !== 0 &&
            (
                !nnz.alreadyMappedTo ||
                (
                    !nnz.alreadyMappedTo.has(renewalEdit.id) &&
                    nnz.available_seats > nnz.alreadyMappedTo.size
                )
            );
    }

    private isSectorShownByCalculatedAvailableSeatsAndMappedInfo(
        renewalEdit: VmSeasonTicketRenewalEdit,
        sector: VmSeasonTicketRenewalAvailableSector
    ): boolean {
        return sector.availableSeatsCalc !== 0 &&
            (
                !sector.alreadyMappedTo ||
                (
                    !sector.alreadyMappedTo.has(renewalEdit.id) &&
                    sector.available_seats > sector.alreadyMappedTo.size
                )
            );
    }

    private isRenewalEditMapped(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return !!renewalEdit.actual_seat;
    }

    private getMappedSectorIndex(
        renewalEdit: VmSeasonTicketRenewalEdit,
        availableSectors: VmSeasonTicketRenewalAvailableSector[]
    ): number {
        return availableSectors.findIndex(sector =>
            sector.sector_id === renewalEdit.actual_seat.sector_id);
    }

    private updateAvailableSectorsForRowsWithMappedInfo(
        mappedSectorIndex: number,
        renewalEdit: VmSeasonTicketRenewalEdit,
        updatedAvailableSectors: VmSeasonTicketRenewalAvailableSector[]
    ): void {
        if (mappedSectorIndex === -1) {
            const mappedRow = this.getMappedRow(renewalEdit);
            const mappedSector = this.getMappedSectorForRows(renewalEdit, mappedRow);
            updatedAvailableSectors.push(mappedSector);
            return;
        }

        const mappedRowIndex = this.getMappedRowIndex(
            renewalEdit,
            updatedAvailableSectors[mappedSectorIndex]
        );

        if (mappedRowIndex === -1) {
            const mappedRow = this.getMappedRow(renewalEdit);
            this.updateMappedSectorForRows(
                renewalEdit,
                updatedAvailableSectors[mappedSectorIndex],
                mappedRow
            );
            return;
        }

        this.updateMappedRow(
            renewalEdit,
            updatedAvailableSectors[mappedSectorIndex].rows[mappedRowIndex]
        );

        this.updateMappedSectorForRows(
            renewalEdit,
            updatedAvailableSectors[mappedSectorIndex]
        );
    }

    private getMappedRow(renewalEdit: VmSeasonTicketRenewalEdit): VmSeasonTicketRenewalAvailableRow {
        return {
            available_seats: 1,
            row_id: renewalEdit.actual_seat.row_id,
            row_name: renewalEdit.actual_seat.row,
            alreadyMappedTo: (new Set<string>()).add(renewalEdit.id)
        };
    }

    private getMappedSectorForRows(
        renewalEdit: VmSeasonTicketRenewalEdit,
        row: VmSeasonTicketRenewalAvailableRow
    ): VmSeasonTicketRenewalAvailableSector {
        return {
            rows: [row],
            not_numbered_zones: [],
            sector_id: renewalEdit.actual_seat.sector_id,
            available_seats: 1,
            sector_name: renewalEdit.actual_seat.sector,
            alreadyMappedTo: (new Set<string>()).add(renewalEdit.id)
        };
    }

    private getMappedInfoForRows(
        renewalEdit: VmSeasonTicketRenewalEdit,
        renewalIndex: number,
        sector: VmSeasonTicketRenewalAvailableSector,
        row: VmSeasonTicketRenewalAvailableRow
    ): MappedInfo {
        return {
            sector,
            row,
            seat: {
                seat_id: renewalEdit.actual_seat.seat_id,
                seat_name: renewalEdit.actual_seat.seat,
                alreadyMappedTo: renewalEdit.id
            },
            renewal: renewalEdit,
            renewalIndex
        };
    }

    private getMappedRowIndex(
        renewalEdit: VmSeasonTicketRenewalEdit,
        sector: VmSeasonTicketRenewalAvailableSector
    ): number {
        return sector.rows.findIndex(row =>
            row.row_id === renewalEdit.actual_seat.row_id);
    }

    private updateMappedSectorForRows(
        renewalEdit: VmSeasonTicketRenewalEdit,
        sector: VmSeasonTicketRenewalAvailableSector,
        row?: VmSeasonTicketRenewalAvailableRow
    ): void {
        if (row) {
            sector.rows.push(row);
        }
        sector.available_seats += 1;
        sector.alreadyMappedTo = (sector.alreadyMappedTo ?? new Set()).add(renewalEdit.id);
    }

    private updateMappedRow(
        renewalEdit: VmSeasonTicketRenewalEdit,
        row: VmSeasonTicketRenewalAvailableRow
    ): void {
        row.available_seats += 1;
        row.alreadyMappedTo = (row.alreadyMappedTo ?? new Set()).add(renewalEdit.id);
    }

    private updateAvailableSectorsForNnzsWithMappedInfo(
        mappedSectorIndex: number,
        renewalEdit: VmSeasonTicketRenewalEdit,
        updatedAvailableSectors: VmSeasonTicketRenewalAvailableSector[]
    ): void {
        if (mappedSectorIndex === -1) {
            const mappedNNZ = this.getMappedNnz(renewalEdit);
            const mappedSector = this.getMappedSectorForNnz(renewalEdit, mappedNNZ);
            updatedAvailableSectors.push(mappedSector);
            return;
        }

        const mappedNnzIndex = this.getMappedNnzIndex(
            renewalEdit,
            updatedAvailableSectors[mappedSectorIndex]
        );

        if (mappedNnzIndex === -1) {
            const mappedNnz = this.getMappedNnz(renewalEdit);
            this.updateMappedSectorForNnz(
                renewalEdit,
                updatedAvailableSectors[mappedSectorIndex],
                mappedNnz
            );
            return;
        }

        this.updateMappedNnz(
            renewalEdit,
            updatedAvailableSectors[mappedSectorIndex].not_numbered_zones[mappedNnzIndex]
        );

        this.updateMappedSectorForNnz(
            renewalEdit,
            updatedAvailableSectors[mappedSectorIndex]
        );
    }

    private getMappedNnzIndex(
        renewalEdit: VmSeasonTicketRenewalEdit,
        sector: VmSeasonTicketRenewalAvailableSector
    ): number {
        return sector.not_numbered_zones.findIndex(nnz =>
            nnz.not_numbered_zone_id === renewalEdit.actual_seat.not_numbered_zone_id);
    }

    private getMappedNnz(renewalEdit: VmSeasonTicketRenewalEdit): VmSeasonTicketRenewalAvailableNnz {
        return {
            available_seats: 1,
            not_numbered_zone_id: renewalEdit.actual_seat.not_numbered_zone_id,
            not_numbered_zone_name: renewalEdit.actual_seat.not_numbered_zone,
            alreadyMappedTo: (new Set<string>()).add(renewalEdit.id)
        };
    }

    private getMappedSectorForNnz(
        renewalEdit: VmSeasonTicketRenewalEdit,
        nnz: VmSeasonTicketRenewalAvailableNnz
    ): VmSeasonTicketRenewalAvailableSector {
        return {
            not_numbered_zones: [nnz],
            rows: [],
            sector_id: renewalEdit.actual_seat.sector_id,
            available_seats: 1,
            sector_name: renewalEdit.actual_seat.sector,
            alreadyMappedTo: (new Set<string>()).add(renewalEdit.id)
        };
    }

    private updateMappedSectorForNnz(
        renewalEdit: VmSeasonTicketRenewalEdit,
        sector: VmSeasonTicketRenewalAvailableSector,
        nnz?: VmSeasonTicketRenewalAvailableNnz
    ): void {
        if (nnz) {
            sector.not_numbered_zones.push(nnz);
        }
        sector.available_seats += 1;
        sector.alreadyMappedTo = (sector.alreadyMappedTo ?? new Set()).add(renewalEdit.id);
    }

    private updateMappedNnz(
        renewalEdit: VmSeasonTicketRenewalEdit,
        nnz: VmSeasonTicketRenewalAvailableNnz
    ): void {
        nnz.available_seats += 1;
        nnz.alreadyMappedTo = (nnz.alreadyMappedTo ?? new Set()).add(renewalEdit.id);
    }

    private getMappedInfoForNnzs(
        renewalEdit: VmSeasonTicketRenewalEdit,
        renewalIndex: number,
        sector: VmSeasonTicketRenewalAvailableSector,
        nnz: VmSeasonTicketRenewalAvailableNnz
    ): MappedInfo {
        return {
            sector,
            nnz,
            seat: {
                seat_id: renewalEdit.actual_seat.seat_id,
                seat_name: renewalEdit.actual_seat.seat,
                alreadyMappedTo: renewalEdit.id
            },
            renewal: renewalEdit,
            renewalIndex
        };
    }
}
