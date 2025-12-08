import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { FlcIncompatibilitiesEngineData } from '../models/flc-incompatibilities-engine-data.model';

@Injectable({
    providedIn: 'root'
})
export class ExternalManagementState {
    // FLC incompatibilities engine login data
    private _flcIncompatibilitiesEngineData = new BaseStateProp<FlcIncompatibilitiesEngineData>();
    readonly getFlcIncompatibilitiesEngineData$ = this._flcIncompatibilitiesEngineData.getValueFunction();
    readonly setFlcIncompatibilitiesEngineData = this._flcIncompatibilitiesEngineData.setValueFunction();
    readonly getFlcIncompatibilitiesEngineDataError$ = this._flcIncompatibilitiesEngineData.getErrorFunction();
    readonly setFlcIncompatibilitiesEngineDataError = this._flcIncompatibilitiesEngineData.setErrorFunction();
    readonly isFlcIncompatibilitiesEngineDataInProgress$ = this._flcIncompatibilitiesEngineData.getInProgressFunction();
    readonly setFlcIncompatibilitiesEngineDataInProgress = this._flcIncompatibilitiesEngineData.setInProgressFunction();
}
