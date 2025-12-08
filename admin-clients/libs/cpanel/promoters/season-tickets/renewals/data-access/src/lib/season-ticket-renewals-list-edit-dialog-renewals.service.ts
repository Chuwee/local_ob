import { inject, Injectable } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';
import { MappedInfo } from './models/mapped-info.model';
import { VmSeasonTicketRenewalAvailableNnz } from './models/vm-season-ticket-renewal-available-nnz.model';
import { VmSeasonTicketRenewalAvailableRow } from './models/vm-season-ticket-renewal-available-row.model';
import { VmSeasonTicketRenewalAvailableSeat } from './models/vm-season-ticket-renewal-available-seat.model';
import { VmSeasonTicketRenewalAvailableSector } from './models/vm-season-ticket-renewal-available-sector.model';
import { VmSeasonTicketRenewalEdit } from './models/vm-season-ticket-renewal-edit.model';
import { SeasonTicketRenewalsListEditDialogState } from './state/season-ticket-renewals-list-edit-dialog.state';

@Injectable()
export class SeasonTicketRenewalsListEditDialogRenewalsService {
    readonly #dialogState = inject(SeasonTicketRenewalsListEditDialogState);

    #renewalEdits: VmSeasonTicketRenewalEdit[] = [];

    getRenewalEdits(): VmSeasonTicketRenewalEdit[] {
        return this.#renewalEdits;
    }

    getRenewalEdits$(): Observable<VmSeasonTicketRenewalEdit[]> {
        return this.#dialogState.getRenewalEdits$();
    }

    setRenewalEdits(renewalEdits: VmSeasonTicketRenewalEdit[]): void {
        this.#renewalEdits = renewalEdits;
        this.#dialogState.setRenewalEdits(renewalEdits);
    }

