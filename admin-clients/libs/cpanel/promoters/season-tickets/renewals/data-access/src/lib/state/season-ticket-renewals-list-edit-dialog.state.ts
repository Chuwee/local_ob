import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { VmSeasonTicketRenewalAvailableNnz } from '../models/vm-season-ticket-renewal-available-nnz.model';
import { VmSeasonTicketRenewalAvailableRow } from '../models/vm-season-ticket-renewal-available-row.model';
import { VmSeasonTicketRenewalAvailableSeat } from '../models/vm-season-ticket-renewal-available-seat.model';
import { VmSeasonTicketRenewalAvailableSector } from '../models/vm-season-ticket-renewal-available-sector.model';
import { VmSeasonTicketRenewalEdit } from '../models/vm-season-ticket-renewal-edit.model';

@Injectable()
export class SeasonTicketRenewalsListEditDialogState {
    private readonly _renewalEdits = new BaseStateProp<VmSeasonTicketRenewalEdit[]>();
    readonly getRenewalEdits$ = this._renewalEdits.getValueFunction();
    readonly setRenewalEdits = this._renewalEdits.setValueFunction();

    private readonly _availableSectors = new BaseStateProp<VmSeasonTicketRenewalAvailableSector[]>([]);
    readonly getAvailableSectors$ = this._availableSectors.getValueFunction();
    readonly setAvailableSectors = this._availableSectors.setValueFunction();

    readonly initRowsRecordKey = 'init';
    private readonly _recordOfAvailableRows = new BaseStateProp<Record<string, VmSeasonTicketRenewalAvailableRow[]>>(
        {
            [this.initRowsRecordKey]: []
        });

    readonly getRecordOfAvailableRows$ = this._recordOfAvailableRows.getValueFunction();
    readonly setRecordOfAvailableRows = this._recordOfAvailableRows.setValueFunction();

    readonly initRowSeatsRecordKey = 'init';
    private readonly _recordOfAvailableRowSeats = new BaseStateProp<Record<string, VmSeasonTicketRenewalAvailableSeat[]>>(
        {
            [this.initRowSeatsRecordKey]: []
        });

    readonly getRecordOfAvailableRowSeats$ = this._recordOfAvailableRowSeats.getValueFunction();
    readonly setRecordOfAvailableRowSeats = this._recordOfAvailableRowSeats.setValueFunction();

    readonly initNNZsRecordKey = 'init';
    private readonly _recordOfAvailableNnzs = new BaseStateProp<Record<string, VmSeasonTicketRenewalAvailableNnz[]>>(
        {
            [this.initNNZsRecordKey]: []
        });

    readonly getRecordOfAvailableNnzs$ = this._recordOfAvailableNnzs.getValueFunction();
    readonly setRecordOfAvailableNnzs = this._recordOfAvailableNnzs.setValueFunction();

    readonly initNnzSeatsRecordKey = 'init';
    private readonly _recordOfAvailableNnzSeats = new BaseStateProp<Record<string, VmSeasonTicketRenewalAvailableSeat[]>>(
        {
            [this.initNnzSeatsRecordKey]: []
        });

    readonly getRecordOfAvailableNnzSeats$ = this._recordOfAvailableNnzSeats.getValueFunction();
    readonly setRecordOfAvailableNnzSeats = this._recordOfAvailableNnzSeats.setValueFunction();
}
