import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { ExternalManagementApi } from './api/external-management.api';
import { FlcIncompatibilitiesEngineData } from './models/flc-incompatibilities-engine-data.model';
import { ExternalManagementState } from './state/external-management.state';

@Injectable({
    providedIn: 'root'
})
export class ExternalManagementService {

    constructor(
        private _externalManagementApi: ExternalManagementApi,
        private _externalManagementState: ExternalManagementState
    ) { }

    loadFlcIncompatibilitiesEngineData(): void {
        this._externalManagementState.setFlcIncompatibilitiesEngineDataError(null);
        this._externalManagementState.setFlcIncompatibilitiesEngineDataInProgress(true);
        this._externalManagementApi.getFlcIncompatibilitiesEngineData()
            .pipe(
                catchError(error => {
                    this._externalManagementState.setFlcIncompatibilitiesEngineDataError(error);
                    return of(null);
                }),
                finalize(() => this._externalManagementState.setFlcIncompatibilitiesEngineDataInProgress(false))
            )
            .subscribe(producer =>
                this._externalManagementState.setFlcIncompatibilitiesEngineData(producer)
            );
    }

    isFlcIncompatibilitiesEngineDataLoading$(): Observable<boolean> {
        return this._externalManagementState.isFlcIncompatibilitiesEngineDataInProgress$();
    }

    getFlcIncompatibilitiesEngineData$(): Observable<FlcIncompatibilitiesEngineData> {
        return this._externalManagementState.getFlcIncompatibilitiesEngineData$();
    }

    getFlcIncompatibilitiesEngineDataError$(): Observable<HttpErrorResponse> {
        return this._externalManagementState.getFlcIncompatibilitiesEngineDataError$();
    }

    clearFlcIncompatibilitiesEngineData(): void {
        this._externalManagementState.setFlcIncompatibilitiesEngineData(null);
    }
}