    assignSector(sectorId: number, renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenSectorIdAssigned(sectorId, this.#renewalEdits, renewalEditIndex);
        this.#renewalEdits = this.#getRenewalsEditsWhenAssignedRowIdToNull(this.#renewalEdits, renewalEditIndex);
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    assignRow(rowId: number, renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenRowIdAssigned(rowId, this.#renewalEdits, renewalEditIndex);
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    assignNnz(nnzId: number, renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenNnzIdAssigned(nnzId, this.#renewalEdits, renewalEditIndex);
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    assignRowSeat(seatId: number, renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenSeatIdAssigned(
            seatId,
            this.#renewalEdits,
            renewalEditIndex
        );
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    assignNnzSeat(seatId: number, renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenSeatIdAssigned(seatId, this.#renewalEdits, renewalEditIndex);
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    resetSector(renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenAssignedSectorIdToNull(
            this.#renewalEdits,
            renewalEditIndex
        );
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    resetRow(renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenAssignedRowIdToNull(this.#renewalEdits, renewalEditIndex);
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    resetNnz(renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenAssignedNnzIdToNull(this.#renewalEdits, renewalEditIndex);
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    resetRate(renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalEditsWhenResetRate(
            this.#renewalEdits,
            renewalEditIndex
        );
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    resetAutoRenewal(renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalEditsWhenResetAutoRenewal(
            this.#renewalEdits,
            renewalEditIndex
        );
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    changeRate(renewalEditIndex: number, rateName: string): void {
        this.#renewalEdits = this.#getRenewalEditsWhenRateChange(
            rateName,
            this.#renewalEdits,
            renewalEditIndex
        );
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    changeAutoRenewal(renewalEditIndex: number, autoRenewal: boolean): void {
        this.#renewalEdits = this.#getRenewalEditsWhenAutoRenewalChange(
            autoRenewal,
            this.#renewalEdits,
            renewalEditIndex
        );
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    setRowIdToNullWhenNotShownButIsAssigned(row: VmSeasonTicketRenewalAvailableRow, renewalEditIndex: number): void {
        if (row.row_id === this.#renewalEdits[renewalEditIndex].assignedRowId) {
            this.#renewalEdits = this.#getRenewalsEditsWhenAssignedRowIdToNull(
                this.#renewalEdits,
                renewalEditIndex
            );
            this.#dialogState.setRenewalEdits(this.#renewalEdits);
        }
    }

    setNnzIdToNullWhenNotShownButIsAssigned(nnz: VmSeasonTicketRenewalAvailableNnz, renewalEditIndex: number): void {
        if (nnz.not_numbered_zone_id === this.#renewalEdits[renewalEditIndex].assignedNnzId) {
            this.#renewalEdits = this.#getRenewalsEditsWhenAssignedNnzIdToNull(
                this.#renewalEdits,
                renewalEditIndex
            );
            this.#dialogState.setRenewalEdits(this.#renewalEdits);
        }
    }

    setSectorIdToNullWhenNotShownButIsAssigned(sector: VmSeasonTicketRenewalAvailableSector, renewalEditIndex: number): void {
        if (sector.sector_id === this.#renewalEdits[renewalEditIndex].assignedSectorId) {
            this.#renewalEdits = this.#getRenewalsEditsWhenAssignedSectorIdToNull(
                this.#renewalEdits,
                renewalEditIndex
            );
            this.#dialogState.setRenewalEdits(this.#renewalEdits);
        }
    }

    isRateValid(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return renewalEdit.assignedRate && (renewalEdit.assignedRate !== renewalEdit.actual_rate);
    }

    isRateDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return renewalEdit.assignedRate && (renewalEdit.assignedRate !== renewalEdit.actual_rate);
    }

    assignRowsAndNnzsWithMappedInfo(mappedInfos: MappedInfo[]): void {
        this.#renewalEdits = mappedInfos.reduce((accRenewalEdits, mappedInfo) => {
            accRenewalEdits = this.#getRenewalsEditsWhenSectorIdAssigned(
                mappedInfo.sector.sector_id,
                accRenewalEdits,
                mappedInfo.renewalIndex);

            if (mappedInfo.row) {
                accRenewalEdits = this.#getRenewalsEditsWhenRowIdAssigned(
                    mappedInfo.row.row_id,
                    accRenewalEdits,
                    mappedInfo.renewalIndex
                );
            } else if (mappedInfo.nnz) {
                accRenewalEdits = this.#getRenewalsEditsWhenNnzIdAssigned(
                    mappedInfo.nnz.not_numbered_zone_id,
                    accRenewalEdits,
                    mappedInfo.renewalIndex
                );
            }

            return accRenewalEdits;
        }, this.#renewalEdits);
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    resetSeat(renewalEditIndex: number): void {
        this.#renewalEdits = this.#getRenewalsEditsWhenAssignedSeatIdToNull(this.#renewalEdits, renewalEditIndex);
        this.#dialogState.setRenewalEdits(this.#renewalEdits);
    }

    completeLocationInfo(
        renewalEdit: VmSeasonTicketRenewalEdit,
        availableSectors: VmSeasonTicketRenewalAvailableSector[],
        recordOfAvailableRows: Record<string, VmSeasonTicketRenewalAvailableRow[]>,
        recordOfAvailableNnzs: Record<string, VmSeasonTicketRenewalAvailableNnz[]>,
        recordOfAvailableRowSeats: Record<string, VmSeasonTicketRenewalAvailableSeat[]>,
        recordOfAvailableNnzSeats: Record<string, VmSeasonTicketRenewalAvailableSeat[]>
    ): VmSeasonTicketRenewalEdit {
        let completedRenewalEdit: VmSeasonTicketRenewalEdit = renewalEdit;
        if (this.isNnzAssigned(renewalEdit)) {
            completedRenewalEdit = {
                ...renewalEdit,
                assignedSector: availableSectors.find(sector =>
                    sector.sector_id === renewalEdit.assignedSectorId),
                assignedNnz: recordOfAvailableNnzs[renewalEdit.nnzsRecordKey].find(nnz =>
                    nnz.not_numbered_zone_id === renewalEdit.assignedNnzId),
                assignedSeat: recordOfAvailableNnzSeats[renewalEdit.nnzSeatsRecordKey].find(seat =>
                    seat.seat_id === renewalEdit.assignedSeatId)
            };
        } else if (this.isRowAssigned(renewalEdit)) {
            completedRenewalEdit = {
                ...renewalEdit,
                assignedSector: availableSectors.find(sector =>
                    sector.sector_id === renewalEdit.assignedSectorId),
                assignedRow: recordOfAvailableRows[renewalEdit.rowsRecordKey].find(row =>
                    row.row_id === renewalEdit.assignedRowId),
                assignedSeat: recordOfAvailableRowSeats[renewalEdit.rowSeatsRecordKey].find(seat =>
                    seat.seat_id === renewalEdit.assignedSeatId)
            };
        }
        return completedRenewalEdit;
    }

    isSeatDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        if (this.#isLocationMapped(renewalEdit)) {
            return Number.isInteger(renewalEdit.assignedSeatId) &&
                renewalEdit.assignedSeatId !== renewalEdit.actual_seat.seat_id;
        } else {
            return Number.isInteger(renewalEdit.assignedSeatId);
        }
    }

    isLocationNotMappedDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return !this.#isLocationMapped(renewalEdit) && this.#isSectorDirty(renewalEdit);
    }

    isLocationMappedDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return this.#isLocationMapped(renewalEdit) &&
            (
                this.#isSectorDirty(renewalEdit) ||
                this.#isRowDirty(renewalEdit) ||
                this.#isNnzDirty(renewalEdit) ||
                this.isSeatDirty(renewalEdit)
            );
    }

    isSeatAssigned(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return Number.isInteger(renewalEdit.assignedSeatId);
    }

    isRowAssigned(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return Number.isInteger(renewalEdit.assignedRowId);
    }

    isNnzAssigned(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return Number.isInteger(renewalEdit.assignedNnzId);
    }

    isSomeRateValid$(): Observable<boolean> {
        return this.#dialogState.getRenewalEdits$()
            .pipe(
                map(renewalEditSeats => renewalEditSeats.some(renewalEdit =>
                    this.isRateValid(renewalEdit))
                ),
                distinctUntilChanged()
            );
    }

    isAutoRenewalDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return renewalEdit.assignedAutoRenewal !== renewalEdit.auto_renewal;
    }

    hasAnyValidChanges$(): Observable<boolean> {
        return this.#dialogState.getRenewalEdits$()
            .pipe(
                map(renewalEdits => renewalEdits.some(renewalEdit =>
                    this.isSeatDirty(renewalEdit) ||
                    this.isRateValid(renewalEdit) ||
                    this.isAutoRenewalDirty(renewalEdit)
                )),
                distinctUntilChanged()
            );
    }

    hasAnyInvalidLocationChanges$(): Observable<boolean> {
        return combineLatest([
            this.#isSomeLocationDirty$(),
            this.#areAllDirtyLocationsValid$()
        ]).pipe(
            map(([isSomeLocationDirty, areAllDirtyLocationsValid]) =>
                isSomeLocationDirty && !areAllDirtyLocationsValid
            ),
            distinctUntilChanged()
        );
    }

    hasAnyDirtyChanges(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return this.isLocationNotMappedDirty(renewalEdit) ||
            this.isLocationMappedDirty(renewalEdit) ||
            this.isRateDirty(renewalEdit) ||
            this.isAutoRenewalDirty(renewalEdit);
    }

    hasValidChangesToSave(renewalEdit: VmSeasonTicketRenewalEdit, isAutoRenewalAvailable: boolean): boolean {
        return this.isSeatDirty(renewalEdit) ||
            this.isRateValid(renewalEdit) ||
            (isAutoRenewalAvailable && this.isAutoRenewalDirty(renewalEdit));
    }

    #isSomeLocationDirty$(): Observable<boolean> {
        return this.#dialogState.getRenewalEdits$()
            .pipe(
                map(renewalEditSeats => renewalEditSeats.some(renewalEdit =>
                    this.isLocationNotMappedDirty(renewalEdit) || this.isLocationMappedDirty(renewalEdit)
                )),
                distinctUntilChanged()
            );
    }

    #areAllDirtyLocationsValid$(): Observable<boolean> {
        return combineLatest([
            this.#isSomeLocationDirty$(),
            this.#dialogState.getRenewalEdits$()
        ]).pipe(
            map(([hasDirtyLocations, renewalEdits]) => {
                if (!hasDirtyLocations) return true;

                const dirtyRenewals = renewalEdits.filter(renewalEdit =>
                    this.isLocationNotMappedDirty(renewalEdit) || this.isLocationMappedDirty(renewalEdit)
                );
                return dirtyRenewals.every(renewalEdit => this.isSeatDirty(renewalEdit));
            }),
            distinctUntilChanged()
        );
    }

    #getRenewalEditsWhenResetRate(renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number): VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedRate: newRenewalEdits[renewalEditIndex].actual_rate
        };
        return newRenewalEdits;
    }

    #getRenewalEditsWhenResetAutoRenewal(renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedAutoRenewal: newRenewalEdits[renewalEditIndex].auto_renewal
        };
        return newRenewalEdits;
    }

    #getRenewalEditsWhenRateChange(rateName: string, renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedRate: rateName
        };
        return newRenewalEdits;
    }

    #getRenewalEditsWhenAutoRenewalChange(autoRenewal: boolean, renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedAutoRenewal: autoRenewal
        };
        return newRenewalEdits;
    }

    #getRenewalsEditsWhenAssignedSeatIdToNull(renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedSeatId: null
        };
        return newRenewalEdits;
    }

    #getRenewalsEditsWhenAssignedSectorIdToNull(renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedSectorId: null,
            rowsRecordKey: this.#dialogState.initRowsRecordKey,
            nnzSeatsRecordKey: this.#dialogState.initNnzSeatsRecordKey
        };
        return newRenewalEdits;
    }

    #getRenewalsEditsWhenSectorIdAssigned(sectorId: number, renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedSectorId: sectorId,
            rowsRecordKey: `${sectorId}`,
            nnzsRecordKey: `${sectorId}`
        };
        return newRenewalEdits;
    }

    #getRenewalsEditsWhenAssignedRowIdToNull(renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedRowId: null,
            rowSeatsRecordKey: this.#dialogState.initRowSeatsRecordKey
        };
        return newRenewalEdits;
    }

    #getRenewalsEditsWhenRowIdAssigned(rowId: number, renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedRowId: rowId,
            rowSeatsRecordKey: `${newRenewalEdits[renewalEditIndex].assignedSectorId}-${rowId}`
        };
        return newRenewalEdits;
    }

    #getRenewalsEditsWhenAssignedNnzIdToNull(renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedNnzId: null,
            nnzSeatsRecordKey: this.#dialogState.initNnzSeatsRecordKey
        };
        return newRenewalEdits;
    }

    #getRenewalsEditsWhenNnzIdAssigned(nnzId: number, renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedNnzId: nnzId,
            nnzSeatsRecordKey: `${newRenewalEdits[renewalEditIndex].assignedSectorId}-${nnzId}`
        };
        return newRenewalEdits;
    }

    #getRenewalsEditsWhenSeatIdAssigned(seatId: number, renewalEdits: VmSeasonTicketRenewalEdit[], renewalEditIndex: number):
        VmSeasonTicketRenewalEdit[] {
        const newRenewalEdits = renewalEdits.slice();
        newRenewalEdits[renewalEditIndex] = {
            ...newRenewalEdits[renewalEditIndex],
            assignedSeatId: seatId
        };
        return newRenewalEdits;
    }

    #isLocationMapped(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return !!renewalEdit.actual_seat;
    }

    #isSectorDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        if (this.#isLocationMapped(renewalEdit)) {
            return Number.isInteger(renewalEdit.assignedSectorId) &&
                renewalEdit.assignedSectorId !== renewalEdit.actual_seat.sector_id;
        } else {
            return Number.isInteger(renewalEdit.assignedSectorId);
        }
    }

    #isRowDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        if (this.#isLocationMapped(renewalEdit)) {
            return Number.isInteger(renewalEdit.assignedRowId) &&
                renewalEdit.assignedRowId !== renewalEdit.actual_seat.row_id;
        } else {
            return Number.isInteger(renewalEdit.assignedRowId);
        }
    }

    #isNnzDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        if (this.#isLocationMapped(renewalEdit)) {
            return Number.isInteger(renewalEdit.assignedNnzId) &&
                renewalEdit.assignedNnzId !== renewalEdit.actual_seat.not_numbered_zone_id;
        } else {
            return Number.isInteger(renewalEdit.assignedNnzId);
        }
    }
}
